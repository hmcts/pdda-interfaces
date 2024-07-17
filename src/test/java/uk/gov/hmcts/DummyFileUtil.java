package uk.gov.hmcts;

import uk.gov.hmcts.pdda.business.services.pdda.PddaHelper;
import uk.gov.hmcts.pdda.common.publicdisplay.events.PublicDisplayEvent;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DummyFileUtil {

    public static final String[] VALID_CP_MESSAGE_TYPE = {"DailyList", "WarnList"};
    private static final String FILE_CONTENTS = " file contents";
    
    private DummyFileUtil() {
        // Do nothing
    }
    
    public static List<String> getFilenames() {
        List<String> filenames = new ArrayList<>();
        filenames.add("Filename1.xml");
        filenames.add("Filename2.xml");
        return filenames;
    }

    public static Map<String, InputStream> getFiles(List<String> filenames) {
        Map<String, InputStream> result = new ConcurrentHashMap<>();
        for (String filename : filenames) {
            result.put(filename, DummyServicesUtil.getByteArrayInputStream(filename.getBytes()));
        }
        return result;
    }
    
    public static List<FileResults> getAllValidCpFiles(boolean isValid) {
        List<FileResults> result = new ArrayList<>();
        String nowAsString = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        FileResults fileResult;
        for (String messageType : VALID_CP_MESSAGE_TYPE) {
            fileResult = getFileResults();
            fileResult.filename = messageType + "_453_" + nowAsString + ".xml";
            fileResult.fileContents = messageType + FILE_CONTENTS;
            fileResult.isValid = isValid;
            String filenameToTest = VALID_CP_MESSAGE_TYPE[1];
            String filename = fileResult.filename;
            fileResult.alreadyProcessedTest = filename.startsWith(filenameToTest);
            result.add(fileResult);
        }
        return result;
    }
    
    public static List<FileResults> getAllValidXhibitFiles(boolean isValid) {
        List<FileResults> result = new ArrayList<>();
        String nowAsString = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        FileResults fileResult;
        for (String messageType : DummyEventUtil.VALID_XHIBIT_MESSAGE_TYPE) {
            PublicDisplayEvent event = DummyEventUtil.getEvent(messageType);
            fileResult = DummyFileUtil.getFileResults();
            fileResult.filename = messageType + "_111_" + nowAsString + ".xml";
            fileResult.fileContents = 
                PddaHelper.encodePublicEvent(PddaHelper.serializePublicEvent(event));
            fileResult.isValid = isValid;
            result.add(fileResult);
        }
        return result;
    }
    
    public static FileResults getFileResults() {
        return new FileResults();
    }
    
    public static class FileResults {
        public String filename;
        public String fileContents;
        public boolean alreadyProcessedTest;
        public boolean isValid;
    }
}
