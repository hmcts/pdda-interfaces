package uk.gov.hmcts.pdda.business.services.cpp;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.AbstractControllerBean;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundDao;
import uk.gov.hmcts.pdda.business.services.cpplist.CppListControllerBean;
import uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3.CppStagingInboundControllerBean;
import uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3.CppStagingInboundHelper;
import uk.gov.hmcts.pdda.business.services.formatting.AbstractXmlMergeUtils;
import uk.gov.hmcts.pdda.business.services.validation.ValidationService;
import uk.gov.hmcts.pdda.business.services.validation.sax.FileEntityResolver;
import uk.gov.hmcts.pdda.business.services.validation.sax.SaxValidationService;

public abstract class AbstractCppInitialProcessingControllerBean extends AbstractControllerBean {

    private static final Logger LOG =
        LoggerFactory.getLogger(CppInitialProcessingControllerBean.class);

    protected static final String ENTERED = " : entered";
    protected static final String BATCH_USERNAME = "CPPX_SCHEDULED_JOB";
    protected static final String ROLLBACK_MSG = ": failed! Transaction Rollback";

    private CppStagingInboundControllerBean cppStagingInboundControllerBean;
    private CppListControllerBean cppListControllerBean;
    private CppStagingInboundHelper cppStagingInboundHelper;
    private AbstractXmlMergeUtils xmlUtils;
    private ValidationService validationService;

    public AbstractCppInitialProcessingControllerBean() {
        super();
    }

    public AbstractCppInitialProcessingControllerBean(EntityManager entityManager) {
        super(entityManager);
    }

    public abstract boolean processValidatedDocument(XhbCppStagingInboundDao thisDoc);

    protected void handleStuckDocuments(XhbCppStagingInboundDao doc) {
        try {
            if (doc != null) {
                // Now attempt to process the validated document - i.e. examine XML and
                // insert
                // into downstream database tables
                if (processValidatedDocument(doc)) {
                    LOG.debug("The validated document has been successfully processed");
                } else {
                    LOG.debug("The validated document has failed to be processed. Check database "
                        + "error column for this record for further details.");
                }
            }

            // Not throwing any exceptions here as a failure processing one document should
            // not impact others
        } catch (Exception e) {
            LOG.error(
                "Error processing a document that has already been validated. Turn debugging on for more info."
                    + " Error: " + e.getMessage());
        }
    }


    /**
     * Retrieves a reference to the cppStagingInboundControllerBean.
     * 
     * @return CppStagingInboundControllerBean
     */
    protected CppStagingInboundControllerBean getCppStagingInboundControllerBean() {
        if (cppStagingInboundControllerBean == null) {
            cppStagingInboundControllerBean = new CppStagingInboundControllerBean(
                getEntityManager(), getXhbConfigPropRepository(), getCppStagingInboundHelper(),
                getXhbCourtRepository(), getXhbClobRepository(), getXhbBlobRepository(),
                getValidationService());
        }
        return cppStagingInboundControllerBean;
    }

    /**
     * Retrieves a reference to the cppListControllerBean.
     * 
     * @return CppListControllerBean
     */
    protected CppListControllerBean getCppListControllerBean() {
        if (cppListControllerBean == null) {
            cppListControllerBean = new CppListControllerBean(getEntityManager());
        }
        return cppListControllerBean;
    }

    protected CppStagingInboundHelper getCppStagingInboundHelper() {
        if (cppStagingInboundHelper == null) {
            cppStagingInboundHelper = new CppStagingInboundHelper(getEntityManager());
        }
        return cppStagingInboundHelper;
    }

    protected ValidationService getValidationService() {
        if (validationService == null) {
            validationService = new SaxValidationService(new FileEntityResolver());
        }
        return validationService;
    }

    protected AbstractXmlMergeUtils getXmlUtils() {
        return xmlUtils;
    }

    protected void setXmlUtils(AbstractXmlMergeUtils xmlUtils) {
        this.xmlUtils = xmlUtils;
    }
}
