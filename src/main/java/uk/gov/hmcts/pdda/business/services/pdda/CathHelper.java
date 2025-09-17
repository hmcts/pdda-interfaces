package uk.gov.hmcts.pdda.business.services.pdda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pdda.hb.jpa.RepositoryUtil;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.CourtelJson;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.Language;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.ListJson;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.ListType;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.WebPageJson;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListDao;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentDao;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentRepository;
import uk.gov.hmcts.pdda.business.services.pdda.cath.CathOAuth2Helper;
import uk.gov.hmcts.pdda.business.services.pdda.cath.CathUtils;

import java.io.IOException;
import java.io.StringReader;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
@SuppressWarnings({"PMD.TooManyMethods", "PMD.CouplingBetweenObjects", "PMD.ExcessiveImports",
    "PMD.CognitiveComplexity", "PMD.GodClass"})
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
    protected static final String[] VALID_LISTS = {"DL", "DLP", "FL", "WL"};
    
    private final EntityManager entityManager;
    private XhbXmlDocumentRepository xhbXmlDocumentRepository;
    private XhbClobRepository xhbClobRepository;
    private XhbCourtRepository xhbCourtRepository;

    private CathOAuth2Helper cathOAuth2Helper;

    public CathHelper(EntityManager entityManager,
        XhbXmlDocumentRepository xhbXmlDocumentRepository,
        XhbClobRepository xhbClobRepository,
        XhbCourtRepository xhbCourtRepository) {
        super();
        this.entityManager = entityManager;
        this.xhbXmlDocumentRepository = xhbXmlDocumentRepository;
        this.xhbClobRepository = xhbClobRepository;
        this.xhbCourtRepository = xhbCourtRepository;
    }

    // JUnit
    public CathHelper(CathOAuth2Helper cathOAuth2Helper, EntityManager entityManager,
        XhbXmlDocumentRepository xhbXmlDocumentRepository,
        XhbClobRepository xhbClobRepository,
        XhbCourtRepository xhbCourtRepository) {
        this.cathOAuth2Helper = cathOAuth2Helper;
        this.entityManager = entityManager;
        this.xhbXmlDocumentRepository = xhbXmlDocumentRepository;
        this.xhbClobRepository = xhbClobRepository;
        this.xhbCourtRepository = xhbCourtRepository;
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
            // Calling the CathOAuth2Helper to use the CaTH specific key vault values
            return getCathOAuth2Helper().getAccessToken();
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
        LOG.debug("sendToCath()");
        String clobData = getDocumentClob(document);
        if (clobData != null && !"".equals(clobData)) {
            LOG.debug("Sending {} {} to CaTH", document.getDocumentTitle(), clobData);
            // Generate the CourtelJson object from the document type
            CourtelJson courtelJson = getJsonObjectByDocType(document);
            // If the courtelJson has been made, set the clob data and send it to CaTH
            if (courtelJson != null) {
                courtelJson.setJson(clobData);
                send(courtelJson);
                return SUCCESS;
            }
        }
        return FAILED;
    }

    private String getDocumentClob(XhbXmlDocumentDao document) {
        Optional<XhbClobDao> xhbClobDao =
            getXhbClobRepository().findByIdSafe(document.getXmlDocumentClobId());
        if (xhbClobDao.isPresent()) {
            return xhbClobDao.get().getClobData();
        }
        return null;
    }
    
    
    protected CourtelJson getJsonObjectByDocType(XhbXmlDocumentDao xhbXmlDocumentDao) {
        Optional<XhbCourtDao> xhbCourtDao =
            getXhbCourtRepository().findByIdSafe(xhbXmlDocumentDao.getCourtId());
        if (xhbCourtDao.isEmpty()) {
            LOG.debug("No XhbCourtDao found for id {}", xhbXmlDocumentDao.getCourtId());
            return null;
        }
        
        // Check Document Type and create appropriate object
        if (Arrays.asList(VALID_LISTS).contains(xhbXmlDocumentDao.getDocumentType())) {
            return populateJsonObject(new ListJson(), xhbXmlDocumentDao, xhbCourtDao.get());
        } else {
            return populateJsonObject(new WebPageJson(), xhbXmlDocumentDao,
                xhbCourtDao.get());
        }
    }

    private CourtelJson populateJsonObject(CourtelJson jsonObject,
        XhbXmlDocumentDao xhbXmlDocumentDao, XhbCourtDao xhbCourtDao) {
        // Populate type specific fields
        if (jsonObject instanceof ListJson listJson) {
            listJson.setListType(ListType.fromString(xhbXmlDocumentDao.getDocumentType()));
        }
        // Populate shared fields
        jsonObject.setCrestCourtId(xhbCourtDao.getCrestCourtId());
        jsonObject.setContentDate(LocalDate.now().atStartOfDay(ZoneOffset.UTC));
        jsonObject.setLanguage(Language.ENGLISH);
        jsonObject.setDocumentName(xhbXmlDocumentDao.getDocumentTitle());
        
        // Fetch and populate the end date from the clob
        try {
            jsonObject.setEndDate(getEndDateFromClob(xhbXmlDocumentDao.getXmlDocumentClobId()));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOG.debug("Error getting endDate from Clob: {}", e.getMessage());
        }
        return jsonObject;
    }
    
    private ZonedDateTime getEndDateFromClob(Long clobId) 
        throws ParserConfigurationException, SAXException, IOException {
        // Get the clob data
        Optional<XhbClobDao> xhbClobDao = getXhbClobRepository().findByIdSafe(clobId);
        if (!xhbClobDao.isEmpty()) {
            // Perform a node search across the clob data and find the end date field
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newDefaultInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(xhbClobDao.get().getClobData()));
            Document document = documentBuilder.parse(inputSource);

            // Get the cs:ListHeader nodes. This is the clob before transformation, so it uses the cs namespace
            Node listHeaderNode = document.getElementsByTagName("cs:ListHeader").item(0);
            
            if (listHeaderNode != null) {
                NodeList listHeaderChildNodes = listHeaderNode.getChildNodes();
                for (int i = 0; i < listHeaderChildNodes.getLength(); i++) {
                    Node node = listHeaderChildNodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE
                        && Objects.equals("cs:EndDate", node.getNodeName())) {
                        String endDate = node.getTextContent();
                        if (endDate != null) {
                            try {
                                // Convert the end date to LocalDateTime and return it
                                return LocalDate.parse(endDate).atTime(23, 59).atZone(ZoneOffset.UTC);
                            } catch (Exception e) {
                                // If there is an error parsing the date, log it and return current date
                                LOG.debug("Error parsing endDate: {}", e.getMessage());
                                return LocalDate.now().atTime(23, 59).atZone(ZoneOffset.UTC);
                            }
                        }
                    }
                }
            }
        }
        // Default to todays date if any above conditions are not met
        return LocalDate.now().atTime(23, 59).atZone(ZoneOffset.UTC);
    }
    
    private CathOAuth2Helper getCathOAuth2Helper() {
        if (cathOAuth2Helper == null) {
            this.cathOAuth2Helper = new CathOAuth2Helper();
        }
        return cathOAuth2Helper;
    }

    private XhbXmlDocumentRepository getXhbXmlDocumentRepository() {
        if (!RepositoryUtil.isRepositoryActive(xhbXmlDocumentRepository)) {
            xhbXmlDocumentRepository = new XhbXmlDocumentRepository(entityManager);
        }
        return xhbXmlDocumentRepository;
    }

    protected XhbCourtRepository getXhbCourtRepository() {
        if (!RepositoryUtil.isRepositoryActive(xhbCourtRepository)) {
            xhbCourtRepository = new XhbCourtRepository(entityManager);
        }
        return xhbCourtRepository;
    }
    
    private XhbClobRepository getXhbClobRepository() {
        if (!RepositoryUtil.isRepositoryActive(xhbClobRepository)) {
            xhbClobRepository = new XhbClobRepository(entityManager);
        }
        return xhbClobRepository;
    }
}
