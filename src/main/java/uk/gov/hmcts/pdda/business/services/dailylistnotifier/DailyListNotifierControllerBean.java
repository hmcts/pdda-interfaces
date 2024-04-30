package uk.gov.hmcts.pdda.business.services.dailylistnotifier;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.ejb.ApplicationException;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.framework.scheduler.RemoteTask;
import uk.gov.hmcts.pdda.business.AbstractControllerBean;
import uk.gov.hmcts.pdda.business.services.pdda.PddaDlNotifierHelper;
import uk.gov.hmcts.pdda.business.services.publicdisplay.PdConfigurationControllerBean;
import uk.gov.hmcts.pdda.common.publicdisplay.events.ConfigurationChangeEvent;
import uk.gov.hmcts.pdda.common.publicdisplay.jms.PublicDisplayNotifier;
import uk.gov.hmcts.pdda.common.publicdisplay.types.configuration.CourtConfigurationChange;

@Stateless
@Service
@Transactional
@LocalBean
@ApplicationException(rollback = true)
public class DailyListNotifierControllerBean extends AbstractControllerBean implements RemoteTask {

    private static final Logger LOG = LoggerFactory.getLogger(DailyListNotifierControllerBean.class);

    private static final String METHOD_END = ") - ";
    private static final String ENTERED = " : entered";

    private PublicDisplayNotifier publicDisplayNotifier;
    private PddaDlNotifierHelper pddaDlNotifierHelper;
    private PdConfigurationControllerBean pdConfigurationController;

    public DailyListNotifierControllerBean(EntityManager entityManager) {
        super(entityManager);
    }

    // Junit constructor
    public DailyListNotifierControllerBean(EntityManager entityManager,
        PdConfigurationControllerBean pdConfigurationController, PddaDlNotifierHelper pddaDlNotifierHelper) {
        super(entityManager, null, null, null, null);
        this.pdConfigurationController = pdConfigurationController;
        this.pddaDlNotifierHelper = pddaDlNotifierHelper;
    }

    public DailyListNotifierControllerBean() {
        super();
    }

    /**
     * Implementation of RemoteTask so that this process is called by the timer process. This scheduled
     * job replaces the need for DailyListNotifier due to the removal of ActiveMQ.
     * 
     */
    @Override
    public void doTask() {
        callDailyListNotifierHelper();
    }

    /**
     * Implementation of RemoteTask so that this process is called by the timer process.
     * 
     */
    public void callDailyListNotifierHelper() {
        String methodName = "callDailyListNotifierHelper(" + METHOD_END;
        int[] courtsForPublicDisplay;
        LOG.debug(methodName + ENTERED);
        PdConfigurationControllerBean pdConfigurationControllerBean = getPdConfigurationControllerBean();
        // Write to the XHB_PDDA_DL_NOTIFIER table
        getPddaDlNotifierHelper().runDailyListNotifier();
        // Notify the public displays that it is a new day so it can accept a new daily list
        courtsForPublicDisplay = pdConfigurationControllerBean.getCourtsForPublicDisplay();
        for (int court : courtsForPublicDisplay) {
            refreshPublicDisplaysForCourt(court);
        }
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

    /**
     * Returns a reference to the publicDisplayNotifier object.
     * 
     * @return publicDisplayNotifier
     */
    private PublicDisplayNotifier getPublicDisplayNotifier() {
        if (publicDisplayNotifier == null) {
            publicDisplayNotifier = new PublicDisplayNotifier();
        }
        return publicDisplayNotifier;
    }

    /**
     * Returns a reference to the pddaDlNotifierHelper object.
     * 
     * @return pddaDlNotifierHelper
     */
    private PddaDlNotifierHelper getPddaDlNotifierHelper() {
        if (pddaDlNotifierHelper == null) {
            pddaDlNotifierHelper = new PddaDlNotifierHelper(EntityManagerUtil.getEntityManager());
        }
        return pddaDlNotifierHelper;
    }

    /**
     * Returns a reference to the pdConfigurationController object.
     * 
     * @return PDConfigurationControllerBean
     */
    private PdConfigurationControllerBean getPdConfigurationControllerBean() {
        if (pdConfigurationController == null) {
            return new PdConfigurationControllerBean();
        }
        return pdConfigurationController;
    }
}
