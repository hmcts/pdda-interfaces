package uk.gov.hmcts.pdda.business.services.pdda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pdda.hb.jpa.RepositoryUtil;
import jakarta.persistence.EntityManager;
import org.json.JSONObject;
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
import uk.gov.hmcts.pdda.business.services.formatting.TransformerUtils;
import uk.gov.hmcts.pdda.business.services.pdda.cath.CathOAuth2Helper;
import uk.gov.hmcts.pdda.business.services.pdda.cath.CathUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

/**

 * Title: CathHelper.


 * Description:


 * Copyright: Copyright (c) 2024


 * Company: CGI

 * @author Luke Gittins
 * @version 1.0
 */
@SuppressWarnings("PMD")
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
    private static final String EMPTY = "";
    private static final Map<String, String> VALID_LISTS = Map.of(
        "Daily List", "DL",
        "Firm List", "FL",
        "Warned List", "WL"
    );
    
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
        
        // Generate the HttpRequest based on the type of Document
        HttpRequest httpRequest;
        if (courtelJson instanceof ListJson) {
            httpRequest = CathUtils.getListHttpPostRequest(cathUri, courtelJson);
        } else {
            httpRequest = CathUtils.getWebPageHttpPostRequest(cathUri, courtelJson);
        }
        
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
    
    protected void processDocuments() throws TransformerException {
        List<XhbXmlDocumentDao> documents = getDocuments(NOT_PROCESSED_STATUS);
        // Check if documents are null to prevent process from kicking off further
        if (!documents.isEmpty()) {
            updateAndSend(documents, FAILED_STATUS_ONE);
        }
    }

    protected void processFailedDocuments() throws TransformerException {
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

    public void updateAndSend(List<XhbXmlDocumentDao> documents, String failedStatus) throws TransformerException {
        for (XhbXmlDocumentDao document : documents) {
            updateDocumentStatus(document, IN_PROGRESS_STATUS);
            if (document.getDocumentType().equals("IWP") && document.getDocumentTitle().contains("WebPage_")) {
                transformCpXmlWebPageIntoHtml(document);
            }
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
        String listType = checkForListDocument(xhbXmlDocumentDao);
        
        if (EMPTY.equals(listType)) {
            return populateJsonObject(new WebPageJson(), xhbXmlDocumentDao,
                xhbCourtDao.get(), listType);
        } else {
            return populateJsonObject(new ListJson(), xhbXmlDocumentDao, xhbCourtDao.get(), listType);
        }
    }

    private String checkForListDocument(XhbXmlDocumentDao xhbXmlDocumentDao) {
        for (Map.Entry<String, String> listName : VALID_LISTS.entrySet()) {
            if (xhbXmlDocumentDao.getDocumentTitle().contains(listName.getKey())) {
                return listName.getValue();
            }
        }
        return "";
    }
    
    private CourtelJson populateJsonObject(CourtelJson jsonObject,
        XhbXmlDocumentDao xhbXmlDocumentDao, XhbCourtDao xhbCourtDao, String listType) {
        // Populate type specific fields
        if (jsonObject instanceof ListJson listJson) {
            listJson.setListType(ListType.fromString(listType));
            // Get end date from json clob for lists
            jsonObject.setEndDate(getListEndDateFromClob(xhbXmlDocumentDao.getXmlDocumentClobId(), listType));
        } else {
            try {
                // Get end date from html clob for web pages
                jsonObject.setEndDate(getWebPageEndDateFromClob(xhbXmlDocumentDao.getXmlDocumentClobId()));
            } catch (ParserConfigurationException | SAXException | IOException e) {
                LOG.debug("Error getting endDate from Clob: {}", e.getMessage());
            }
        }
        // Populate shared fields
        jsonObject.setCrestCourtId(xhbCourtDao.getCrestCourtId());
        jsonObject.setContentDate(LocalDate.now().atStartOfDay(ZoneOffset.UTC));
        jsonObject.setLanguage(Language.ENGLISH);
        jsonObject.setDocumentName(xhbXmlDocumentDao.getDocumentTitle());
        
        return jsonObject;
    }
    
    private ZonedDateTime getWebPageEndDateFromClob(Long clobId) 
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
    
    private ZonedDateTime getListEndDateFromClob(Long clobId, String listType) {
        Optional<XhbClobDao> xhbClobDao = getXhbClobRepository().findByIdSafe(clobId);
        
        // Get the list type root node for JSON parsing
        String jsonListRootNode = "";
        switch (listType) {
            case "DL" -> jsonListRootNode = "DailyList";
            case "FL" -> jsonListRootNode = "FirmList";
            case "WL" -> jsonListRootNode = "WarnedList";
            default -> LOG.debug("Unknown List Type detected");
        }
        
        if (!xhbClobDao.isEmpty()) {
            JSONObject obj = new JSONObject(xhbClobDao.get().getClobData());
            String endDate = obj.getJSONObject(jsonListRootNode)
                                .getJSONObject("ListHeader")
                                .getString("EndDate");
            return LocalDate.parse(endDate).atTime(23, 59).atZone(ZoneOffset.UTC);
        }
        // Default to todays date if any above conditions are not met
        return LocalDate.now().atTime(23, 59).atZone(ZoneOffset.UTC);
    }
    
    private void transformCpXmlWebPageIntoHtml(XhbXmlDocumentDao xhbXmlDocumentDao) throws TransformerException {
        StringBuilder iwpSchemaPath = new StringBuilder(100);
        iwpSchemaPath.append("config/xml/internet/InternetWebPageTemplate.xsl");
        
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        
        Source xsltSource =
            new StreamSource(new File(classLoader.getResource(iwpSchemaPath.toString()).getFile()));
        Templates templates = transformerFactory.newTemplates(xsltSource);
        Transformer cutDowntransformer = templates.newTransformer();
        
        // Get the XML clob data
        Optional<XhbClobDao> xhbClobDao =
            getXhbClobRepository().findByIdSafe(xhbXmlDocumentDao.getXmlDocumentClobId());
        
        if (xhbClobDao.isPresent()) {
            Source xmlSource = new StreamSource(new StringReader(xhbClobDao.get().getClobData()));
            // Transform the XML to HTML
            StringWriter outWriter = TransformerUtils.transformList(cutDowntransformer, xmlSource);
            
            // Save the new HTML clob data
            XhbClobDao htmlClobDao = new XhbClobDao();
            htmlClobDao.setClobData(outWriter.toString());
            getXhbClobRepository().savePersist(htmlClobDao);
            
            // Re-point the xhb_xml_document to the new HTML clob
            xhbXmlDocumentDao.setXmlDocumentClobId(htmlClobDao.getClobId());
            getXhbXmlDocumentRepository().update(xhbXmlDocumentDao);
        }
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
