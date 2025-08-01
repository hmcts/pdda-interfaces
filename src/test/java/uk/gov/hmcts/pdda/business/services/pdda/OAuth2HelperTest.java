
package uk.gov.hmcts.pdda.business.services.pdda;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
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
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**

 * Title: OAuth2HelperTest Test.


 * Description:


 * Copyright: Copyright (c) 2024


 * Company: CGI

 * @author Mark Harris
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("PMD")
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
        classUnderTest = new OAuth2Helper(mockEnvironment) {
            @Override
            protected String getTenantId() {
                return DUMMY_TENANTID;
            }

            @Override
            protected String getTokenUrl() {
                return String.format(DUMMY_TOKENURL, DUMMY_TENANTID);
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
    void shouldReturnAccessTokenWhenResponseIsValid() {
        runAccessTokenScenario(DummyCourtelUtil.getHttpResponse(200, VALID_RESPONSE));
    }

    @Test
    void shouldHandleInvalidAccessTokenResponseGracefully() {
        runAccessTokenScenario(DummyCourtelUtil.getHttpResponse(404, INVALID_RESPONSE));
    }

    @Test
    void shouldHandleNullAccessTokenResponseGracefully() {
        runAccessTokenScenario(DummyCourtelUtil.getHttpResponse(404, null));
    }


    @Test
    void testGetAccessTokenInvalid() {
        try (
            MockedStatic<HttpRequest> staticHttpRequest = Mockito.mockStatic(HttpRequest.class);
            MockedStatic<HttpClient> staticHttpClient = Mockito.mockStatic(HttpClient.class)
        ) {
            staticHttpRequest.when(HttpRequest::newBuilder).thenReturn(mockBuilder);
            Mockito.when(mockBuilder.uri(Mockito.any(URI.class))).thenReturn(mockBuilder);
            Mockito.when(mockBuilder.headers(Mockito.any(String[].class))).thenReturn(mockBuilder);
            Mockito.when(mockBuilder.POST(Mockito.any(BodyPublisher.class))).thenReturn(mockBuilder);
            Mockito.when(mockBuilder.build()).thenReturn(mockHttpRequest);

            staticHttpClient.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            Mockito.when(mockHttpClient.send(mockHttpRequest, BodyHandlers.ofString()))
                .thenReturn(DummyCourtelUtil.getHttpResponse(404, INVALID_RESPONSE));

            String result = classUnderTest.getAccessToken();
            assertNotNull(result, NOTNULL);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }


    @Test
    void testGetAccessTokenNull() {
        try (
            MockedStatic<HttpRequest> staticHttpRequest = Mockito.mockStatic(HttpRequest.class);
            MockedStatic<HttpClient> staticHttpClient = Mockito.mockStatic(HttpClient.class)
        ) {
            staticHttpRequest.when(HttpRequest::newBuilder).thenReturn(mockBuilder);
            Mockito.when(mockBuilder.uri(Mockito.any(URI.class))).thenReturn(mockBuilder);
            Mockito.when(mockBuilder.headers(Mockito.any(String[].class))).thenReturn(mockBuilder);
            Mockito.when(mockBuilder.POST(Mockito.any(BodyPublisher.class))).thenReturn(mockBuilder);
            Mockito.when(mockBuilder.build()).thenReturn(mockHttpRequest);

            staticHttpClient.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            Mockito.when(mockHttpClient.send(mockHttpRequest, BodyHandlers.ofString()))
                .thenReturn(DummyCourtelUtil.getHttpResponse(404, null));

            String result = classUnderTest.getAccessToken();
            assertNotNull(result, NOTNULL);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }


    private void runAccessTokenScenario(HttpResponse<String> response) {
        try (
            MockedStatic<HttpRequest> staticHttpRequest = Mockito.mockStatic(HttpRequest.class);
            MockedStatic<HttpClient> staticHttpClient = Mockito.mockStatic(HttpClient.class)
        ) {
            staticHttpRequest.when(HttpRequest::newBuilder).thenReturn(mockBuilder);
            Mockito.when(mockBuilder.uri(Mockito.any(URI.class))).thenReturn(mockBuilder);
            Mockito.when(mockBuilder.headers(Mockito.any(String[].class))).thenReturn(mockBuilder);
            Mockito.when(mockBuilder.POST(Mockito.any(BodyPublisher.class))).thenReturn(mockBuilder);
            Mockito.when(mockBuilder.build()).thenReturn(mockHttpRequest);

            staticHttpClient.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            Mockito.when(mockHttpClient.send(mockHttpRequest, BodyHandlers.ofString()))
                .thenReturn(response);

            String result = classUnderTest.getAccessToken();
            assertNotNull(result, NOTNULL);
        } catch (IOException | InterruptedException | RuntimeException exception) {
            fail(exception.getMessage());
        }
    }

    
    @Test
    void testGetClientCredentialsForm() throws Exception {
        // Act
        var method = OAuth2Helper.class.getDeclaredMethod("getClientCredentialsForm");
        method.setAccessible(true);
        String result = (String) method.invoke(classUnderTest);

        // Assert
        assertNotNull(result, NOTNULL);
        assertTrue(result.contains("grant_type=client_credentials"), "Form should contain grant_type");
    }
    
    
    @Test
    void testGetAuthenticationRequest() throws Exception {
        try (MockedStatic<HttpRequest> staticHttpRequest = Mockito.mockStatic(HttpRequest.class)) {
            staticHttpRequest.when(HttpRequest::newBuilder).thenReturn(mockBuilder);
            Mockito.when(mockBuilder.uri(Mockito.any(URI.class))).thenReturn(mockBuilder);
            Mockito.when(mockBuilder.headers(Mockito.any(String[].class))).thenReturn(mockBuilder);
            Mockito.when(mockBuilder.POST(Mockito.any(BodyPublisher.class))).thenReturn(mockBuilder);
            Mockito.when(mockBuilder.build()).thenReturn(mockHttpRequest);

            // Stub URI
            URI dummyUri = new URI("https://dummy.token.url");
            Mockito.when(mockHttpRequest.uri()).thenReturn(dummyUri);

            // Stub headers
            HttpHeaders mockHeaders = Mockito.mock(HttpHeaders.class);
            Mockito.when(mockHttpRequest.headers()).thenReturn(mockHeaders);
            Mockito.when(mockHeaders.map()).thenReturn(Map.of("Authorization", List.of("Basic dummyvalue")));

            var method = OAuth2Helper.class.getDeclaredMethod("getAuthenticationRequest", String.class);
            method.setAccessible(true);
            HttpRequest request = (HttpRequest) method.invoke(classUnderTest, "https://dummy.token.url");

            assertNotNull(request, NOTNULL);
            assertTrue(request.headers().map().containsKey("Authorization"), "Authorization header missing");
            assertTrue(request.uri().toString().equals("https://dummy.token.url"), "URI mismatch");
        }
    }

    
    @Test
    void testSendAuthenticationRequestException() throws Exception {
        try (MockedStatic<HttpClient> staticHttpClient = Mockito.mockStatic(HttpClient.class)) {
            staticHttpClient.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            Mockito.when(mockHttpClient.send(Mockito.any(HttpRequest.class), Mockito.any()))
                .thenThrow(new IOException("Simulated IO error"));

            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost"))
                .POST(HttpRequest.BodyPublishers.ofString("test"))
                .build();

            var sendMethod = OAuth2Helper.class.getDeclaredMethod("sendAuthenticationRequest", HttpRequest.class);
            sendMethod.setAccessible(true);

            String result = (String) sendMethod.invoke(classUnderTest, request);

            assertNotNull(result, NOTNULL);
            assertTrue(result.isEmpty(), "Expected empty string on exception");
        }
    }

    
    @Test
    void testGetAccessTokenFromResponseInvalidJson() throws Exception {
        // Arrange
        String malformedJson = "{\"access_token\":}";

        var method = OAuth2Helper.class.getDeclaredMethod("getAccessTokenFromResponse", String.class);
        method.setAccessible(true);

        // Act
        String result = (String) method.invoke(classUnderTest, malformedJson);

        // Assert
        assertNotNull(result, NOTNULL);
        assertTrue(result.isEmpty(), "Result should be empty on parse failure");
    }
    
    @Test
    void testGetTokenUrlWhenTokenUrlIsNull() {
        Mockito.when(mockEnvironment.getProperty("spring.cloud.azure.oauth2.token-url")).thenReturn(null);

        OAuth2Helper helper = new OAuth2Helper(mockEnvironment);
        String result = helper.getTokenUrl();

        assertTrue(result.isEmpty(), "Expected empty token URL when property is null");
    }

    @Test
    void testGetTokenUrlWhenTenantIdIsNull() {
        Mockito.when(mockEnvironment.getProperty("spring.cloud.azure.oauth2.token-url"))
            .thenReturn("https://example.com/%s/oauth2/token");
        Mockito.when(mockEnvironment.getProperty("spring.cloud.azure.active-directory.profile.tenant-id"))
            .thenReturn(null);

        OAuth2Helper helper = new OAuth2Helper(mockEnvironment);
        String result = helper.getTokenUrl();

        assertTrue(result.isEmpty(), "Expected empty token URL when tenant ID is null");
    }

    @Test
    void testGetClientIdWhenPropertyIsNull() {
        Mockito.when(mockEnvironment.getProperty("spring.cloud.azure.active-directory.credential.client-id"))
            .thenReturn(null);

        OAuth2Helper helper = new OAuth2Helper(mockEnvironment);
        String result = helper.getClientId();

        assertTrue(result.isEmpty(), "Expected empty client ID when property is null");
    }

    @Test
    void testGetClientSecretWhenPropertyIsNull() {
        Mockito.when(mockEnvironment.getProperty("spring.cloud.azure.active-directory.credential.client-secret"))
            .thenReturn(null);

        OAuth2Helper helper = new OAuth2Helper(mockEnvironment);
        String result = helper.getClientSecret();

        assertTrue(result.isEmpty(), "Expected empty client secret when property is null");
    }

    @Test
    void testGetAccessTokenFromResponseEmptyInput() throws Exception {
        var method = OAuth2Helper.class.getDeclaredMethod("getAccessTokenFromResponse", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(classUnderTest, "");

        assertNotNull(result, NOTNULL);
        assertTrue(result.isEmpty(), "Expected empty token from empty response");
    }

    
    @Test
    void testSendAuthenticationRequestWithRuntimeException() throws Exception {
        try (MockedStatic<HttpClient> staticHttpClient = Mockito.mockStatic(HttpClient.class)) {
            staticHttpClient.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            Mockito.when(mockHttpClient.send(Mockito.any(HttpRequest.class), Mockito.any()))
                .thenThrow(new RuntimeException("Simulated runtime failure"));

            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost"))
                .POST(HttpRequest.BodyPublishers.ofString("test"))
                .build();

            var sendMethod = OAuth2Helper.class.getDeclaredMethod("sendAuthenticationRequest", HttpRequest.class);
            sendMethod.setAccessible(true);

            String result = (String) sendMethod.invoke(classUnderTest, request);

            assertNotNull(result, NOTNULL);
            assertTrue(result.isEmpty(), "Expected empty string on runtime exception");
        }
    }
    
    @Test
    void testSetEnvironmentAndGetTenantId() {
        String expectedTenantId = "realTenant123";
        Mockito.when(mockEnvironment.getProperty("spring.cloud.azure.active-directory.profile.tenant-id"))
            .thenReturn(expectedTenantId);

        OAuth2Helper helper = new OAuth2Helper();
        helper.setEnvironment(mockEnvironment);

        String actualTenantId = helper.getTenantId();
        assertNotNull(actualTenantId);
        assertTrue(actualTenantId.equals(expectedTenantId));
    }

    
    @Test
    void testGetTokenUrlWithValidValues() {
        String tokenUrlTemplate = "https://example.com/%s/oauth2/token";
        String tenantId = "tenant-abc";

        Mockito.when(mockEnvironment.getProperty("spring.cloud.azure.oauth2.token-url"))
            .thenReturn(tokenUrlTemplate);
        Mockito.when(mockEnvironment.getProperty("spring.cloud.azure.active-directory.profile.tenant-id"))
            .thenReturn(tenantId);

        OAuth2Helper helper = new OAuth2Helper(mockEnvironment);
        String result = helper.getTokenUrl();

        assertTrue(result.equals("https://example.com/tenant-abc/oauth2/token"), "Formatted token URL is incorrect");
    }

    
    @Test
    void testGetClientIdWhenPropertyExists() {
        String expectedClientId = "client-id-xyz";
        Mockito.when(mockEnvironment.getProperty("spring.cloud.azure.active-directory.credential.client-id"))
            .thenReturn(expectedClientId);

        OAuth2Helper helper = new OAuth2Helper(mockEnvironment);
        String result = helper.getClientId();

        assertTrue(result.equals(expectedClientId));
    }

    
    @Test
    void testGetClientSecretWhenPropertyExists() {
        String expectedSecret = "super-secret";
        Mockito.when(mockEnvironment.getProperty("spring.cloud.azure.active-directory.credential.client-secret"))
            .thenReturn(expectedSecret);

        OAuth2Helper helper = new OAuth2Helper(mockEnvironment);
        String result = helper.getClientSecret();

        assertTrue(result.equals(expectedSecret));
    }


}
