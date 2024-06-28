package uk.gov.hmcts.pdda.business.services.pdda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListJson;

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

    private final BlobHelper blobHelper;

    public CathHelper(BlobHelper blobHelper) {
        this.blobHelper = blobHelper;
    }

    public XhbCourtelListJson convertDaoToJsonObject(XhbCourtelListDao xhbCourtelListDao) {
        XhbCourtelListJson xhbCourtelListJson = new XhbCourtelListJson();
        xhbCourtelListJson.setBlobData(blobHelper.getBlobData(xhbCourtelListDao.getBlobId()));
        return xhbCourtelListJson;
    }

    public String generateJsonString(XhbCourtelListJson xhbCourtelListJson) {
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try {
            json = mapper.writeValueAsString(xhbCourtelListJson);
        } catch (JsonProcessingException e) {
            LOG.error("Error creating JSON String for {} object.", xhbCourtelListJson);
        }
        return json;
    }

    public void send(String jsonString) {
        LOG.debug("send({})", jsonString);
        // Get the credentials for the azure server
        String credentials = getCredentials();
        // Get the authentication token
        String token = getToken(credentials);
        // Add the authentication token to the json header
        String jsonWithToken = addTokenToJsonHeader(token, jsonString);
        // Post the json to CaTH
        String errorMessage = postJsonToCath(jsonWithToken);
        if (!EMPTY_STRING.equals(errorMessage)) {
            LOG.error("Error sending Json: {}",jsonString);
            LOG.error("Error from CaTH: {}",errorMessage);
        }
    }
    
    protected String getCredentials() {
        LOG.debug("getCredentials()");
        // PDDA-388
        return "";
    }
    
    protected String getToken(String credentials) {
        LOG.debug("getToken()");
        // PDDA-389
        return "";
    }
    
    protected String addTokenToJsonHeader(String token, String jsonString) {
        LOG.debug("addTokenToJsonHeader()");
        return jsonString.replace(OAUTHTOKEN_PLACEHOLDER, token);
    }
    
    protected String postJsonToCath(String jsonWithToken) {
        LOG.debug("postJsonToCath()");
        // TODO
        return EMPTY_STRING;
    }
}
