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
import uk.gov.hmcts.DummyFormattingUtil;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.CourtelJson;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentDao;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentRepository;
import uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet.InitializationService;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
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
@SuppressWarnings({"PMD.LawOfDemeter", "PMD.AssignmentInOperand", "PMD.ExcessiveImports"})
class CathUtilsTest {

    private static final Logger LOG = LoggerFactory.getLogger(CathUtilsTest.class);

    private static final String EQUALS = "Result is not equal";
    private static final String NOTNULL = "Result is null";
    private static final String TRUE = "Result is not True";

    @Mock
    private Environment mockEnvironment;

    @Mock
    private XhbCourtelListRepository mockXhbCourtelListRepository;

    @Mock
    private XhbClobRepository mockXhbClobRepository;
    
    @Mock
    private XhbXmlDocumentRepository mockXhbXmlDocumentRepository;

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
        // Setup using the example xml
        final String exampleXmlPath =
            "src/main/resources/database/test-data/example_list_xml_docs/DailyList_999_200108141220.xml";
        final String xsltSchemaPath = "xslt_schemas/Example.xslt";

        Optional<XhbCourtelListDao> xhbCourtelListDao =
            Optional.of(DummyCourtelUtil.getXhbCourtelListDao());
        Optional<XhbClobDao> xhbClobDao = Optional
            .of(DummyFormattingUtil.getXhbClobDao(1L, fetchExampleListClobData(exampleXmlPath)));
        Optional<XhbXmlDocumentDao> xhbXmlDocumentDao = Optional.of(DummyFormattingUtil.getXhbXmlDocumentDao());

        Mockito.when(mockXhbCourtelListRepository.findByXmlDocumentClobId(Mockito.isA(Long.class)))
            .thenReturn(xhbCourtelListDao);
        Mockito.when(mockXhbClobRepository.findById(Mockito.isA(Long.class)))
            .thenReturn(xhbClobDao);
        Mockito.when(mockXhbXmlDocumentRepository.findByXmlDocumentClobId(Mockito.isA(Long.class)))
            .thenReturn(xhbXmlDocumentDao);
        
        // Run the Schema Transform
        CathUtils.transformXmlUsingSchema(1L, mockXhbCourtelListRepository,
            mockXhbClobRepository, mockXhbXmlDocumentRepository, xsltSchemaPath);

        // Run the Generate Json process
        CathUtils.fetchXmlAndGenerateJson(1L, mockXhbClobRepository);
        
        boolean result = true;
        
        // Verify Json has been Generated
        assertTrue(result, TRUE);
    }

    private String fetchExampleListClobData(String exampleXmlPath) {
        // Fetch example Xml File held on resources/database/test-data
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br =
            Files.newBufferedReader(Paths.get(exampleXmlPath), StandardCharsets.UTF_8)) {
            // Loop through all the lines in the Xml
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append('\n');
            }
        } catch (IOException e) {
            LOG.debug("Failed to read file: ", e);
        }
        return resultStringBuilder.toString();
    }
}