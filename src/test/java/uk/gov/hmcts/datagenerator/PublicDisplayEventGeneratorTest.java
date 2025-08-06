package uk.gov.hmcts.datagenerator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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

    @Test
    void testMain_withDefaultArgs_createsOutput(@TempDir Path tempDir) {
        String[] args = {tempDir.toString()};

        PublicDisplayEventGenerator.main(args);

        File[] files = tempDir.toFile().listFiles((dir, name) -> name.startsWith("PDDA_XPD_"));
        assertNotNull(files);
        assertEquals(10, files.length); // default count is 10
    }

    @Test
    void testMain_invalidOutputFolder_failsGracefully() {
        String[] args = {"/invalid-path/<>", "2"};

        assertDoesNotThrow(() -> PublicDisplayEventGenerator.main(args));
        // Error message printed, but app doesn’t crash
    }

    @Test
    void testMain_withZeroCount_createsNothing(@TempDir Path tempDir) {
        String[] args = {tempDir.toString(), "0"};

        PublicDisplayEventGenerator.main(args);
        File[] files = tempDir.toFile().listFiles((dir, name) -> name.startsWith("PDDA_XPD_"));
        assertNotNull(files);
        assertEquals(0, files.length);
    }
}
