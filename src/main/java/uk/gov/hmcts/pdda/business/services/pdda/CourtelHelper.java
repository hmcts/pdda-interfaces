package uk.gov.hmcts.pdda.business.services.pdda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.CourtelJson;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.ListJson;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentDao;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
public class CourtelHelper {

    private static final Logger LOG = LoggerFactory.getLogger(CourtelHelper.class);
    private static final String NO = "N";

    private static final String CONFIG_COURTEL_MAX_RETRY = "COURTEL_MAX_RETRY";
    private static final String CONFIG_MESSAGE_LOOKUP_DELAY = "MESSAGE_LOOKUP_DELAY";
    private static final String CONFIG_COURTEL_LIST_AMOUNT = "COURTEL_LIST_AMOUNT";
    protected static final String[] VALID_LISTS = {"DL", "DLP", "FL", "WL"};
    private static final Integer SECONDS_IN_A_DAY = 86_400;

    private final XhbClobRepository xhbClobRepository;
    private final XhbCourtelListRepository xhbCourtelListRepository;
    private final XhbXmlDocumentRepository xhbXmlDocumentRepository;
    private final XhbConfigPropRepository xhbConfigPropRepository;
    private ConfigPropMaintainer configPropMaintainer;

    private final BlobHelper blobHelper;
    private CathHelper cathHelper;

    public CourtelHelper(XhbClobRepository xhbClobRepository,
        XhbCourtelListRepository xhbCourtelListRepository,
        XhbXmlDocumentRepository xhbXmlDocumentRepository, BlobHelper blobHelper,
        XhbConfigPropRepository xhbConfigPropRepository) {
        this.xhbClobRepository = xhbClobRepository;
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
        Optional<XhbClobDao> clobDao = xhbClobRepository.findById(xmlDocumentClobId);
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
                xhbCourtelListDao.setNumSendAttempts(Integer.valueOf(0));
                // Write to Courtel
                xhbCourtelListRepository.save(xhbCourtelListDao);
            }
        }
    }

    private Integer getXmlDocumentIdForClobId(final Long xmlDocumentClobId) {
        // Get the latest xmlDocumentId
        Optional<XhbXmlDocumentDao> xmlDocumentList =
            xhbXmlDocumentRepository.findByXmlDocumentClobId(xmlDocumentClobId);
        if (xmlDocumentList.isPresent()) {
            Integer xmlDocumentId = xmlDocumentList.get().getXmlDocumentId();
            LOG.debug("Fetched XmlDocumentId {}", xmlDocumentId);
            Optional<XhbCourtelListDao> xhbCourtelListDao =
                xhbCourtelListRepository.findByXmlDocumentId(xmlDocumentId);
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
        return xhbCourtelListRepository.findCourtelList(
            getConfigPropValue(CONFIG_COURTEL_MAX_RETRY),
            getIntervalValue(getConfigPropValue(CONFIG_MESSAGE_LOOKUP_DELAY)),
            LocalDateTime.now().plusMinutes(getConfigPropValue(CONFIG_COURTEL_LIST_AMOUNT)));
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
        String json = getCathHelper().generateJsonString(
            getCathHelper().populateCourtelListBlob(xhbCourtelListDao),
            getJsonObjectByDocType(xhbCourtelListDao));
        getCathHelper().send(json);
    }

    private CourtelJson getJsonObjectByDocType(XhbCourtelListDao xhbCourtelListDao) {
        Optional<XhbXmlDocumentDao> xhbXmlDocumentDao =
            xhbXmlDocumentRepository.findById(xhbCourtelListDao.getXmlDocumentId());

        if (!xhbXmlDocumentDao.isEmpty()) {
            ListJson listJson = new ListJson();
            listJson.setListType(xhbXmlDocumentDao.get().getDocumentType());
            listJson.setCourtId(xhbXmlDocumentDao.get().getCourtId());
            listJson.setContentDate(LocalDateTime.now());
            listJson.setProvenance("UK");
            listJson.setLanguage("en");
            return new ListJson();
        }
        return new CourtelJson();
    }

    protected ConfigPropMaintainer getConfigPropMaintainer() {
        if (configPropMaintainer == null) {
            configPropMaintainer = new ConfigPropMaintainer(xhbConfigPropRepository);
        }
        return configPropMaintainer;
    }

    private CathHelper getCathHelper() {
        if (cathHelper == null) {
            this.cathHelper = new CathHelper(blobHelper);
        }
        return cathHelper;
    }
}
