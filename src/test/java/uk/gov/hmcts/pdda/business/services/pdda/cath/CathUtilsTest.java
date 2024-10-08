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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.http.HttpRequest;
import java.time.LocalDateTime;
import java.util.Locale;
import javax.xml.transform.TransformerException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
@SuppressWarnings({"PMD.LawOfDemeter", "PMD.AvoidFileStream", "PMD.AssignmentInOperand"})
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
        JSONObject result =
            CathUtils.generateJsonFromString(fetchAndReadFile("ExampleDailyList_V3.xml"));
        // Indentation value 4 matches the indentation of the xml before json conversion
        String jsonAsString = result.toString(4);
        assertNotNull(jsonAsString, NOTNULL);
    }

    @Test
    void testGenerateWarnedListJsonFromString() throws IOException {
        JSONObject result =
            CathUtils.generateJsonFromString(fetchAndReadFile("ExampleWarnedList_V1.xml"));
        // Indentation value 4 matches the indentation of the xml before json conversion
        String jsonAsString = result.toString(4);
        assertNotNull(jsonAsString, NOTNULL);
    }
    
    @Test
    void testGenerateFirmListJsonFromString() throws IOException {
        JSONObject result =
            CathUtils.generateJsonFromString(fetchAndReadFile("ExampleFirmList_V1.xml"));
        // Indentation value 4 matches the indentation of the xml before json conversion
        String jsonAsString = result.toString(4);
        assertNotNull(jsonAsString, NOTNULL);
    }

    @Test
    void testTransformXmlUsingTemplate() throws TransformerException {
        // Using the example xml and xsl files defined in resources
        String inputXml = "database/test-data/example_list_xml_docs/DailyList_999_200108141220.xml";
        String inputXslt = "xslt_schemas/Example.xslt";
        // Output the xml result as string
        String stringResult = CathUtils.transformXmlUsingTemplate(inputXml, inputXslt);
        // Verify
        assertNotNull(stringResult, NOTNULL);
    }

    private String fetchAndReadFile(String fileName) throws FileNotFoundException {
        // Fetch File
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader
            .getResource("database/test-data/example_list_xml_docs/" + fileName).getFile());
        InputStream inputStream = new FileInputStream(file);
        // Read File
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append('\n');
            }
        } catch (IOException e) {
            LOG.debug("Failed to read file: {}", e);
        }
        return resultStringBuilder.toString();
    }
}
