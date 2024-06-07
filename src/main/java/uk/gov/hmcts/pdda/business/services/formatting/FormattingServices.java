package uk.gov.hmcts.pdda.business.services.formatting;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import uk.gov.hmcts.framework.services.CsServices;
import uk.gov.hmcts.framework.util.DateTimeUtilities;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropDao;
import uk.gov.hmcts.pdda.business.entities.xhbformatting.XhbFormattingDao;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentDao;
import uk.gov.hmcts.pdda.business.exception.formatting.FormattingException;
import uk.gov.hmcts.pdda.business.services.pdda.BlobHelper;
import uk.gov.hmcts.pdda.business.services.pdda.CourtelHelper;
import uk.gov.hmcts.pdda.business.vos.formatting.FormattingValue;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPathExpressionException;

/**
 * <p>
 * Title: Formatting of xml using passed in parameters consisting of a number of xsls.
 * </p>
 * <p>
 * Description: An XML pipeline is created to transform the inputted xml into formatted xml/pdf
 * depending on the MimeType parameter.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Bal Bhamra
 * @version $Id: FormattingServices.java,v 1.14 2014/06/20 16:49:28 atwells Exp $
 */

public class FormattingServices extends FormattingServicesProcessing {
    // Logging
    private static final Logger LOG = LoggerFactory.getLogger(FormattingServices.class);
    private final CourtelHelper courtelHelper;
    
    
    // Date Format for java date
    private static final String MERGE_CUT_OFF_TIME = "MERGE_CUT_OFF_TIME";
    private static final String PDDA_SWITCHER = "PDDA_SWITCHER";
    private static final String FORMATTING_LIST_DELAY = "FORMATTING_LIST_DELAY";

    public FormattingServices(EntityManager entityManager, CourtelHelper courtelHelper, BlobHelper blobHelper) {
        super(entityManager, blobHelper);
        this.courtelHelper = courtelHelper;
    }
    
    /**
     * This method is responsible for transforming the information from the reader using xslts dependant
     * on the FormattingParameters and writes the result to Output stream.
     * 
     * @param formattingValue FormattingValue
     * @throws FormattingException Exception
     */
    public void processDocument(FormattingValue formattingValue, final EntityManager entityManager) {
        setEntityManager(entityManager);
        if (FormattingServiceUtils.isPddaOnly(getXhbConfigPropRepository().findByPropertyName(PDDA_SWITCHER))) {
            LOG.debug("isPDDAOnly() - true");
        } else {
            LOG.debug("isPDDAOnly() - false");
            setXmlUtils(null);
            return;
        }
        try {
            // Get the xmlUtils type
            setXmlUtils(getXmlUtils(formattingValue.getDocumentType()));

            if (FormattingServiceUtils.isInactiveOnPdda(formattingValue)) {
                LOG.debug("{} is flagged as Inactive on PDDA", formattingValue.getDocumentType());
            } else if (IWP.equals(formattingValue.getDocumentType()) && isMergeAllowed()) {
                processIwpDocument(formattingValue, getTranslationBundles().toXml());
            } else if (FormattingServiceUtils.isProcessingList(formattingValue)) {
                processListDocument(formattingValue, getTranslationBundles().toXml());
            } else {
                TransformerUtils.transform(getXslServices(), getFormattingConfig(), formattingValue,
                    getTranslationBundles().toXml(), null);
            }

        } catch (TransformerException | ParserConfigurationException | TransformerFactoryConfigurationError
            | SAXException | IOException e) {
            throw new FormattingException(TRANSFORMATION_ERROR, e);
        }
        
        if (courtelHelper.isCourtelSendableDocument(formattingValue.getDocumentType())) {
            courtelHelper.writeToCourtel(formattingValue.getXmlDocumentClobId());
        }
    }

   
    /**
     * Get the next document for processing.
     * 
     * @return XhbFormattingDAO
     */
    public XhbFormattingDao getNextFormattingDocument() {
        // Get the New documents
        String formatStatus = "ND";
        String newFormatStatus = "FD";
        XhbFormattingDao dao = getNextDocumentFromList(formatStatus);

        // If we dont have a dao yet, try the Formatting Error documents
        if (dao == null) {
            formatStatus = "FE";
            newFormatStatus = "NF";
            dao = getNextDocumentFromList(formatStatus);
        }

        // If we've found a document then update the status
        if (dao != null) {
            LOG.debug("getNextDocument() - FormattingId={}", dao.getFormattingId());
            if (dao.getFormattedDocumentBlobId() == null) {
                Long blobId = createBlob(FormattingServiceUtils.getEmptyByteArray());
                dao.setFormattedDocumentBlobId(blobId);
            }
            dao.setFormatStatus(newFormatStatus);
            Optional<XhbFormattingDao> savedDao = getXhbFormattingRepository().update(dao);
            if (savedDao.isPresent()) {
                dao = savedDao.get();
            }
        }
        return dao;
    }

