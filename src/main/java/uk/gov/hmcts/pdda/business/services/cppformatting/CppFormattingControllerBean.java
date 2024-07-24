package uk.gov.hmcts.pdda.business.services.cppformatting;

import jakarta.ejb.ApplicationException;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.ConfigurationChangeEvent;
import uk.gov.hmcts.framework.scheduler.RemoteTask;
import uk.gov.hmcts.pdda.business.AbstractControllerBean;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingDao;
import uk.gov.hmcts.pdda.common.publicdisplay.jms.PublicDisplayNotifier;
import uk.gov.hmcts.pdda.common.publicdisplay.types.configuration.CourtConfigurationChange;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Stateless
@Service
@Transactional
@LocalBean
@ApplicationException(rollback = true)
public class CppFormattingControllerBean extends AbstractControllerBean implements RemoteTask {

    private static final Logger LOG = LoggerFactory.getLogger(CppFormattingControllerBean.class);

    private static final String METHOD_END = ") - ";
    private static final String ENTERED = " : entered";

    private PublicDisplayNotifier publicDisplayNotifier;

    private static final String DOC_TYPE_PUBLIC_DISPLAY = "PD";

    private static final String DOC_TYPE_WEB_PAGE = "WP";

    protected static final String FORMAT_STATUS_SUCCESS = "MS";

    protected static final String FORMAT_STATUS_FAIL = "MF";

    public CppFormattingControllerBean(EntityManager entityManager) {
        super(entityManager);
    }

    public CppFormattingControllerBean() {
        super();
    }

    /**
     * Implementation of RemoteTask so that this process is called by the timer process.
     * 
     */
    @Override
    public void doTask() {
        processCppPublicDisplayDocs();
    }

    /**
     * Implementation of RemoteTask so that this process is called by the timer process.
     * 
     */
    public void processCppPublicDisplayDocs() {
        // Get a list of CPP_FORMATTING objects that have a type of 'PD', a format status of 'ND'
        // and
        // a date in of today
        List<XhbCppFormattingDao> cppList = getXhbCppFormattingRepository().findAllNewByDocType(
            DOC_TYPE_PUBLIC_DISPLAY, LocalDateTime.now().truncatedTo(ChronoUnit.DAYS));
        for (XhbCppFormattingDao dao : cppList) {
            // For each CPP_FORMATTING object, extract the court Id, use it to refresh all pages for
            // that
            // court
            refreshPublicDisplaysForCourt(dao.getCourtId());

            // Update the format status to 'MS'
            updateStatusSuccess(dao);
        }
    }

    /**
     * <p>
     * Returns the latest unprocessed XHB_CPP_FORMATTING record for Public Display.
     * </p>
     * 
     * @param courtId ID of the court
     * @return XhbCppFormattingDAO
     * 
     */
    public XhbCppFormattingDao getLatestPublicDisplayDocument(Integer courtId) {
        String methodName = "getLatestPublicDisplayDocument(" + courtId + METHOD_END;
        LOG.debug(methodName + ENTERED);

        return getXhbCppFormattingRepository().getLatestDocumentByCourtIdAndType(courtId,
            DOC_TYPE_PUBLIC_DISPLAY, LocalDateTime.now());
    }

    /**
     * <p>
     * Returns the latest unprocessed XHB_CPP_FORMATTING record for Internet Web Pages.
     * </p>
     * 
     * @param courtId ID of the court
     * @return XhbCppFormattingDAO
     * 
     */
    public XhbCppFormattingDao getLatestWebPageDocument(Integer courtId) {
        String methodName = "getLatestWebPageDocument(" + courtId + METHOD_END;
        LOG.debug(methodName + ENTERED);

        return getXhbCppFormattingRepository().getLatestDocumentByCourtIdAndType(courtId,
            DOC_TYPE_WEB_PAGE, LocalDateTime.now());
    }

    /**
     * Updates an XHB_CPP_FORMATTING record with a status of successfully merged/processed.
     * 
     * @param dao XhbCppFormattingDao
     * 
     */
    public void updateStatusSuccess(XhbCppFormattingDao dao) {
        String methodName = "updateStatusSuccess(" + dao + METHOD_END;
        LOG.debug(methodName + ENTERED);
        dao.setFormatStatus(FORMAT_STATUS_SUCCESS);
        getXhbCppFormattingRepository().update(dao);
    }

    /**
     * Updates an XHB_CPP_FORMATTING record with a status of merge failed.
     * 
     * @param dao XhbCppFormattingDao
     * 
     */
    public void updateStatusFailed(XhbCppFormattingDao dao) {
        String methodName = "updateStatusFailed(" + dao + METHOD_END;
        LOG.debug(methodName + ENTERED);
        dao.setFormatStatus(FORMAT_STATUS_FAIL);
        getXhbCppFormattingRepository().update(dao);
    }

    /**
     * Refreshes all public displays for the court specified.
     * 
     * @param courtId Court Id
     */
    public void refreshPublicDisplaysForCourt(Integer courtId) {
        LOG.debug("refreshPublicDisplaysForCourt({})", courtId);
        CourtConfigurationChange ccc = new CourtConfigurationChange(courtId.intValue(), true);
        ConfigurationChangeEvent ccEvent = new ConfigurationChangeEvent(ccc);
        getPublicDisplayNotifier().sendMessage(ccEvent);
    }

    private PublicDisplayNotifier getPublicDisplayNotifier() {
        if (publicDisplayNotifier == null) {
            publicDisplayNotifier = new PublicDisplayNotifier();
        }
        return publicDisplayNotifier;
    }
}
