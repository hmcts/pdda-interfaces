package uk.gov.hmcts.pdda.business.services.pdda.sftp;

import net.schmizz.sshj.sftp.RemoteFile;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.services.pdda.BaisValidation;
import uk.gov.hmcts.pdda.business.services.pdda.PddaHelper.BaisXhibitValidation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is used to run all the sftp commands on a remote folder.
 */
public class PddaSftpHelperSshj {
    private static final Logger LOG = LoggerFactory.getLogger(PddaSftpHelperSshj.class);
    protected static final String LOG_CALLED = " called";
    private static final String TWO_PARAMS = "{}{}";

    /**
     * Fetch files from a remote folder.
     * 
     * @param remoteFolder The remote folder
     * @param validation The validation
     * @return The files
     * @throws IOException The IO Exception
     */
    public Map<String, String> sftpFetch(SFTPClient sftpClient, String remoteFolder,
        BaisXhibitValidation validation) throws IOException {
        String methodName = "sftpFetch()";
        LOG.debug(TWO_PARAMS, methodName, LOG_CALLED);

        // Fetch Files
        Map<String, String> files = fetchFiles(sftpClient, remoteFolder, validation);
        LOG.debug("No Of Files Fetched: {}", files.size());

        return files;
    }

    /**
     * Fetch files from a remote folder.
     * 
     * @param remoteFolder The remote folder
     * @param validation The validation
     * @return The files
     * @throws IOException The IO Exception
     */
    public Map<String, String> sftpFetch(SFTPClient sftpClient, String remoteFolder,
        BaisValidation validation) throws IOException {
        String methodName = "sftpFetch()";
        LOG.debug(TWO_PARAMS, methodName, LOG_CALLED);

        testNull(sftpClient);
        testNull(remoteFolder);
        testNull(sftpClient.ls(remoteFolder));

        // Fetch Files
        Map<String, String> files = fetchFiles(sftpClient, remoteFolder, validation);
        LOG.debug("No Of Files Fetched: {}", files.size());

        return files;
    }


    /**
     * Delete a file from a remote folder.
     * 
     * @param remoteFolder The remote folder
     * @param filename The filename
     * @throws IOException The IO Exception
     */
    public void sftpDeleteFile(SFTPClient sftpClient, String remoteFolder, String filename)
        throws IOException {
        String methodName = "sftpDeleteFile()";
        LOG.debug(TWO_PARAMS, methodName, LOG_CALLED);

        testNull(sftpClient);
        testNull(remoteFolder);
        testNull(sftpClient.ls(remoteFolder));

        try {
            sftpClient.rm(remoteFolder + filename);
        } catch (IOException e) {
            LOG.error("Stacktrace2:: {}", ExceptionUtils.getStackTrace(e));
            throw e;
        }
    }


    /**
     * Fetch the files from the remote folder.
     * 
     * @param sftpClient The SFTP Client
     * @param remoteFolder The remote folder
     * @param validation The validation
     * @return The files
     * @throws IOException The IO Exception
     */
    private Map<String, String> fetchFiles(SFTPClient sftpClient, String remoteFolder,
        BaisValidation validation) throws IOException {
        String methodName = "fetchFiles()";
        LOG.debug(TWO_PARAMS, methodName, LOG_CALLED);

        Map<String, String> files = new ConcurrentHashMap<>();
        try {
            List<String> listOfFilesInFolder =
                listFilesInFolder(sftpClient, remoteFolder, validation);

            if (LOG.isDebugEnabled()) {
                if (listOfFilesInFolder != null) {
                    LOG.debug("List of files in remote folder: {}", remoteFolder);
                    listOfFilesInFolder.forEach(filename -> LOG.debug("File: {}", filename));
                } else {
                    LOG.debug("No files in remote folder");
                }
            }
            if (listOfFilesInFolder != null) {
                for (String filename : listOfFilesInFolder) {
                    try (RemoteFile remoteFile =
                        sftpClient.getSFTPEngine().open(remoteFolder + filename)) {
                        try (InputStream is = getFile(remoteFile);) {
                            String fileContents = getFileContents(filename, is);
                            files.put(filename, fileContents);
                        } finally {
                            LOG.debug("File closed: {}", filename);
                        }
                    } catch (IOException e) {
                        LOG.error("Error reading: {}", filename);
                        LOG.error("Stacktrace1:: {}", ExceptionUtils.getStackTrace(e));
                    }
                }
            }
            return files;
        } catch (IOException e1) {
            LOG.error("Stacktrace4:: {}", ExceptionUtils.getStackTrace(e1));
            throw e1;
        }
    }

    /**
     * List the files in a folder.
     * 
     * @param sftpClient The SFTP Client
     * @param folder The folder
     * @param validation The validation
     * @return The list of files
     * @throws SftpException The SFTP Exception
     * @throws IOException The IO Exception
     */
    public List<String> listFilesInFolder(SFTPClient sftpClient, String folder,
        BaisValidation validation) throws IOException {
        String methodName = "listFilesInFolder()";
        LOG.debug(TWO_PARAMS, methodName, LOG_CALLED);

        // Get the directory contents from the OS
        testNull(sftpClient);
        testNull(folder);
        testNull(sftpClient.ls(folder));

        List<RemoteResourceInfo> filesInFolder = new ArrayList<>(sftpClient.ls(folder));
        LOG.debug("No Of Files In Folder: {}", filesInFolder.size());

        // Validate whether to include directories, etc
        List<String> results = validation.validateFilesInFolder(filesInFolder);
        LOG.debug("No Of Files (excluding any folders): {}", results != null ? results.size() : 0);

        // User validation that can be overridden in the calling class
        results = validation.validateFilenames(results);
        LOG.debug("No Of Files (after filters applied): {}", results != null ? results.size() : 0);

        // Return the end list of files required
        return results;
    }


    /**
     * Separate method to get the file., due to PMD rules.
     * 
     * @param remoteFile The remote file
     * @return The file
     */
    private InputStream getFile(RemoteFile remoteFile) {
        return remoteFile.new RemoteFileInputStream(0);
    }

    /**
     * Get the contents of a file.
     * 
     * @param filename The filename
     * @param inputStream The input stream
     * @return The file contents
     */
    private String getFileContents(String filename, InputStream inputStream) {
        String fileContents = null;
        String methodName = "getFileContents(" + filename + ")";
        LOG.debug(TWO_PARAMS, methodName, LOG_CALLED);

        try (InputStreamReader fileReader = new InputStreamReader(inputStream)) {
            try (BufferedReader reader = new BufferedReader(fileReader)) {
                StringBuilder stringBuilder = new StringBuilder();
                String line = reader.readLine();
                while (line != null) {
                    stringBuilder.append(line);
                    line = reader.readLine();
                }
                fileContents = stringBuilder.toString();
                LOG.debug("File contents read");
            }
        } catch (IOException e) {
            LOG.error("Error reading: {}", filename);
            LOG.error("Stacktrace3:: {}", ExceptionUtils.getStackTrace(e));
        }
        return fileContents;
    }


    private void testNull(Object obj) throws IOException {
        if (obj == null) {
            LOG.error("{} is null", obj);
            throw new IOException("Folder is null");
        }
    }

}