    private XhbFormattingDao getNextDocumentFromList(String formatStatus) {
        List<XhbFormattingDao> formattingDaoList = getXhbFormattingRepository().findByFormatStatus(formatStatus);
        if (!formattingDaoList.isEmpty()) {
            LocalDateTime timeDelay = null;
            // Get the timeDelay for new documents
            if ("ND".equals(formatStatus)) {
                List<XhbConfigPropDao> configs = getXhbConfigPropRepository().findByPropertyName(FORMATTING_LIST_DELAY);
                timeDelay = FormattingServiceUtils.getTimeDelay(configs);
            }
            // Loop through and get the first valid document
            for (XhbFormattingDao formattingDao : formattingDaoList) {
                if (isNextDocumentValid(formattingDao, timeDelay)) {
                    return formattingDao;
                }
            }
        }
        return null;
    }

    private boolean isNextDocumentValid(XhbFormattingDao formattingDao, LocalDateTime timeDelay) {
        // If this is a Formatting Error then try again
        if ("FE".equals(formattingDao.getFormatStatus())) {
            return true;
        } else {
            // Get the lists by their xmlDocumentClobId
            if (formattingDao.getXmlDocumentClobId() != null) {
                List<XhbXmlDocumentDao> xmlDocumentDaoList =
                    getXhbXmlDocumentRepository().findDocumentByClobId(formattingDao.getXmlDocumentClobId(), timeDelay);
                if (!xmlDocumentDaoList.isEmpty()) {
                    // Document is valid
                    return true;
                }
            }
        }

        // Document is invalid
        return false;
    }

    //
    // Utiltity classes
    //

    /**
     * This is a public method so that it can be used by other areas of the CPP interface.
     * 
     * @param documentType String
     * @return AbstractXmlMergeUtils
     */
    public static AbstractXmlMergeUtils getXmlUtils(String documentType) {
        try {
            AbstractXmlMergeUtils xmlUtils = null;
            if (FormattingServiceUtils.isDailyList(documentType)) {
                xmlUtils = new DailyListXmlMergeUtils();
            } else if (FormattingServiceUtils.isFirmList(documentType)) {
                xmlUtils = new FirmListXmlMergeUtils();
            } else if (FormattingServiceUtils.isWarnedList(documentType)) {
                xmlUtils = new WarnedListXmlMergeUtils();
            } else if (IWP.equals(documentType)) {
                xmlUtils = new IwpXmlMergeUtils();
            }
            return xmlUtils;
        } catch (XPathExpressionException e) {
            CsServices.getDefaultErrorHandler().handleError(e, FormattingServices.class);
            throw new FormattingException(" An error has occured during the setup of the xmlUtils process", e);
        }
    }

    /**
     * Returns true if merge is allowed i.e. the time is before the cut off time defined in the db.
     * 
     * @return boolean
     */
    private boolean isMergeAllowed() {
        LOG.debug("About to check if a merge is allowed");
        String[] mergeCutOffTime =
            getXhbConfigPropRepository().findByPropertyName(MERGE_CUT_OFF_TIME).get(0).getPropertyValue().split(":");
        if (mergeCutOffTime != null && mergeCutOffTime.length == 3) {
            final Calendar now = Calendar.getInstance();
            Calendar mergeCutOff = Calendar.getInstance();
            mergeCutOff.set(Calendar.HOUR_OF_DAY, Integer.parseInt(mergeCutOffTime[0]));
            mergeCutOff.set(Calendar.MINUTE, Integer.parseInt(mergeCutOffTime[1]));
            mergeCutOff.set(Calendar.SECOND, Integer.parseInt(mergeCutOffTime[2]));

            LOG.debug("Is {} after {}", DateTimeUtilities.calendarToDate(mergeCutOff),
                DateTimeUtilities.calendarToDate(now));
            return mergeCutOff.after(now);
        }
        return false;
    }
}
