package uk.gov.hmcts.pdda.business.services.pdda.cath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.pdda.business.services.pdda.OAuth2Helper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**

 * Title: CathOAuth2Helper.


 * Description:


 * Copyright: Copyright (c) 2025


 * Company: CGI

 * @author Mark Harris
 * @version 1.0
 */
@Component
@SuppressWarnings({"PMD.PreserveStackTrace", "PMD.AvoidThrowingRawExceptionTypes"})
public class CathOAuth2Helper extends OAuth2Helper {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(CathOAuth2Helper.class);
    
    private static final String CATH_AZURE_TOKEN_URL = 
        "cath.azure.oauth2.token-url";
    private static final String CATH_AZURE_HEALTH_ENDPOINT_URL = 
        "cath.azure.oauth2.health-endpoint-url";
    private static final String CATH_AZURE_CLIENT_ID =
        "cath.azure.active-directory.credential.client-id";
    private static final String CATH_AZURE_CLIENT_SECRET =
        "cath.azure.active-directory.credential.client-secret";
    private static final String CATH_AZURE_AUTH_SCOPE =
        "cath.azure.active-directory.credential.auth-scope";

    @Override
    protected String getTokenUrl() {
        LOG.debug("Getting CaTH Token URL {}", CATH_AZURE_TOKEN_URL);
        String tokenUrl = env.getProperty(CATH_AZURE_TOKEN_URL);
        if (tokenUrl == null) {
            LOG.error("Token URL property '{}' not found in environment.", CATH_AZURE_TOKEN_URL);
            return "";
        }
        return env.getProperty(CATH_AZURE_TOKEN_URL);
    }

    @Override
    protected String getClientId() {
        LOG.debug("Getting CaTH Client ID {}", CATH_AZURE_CLIENT_ID);
        String clientId = env.getProperty(CATH_AZURE_CLIENT_ID);
        if (clientId == null) {
            LOG.error("Client ID property '{}' not found in environment.", CATH_AZURE_CLIENT_ID);
            return "";
        }
        return env.getProperty(CATH_AZURE_CLIENT_ID);
    }

    @Override
    protected String getClientSecret() {
        LOG.debug("Getting CaTH Client Secret {}", CATH_AZURE_CLIENT_SECRET);
        String clientSecret = env.getProperty(CATH_AZURE_CLIENT_SECRET);
        if (clientSecret == null) {
            LOG.error("Client Secret property '{}' not found in environment.", CATH_AZURE_CLIENT_SECRET);
            return "";
        }
        return env.getProperty(CATH_AZURE_CLIENT_SECRET);
    }
    
    protected String getAuthScope() {
        LOG.debug("Getting CaTH Auth Scope {}", CATH_AZURE_AUTH_SCOPE);
        String authScope = env.getProperty(CATH_AZURE_AUTH_SCOPE);
        if (authScope == null) {
            LOG.error("Auth Scope property '{}' not found in environment.", CATH_AZURE_AUTH_SCOPE);
            return "";
        }
        return env.getProperty(CATH_AZURE_AUTH_SCOPE);
    }
    
    protected String getHealthEndpointUrl() {
        LOG.debug("Getting CaTH Health Endpoint URL {}", CATH_AZURE_HEALTH_ENDPOINT_URL);
        String healthEndpointUrl = env.getProperty(CATH_AZURE_HEALTH_ENDPOINT_URL);
        if (healthEndpointUrl == null) {
            LOG.error("Health Endpoint URL property '{}' not found in environment.", CATH_AZURE_HEALTH_ENDPOINT_URL);
            return "";
        }
        return env.getProperty(CATH_AZURE_HEALTH_ENDPOINT_URL);
    }
    
    @Override
    protected HttpRequest getAuthenticationRequest(String url) {
        LOG.info("getAuthenticationRequest({})", url);
        // Build the authentication post request
        try {
            return HttpRequest.newBuilder().uri(URI.create(url))
                .headers("Content-Type", "application/x-www-form-urlencoded")
                .POST(BodyPublishers.ofString(getClientCredentialsForm())).build();
        } catch (Exception ex) {
            throw new RuntimeException(
                String.format("Error in building HTTP request: %s", ex.getMessage()));
        }
    }
    
    @Override
    protected String getClientCredentialsForm() {
        LOG.debug("getClientCredentialsForm()");
        Map<String, String> parameters = new ConcurrentHashMap<>();
        parameters.put("client_id", getClientId());
        parameters.put("scope", getAuthScope());
        parameters.put("client_secret", getClientSecret());
        parameters.put("grant_type", "client_credentials");
        return parameters.keySet().stream()
            .map(key -> key + "=" + URLEncoder.encode(parameters.get(key), StandardCharsets.UTF_8))
            .collect(Collectors.joining("&"));
    }
    
    public String getHealthEndpointStatus(String accessToken) {
        LOG.debug("getHealthEndpointStatus()");
        // Get the health endpoint request
        HttpRequest request = getHealthEndpointRequest(accessToken);
        // Send the request
        return sendRequest(request);
    }
    
    private HttpRequest getHealthEndpointRequest(String accessToken) {
        String url = getHealthEndpointUrl();
        // Build the health endpoint GET request
        try {
            return HttpRequest.newBuilder().uri(URI.create(url))
                .headers("Authorization", "Bearer " + accessToken)
                .GET().build();
        } catch (Exception ex) {
            throw new RuntimeException(
                String.format("Error in building HTTP request: %s", ex.getMessage()));
        }
    }

}
