package uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3;

import jakarta.ejb.EJBException;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.AbstractControllerBean;
import uk.gov.hmcts.pdda.business.entities.xhbblob.XhbBlobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundDao;
import uk.gov.hmcts.pdda.business.services.validation.ValidationService;
import uk.gov.hmcts.pdda.business.services.validation.sax.FileEntityResolver;
import uk.gov.hmcts.pdda.business.services.validation.sax.SaxValidationService;

import java.util.List;

public class AbstractCppStagingInboundControllerBean extends AbstractControllerBean {

    private static final Logger LOG =
        LoggerFactory.getLogger(AbstractCppStagingInboundControllerBean.class);

    // Location of the XSD schemas
    protected static final String SCHEMA_DIR_DEFAULT = "config/xsd/";

    protected static final String ENTERED = " : entered";
    protected static final String METHOD_NAME_SUFFIX = ") - ";
    protected static final Integer REASON_LIMIT = 4000;
    protected static final String EMPTY_STRING = " ";

    private CppStagingInboundHelper cppStagingInboundHelper;
    private ValidationService validationService;

    public AbstractCppStagingInboundControllerBean() {
        super();
    }

    protected AbstractCppStagingInboundControllerBean(EntityManager entityManager,
        XhbConfigPropRepository xhbConfigPropRepository,
        CppStagingInboundHelper cppStagingInboundHelper, XhbCourtRepository xhbCourtRepository,
        XhbClobRepository xhbClobRepository, XhbBlobRepository xhbBlobRepository,
        ValidationService validationService) {
        super(entityManager, xhbClobRepository, xhbBlobRepository, xhbCourtRepository,
            xhbConfigPropRepository, null);
        this.cppStagingInboundHelper = cppStagingInboundHelper;
        this.validationService = validationService;
    }

    /**
     * Get courtId from XHB_COURT using crest court id (aka court code).
     * 
     * @param courtCode Integer
     * @return int
     * 
     */
    public int getCourtId(Integer courtCode) {
        String methodName = "getCourtId(" + courtCode.intValue() + METHOD_NAME_SUFFIX;
        LOG.debug(methodName + ENTERED);

        int courtId = 0;

        List<XhbCourtDao> data =
            getXhbCourtRepository().findByCrestCourtIdValue(courtCode.toString());
        if (data.isEmpty()) {
            LOG.debug("No court site items returned when searching for court code: "
                + courtCode.intValue());
        } else {
            XhbCourtDao courtSite = data.get(0);
            courtId = courtSite.getCourtId();
        }
        return courtId;
    }

    /**
     * Updates an XHB_CPP_STAGING_INBOUND record such that all status values are reset back to when
     * there initial values This is useful for testing.
     * 
     * @param cppStagingInboundDao CppStagingInboundDao
     * @param userDisplayName String
     * 
     */
    public void resetDocumentStatus(XhbCppStagingInboundDao cppStagingInboundDao,
        String userDisplayName) {
        String methodName = "resetDocumentStatus(" + cppStagingInboundDao + "," + userDisplayName
            + METHOD_NAME_SUFFIX;
        LOG.debug(methodName + ENTERED);
        try {
            getCppStagingInboundHelper().updateCppStagingInbound(cppStagingInboundDao,
                userDisplayName);

        } catch (EJBException e) {
            LOG.error(e.getMessage());
            throw e;
        }
    }

    /**
     * Based on the document type return the (name of the) schema that is to be used to validate the
     * XML The schema document itself will be picked up from SCHEMA_DIR as defined at the top of the
     * class.
     * 
     * @param documentType String
     * @return String
     * 
     */
    public String getSchemaName(String documentType) {
        String methodName = "getSchemaName(" + documentType + METHOD_NAME_SUFFIX;
        LOG.debug(methodName + ENTERED);

        List<XhbConfigPropDao> configPropReturnList =
            getXhbConfigPropRepository().findByPropertyName("CPPX_Schema" + documentType);
        
        for (XhbConfigPropDao configPropReturn : configPropReturnList) {
            LOG.debug("Config prop return: {} {} {}", configPropReturn.getConfigPropId(),
                configPropReturn.getPropertyName(), configPropReturn.getPropertyValue());
        }

        // No schema defined to validate against
        if (configPropReturnList.isEmpty()) {
            LOG.debug("Config prop returned no schema");
            return EMPTY_STRING;
        }
        XhbConfigPropDao tempConfigProp = configPropReturnList.get(0);
        return tempConfigProp.getPropertyValue();
    }

    public String findConfigEntryByPropertyName(String propertyName) {
        String returnString = null;
        LOG.info("findConfigEntryByPropertyName(" + propertyName + ")");
        List<XhbConfigPropDao> properties =
            getXhbConfigPropRepository()
                .findByPropertyName("scheduledtasks.pdda");
        if (null != properties && !properties.isEmpty()) {
            returnString = properties.get(0).getPropertyValue();
        } else {
            LOG.debug("findConfigEntryByPropertyName(" + propertyName
                + "): cannot find property in database.");
        }
        return returnString;
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
}
