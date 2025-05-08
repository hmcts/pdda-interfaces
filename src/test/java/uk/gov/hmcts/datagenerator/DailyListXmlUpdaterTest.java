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
}

