package uk.gov.hmcts.pdda.business.services.pdda;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
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
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet.InitializationService;

/**
 * <p>
 * Title: OAuth2HelperTest Test.
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
class OAuth2HelperTest {

    private static final String NOTNULL = "Result is null";
    private static final String TRUE = "Result is false";
    private static final String DUMMY_TENANTID = "tenantIdValue";
    private static final String DUMMY_CLIENTID = "clientIdValue";
    private static final String DUMMY_CLIENTSECRET = "clientSecretValue";
    private static final String DUMMY_TOKENURL = "https://dummy.com/%s/oauth2/token";
    private static final String VALID_RESPONSE = "{\"access_token\":\"ThisIsTheToken\"}";
    private static final String INVALID_RESPONSE = "{\"access_token\":?}";

    @Mock
    private Environment mockEnvironment;

    @Mock
    private Builder mockBuilder;

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpRequest mockHttpRequest;

    @InjectMocks
    private OAuth2Helper classUnderTest;

    @BeforeEach
    public void setUp() {
        Mockito.mockStatic(HttpRequest.class);
        Mockito.mockStatic(HttpClient.class);

        classUnderTest = new OAuth2Helper(mockEnvironment);

        ReflectionTestUtils.setField(classUnderTest, "tenantId", DUMMY_TENANTID);
        ReflectionTestUtils.setField(classUnderTest, "clientId", DUMMY_CLIENTID);
        ReflectionTestUtils.setField(classUnderTest, "clientSecret", DUMMY_CLIENTSECRET);
        ReflectionTestUtils.setField(classUnderTest, "tokenUrl", DUMMY_TOKENURL);
    }

    @AfterEach
    public void tearDown() {
        // Clear down statics
        Mockito.clearAllCaches();
    }

    @Test
    void testDefaultConstructor() {
        Mockito.mockStatic(InitializationService.class);
        InitializationService mockInitializationService = Mockito.mock(InitializationService.class);
        Mockito.when(InitializationService.getInstance()).thenReturn(mockInitializationService);
        Mockito.when(mockInitializationService.getEnvironment()).thenReturn(mockEnvironment);
        boolean result = false;
        try {
            new OAuth2Helper();
            result = true;
        } catch (Exception exception) {
            fail(exception.getMessage());
        }
        assertTrue(result, TRUE);
    }

}