package uk.gov.hmcts.datagenerator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("PMD")
class DailyListXmlUpdaterTest {

    @Test
    void testMain_withValidXml_updatesAndSavesFile(@TempDir Path tempDir) throws Exception {
        // Arrange
        Path inputXml = tempDir.resolve("input.xml");
        Files.writeString(inputXml, """
            <CourtList>
              <DocumentName></DocumentName>
              <TimeStamp></TimeStamp>
              <StartDate></StartDate>
              <EndDate></EndDate>
              <PublishedTime></PublishedTime>
              <HearingDate></HearingDate>
              <CourtHouseCode>XYZ</CourtHouseCode>
            </CourtList>
            """);

        String[] args = {inputXml.toString(), tempDir.toString(), "2025-05-08", "CPP"};

        // Act
        DailyListXmlUpdater.main(args);

        // Assert
        File[] files = tempDir.toFile().listFiles((dir, name) -> name.startsWith("DailyList_"));
        assertNotNull(files);
        assertTrue(files.length > 0, "Expected output file to be created");
        String content = Files.readString(files[0].toPath());
        assertTrue(content.contains("Daily List FINAL v1"));
        assertTrue(content.contains("2025-05-08"));
    }

    @Test
    void testMain_withInvalidDate_usesToday(@TempDir Path tempDir) throws Exception {
        Path inputXml = tempDir.resolve("input.xml");
        Files.writeString(inputXml, """
            <CourtList>
              <DocumentName></DocumentName>
              <TimeStamp></TimeStamp>
              <StartDate></StartDate>
              <EndDate></EndDate>
              <PublishedTime></PublishedTime>
              <HearingDate></HearingDate>
              <CourtHouseCode>XYZ</CourtHouseCode>
            </CourtList>
            """);

        String[] args = {inputXml.toString(), tempDir.toString(), "INVALID_DATE", "CPP"};

        DailyListXmlUpdater.main(args);

        File[] files = tempDir.toFile().listFiles((dir, name) -> name.startsWith("DailyList_"));
        assertNotNull(files);
        assertTrue(files.length > 0);
    }

    @Test
    void testMain_withXhibitMode_generatesExpectedFilename(@TempDir Path tempDir) throws Exception {
        Path inputXml = tempDir.resolve("input.xml");
        Files.writeString(inputXml, """
            <CourtList>
              <DocumentName></DocumentName>
              <TimeStamp></TimeStamp>
              <StartDate></StartDate>
              <EndDate></EndDate>
              <PublishedTime></PublishedTime>
              <HearingDate></HearingDate>
              <CourtHouseCode>XYZ</CourtHouseCode>
            </CourtList>
            """);

        String[] args = {inputXml.toString(), tempDir.toString(), "2025-05-08", "XHIBIT"};

        DailyListXmlUpdater.main(args);

        File[] files = tempDir.toFile().listFiles((dir, name) -> name.startsWith("PDDA_XDL_"));
        assertNotNull(files);
        assertTrue(files.length > 0);
    }

    @Test
    void testMain_withMissingArgs_usesDefaults(@TempDir Path tempDir) throws Exception {
        File defaultInput = new File("DailyList.xml");
        if (!defaultInput.exists()) {
            // This test is only valid if DailyList.xml exists in the working directory
            return;
        }

        String[] args = {}; // empty args, defaults used
        DailyListXmlUpdater.main(args);
        // Not asserting as output folder is hardcoded; this ensures no crash
    }
}

