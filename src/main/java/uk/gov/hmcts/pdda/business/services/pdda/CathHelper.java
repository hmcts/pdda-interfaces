package uk.gov.hmcts.pdda.business.services.pdda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.CourtelJson;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListDao;
import uk.gov.hmcts.pdda.business.services.pdda.cath.ArtefactType;


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
    private static final String PROVENANCE = "Provenance";

    private final BlobHelper blobHelper;
    private OAuth2Helper oauth2Helper;

    public CathHelper(BlobHelper blobHelper) {
        this.blobHelper = blobHelper;
    }

    // JUnit
    public CathHelper(BlobHelper blobHelper, OAuth2Helper oauth2Helper) {
        this(blobHelper);
        this.oauth2Helper = oauth2Helper;
    }

    public XhbCourtelListDao populateCourtelListBlob(XhbCourtelListDao xhbCourtelListDao) {
        xhbCourtelListDao.setBlob(blobHelper.getBlob(xhbCourtelListDao.getBlobId()));
        return xhbCourtelListDao;
    }

    public String generateJsonString(XhbCourtelListDao xhbCourtelListDao, CourtelJson courtelJson) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            courtelJson.setJson(mapper.writeValueAsString(xhbCourtelListDao));
        } catch (JsonProcessingException e) {
            LOG.error("Error creating JSON String for {} object.", xhbCourtelListDao);
        }
        return courtelJson.getJson();
    }

    public void send(ArtefactType artefactType, String jsonString) {
        LOG.debug("send({}, {})", artefactType, jsonString);
        // Get the authentication token
        String token = getToken();
        // Add the authentication token to the json header
        String jsonWithToken = addTokenToJsonHeader(token, jsonString);
        // Post the json to CaTH
        String errorMessage = postJsonToCath(artefactType, jsonWithToken);
        if (!EMPTY_STRING.equals(errorMessage)) {
            LOG.error("Error sending Json: {}", jsonString);
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

    protected String postJsonToCath(ArtefactType artefactType, String jsonWithToken) {
        LOG.debug("postJsonToCath()");
        // TODO - review these
//        String now = LocalDateTime.now().toString();
//        String nextMonth = LocalDateTime.now().plusMonths(1).toString(); 
//        Integer courtId = Integer.valueOf(1);
//        ListType listType = ListType.CROWN_DAILY_LIST;
//        String pddaSystemid = "12345"; 
//        String language = Language.ENGLISH.toString();
        // TODO - POST the json to CaTH
//        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(POST_URL))
//            .header(PublicationConfiguration.TYPE_HEADER, artefactType.toString())
//            .header(PublicationConfiguration.PROVENANCE_HEADER, PROVENANCE)
//            .header(PublicationConfiguration.SOURCE_ARTEFACT_ID_HEADER, pddaSystemid)
//            .header(PublicationConfiguration.DISPLAY_FROM_HEADER, now)
//            .header(PublicationConfiguration.DISPLAY_TO_HEADER, nextMonth)
//            .header(PublicationConfiguration.COURT_ID, courtId.toString())
//            .header(PublicationConfiguration.LIST_TYPE, listType.toString())
//            .header(PublicationConfiguration.LANGUAGE_HEADER, language)
//            .header(PublicationConfiguration.CONTENT_DATE, now)).content(jsonWithToken)
//            .contentType(MediaType.APPLICATION_JSON);
//        
        
        return EMPTY_STRING;
    }

    private OAuth2Helper getOAuth2Helper() {
        if (oauth2Helper == null) {
            this.oauth2Helper = new OAuth2Helper();
        }
        return oauth2Helper;
    }
}
