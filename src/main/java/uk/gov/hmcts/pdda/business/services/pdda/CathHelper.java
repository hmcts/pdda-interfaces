package uk.gov.hmcts.pdda.business.services.pdda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pdda.hb.jpa.RepositoryUtil;
import jakarta.persistence.EntityManager;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private static final Map<String, String> WELSH_DAYS_OF_WEEK = Map.of(
        "Dydd Llun", "Monday",
        "Dydd Mawrth", "Tuesday",
        "Dydd Mercher", "Wednesday",
        "Dydd Iau", "Thursday",
        "Dydd Gwener", "Friday",
        "Dydd Sadwrn", "Saturday",
        "Dydd Sul", "Sunday"
    );
    private static final Map<String, String> WELSH_MONTHS = Map.ofEntries(
        Map.entry("Ionawr", "January"),
        Map.entry("Chwefror", "February"),
        Map.entry("Mawrth", "March"),
        Map.entry("Ebrill", "April"),
        Map.entry("Mai", "May"),
        Map.entry("Mehefin", "June"),
        Map.entry("Gorffennaf", "July"),
        Map.entry("Awst", "August"),
        Map.entry("Medi", "September"),
        Map.entry("Hydref", "October"),
        Map.entry("Tachwedd", "November"),
        Map.entry("Rhagfyr", "December")
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
            // Get date from json clob for lists
            LocalDate dateTime = getListDateFromClob(xhbXmlDocumentDao.getXmlDocumentClobId(), listType);
            jsonObject.setContentDate(dateTime.atStartOfDay(ZoneOffset.UTC));
            jsonObject.setEndDate(dateTime.atTime(23, 59).atZone(ZoneOffset.UTC));
            
        } else {
            // Get end date from html clob for web pages
            LocalDate dateTime = getHtmlWebPageDateFromClob(xhbXmlDocumentDao.getXmlDocumentClobId(),
                xhbXmlDocumentDao.getDocumentTitle());
            jsonObject.setContentDate(dateTime.atStartOfDay(ZoneOffset.UTC));
            jsonObject.setEndDate(dateTime.atTime(23, 59).atZone(ZoneOffset.UTC));
        }
        
        // Populate shared fields
        jsonObject.setCrestCourtId(xhbCourtDao.getCrestCourtId());
        jsonObject.setDocumentName(xhbXmlDocumentDao.getDocumentTitle());
        
        // Populate the language based on web page contents, therefore cp and xhibit web pages are handled the same
        if (xhbXmlDocumentDao.getDocumentType().equals("IWP")) {
            jsonObject.setLanguage(getLanguageFromWebPageContent(xhbXmlDocumentDao.getXmlDocumentClobId()));
        } else {
            // Lists are always in English
            jsonObject.setLanguage(Language.ENGLISH);
        }
        
        return jsonObject;
    }
    
    private LocalDate getHtmlWebPageDateFromClob(Long clobId, String documentTitle) {
        // Get the html clob
        Optional<XhbClobDao> xhbClobDao = getXhbClobRepository().findByIdSafe(clobId);
        
        if (!xhbClobDao.isEmpty()) {
            Document doc = Jsoup.parse(xhbClobDao.get().getClobData());
            Element dateElement = doc.selectFirst("#content-column p");
            String dateContent = dateElement.text(); // i.e: "Monday 1 January 2026 10:15"
           
            DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("EEEE d MMMM yyyy HH:mm");
            
            if (documentTitle.contains("_cy")) {
                // Translate the day to english
                for (Map.Entry<String, String> dayEntry : WELSH_DAYS_OF_WEEK.entrySet()) {
                    if (dateContent.contains(dayEntry.getKey())) {
                        dateContent = dateContent.replace(dayEntry.getKey(), dayEntry.getValue());
                        break;
                    }
                }
                // Translate the month to english
                for (Map.Entry<String, String> monthEntry : WELSH_MONTHS.entrySet()) {
                    if (dateContent.contains(monthEntry.getKey())) {
                        dateContent = dateContent.replace(monthEntry.getKey(), monthEntry.getValue());
                        break;
                    }
                }
                // Return the parsed date in UTC
                return LocalDate.parse(dateContent, formatter);
            } else {
                // Return the parsed date in UTC
                return LocalDate.parse(dateContent, formatter);
            }
        }
        // Default to todays date if any above conditions are not met
        return LocalDate.now();
    }
    
    private LocalDate getListDateFromClob(Long clobId, String listType) {
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
            return LocalDate.parse(endDate);
        }
        // Default to todays date if any above conditions are not met
        return LocalDate.now();
    }
    
    private Language getLanguageFromWebPageContent(Long clobId) {
        Optional<XhbClobDao> xhbClobDao = getXhbClobRepository().findByIdSafe(clobId);
        
        if (!xhbClobDao.isEmpty()) {
            Document doc = Jsoup.parse(xhbClobDao.get().getClobData());
            Element dateElement = doc.selectFirst("#content-column h1");
            String dateContent = dateElement.text();
            
            // Checking the language of the "Daily Court Status" header as this is on all web pages
            if (dateContent.contains("Daily Court Status")) {
                return Language.ENGLISH;
            } else {
                return Language.WELSH;
            }
        }
        // Return English by default if the conditions are not met
        return Language.ENGLISH;
    }
    
    private void transformCpXmlWebPageIntoHtml(XhbXmlDocumentDao xhbXmlDocumentDao) throws TransformerException {
        // Check if the document is Welsh
        boolean isWelsh = xhbXmlDocumentDao.getDocumentTitle().contains("_cy");
        // Get the XML clob data
        Optional<XhbClobDao> xhbClobDao =
            getXhbClobRepository().findByIdSafe(xhbXmlDocumentDao.getXmlDocumentClobId());
        
        if (xhbClobDao.isPresent()) {
            // Initialize the transformer & classloader
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            
            // Get the schema for the HTML transformation
            StringBuilder iwpSchemaPath = new StringBuilder(100);
            iwpSchemaPath.append("config/xml/internet/");
            if (isWelsh) {
                // Welsh transformation
                iwpSchemaPath.append("InternetWebPageTemplate_cy.xsl");
            } else {
                // English transformation
                iwpSchemaPath.append("InternetWebPageTemplate.xsl");
            }
            
            // Set the xsl source
            Source xsltSource =
                new StreamSource(new File(classLoader.getResource(iwpSchemaPath.toString()).getFile()));
            Templates templates = transformerFactory.newTemplates(xsltSource);
            Transformer transformer = templates.newTransformer();
            
            // Transform the XML to HTML
            Source xmlSource = new StreamSource(new StringReader(xhbClobDao.get().getClobData()));
            StringWriter outWriter = TransformerUtils.transformList(transformer, xmlSource);
            
            // Create the new HTML clob
            XhbClobDao htmlClobDao = new XhbClobDao();
            
            if (isWelsh) {
                // Translate the HTML into Welsh
                String welshTranslatedHtml =
                    translateHtmlToWelsh(outWriter.toString(), transformerFactory, classLoader);
                // Save the welsh translated HTML clob data
                htmlClobDao.setClobData(welshTranslatedHtml);
                getXhbClobRepository().savePersist(htmlClobDao);
            } else {
                // Save the english HTML clob data
                htmlClobDao.setClobData(outWriter.toString());
                getXhbClobRepository().savePersist(htmlClobDao);
            }
            
            // Re-point the xhb_xml_document to the new HTML clob
            xhbXmlDocumentDao.setXmlDocumentClobId(htmlClobDao.getClobId());
            getXhbXmlDocumentRepository().update(xhbXmlDocumentDao);
        }
    }
    
    private String translateHtmlToWelsh(String htmlData,
        TransformerFactory transformerFactory, ClassLoader classLoader) throws TransformerException {
        String cyTranslatorXslPath = "config/xml/internet/cy_translator.xsl";
        // Set the xsl source
        Source translatorXslSource =
            new StreamSource(new File(classLoader.getResource(cyTranslatorXslPath).getFile()));
        Templates templates = transformerFactory.newTemplates(translatorXslSource);
        Transformer transformer = templates.newTransformer();
        // Translate the HTML text nodes to Welsh using the keys in the translations.xml
        Source htmlSource = new StreamSource(new StringReader(htmlData));
        StringWriter outWriter = TransformerUtils.transformList(transformer, htmlSource);
        
        return outWriter.toString();
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
