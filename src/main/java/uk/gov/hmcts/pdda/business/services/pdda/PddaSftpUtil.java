package uk.gov.hmcts.pdda.business.services.pdda;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * PddaSftpUtil.
 **/
public final class PddaSftpUtil {

    private static final Logger LOG = LoggerFactory.getLogger(PddaSftpUtil.class);

    private static final String SFTP = "sftp";
    private static final String SLASH_WINDOWS = "\\";
    private static final String SLASH_LINUX = "/";

    protected PddaSftpUtil() {
        throw new IllegalStateException("Utility class");
    }
    
    public static ChannelSftp createChannel(Session session) throws JSchException {
        ChannelSftp sftpChannel = (ChannelSftp) session.openChannel(SFTP);
        sftpChannel.connect();
        return sftpChannel;
    }

    public static void validateFolder(ChannelSftp sftpChannel, String folder) throws JSchException {
        try {
            LOG.debug("validateFolder({},{})", sftpChannel, folder);
            sftpChannel.stat(folder);
            if (!folder.endsWith(SLASH_WINDOWS) && !folder.endsWith(SLASH_LINUX)) {
                throw new JSchException("Invalid Folder (Missing end slash): " + folder);
            }
        } catch (SftpException e) {
            throw new JSchException("Invalid Folder: " + folder, e);
        }
    }

    public static void transferFiles(ChannelSftp sftpChannel, String remoteFolder, Map<String, InputStream> files)
        throws SftpException {
        LOG.debug("transferFiles({},{},{})", sftpChannel, remoteFolder, files);
        for (Map.Entry<String, InputStream> entry : files.entrySet()) {
            String filename = entry.getKey();
            try (InputStream file = entry.getValue()) {
                transferFile(sftpChannel, remoteFolder, filename, file);
            } catch (IOException e) {
                LOG.error("Error reading: {}", filename);
            }
        }
    }

    private static void transferFile(ChannelSftp sftpChannel, String remoteFolder, String filename, InputStream file)
        throws SftpException {
        LOG.debug("transferFile({})", filename);
        sftpChannel.put(file, remoteFolder + filename);
    }

    public static void disconnectSession(Session session) {
        LOG.debug("disconnectSession()");
        session.disconnect();
    }
}
