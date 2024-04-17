package uk.gov.hmcts.pdda.business.services.dailylistnotifier;

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

    public DailyListNotifierControllerBean(EntityManager entityManager) {
        super(entityManager);
    }

    public DailyListNotifierControllerBean() {
        super();
    }

    /**
     * Implementation of RemoteTask so that this process is called by the timer process.
     * 
     */
    @Override
    public void doTask() {
        LOG.debug("Entered DL Notifier doTask method");
        callDlHelper();
    }

    /**
     * Implementation of RemoteTask so that this process is called by the timer process.
     * 
     */
    public void callDlHelper() {
        String methodName = "callDlHelper(" + METHOD_END;
        LOG.debug(methodName + ENTERED);
        //Call PddaDlNotifierHelper here
        //Call refreshPublicDisplaysForCourt()
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
