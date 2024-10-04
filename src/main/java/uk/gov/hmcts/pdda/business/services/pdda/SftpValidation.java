package uk.gov.hmcts.pdda.business.services.pdda;

import com.jcraft.jsch.ChannelSftp.LsEntry;
import net.schmizz.sshj.sftp.RemoteResourceInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * SftpValidation.
 **/
@SuppressWarnings("PMD.LawOfDemeter")
public class SftpValidation {
    
    protected static final String EMPTY_STRING = "";
    
    private final boolean includeDirs;

    public SftpValidation(boolean includeDirs) {
        this.includeDirs = includeDirs;
    }

    /* Filter the directories (if required) */
    public List<String> validateFilesInFolder(List filesInFolder) {
        List<String> results = new ArrayList<>();

        if (filesInFolder != null) {
            for (Object obj : filesInFolder) {
                if (obj instanceof RemoteResourceInfo) {
                    RemoteResourceInfo remoteResourceInfo = (RemoteResourceInfo) obj;
                    String filename = remoteResourceInfo.getName();
                    results.add(filename);
                } else {
                    String filename = getFilename(obj);
                    if (filename != null) {
                        results.add(filename);
                    }
                }
            }
        }

        return results;
    }

    public String getFilename(Object obj) {
        if (obj instanceof LsEntry) {
            LsEntry lsEntry = (LsEntry) obj;
            if (!lsEntry.getAttrs().isDir() || includeDirs) {
                return lsEntry.getFilename();
            }
        }
        return null;
    }

    /* Apply user defined string patterns (overridden in the calling class) */
    protected String validateFilename(String filename) {
        if (EMPTY_STRING.equals(filename)) {
            return "Filename is Empty";
        }
        return null;
    }

    /* Apply user defined string patterns (overridden in the calling class) */
    public List<String> validateFilenames(List<String> filenames) {
        return filenames;
    }
}
