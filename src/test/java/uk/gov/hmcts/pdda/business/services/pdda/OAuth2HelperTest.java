package uk.gov.hmcts.pdda.business.services.pdda;

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
import uk.gov.hmcts.DummyCourtelUtil;
import uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet.InitializationService;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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

        classUnderTest = new OAuth2Helper(mockEnvironment) {
            @Override
            protected String getTenantId() {
                return DUMMY_TENANTID;
            }
            
            @Override
            protected String getTokenUrl() {
                return DUMMY_CLIENTSECRET;
            }
            
            @Override
            protected String getClientId() {
                return DUMMY_CLIENTID;
            }
            
            @Override
            protected String getClientSecret() {
                return DUMMY_CLIENTSECRET;
            }
        };
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

    @Test
    void testGetAccessTokenValid() {
        String result = testGetAccessToken(DummyCourtelUtil.getHttpResponse(200, VALID_RESPONSE));
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testGetAccessTokenInvalid() {
        String result = testGetAccessToken(DummyCourtelUtil.getHttpResponse(404, INVALID_RESPONSE));
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testGetAccessTokenNull() {
        String result = testGetAccessToken(DummyCourtelUtil.getHttpResponse(404, null));
        assertNotNull(result, NOTNULL);
    }

    private String testGetAccessToken(HttpResponse<String> response) {
        try {
            // Expects - getAuthenticationRequest
            Mockito.when(HttpRequest.newBuilder()).thenReturn(mockBuilder);
            Mockito.when(mockBuilder.uri(Mockito.isA(URI.class))).thenReturn(mockBuilder);
            Mockito.when(mockBuilder.headers(Mockito.isA(String[].class))).thenReturn(mockBuilder);
            Mockito.when(mockBuilder.POST(Mockito.isA(BodyPublisher.class)))
                .thenReturn(mockBuilder);
            Mockito.when(mockBuilder.build()).thenReturn(mockHttpRequest);

            // Expects - sendAuthenticationRequest
            Mockito.when(HttpClient.newHttpClient()).thenReturn(mockHttpClient);
            Mockito.when(mockHttpClient.send(mockHttpRequest, BodyHandlers.ofString()))
                .thenReturn(response);

            // Run
            return classUnderTest.getAccessToken();
        } catch (IOException | InterruptedException | RuntimeException exception) {
            fail(exception.getMessage());
            return null;
        }
    }
}
