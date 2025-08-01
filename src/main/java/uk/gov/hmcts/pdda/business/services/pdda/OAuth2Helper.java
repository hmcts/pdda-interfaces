package uk.gov.hmcts.pdda.business.services.pdda;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet.InitializationService;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**

 * Title: OAuth2Helper.


 * Description:


 * Copyright: Copyright (c) 2024


 * Company: CGI

 * @author Mark Harris
 * @version 1.0
 */
@SuppressWarnings({"squid:S6813", "squid:S1948", "squid:S112",
    "PMD.PreserveStackTrace", "PMD.AvoidThrowingRawExceptionTypes"})
public class OAuth2Helper implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(OAuth2Helper.class);
    private static final String EMPTY_STRING = "";
    private static final String PDDA_AZURE_TOKEN_URL = "spring.cloud.azure.oauth2.token-url";
    private static final String PDDA_AZURE_TENANT_ID =
        "spring.cloud.azure.active-directory.profile.tenant-id";
    private static final String PDDA_AZURE_CLIENT_ID =
        "spring.cloud.azure.active-directory.credential.client-id";
    private static final String PDDA_AZURE_CLIENT_SECRET =
        "spring.cloud.azure.active-directory.credential.client-secret";
    protected Environment env;

    public OAuth2Helper() {
        this(InitializationService.getInstance().getEnvironment());
    }

    protected OAuth2Helper(Environment env) {
        LOG.info("Environment = {}", env);
        this.env = env;
    }

    @Autowired
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }


    protected String getTenantId() {
        return env.getProperty(PDDA_AZURE_TENANT_ID);
    }

    protected String getTokenUrl() {
        String authTokenUrl = env.getProperty(PDDA_AZURE_TOKEN_URL);
        if (authTokenUrl == null) {
            LOG.error("Token URL property '{}' not found in environment.", PDDA_AZURE_TOKEN_URL);
            return "";
        }

        String tenantId = getTenantId();
        if (tenantId == null) {
            LOG.error("Tenant ID property '{}' not found in environment.", PDDA_AZURE_TENANT_ID);
            return "";
        }

        return String.format(authTokenUrl, tenantId);
    }


    protected String getClientId() {
        String clientId = env.getProperty(PDDA_AZURE_CLIENT_ID);
        if (clientId == null) {
            LOG.error("Client ID property '{}' not found in environment.", PDDA_AZURE_CLIENT_ID);
            return "";
        }
        return env.getProperty(PDDA_AZURE_CLIENT_ID);
    }

    protected String getClientSecret() {
        String clientSecret = env.getProperty(PDDA_AZURE_CLIENT_SECRET);
        if (clientSecret == null) {
            LOG.error("Client Secret property '{}' not found in environment.",
                PDDA_AZURE_CLIENT_SECRET);
            return "";
        }

        return env.getProperty(PDDA_AZURE_CLIENT_SECRET);
    }

    public String getAccessToken() {
        LOG.debug("getAccessToken()");
        String url = getTokenUrl();
        // Get the authentication request
        HttpRequest request = getAuthenticationRequest(url);
        // Send the authentication request
        String response = sendAuthenticationRequest(request);
        // Get the access token from the authentication response
        return getAccessTokenFromResponse(response);
    }

    private HttpRequest getAuthenticationRequest(String url) {
        LOG.info("getAuthorizationRequest({})", url);
        // Build the encoded clientId / clientSecret key
        String key = getClientId() + ":" + getClientSecret();
        String encodedKey = Base64.getEncoder().encodeToString(key.getBytes());
        LOG.debug("encodedKey generated");
        // Build the authentication post request
        try {
            return HttpRequest.newBuilder().uri(URI.create(url))
                .headers("Content-Type", "application/x-www-form-urlencoded", "Authorization",
                    "Basic " + encodedKey)
                .POST(BodyPublishers.ofString(getClientCredentialsForm())).build();
        } catch (Exception ex) {
            throw new RuntimeException(
                String.format("Error in building HTTP request: %s", ex.getMessage()));
        }
    }

    private String getClientCredentialsForm() {
        LOG.debug("getClientCredentialsForm()");
        Map<String, String> parameters = new ConcurrentHashMap<>();
        parameters.put("grant_type", "client_credentials");
        return parameters.keySet().stream()
            .map(key -> key + "=" + URLEncoder.encode(parameters.get(key), StandardCharsets.UTF_8))
            .collect(Collectors.joining("&"));
    }

    @SuppressWarnings("squid:S2142")
    private String sendAuthenticationRequest(HttpRequest request) {
        LOG.info("sendAuthorizationRequest()");
        try {
            // Send the authentication request and get the response
            HttpResponse<?> httpResponse =
                HttpClient.newHttpClient().send(request, BodyHandlers.ofString());

            Integer statusCode = httpResponse.statusCode();
            LOG.info("Response status code: {}", statusCode);
            String response = httpResponse.body().toString();
            LOG.info("Response: {}", response);
            return response;
        } catch (IOException | InterruptedException | RuntimeException exception) {
            LOG.error("Error in sendAuthenticationRequest(): {}", exception.getMessage());
        }
        return EMPTY_STRING;
    }

    private String getAccessTokenFromResponse(String response) {
        LOG.info("getAccessTokenFromResponse()");
        if (!EMPTY_STRING.equalsIgnoreCase(response)) {
            try {
                TokenResponse tokenResponse =
                    new ObjectMapper().readValue(response, TokenResponse.class);
                LOG.info("Fetched token response {}", tokenResponse.toString());
                return tokenResponse.accessToken();
            } catch (JsonProcessingException exception) {
                LOG.error("Error in getAccessTokenFromResponse(): {}", exception.getMessage());
            }
        }
        return EMPTY_STRING;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record TokenResponse(@JsonProperty("access_token") String accessToken) {
    }
}
