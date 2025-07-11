package uk.gov.hmcts.pdda.business.services.pdda;

import net.schmizz.sshj.sftp.RemoteResourceInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * SftpValidation.
 **/
public class SftpValidation {
    
    protected static final String EMPTY_STRING = "";
    protected final boolean includeDirs;

    public SftpValidation(boolean includeDirs) {
        this.includeDirs = includeDirs;
    }

    /* Filter the directories (if required) */
    public List<String> validateFilesInFolder(List filesInFolder) {
        List<String> results = new ArrayList<>();

        if (filesInFolder != null) {
            for (Object obj : filesInFolder) {
                if (obj instanceof RemoteResourceInfo remoteResourceInfo) {
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
        return null;
    }

    /* Apply user defined string patterns (overridden in the calling class) */
    public String validateFilename(String filename) {
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
