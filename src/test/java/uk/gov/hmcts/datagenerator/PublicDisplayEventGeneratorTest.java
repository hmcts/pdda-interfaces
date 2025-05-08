package uk.gov.hmcts.datagenerator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("PMD")
class PublicDisplayEventGeneratorTest {

    @Test
    void testMain_generatesSerializedFiles(@TempDir Path tempDir) {
        // Arrange
        String[] args = {tempDir.toString(), "5", // 5 events
            "80", "457", "8107"};

        // Act
        PublicDisplayEventGenerator.main(args);

        // Assert
        File[] files = tempDir.toFile().listFiles((dir, name) -> name.startsWith("PDDA_XPD_"));
        assertNotNull(files);
        assertEquals(5, files.length, "Expected 5 serialized files");
        for (File file : files) {
            assertTrue(file.length() > 0, "File should not be empty: " + file.getName());
        }
    }
}
