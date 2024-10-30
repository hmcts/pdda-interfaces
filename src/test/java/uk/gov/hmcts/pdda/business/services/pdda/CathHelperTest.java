package uk.gov.hmcts.pdda.business.services.pdda;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyCourtelUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.CourtelJson;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListDao;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentRepository;
import uk.gov.hmcts.pdda.business.services.pdda.cath.CathUtils;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>
 * Title: CathHelperTest Test.
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
class CathHelperTest {

    private static final String EMPTY_STRING = "";
    private static final String EQUALS = "Result is not equal";
    private static final String NOTNULL = "Result is null";
    private static final String TRUE = "Result is False";

    @Mock
    private BlobHelper mockBlobHelper;

    @Mock
    private OAuth2Helper mockOAuth2Helper;
    
    @Mock
    private EntityManager mockEntityManager;
    
    @Mock
    private XhbXmlDocumentRepository mockXhbXmlDocumentRepository;

    @Mock
    private HttpRequest mockHttpRequest;

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockHttpResponse;

    @InjectMocks
    private CathHelper classUnderTest;

    @BeforeEach
    public void setUp() {
        Mockito.mockStatic(CathUtils.class);
        Mockito.mockStatic(HttpClient.class);

        classUnderTest = new CathHelper(mockOAuth2Helper, mockEntityManager, mockXhbXmlDocumentRepository);
    }

    @AfterEach
    public void tearDown() {
        // Test default constructor
        classUnderTest = new CathHelper(mockEntityManager, mockXhbXmlDocumentRepository);
        // Clear down statics
        Mockito.clearAllCaches();
    }

    @Test
    void testGenerateJsonString() {
        // Setup
        XhbCourtelListDao xhbCourtelListDao = DummyCourtelUtil.getXhbCourtelListDao();
        CourtelJson courtelJson = DummyCourtelUtil.getListJson();
        // Run
        String result = classUnderTest.generateJsonString(xhbCourtelListDao, courtelJson);
        assertNotNull(result, NOTNULL);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testSend() {
        boolean result = false;
        try {
            // Setup
            CourtelJson json = DummyCourtelUtil.getListJson();
            String uri = "uri";
            // Expects
            Mockito.when(mockOAuth2Helper.getAccessToken()).thenReturn("accessToken");
            Mockito.when(CathUtils.isApimEnabled()).thenReturn(true);
            Mockito.when(CathUtils.getApimUri()).thenReturn(uri);
            Mockito.when(CathUtils.getHttpPostRequest(uri, json)).thenReturn(mockHttpRequest);

            // Expects - HttpClient.newHttpClient()
            Mockito.when(HttpClient.newHttpClient()).thenReturn(mockHttpClient);
            Mockito.when(mockHttpClient.send(Mockito.isA(HttpRequest.class),
                Mockito.isA(BodyHandlers.ofString().getClass()))).thenReturn(mockHttpResponse);
            Mockito.when(mockHttpResponse.statusCode()).thenReturn(200);
            Mockito.when(mockHttpResponse.body()).thenReturn("Body");

            // Run
            classUnderTest.send(json);
            result = true;
        } catch (Exception exception) {
            fail(exception.getMessage());
        }
        // Checks
        assertTrue(result, TRUE);
    }

    @Test
    void testGetTokenEmpty() {
        Mockito.when(CathUtils.isApimEnabled()).thenReturn(false);
        // Run
        String result = classUnderTest.getToken();
        // Checks
        assertEquals(EMPTY_STRING, result, EQUALS);
    }
}
