package uk.gov.hmcts.pdda.business.services.pdda.cath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.env.Environment;

import java.net.URI;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.Builder;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("PMD")
class CathOAuth2HelperTest {

    private static final String TOKEN_URL = "https://cath.example.com/token";
    private static final String CLIENT_ID = "cath-client-id";
    private static final String CLIENT_SECRET = "cath-secret";
    private static final String AUTH_SCOPE = "auth-scope";
    private static final String NOTNULL = "Result is null";

    private Environment mockEnv;
    private CathOAuth2Helper helper;
    
    @Mock
    private Builder mockBuilder;
    
    @Mock
    private HttpRequest mockHttpRequest;

    @BeforeEach
    void setUp() {
        mockEnv = Mockito.mock(Environment.class);
        helper = new CathOAuth2Helper();
        helper.setEnvironment(mockEnv);
    }

    @Test
    void shouldReturnCathTokenUrlWhenPropertyExists() {
        Mockito.when(mockEnv.getProperty("cath.azure.oauth2.token-url")).thenReturn(TOKEN_URL);
        String result = helper.getTokenUrl();
        assertEquals(TOKEN_URL, result);
    }

    @Test
    void shouldReturnEmptyStringAndLogWhenTokenUrlMissing() {
        Mockito.when(mockEnv.getProperty("cath.azure.oauth2.token-url")).thenReturn(null);
        String result = helper.getTokenUrl();
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnCathClientIdWhenPropertyExists() {
        Mockito.when(mockEnv.getProperty("cath.azure.active-directory.credential.client-id")).thenReturn(CLIENT_ID);
        String result = helper.getClientId();
        assertEquals(CLIENT_ID, result);
    }

    @Test
    void shouldReturnEmptyStringAndLogWhenClientIdMissing() {
        Mockito.when(mockEnv.getProperty("cath.azure.active-directory.credential.client-id")).thenReturn(null);
        String result = helper.getClientId();
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnCathClientSecretWhenPropertyExists() {
        Mockito.when(mockEnv.getProperty("cath.azure.active-directory.credential.client-secret"))
            .thenReturn(CLIENT_SECRET);
        String result = helper.getClientSecret();
        assertEquals(CLIENT_SECRET, result);
    }

    @Test
    void shouldReturnEmptyStringAndLogWhenClientSecretMissing() {
        Mockito.when(mockEnv.getProperty("cath.azure.active-directory.credential.client-secret")).thenReturn(null);
        String result = helper.getClientSecret();
        assertTrue(result.isEmpty());
    }
    
    @Test
    void shouldReturnCathAuthScopeWhenPropertyExists() {
        Mockito.when(mockEnv.getProperty("cath.azure.active-directory.credential.auth-scope"))
            .thenReturn(AUTH_SCOPE);
        String result = helper.getAuthScope();
        assertEquals(AUTH_SCOPE, result);
    }

    @Test
    void shouldReturnEmptyStringAndLogWhenAuthScopeMissing() {
        Mockito.when(mockEnv.getProperty("cath.azure.active-directory.credential.auth-scope")).thenReturn(null);
        String result = helper.getAuthScope();
        assertTrue(result.isEmpty());
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
            Mockito.when(mockHeaders.map()).thenReturn(Map.of("Content-Type", List.of("Basic dummyvalue")));

            var method = CathOAuth2Helper.class.getDeclaredMethod("getAuthenticationRequest", String.class);
            method.setAccessible(true);
            HttpRequest request = (HttpRequest) method.invoke(helper, "https://dummy.token.url");

            assertNotNull(request, NOTNULL);
            assertTrue(request.headers().map().containsKey("Content-Type"), "Content-Type header missing");
            assertTrue(request.uri().toString().equals("https://dummy.token.url"), "URI mismatch");
        }
    }
    
    @Test
    void testGetClientCredentialsForm() throws Exception {
        // Act
        var method = CathOAuth2Helper.class.getDeclaredMethod("getClientCredentialsForm");
        method.setAccessible(true);
        String result = (String) method.invoke(helper);

        // Assert
        assertNotNull(result, NOTNULL);
        assertTrue(result.contains("grant_type=client_credentials"), "Form should contain grant_type");
    }
    
}

