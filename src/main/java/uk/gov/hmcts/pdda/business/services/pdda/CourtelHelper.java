package uk.gov.hmcts.pdda.business.services.pdda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;

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

    protected static final String[] VALID_LISTS = {"DL", "DLP", "FL", "WL"};

    private final XhbClobRepository xhbClobRepository;

    public CourtelHelper(XhbClobRepository xhbClobRepository) {
        this.xhbClobRepository = xhbClobRepository;
    }

    public boolean isCourtelSendableDocument(String documentType) {
        return Arrays.asList(VALID_LISTS).contains(documentType);
    }

    public void writeToCourtel(final Long xmlDocumentClobId) {
        Optional<XhbClobDao> clobDao = xhbClobRepository.findById(xmlDocumentClobId);
        if (clobDao.isPresent()) {
            LOG.debug("Fetched clob for xmlDocumentClobId {}", xmlDocumentClobId);
            //
            //             Fetch the xml clob via the XhbXmlDocument.xmlDocumentClobId.
            //
            //             if (not already exists in XhbCourtelList) and (clob is not empty or no longer exists)
            // {
            //
            //                         Create the XhbCourtelListBasicValue
            //
            // set SentToCourtel=’N’  and NumSendAttempts=0
            //
            // call  XhbCourtelListRepository.Save
        }
    }
}
