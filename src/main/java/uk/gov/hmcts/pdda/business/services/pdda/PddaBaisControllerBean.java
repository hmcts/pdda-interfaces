package uk.gov.hmcts.pdda.business.services.pdda;

import jakarta.ejb.ApplicationException;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.framework.scheduler.RemoteTask;
import uk.gov.hmcts.pdda.business.AbstractControllerBean;
import uk.gov.hmcts.pdda.business.services.pdda.sftp.SftpService;

/**
 * <p>
 * Title: PDDA Bais Controller Bean.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2022
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Mark Harris
 */
@Stateless
@Service
@LocalBean
@ApplicationException(rollback = true)
@SuppressWarnings("PMD.AvoidSynchronizedAtMethodLevel")
public class PddaBaisControllerBean extends AbstractControllerBean implements RemoteTask {

    private static final Logger LOG = LoggerFactory.getLogger(PddaBaisControllerBean.class);
    private static final String LOG_CALLED = " called";
    private static final String TWO_PARAMS = "{}{}";

    private SftpService sftpService;
    
    public PddaBaisControllerBean(EntityManager entityManager) {
        super(entityManager);
    }

    public PddaBaisControllerBean() {
        super();
    }

    /**
     * <p>
     * Scheduler task wrapper to retrieve Bais messages.
     * </p>
     */
    @Override
    public synchronized void doTask() {
        String methodName = "doTask()";
        LOG.debug(TWO_PARAMS, methodName, LOG_CALLED);
        getSftpService().processBaisMessages(0);
    }


    SftpService getSftpService() {
        if (sftpService == null) {
            sftpService = new SftpService(getEntityManager());
        }
        return sftpService;
    }
}
