package uk.gov.hmcts.pdda.business.services.pdda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentDao;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentRepository;

import java.util.Arrays;
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

    protected static final String[] VALID_LISTS = {"DL", "DLP", "FL", "WL"};

    private final XhbClobRepository xhbClobRepository;
    private final XhbCourtelListRepository xhbCourtelListRepository;
    private final XhbXmlDocumentRepository xhbXmlDocumentRepository;

    public CourtelHelper(XhbClobRepository xhbClobRepository, XhbCourtelListRepository xhbCourtelListRepository,
        XhbXmlDocumentRepository xhbXmlDocumentRepository) {
        this.xhbClobRepository = xhbClobRepository;
        this.xhbCourtelListRepository = xhbCourtelListRepository;
        this.xhbXmlDocumentRepository = xhbXmlDocumentRepository;
    }

    public boolean isCourtelSendableDocument(String documentType) {
        return Arrays.asList(VALID_LISTS).contains(documentType);
    }

    public void writeToCourtel(final Long xmlDocumentClobId) {
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
            Optional<XhbCourtelListDao> xhbCourtelListDao = xhbCourtelListRepository.findByXmlDocumentId(xmlDocumentId);
            if (xhbCourtelListDao.isPresent()) {
                LOG.debug("XhbCourtelList (id={}) already exists for XmlDocumentId {}",
                    xhbCourtelListDao.get().getCourtelListId(), xmlDocumentId);
            } else {
                return xmlDocumentId;
            }
        }
        return null;
    }
}
