package uk.gov.hmcts.pdda.business.services.pdda;

import com.pdda.hb.jpa.EntityManagerUtil;
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
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.CourtelJson;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.Language;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.ListJson;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.ListType;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.WebPageJson;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentDao;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentRepository;

import java.io.IOException;
import java.io.StringReader;
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
 * Title: CourtelHelper.
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
@SuppressWarnings({"PMD.NullAssignment", "PMD.TooManyMethods", "PMD.CouplingBetweenObjects", "PMD.ExcessiveImports",
    "PMD.CognitiveComplexity", "PMD.GodClass"})
public class CourtelHelper {

    private static final Logger LOG = LoggerFactory.getLogger(CourtelHelper.class);
    private static final String NO = "N";

    private static final String CONFIG_COURTEL_MAX_RETRY = "COURTEL_MAX_RETRY";
    private static final String CONFIG_MESSAGE_LOOKUP_DELAY = "MESSAGE_LOOKUP_DELAY";
    private static final String CONFIG_COURTEL_LIST_AMOUNT = "COURTEL_LIST_AMOUNT";
    protected static final String[] VALID_LISTS = {"DL", "DLP", "FL", "WL"};
    private static final Integer SECONDS_IN_A_DAY = 86_400;

    private XhbClobRepository xhbClobRepository;
    private XhbCourtRepository xhbCourtRepository;
    private XhbCourtelListRepository xhbCourtelListRepository;
    private XhbXmlDocumentRepository xhbXmlDocumentRepository;
    private XhbConfigPropRepository xhbConfigPropRepository;
    private ConfigPropMaintainer configPropMaintainer;
    private EntityManager entityManager;

    private final BlobHelper blobHelper;
    private CathHelper cathHelper;

    public CourtelHelper(XhbClobRepository xhbClobRepository,
        XhbCourtelListRepository xhbCourtelListRepository,
        XhbXmlDocumentRepository xhbXmlDocumentRepository, BlobHelper blobHelper,
        XhbConfigPropRepository xhbConfigPropRepository, XhbCourtRepository xhbCourtRepository) {
        this.xhbClobRepository = xhbClobRepository;
        this.xhbCourtRepository = xhbCourtRepository;
        this.xhbCourtelListRepository = xhbCourtelListRepository;
        this.xhbXmlDocumentRepository = xhbXmlDocumentRepository;
        this.blobHelper = blobHelper;
        this.xhbConfigPropRepository = xhbConfigPropRepository;
    }

    public boolean isCourtelSendableDocument(String documentType) {
        return Arrays.asList(VALID_LISTS).contains(documentType);
    }

    public void writeToCourtel(final Long xmlDocumentClobId, final Long blobId) {
        // Get the clob data
        Optional<XhbClobDao> clobDao = getXhbClobRepository().findByIdSafe(xmlDocumentClobId);
        if (clobDao.isPresent()) {
            LOG.debug("Fetched clob for xmlDocumentClobId {}", xmlDocumentClobId);
            // Get the xmlDocumentId
            Integer xmlDocumentId = getXmlDocumentIdForClobId(xmlDocumentClobId);
            if (xmlDocumentId != null) {
                XhbCourtelListDao xhbCourtelListDao = new XhbCourtelListDao();
                xhbCourtelListDao.setXmlDocumentId(xmlDocumentId);
                xhbCourtelListDao.setXmlDocumentClobId(xmlDocumentClobId);
                xhbCourtelListDao.setBlobId(blobId);
                xhbCourtelListDao.setSentToCourtel(NO);
                xhbCourtelListDao.setNumSendAttempts(0);
                // Write to Courtel
                getXhbCourtelListRepository().save(xhbCourtelListDao);
            }
        }
    }

    private Integer getXmlDocumentIdForClobId(final Long xmlDocumentClobId) {
        // Get the latest xmlDocumentId
        Optional<XhbXmlDocumentDao> xmlDocumentList =
            getXhbXmlDocumentRepository().findByXmlDocumentClobId(xmlDocumentClobId);
        if (xmlDocumentList.isPresent()) {
            Integer xmlDocumentId = xmlDocumentList.get().getXmlDocumentId();
            LOG.debug("Fetched XmlDocumentId {}", xmlDocumentId);
            Optional<XhbCourtelListDao> xhbCourtelListDao =
                getXhbCourtelListRepository().findByXmlDocumentId(xmlDocumentId);
            if (xhbCourtelListDao.isPresent()) {
                LOG.debug("XhbCourtelList (id={}) already exists for XmlDocumentId {}",
                    xhbCourtelListDao.get().getCourtelListId(), xmlDocumentId);
            } else {
                return xmlDocumentId;
            }
        }
        return null;
    }

    public List<XhbCourtelListDao> getCourtelList() {
        return getXhbCourtelListRepository().findCourtelList(
            getConfigPropValue(CONFIG_COURTEL_MAX_RETRY),
            getIntervalValue(getConfigPropValue(CONFIG_MESSAGE_LOOKUP_DELAY)),
            getConfigPropValue(CONFIG_COURTEL_LIST_AMOUNT));
    }

