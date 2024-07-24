package uk.gov.hmcts;

import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.hmcts.pdda.business.services.pdda.PddaSerializationUtils;

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
    public static final String SERIALIZED_HEARINGSTATUSEVENT =
        "rO0ABXNyAEl1ay5nb3YuY291cnRzZXJ2aWNlLnhoaWJpdC5jb21tb24ucHVibGl"
            + "jZGlzcGxheS5ldmVudHMuSGVhcmluZ1N0YXR1c0V2ZW500UDp/kCipvgCAAB"
            + "4cgBJdWsuZ292LmNvdXJ0c2VydmljZS54aGliaXQuY29tbW9uLnB1YmxpY2R"
            + "pc3BsYXkuZXZlbnRzLkNhc2VDb3VydFJvb21FdmVudAlMRnIzeCUnAgABTAAV"
            + "Y2FzZUNoYW5nZUluZm9ybWF0aW9udABUTHVrL2dvdi9jb3VydHNlcnZpY2Uve"
            + "GhpYml0L2NvbW1vbi9wdWJsaWNkaXNwbGF5L2V2ZW50cy90eXBlcy9DYXNlQ2"
            + "hhbmdlSW5mb3JtYXRpb247eHIARXVrLmdvdi5jb3VydHNlcnZpY2UueGhpYml"
            + "0LmNvbW1vbi5wdWJsaWNkaXNwbGF5LmV2ZW50cy5Db3VydFJvb21FdmVudHOa"
            + "cr0CVH1NAgABTAATY291cnRSb29tSWRlbnRpZmllcnQAUkx1ay9nb3YvY291c"
            + "nRzZXJ2aWNlL3hoaWJpdC9jb21tb24vcHVibGljZGlzcGxheS9ldmVudHMvdH"
            + "lwZXMvQ291cnRSb29tSWRlbnRpZmllcjt4cHNyAFB1ay5nb3YuY291cnRzZXJ"
            + "2aWNlLnhoaWJpdC5jb21tb24ucHVibGljZGlzcGxheS5ldmVudHMudHlwZXMu"
            + "Q291cnRSb29tSWRlbnRpZmllcpbQ5TLRSKc5AgACTAAHY291cnRJZHQAE0xqY"
            + "XZhL2xhbmcvSW50ZWdlcjtMAAtjb3VydFJvb21JZHEAfgAHeHBzcgARamF2YS"
            + "5sYW5nLkludGVnZXIS4qCk94GHOAIAAUkABXZhbHVleHIAEGphdmEubGFuZy5"
            + "OdW1iZXKGrJUdC5TgiwIAAHhwAAAAX3NxAH4ACQAAH9xzcgBSdWsuZ292LmNv"
            + "dXJ0c2VydmljZS54aGliaXQuY29tbW9uLnB1YmxpY2Rpc3BsYXkuZXZlbnRzL"
            + "nR5cGVzLkNhc2VDaGFuZ2VJbmZvcm1hdGlvbvCPqqw6fCKnAgABWgAKY2FzZU" 
            + "FjdGl2ZXhwAQ==";

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
        String nowAsString =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
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
        String nowAsString =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        FileResults fileResult;
        for (String messageType : DummyEventUtil.VALID_XHIBIT_MESSAGE_TYPE) {
            PublicDisplayEvent event = DummyEventUtil.getEvent(messageType);
            fileResult = DummyFileUtil.getFileResults();
            fileResult.filename = messageType + "_111_" + nowAsString + ".xml";
            // if (event instanceof HearingStatusEvent) {
            // fileResult.fileContents = SERIALIZED_HEARINGSTATUSEVENT;
            // } else {
            fileResult.fileContents = PddaSerializationUtils
                .encodePublicEvent(PddaSerializationUtils.serializePublicEvent(event));
            // }
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
