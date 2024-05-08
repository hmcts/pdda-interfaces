package uk.gov.hmcts.pdda.business.services.courtellist;

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

@Stateless
@Service
@Transactional
@LocalBean
@ApplicationException(rollback = true)
public class CourtelListControllerBean extends AbstractControllerBean implements RemoteTask {

    private static final Logger LOG = LoggerFactory.getLogger(CourtelListControllerBean.class);

    private static final String METHOD_END = ") - ";
    private static final String ENTERED = " : entered";

    public CourtelListControllerBean(EntityManager entityManager) {
        super(entityManager);
    }

    public CourtelListControllerBean() {
        super();
    }

    /**
     * Implementation of RemoteTask so that this process is called by the timer process.
     * 
     */
    @Override
    public void doTask() {
        callCourtelListHelper();
    }

    /**
     * Implementation of RemoteTask so that this process is called by the timer process.
     * 
     */
    public void callCourtelListHelper() {
        String methodName = "callCourtelListHelper(" + METHOD_END;
        LOG.debug(methodName + ENTERED);
        processMessages();
    }
    
    /**
     * Processes messages from Courtel.
     * 
     */
    public void processMessages() {
        String methodName = "processMessages(" + METHOD_END;
        LOG.debug(methodName + ENTERED);
    }
}
