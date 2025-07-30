package uk.gov.hmcts.pdda.business.services.pdda.cath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("PMD")
class CathOAuth2HelperTest {

    private static final String TOKEN_URL = "https://cath.example.com/token";
    private static final String CLIENT_ID = "cath-client-id";
    private static final String CLIENT_SECRET = "cath-secret";

    private Environment mockEnv;
    private CathOAuth2Helper helper;

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
}

