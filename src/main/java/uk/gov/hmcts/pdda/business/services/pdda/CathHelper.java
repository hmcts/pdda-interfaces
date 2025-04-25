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
import uk.gov.hmcts.pdda.business.services.pdda.cath.CathOAuth2Helper;
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
@SuppressWarnings({"PMD.TooManyMethods"})
public class CathHelper {

    private static final Logger LOG = LoggerFactory.getLogger(CathHelper.class);
    private static final String EMPTY_STRING = "";
    private static final Boolean SUCCESS = true;
    private static final Boolean FAILED = false;
    private static final String NOT_PROCESSED_STATUS = "ND";
    private static final String IN_PROGRESS_STATUS = "IP";
    private static final String SUCCESSFUL_STATUS = "SC";
    private static final String FAILED_STATUS_ONE = "F1";
    private static final String FAILED_STATUS_TWO = "F2";
    private static final String FAILED_STATUS_THREE = "F3";
    
    private final EntityManager entityManager;
    private XhbXmlDocumentRepository xhbXmlDocumentRepository;
    private XhbClobRepository xhbClobRepository;

    private OAuth2Helper oauth2Helper;

    public CathHelper(EntityManager entityManager,
        XhbXmlDocumentRepository xhbXmlDocumentRepository,
        XhbClobRepository xhbClobRepository) {
        super();
        this.entityManager = entityManager;
        this.xhbXmlDocumentRepository = xhbXmlDocumentRepository;
        this.xhbClobRepository = xhbClobRepository;
    }

    // JUnit
    public CathHelper(OAuth2Helper oauth2Helper, EntityManager entityManager,
        XhbXmlDocumentRepository xhbXmlDocumentRepository,
        XhbClobRepository xhbClobRepository) {
        this.oauth2Helper = oauth2Helper;
        this.entityManager = entityManager;
        this.xhbXmlDocumentRepository = xhbXmlDocumentRepository;
        this.xhbClobRepository = xhbClobRepository;
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
    
    protected void processDocuments() {
        List<XhbXmlDocumentDao> documents = getDocuments(NOT_PROCESSED_STATUS);
        // Check if documents are null to prevent process from kicking off further
        if (!documents.isEmpty()) {
            updateAndSend(documents, FAILED_STATUS_ONE);
        }
    }

    protected void processFailedDocuments() {
        List<XhbXmlDocumentDao> documents = getDocuments(FAILED_STATUS_ONE);
        // Check if F1 documents are null to prevent process from kicking off further
        if (!documents.isEmpty()) {
            updateAndSend(documents, FAILED_STATUS_TWO);
        }
        documents = getDocuments(FAILED_STATUS_TWO);
        // Check if F2 documents are null to prevent process from kicking off further
        if (!documents.isEmpty()) {
            updateAndSend(documents, FAILED_STATUS_THREE);
        }
    }

    public void updateAndSend(List<XhbXmlDocumentDao> documents, String failedStatus) {
        for (XhbXmlDocumentDao document : documents) {
            updateDocumentStatus(document, IN_PROGRESS_STATUS);
            if (Boolean.TRUE.equals(sendToCath(document))) {
                LOG.debug("Sent successfully");
                updateDocumentStatus(document, SUCCESSFUL_STATUS);
            } else {
                LOG.debug("Sent failed");
                updateDocumentStatus(document, failedStatus);
            }
        }
    }

    private void updateDocumentStatus(XhbXmlDocumentDao document, String status) {
        Integer version = document.getVersion();
        document.setStatus(status);
        getXhbXmlDocumentRepository().update(document);
        version++;
        document.setVersion(version);
    }
    
    private List<XhbXmlDocumentDao> getDocuments(String status) {
        return getXhbXmlDocumentRepository().findJsonDocuments(status);
    }

    public Boolean sendToCath(XhbXmlDocumentDao document) {
        String clobData = getDocumentClob(document);
        if (clobData != null && !"".equals(clobData)) {
            LOG.debug("sendToCath");
            LOG.debug("Sending {} {} to CaTH", document.getDocumentTitle(), clobData);
            return SUCCESS;
        }
        return FAILED;
    }

    private String getDocumentClob(XhbXmlDocumentDao document) {
        Optional<XhbClobDao> xhbClobDao =
            getXhbClobRepository().findById(document.getXmlDocumentClobId());
        if (xhbClobDao.isPresent()) {
            return xhbClobDao.get().getClobData();
        }
        return null;
    }

    private OAuth2Helper getOAuth2Helper() {
        if (oauth2Helper == null) {
            this.oauth2Helper = new CathOAuth2Helper();
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
