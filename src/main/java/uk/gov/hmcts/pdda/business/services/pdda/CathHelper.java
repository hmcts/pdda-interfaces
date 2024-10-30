package uk.gov.hmcts.pdda.business.services.pdda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.CourtelJson;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListDao;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentDao;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentRepository;
import uk.gov.hmcts.pdda.business.services.pdda.cath.CathUtils;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


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
    
    private final EntityManager entityManager;
    private XhbXmlDocumentRepository xhbXmlDocumentRepository;
    private XhbClobRepository xhbClobRepository;

    private OAuth2Helper oauth2Helper;

    public CathHelper(EntityManager entityManager,
        XhbXmlDocumentRepository xhbXmlDocumentRepository) {
        super();
        this.entityManager = entityManager;
        this.xhbXmlDocumentRepository = xhbXmlDocumentRepository;
    }

    // JUnit
    public CathHelper(OAuth2Helper oauth2Helper, EntityManager entityManager,
        XhbXmlDocumentRepository xhbXmlDocumentRepository) {
        this.oauth2Helper = oauth2Helper;
        this.entityManager = entityManager;
        this.xhbXmlDocumentRepository = xhbXmlDocumentRepository;
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
        LOG.info("send()");
        // Get the authentication token
        courtelJson.setToken(getToken());
        // Set the content date
        courtelJson.setContentDate(LocalDateTime.now());
        // Post the json to CaTH
        String errorMessage = postJsonToCath(courtelJson);
        if (!EMPTY_STRING.equals(errorMessage)) {
            LOG.error("Error sending Json: {}", courtelJson.getJson());
            LOG.error("Error from CaTH: {}", errorMessage);
        }
    }

    protected String getToken() {
        LOG.info("getToken()");
        if (CathUtils.isApimEnabled()) {
            return getOAuth2Helper().getAccessToken();
        }
        return EMPTY_STRING;
    }

    @SuppressWarnings("squid:S2142")
    protected String postJsonToCath(CourtelJson courtelJson) {
        LOG.debug("postJsonToCath()");
        String cathUri = CathUtils.getApimUri();
        LOG.debug("cathUri - {}", cathUri);
        HttpRequest httpRequest = CathUtils.getHttpPostRequest(cathUri, courtelJson);

        try {
            HttpResponse<?> httpResponse =
                HttpClient.newHttpClient().send(httpRequest, BodyHandlers.ofString());
            Integer statusCode = httpResponse.statusCode();
            LOG.info("Response status code: {}", statusCode);
            String response = httpResponse.body().toString();
            LOG.debug("Response: {}", response);
        } catch (IOException | InterruptedException | RuntimeException exception) {
            LOG.error("Error in postJsonToCath(): {}", exception.getMessage());
            return exception.getMessage();
        }
        return EMPTY_STRING;
    }
    
    protected Optional<XhbClobDao> getDocuments() {
        Optional<XhbClobDao> documents = null;
        List<XhbXmlDocumentDao> xmlDocumentDaoList =
            getXhbXmlDocumentRepository().findJsonDocuments();

        for (XhbXmlDocumentDao document : xmlDocumentDaoList) {
            documents = getXhbClobRepository().findById(document.getXmlDocumentClobId());
        }

        return documents;
    }

    private OAuth2Helper getOAuth2Helper() {
        if (oauth2Helper == null) {
            this.oauth2Helper = new OAuth2Helper();
        }
        return oauth2Helper;
    }
    
    private XhbXmlDocumentRepository getXhbXmlDocumentRepository() {
        if (xhbXmlDocumentRepository == null) {
            xhbXmlDocumentRepository = new XhbXmlDocumentRepository(entityManager);
        }
        return xhbXmlDocumentRepository;
    }
    
    private XhbClobRepository getXhbClobRepository() {
        if (xhbClobRepository == null) {
            xhbClobRepository = new XhbClobRepository(entityManager);
        }
        return xhbClobRepository;
    }
}
