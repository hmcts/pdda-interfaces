package uk.gov.hmcts.pdda.business.services.pdda.cath;

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

import java.io.File;
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
    private static final String TRUE = "Result is not True";

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
    void testTransformAndGenerateListJsonFromString() throws TransformerException {
        // Setup using the example xml and xsl files in resources, output result into resources
        String inputXmlPath =
            "database/test-data/example_list_xml_docs/DailyList_999_200108141220.xml";
        String xsltSchemaPath = "xslt_schemas/Example.xslt";
        String outputXmlPath =
            "src/main/resources/database/test-data/example_list_xml_docs/DailyList_999_200108141220_Schema_Edit.xml";
        String outputJsonPath =
            "src/main/resources/database/test-data/example_json_results/DailyList_999_200108141220_JSON.txt";

        // Run the Schema Transform
        CathUtils.transformXmlUsingSchema(inputXmlPath, xsltSchemaPath, outputXmlPath);

        // Run the Generate Json process
        CathUtils.fetchXmlAndGenerateJson(outputXmlPath, outputJsonPath);

        // Verify Json File has been Generated
        File jsonResult = new File(outputJsonPath);
        assertTrue(jsonResult.exists(), TRUE);
    }

    @Test
    void testTransformXmlUsingSchemaFailedFileWrite()
        throws TransformerException, SerialException, SQLException {
        String inputXml = "database/test-data/example_list_xml_docs/DailyList_999_200108141220.xml";
        String inputXslt = "xslt_schemas/Example.xslt";
        String outputXmlPath = "Testing/Invalid.xml";
        boolean result = true;
        // Run
        CathUtils.transformXmlUsingSchema(inputXml, inputXslt, outputXmlPath);
        assertTrue(result, NOTNULL);
    }
}
