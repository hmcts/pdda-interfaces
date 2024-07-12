package uk.gov.hmcts.pdda.business.services.pdda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.CourtelJson;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListDao;
import uk.gov.hmcts.pdda.business.services.pdda.cath.CathUtils;

import java.net.http.HttpRequest;
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
    private static final String OAUTHTOKEN_PLACEHOLDER = "<OAuthToken>";
    private static final String POST_URL = "/publication";

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
        String token = getToken();
        // Add the authentication token to the json header
        String jsonWithToken = addTokenToJsonHeader(token, courtelJson.getJson());
        courtelJson.setJson(jsonWithToken);
        // Post the json to CaTH
        String errorMessage = postJsonToCath(courtelJson);
        if (!EMPTY_STRING.equals(errorMessage)) {
            LOG.error("Error sending Json: {}", courtelJson.getJson());
            LOG.error("Error from CaTH: {}", errorMessage);
        }
    }

    protected String getToken() {
        LOG.debug("getToken()");
        return getOAuth2Helper().getAccessToken();
    }

    protected String addTokenToJsonHeader(String token, String jsonString) {
        LOG.debug("addTokenToJsonHeader()");
        return jsonString.replace(OAUTHTOKEN_PLACEHOLDER, token);
    }

    protected String postJsonToCath(CourtelJson courtelJson) {
        LOG.debug("postJsonToCath()");
        HttpRequest httpRequest = CathUtils.getHttpPostRequest(LocalDateTime.now(), POST_URL,
            courtelJson);
        LOG.debug(httpRequest.toString());
        return EMPTY_STRING;
    }

    private OAuth2Helper getOAuth2Helper() {
        if (oauth2Helper == null) {
            this.oauth2Helper = new OAuth2Helper();
        }
        return oauth2Helper;
    }
}
