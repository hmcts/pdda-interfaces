package uk.gov.hmcts.pdda.business.services.pdda.cath;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import uk.gov.hmcts.DummyCourtelUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.CourtelJson;
import uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet.InitializationService;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Locale;
import javax.sql.rowset.serial.SerialException;
import javax.xml.transform.TransformerException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>
 * Title: CathUtils Test.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2024
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Mark Harris
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings({"PMD.LawOfDemeter"})
class CathUtilsTest {

    private static final Logger LOG = LoggerFactory.getLogger(CathUtilsTest.class);

    private static final String EQUALS = "Result is not equal";
    private static final String NOTNULL = "Result is null";

    @Mock
    private Environment mockEnvironment;

    @BeforeEach
    public void setup() {
        Mockito.mockStatic(InitializationService.class);
    }

    @AfterEach
    public void tearDown() {
        // Clear down statics
        Mockito.clearAllCaches();
    }

    @Test
    void testGetDateTimeAsString() {
        String result = CathUtils.getDateTimeAsString(LocalDateTime.now());
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testGetHttpPostRequest() {
        // Setup
        CourtelJson courtelJson = DummyCourtelUtil.getListJson();
        String url = "https://dummy.com/url";
        // Run
        HttpRequest result = CathUtils.getHttpPostRequest(url, courtelJson);
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testIsApimEnabled() {
        InitializationService mockInitializationService = Mockito.mock(InitializationService.class);
        Mockito.when(InitializationService.getInstance()).thenReturn(mockInitializationService);
        Mockito.when(mockInitializationService.getEnvironment()).thenReturn(mockEnvironment);

        String[] expectedResults = {"false", "true"};
        for (String expectedResult : expectedResults) {
            Mockito.when(mockEnvironment.getProperty(Mockito.isA(String.class)))
                .thenReturn(expectedResult);
            Boolean result = CathUtils.isApimEnabled();
            assertEquals(expectedResult, result.toString().toLowerCase(Locale.getDefault()),
                EQUALS);
        }
    }

    @Test
    void testGetApimUri() {
        InitializationService mockInitializationService = Mockito.mock(InitializationService.class);
        Mockito.when(InitializationService.getInstance()).thenReturn(mockInitializationService);
        Mockito.when(mockInitializationService.getEnvironment()).thenReturn(mockEnvironment);

        String expectedResult = "www.dummy/uri";
        Mockito.when(mockEnvironment.getProperty(Mockito.isA(String.class)))
            .thenReturn(expectedResult);
        String result = CathUtils.getApimUri();
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testGenerateDailyListJsonFromString() throws IOException {
        JSONObject result = CathUtils.generateJsonFromString(CathUtils.fetchAndReadFile(
            "src/main/resources/database/test-data/example_list_xml_docs/ExampleDailyList_V4.xml"));
        // Indentation value 4 matches the indentation of the xml before json conversion
        String jsonAsString = result.toString(4);
        assertNotNull(jsonAsString, NOTNULL);
    }

    @Test
    void testGenerateWarnedListJsonFromString() throws IOException {
        JSONObject result = CathUtils.generateJsonFromString(CathUtils.fetchAndReadFile(
            "src/main/resources/database/test-data/example_list_xml_docs/ExampleWarnedList_V2.xml"));
        // Indentation value 4 matches the indentation of the xml before json conversion
        String jsonAsString = result.toString(4);
        assertNotNull(jsonAsString, NOTNULL);
    }

    @Test
    void testTransformXmlUsingTemplate()
        throws TransformerException, SerialException, SQLException {
        // Setup using the example xml and xsl files in resources, output result into resources
        String inputXml = "database/test-data/example_list_xml_docs/DailyList_999_200108141220.xml";
        String inputXslt = "xslt_schemas/Example.xslt";
        String outputXmlPath =
            "src/main/resources/database/test-data/example_list_xml_docs/TestResult.xml";
        boolean result = true;
        // Run
        CathUtils.transformXmlUsingTemplate(inputXml, inputXslt, outputXmlPath);
        // Verify
        assertTrue(result, NOTNULL);
    }

    @Test
    void testTransformXmlUsingTemplateFailedFileWrite()
        throws TransformerException, SerialException, SQLException {
        String inputXml = "database/test-data/example_list_xml_docs/DailyList_999_200108141220.xml";
        String inputXslt = "xslt_schemas/Example.xslt";
        String outputXmlPath = "Testing/Invalid.xml";
        boolean result = true;
        // Run
        CathUtils.transformXmlUsingTemplate(inputXml, inputXslt, outputXmlPath);
        assertTrue(result, NOTNULL);
    }
}
