package uk.gov.hmcts.datagenerator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("PMD")
public class PdIwpXmlUpdaterTest {

    private static final String SAMPLE_XML = """
        <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
        <currentcourtstatus xmlns="http://www.courtstatus.test/schema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
            <court>
                <courtsites>
                    <courtsite>
                        <courtrooms>
                            <courtroom>
                                <cases>
                                    <caseDetails>
                                        <currentstatus>
                                            <event>
                                                <date>01/01/24</date>
                                                <time>10:00</time>
                                            </event>
                                            <event>
                                                <date>01/01/24</date>
                                                <time>11:00</time>
                                            </event>
                                        </currentstatus>
                                    </caseDetails>
                                </cases>
                            </courtroom>
                        </courtrooms>
                    </courtsite>
                </courtsites>
            </court>
            <datetimestamp>
                <dayofweek></dayofweek>
                <date></date>
                <month></month>
                <year></year>
                <hour></hour>
                <min></min>
            </datetimestamp>
        </currentcourtstatus>
        """;

    @Test
    void testMainPd_withValidXml_updatesAllTags(@TempDir Path tempDir) throws Exception {
        Path inputXml = tempDir.resolve("PublicDisplay_457_test.xml");
        Files.writeString(inputXml, SAMPLE_XML);

        Path testOutputDir = tempDir.resolve("test_validXml");
        Files.createDirectories(testOutputDir);

        String[] args = {"PublicDisplay", inputXml.toString(), testOutputDir.toString(), "2025-12-25"};
        PdIwpXmlUpdater.main(args);

        File[] files = testOutputDir.toFile().listFiles((dir, name) -> name.startsWith("PublicDisplay_457_"));
        assertNotNull(files);
        assertEquals(1, files.length, "Expected only one output file in isolated folder");
    }
    
    @Test
    void testMainWp_withValidXml_updatesAllTags(@TempDir Path tempDir) throws Exception {
        Path inputXml = tempDir.resolve("WebPage_457_test.xml");
        Files.writeString(inputXml, SAMPLE_XML);

        Path testOutputDir = tempDir.resolve("test_validXml");
        Files.createDirectories(testOutputDir);

        String[] args = {"WebPage", inputXml.toString(), testOutputDir.toString(), "2025-12-25"};
        PdIwpXmlUpdater.main(args);

        File[] files = testOutputDir.toFile().listFiles((dir, name) -> name.startsWith("WebPage_457_"));
        assertNotNull(files);
        assertEquals(1, files.length, "Expected only one output file in isolated folder");
    }

    @Test
    void testMainPd_withInvalidDate_usesCurrentDate(@TempDir Path tempDir) throws Exception {
        Path inputXml = tempDir.resolve("PublicDisplay_457_test.xml");
        Files.writeString(inputXml, SAMPLE_XML);

        Path isolatedOutputDir = tempDir.resolve("test_invalid_date");
        Files.createDirectories(isolatedOutputDir);

        String[] args = {"PublicDisplay", inputXml.toString(), isolatedOutputDir.toString(), "invalid-date"};
        PdIwpXmlUpdater.main(args);

        File[] files = isolatedOutputDir.toFile()
            .listFiles((dir, name) -> name.startsWith("PublicDisplay_457_"));
        assertNotNull(files);
        assertEquals(1, files.length);
        String content = Files.readString(files[0].toPath());
        assertTrue(content.contains("<dayofweek>"), "Expected fallback date to be applied");
    }
    
    @Test
    void testMainWp_withInvalidDate_usesCurrentDate(@TempDir Path tempDir) throws Exception {
        Path inputXml = tempDir.resolve("WebPage_457_test.xml");
        Files.writeString(inputXml, SAMPLE_XML);

        Path isolatedOutputDir = tempDir.resolve("test_invalid_date");
        Files.createDirectories(isolatedOutputDir);

        String[] args = {"WebPage", inputXml.toString(), isolatedOutputDir.toString(), "invalid-date"};
        PdIwpXmlUpdater.main(args);

        File[] files = isolatedOutputDir.toFile()
            .listFiles((dir, name) -> name.startsWith("WebPage_457_"));
        assertNotNull(files);
        assertEquals(1, files.length);
        String content = Files.readString(files[0].toPath());
        assertTrue(content.contains("<dayofweek>"), "Expected fallback date to be applied");
    }