    private Integer getConfigPropValue(String value) {
        try {
            String propertyValue = getConfigPropMaintainer().getPropertyValue(value);
            if (propertyValue != null) {
                Integer maxRetry = Integer.parseInt(propertyValue);
                LOG.error("{} = {}", value, propertyValue);
                return maxRetry;
            } else {
                LOG.error("{} is null", value);
            }
        } catch (Exception ex) {
            LOG.error("{} contains non-numeric data", value);
        }
        return null;
    }

    private Integer getIntervalValue(Integer messageLookupDelay) {
        return messageLookupDelay / SECONDS_IN_A_DAY;
    }

    public void sendCourtelList(XhbCourtelListDao xhbCourtelListDao) {
        // Populate the blob
        xhbCourtelListDao.setBlob(blobHelper.getBlob(xhbCourtelListDao.getBlobId()));
        // Get the Courtel Json object
        CourtelJson courtelJson = getJsonObjectByDocType(xhbCourtelListDao);
        if (courtelJson != null) {
            // Set the Json string
            courtelJson.setJson(getCathHelper().generateJsonString(xhbCourtelListDao, courtelJson));
            // Send the Json to CaTH
            getCathHelper().send(courtelJson);
        }
    }

    private CourtelJson getJsonObjectByDocType(XhbCourtelListDao xhbCourtelListDao) {
        Optional<XhbXmlDocumentDao> xhbXmlDocumentDao =
            getXhbXmlDocumentRepository().findByIdSafe(xhbCourtelListDao.getXmlDocumentId());
        if (xhbXmlDocumentDao.isEmpty()) {
            LOG.debug("No XhbXmlDocumentDao found for id {}", xhbCourtelListDao.getXmlDocumentId());
            return null;
        }
        Optional<XhbCourtDao> xhbCourtDao =
            getXhbCourtRepository().findByIdSafe(xhbXmlDocumentDao.get().getCourtId());
        if (xhbCourtDao.isEmpty()) {
            LOG.debug("No XhbCourtDao found for id {}", xhbXmlDocumentDao.get().getCourtId());
            return null;
        }
        // Check Document Type and create appropriate object
        if (Arrays.asList(VALID_LISTS).contains(xhbXmlDocumentDao.get().getDocumentType())) {
            return populateJsonObject(new ListJson(), xhbXmlDocumentDao.get(), xhbCourtDao.get());
        } else {
            return populateJsonObject(new WebPageJson(), xhbXmlDocumentDao.get(),
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

    protected ConfigPropMaintainer getConfigPropMaintainer() {
        if (configPropMaintainer == null) {
            configPropMaintainer = new ConfigPropMaintainer(getXhbConfigPropRepository());
        }
        return configPropMaintainer;
    }

    protected CathHelper getCathHelper() {
        if (cathHelper == null) {
            this.cathHelper = new CathHelper(getEntityManager(),
                getXhbXmlDocumentRepository(), getXhbClobRepository(), getXhbCourtRepository());
        }
        return cathHelper;
    }

    private void clearRepositories() {
        this.xhbClobRepository = null;
        this.xhbCourtRepository = null;
        this.xhbCourtelListRepository = null;
        this.xhbXmlDocumentRepository = null;
        this.xhbConfigPropRepository = null;
    }

    private EntityManager getEntityManager() {
        if (!EntityManagerUtil.isEntityManagerActive(entityManager)) {
            clearRepositories();
            LOG.debug("getEntityManager() - Creating new entityManager");
            entityManager = EntityManagerUtil.getEntityManager();
        }
        return entityManager;
    }

    protected XhbCourtelListRepository getXhbCourtelListRepository() {
        if (!RepositoryUtil.isRepositoryActive(xhbCourtelListRepository)) {
            xhbCourtelListRepository = new XhbCourtelListRepository(getEntityManager());
        }
        return xhbCourtelListRepository;
    }

    protected XhbCourtRepository getXhbCourtRepository() {
        if (!RepositoryUtil.isRepositoryActive(xhbCourtRepository)) {
            xhbCourtRepository = new XhbCourtRepository(getEntityManager());
        }
        return xhbCourtRepository;
    }
    
    protected XhbClobRepository getXhbClobRepository() {
        if (!RepositoryUtil.isRepositoryActive(xhbClobRepository)) {
            xhbClobRepository = new XhbClobRepository(getEntityManager());
        }
        return xhbClobRepository;
    }
    
    protected XhbConfigPropRepository getXhbConfigPropRepository() {
        if (!RepositoryUtil.isRepositoryActive(xhbConfigPropRepository)) {
            xhbConfigPropRepository = new XhbConfigPropRepository(getEntityManager());
        }
        return xhbConfigPropRepository;
    }
    
    protected XhbXmlDocumentRepository getXhbXmlDocumentRepository() {
        if (!RepositoryUtil.isRepositoryActive(xhbXmlDocumentRepository)) {
            xhbXmlDocumentRepository = new XhbXmlDocumentRepository(getEntityManager());
        }
        return xhbXmlDocumentRepository;
    }
}
