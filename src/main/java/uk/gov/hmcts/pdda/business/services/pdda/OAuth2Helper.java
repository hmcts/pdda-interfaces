package uk.gov.hmcts.pdda.business.services.pdda;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
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
 * <p>
 * Title: OAuth2Helper.
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
 * @version 1.0
 */
@Configuration
public class OAuth2Helper {

    private static final Logger LOG = LoggerFactory.getLogger(OAuth2Helper.class);
    private static final String EMPTY_STRING = "";
    private static final String TOKEN_URL = "http://localhost:8080/api/token"; // TODO


    @Value("${AZURE_CLIENT_ID")
    public String clientId;

    @Value("${AZURE_CLIENT_SECRET}")
    public String clientSecret;


    public String getAccessToken() {
        LOG.debug("getAccessToken()");
        // Get the authentication request
        HttpRequest request = getAuthenticationRequest(TOKEN_URL);
        // Send the authentication request
        String response = sendAuthenticationRequest(request);
        // Get the access token from the authentication response
        return getAccessTokenFromResponse(response);
    }

    private HttpRequest getAuthenticationRequest(String url) {
        LOG.debug("getAuthorizationRequest()");
        // Build the encoded clientId / clientSecret key
        String key = clientId + ":" + clientSecret;
        String encodedKey =  Base64.getEncoder().encodeToString(key.getBytes());
        LOG.debug("encodedKey generated");
        // Build the authentication post request 
        return HttpRequest.newBuilder().uri(URI.create(url))
            .headers("Content-Type", "application/x-www-form-urlencoded", "Authorization",
                "Basic " + encodedKey)
            .POST(BodyPublishers.ofString(getClientCredentialsForm())).build();
    }

    private String getClientCredentialsForm() {
        LOG.debug("getClientCredentialsForm()");
        Map<String, String> parameters = new ConcurrentHashMap<>();
        parameters.put("grant_type", "client_credentials");
        return parameters.keySet().stream()
            .map(key -> key + "=" + URLEncoder.encode(parameters.get(key), StandardCharsets.UTF_8))
            .collect(Collectors.joining("&"));
    }

    private String sendAuthenticationRequest(HttpRequest request) {
        LOG.debug("sendAuthorizationRequest()");
        try {
            // Send the authentication request and get the response
            HttpResponse<?> httpResponse =
                HttpClient.newHttpClient().send(request, BodyHandlers.ofString());

            Integer statusCode = Integer.valueOf(httpResponse.statusCode());
            LOG.debug("Response status code: {}", statusCode);
            String response = httpResponse.body().toString();
            LOG.debug("Response: {}", response);
            return response;
        } catch (IOException | InterruptedException exception) {
            LOG.error("Error in sendAuthenticationRequest(): {}", exception.getMessage());
        }
        return EMPTY_STRING;
    }

    private String getAccessTokenFromResponse(String response) {
        LOG.debug("getAccessTokenFromResponse()");
        if (!EMPTY_STRING.equalsIgnoreCase(response)) {
            try {
                TokenResponse tokenResponse =
                    new ObjectMapper().readValue(response, TokenResponse.class);
                return tokenResponse.accessToken();
            } catch (JsonProcessingException exception) {
                LOG.error("Error in getAccessTokenFromResponse(): {}", exception.getMessage());
            }
        }
        return null;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record TokenResponse(@JsonProperty("access_token") String accessToken) {
    }
}
