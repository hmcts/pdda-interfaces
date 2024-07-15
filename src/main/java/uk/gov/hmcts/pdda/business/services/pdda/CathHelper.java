package uk.gov.hmcts.pdda.business.services.pdda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.CourtelJson;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListDao;
import uk.gov.hmcts.pdda.business.services.pdda.cath.CathUtils;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDateTime;


/**
 * <p>
 * Title: CathHelper.
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
 * @author Luke Gittins
 * @version 1.0
 */
public class CathHelper {

    private static final Logger LOG = LoggerFactory.getLogger(CathHelper.class);
    private static final String EMPTY_STRING = "";

    private OAuth2Helper oauth2Helper;

    public CathHelper() {
        super();
    }

    // JUnit
    public CathHelper(OAuth2Helper oauth2Helper) {
        this.oauth2Helper = oauth2Helper;
    }


    public String generateJsonString(XhbCourtelListDao xhbCourtelListDao, CourtelJson courtelJson) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            courtelJson.setJson(mapper.writeValueAsString(xhbCourtelListDao));
            return courtelJson.getJson();
        } catch (JsonProcessingException e) {
            LOG.error("Error creating JSON String for {} object.", xhbCourtelListDao);
        }
        return EMPTY_STRING;
    }

    public void send(CourtelJson courtelJson) {
        LOG.debug("send({})", courtelJson.getJson());
        // Get the authentication token
        courtelJson.setToken(getToken());
        // Post the json to CaTH
        String errorMessage = postJsonToCath(courtelJson);
        if (!EMPTY_STRING.equals(errorMessage)) {
            LOG.error("Error sending Json: {}", courtelJson.getJson());
            LOG.error("Error from CaTH: {}", errorMessage);
        }
    }

    protected String getToken() {
        LOG.debug("getToken()");
        if (CathUtils.isApimEnabled()) {
            return getOAuth2Helper().getAccessToken();
        }
        return EMPTY_STRING;
    }

    @SuppressWarnings("squid:S2142")
    protected String postJsonToCath(CourtelJson courtelJson) {
        LOG.debug("postJsonToCath()");
        String cathUrl = CathUtils.getApimUrl();
        LOG.debug("cathUrl - {}", cathUrl);
        HttpRequest httpRequest =
            CathUtils.getHttpPostRequest(LocalDateTime.now(), cathUrl, courtelJson);

        try {
            HttpResponse<?> httpResponse =
                HttpClient.newHttpClient().send(httpRequest, BodyHandlers.ofString());
            Integer statusCode = Integer.valueOf(httpResponse.statusCode());
            LOG.debug("Response status code: {}", statusCode);
            String response = httpResponse.body().toString();
            LOG.debug("Response: {}", response);
        } catch (IOException | InterruptedException | RuntimeException exception) {
            LOG.error("Error in postJsonToCath(): {}", exception.getMessage());
            return exception.getMessage();
        }
        return EMPTY_STRING;
    }

    private OAuth2Helper getOAuth2Helper() {
        if (oauth2Helper == null) {
            this.oauth2Helper = new OAuth2Helper();
        }
        return oauth2Helper;
    }
}