    @Test
    void testMainPd_withNoCourtCodeInFilename_usesDefault457(@TempDir Path tempDir) throws Exception {
        Path inputXml = tempDir.resolve("AnotherName.xml");
        Files.writeString(inputXml, SAMPLE_XML);

        String[] args = {"PublicDisplay", inputXml.toString(), tempDir.toString()};
        PdIwpXmlUpdater.main(args);

        File[] files =
            tempDir.toFile().listFiles((dir, name) -> name.startsWith("PublicDisplay_457_"));
        assertNotNull(files);
        assertEquals(1, files.length, "Expected fallback court code '457'");
    }
    
    @Test
    void testMainWp_withNoCourtCodeInFilename_usesDefault457(@TempDir Path tempDir) throws Exception {
        Path inputXml = tempDir.resolve("AnotherName.xml");
        Files.writeString(inputXml, SAMPLE_XML);

        String[] args = {"WebPage", inputXml.toString(), tempDir.toString()};
        PdIwpXmlUpdater.main(args);

        File[] files =
            tempDir.toFile().listFiles((dir, name) -> name.startsWith("WebPage_457_"));
        assertNotNull(files);
        assertEquals(1, files.length, "Expected fallback court code '457'");
    }

    @Test
    void testMainPd_withFilenameMatchingPattern_extractsCourtCode(@TempDir Path tempDir)
        throws Exception {

        // Arrange: input file with matching pattern
        Path inputXml = tempDir.resolve("PublicDisplay_499_customname.xml");
        Files.writeString(inputXml, SAMPLE_XML);

        // Isolated output directory
        Path isolatedOutputDir = tempDir.resolve("test_extract_court_code");
        Files.createDirectories(isolatedOutputDir);

        String[] args = {"PublicDisplay", inputXml.toString(), isolatedOutputDir.toString()};

        // Act
        PdIwpXmlUpdater.main(args);

        // Assert: check that only one file with expected court code prefix exists
        File[] files = isolatedOutputDir.toFile()
            .listFiles((dir, name) -> name.startsWith("PublicDisplay_499_"));
        assertNotNull(files, "Expected files to be created");
        assertEquals(1, files.length, "Expected court code '499' to be extracted from filename");
    }
    
    @Test
    void testMainWp_withFilenameMatchingPattern_extractsCourtCode(@TempDir Path tempDir)
        throws Exception {

        // Arrange: input file with matching pattern
        Path inputXml = tempDir.resolve("WebPage_499_customname.xml");
        Files.writeString(inputXml, SAMPLE_XML);

        // Isolated output directory
        Path isolatedOutputDir = tempDir.resolve("test_extract_court_code");
        Files.createDirectories(isolatedOutputDir);

        String[] args = {"WebPage", inputXml.toString(), isolatedOutputDir.toString()};

        // Act
        PdIwpXmlUpdater.main(args);

        // Assert: check that only one file with expected court code prefix exists
        File[] files = isolatedOutputDir.toFile()
            .listFiles((dir, name) -> name.startsWith("WebPage_499_"));
        assertNotNull(files, "Expected files to be created");
        assertEquals(1, files.length, "Expected court code '499' to be extracted from filename");
    }


    @Test
    void testMainPd_generatesTimestampedOutput(@TempDir Path tempDir) throws Exception {
        Path inputXml = tempDir.resolve("PublicDisplay_457_test.xml");
        Files.writeString(inputXml, SAMPLE_XML);

        String[] args = {"PublicDisplay", inputXml.toString(), tempDir.toString()};
        PdIwpXmlUpdater.main(args);

        File[] files = tempDir.toFile()
            .listFiles((dir, name) -> name.matches("PublicDisplay_457_\\d{14}\\.xml"));
        assertNotNull(files);
        assertEquals(1, files.length, "Output filename should include a 14-digit timestamp");
    }
    
    @Test
    void testMainWp_generatesTimestampedOutput(@TempDir Path tempDir) throws Exception {
        Path inputXml = tempDir.resolve("WebPage_457_test.xml");
        Files.writeString(inputXml, SAMPLE_XML);

        String[] args = {"WebPage", inputXml.toString(), tempDir.toString()};
        PdIwpXmlUpdater.main(args);

        File[] files = tempDir.toFile()
            .listFiles((dir, name) -> name.matches("WebPage_457_\\d{14}\\.xml"));
        assertNotNull(files);
        assertEquals(1, files.length, "Output filename should include a 14-digit timestamp");
    }
}

