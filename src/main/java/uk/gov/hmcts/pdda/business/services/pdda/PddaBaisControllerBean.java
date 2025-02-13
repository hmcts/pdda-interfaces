package uk.gov.hmcts.pdda.business.services.pdda;

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
import uk.gov.hmcts.pdda.business.services.pdda.sftp.SftpService;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
@Transactional
@LocalBean
@ApplicationException(rollback = true)
public class PddaBaisControllerBean extends AbstractControllerBean implements RemoteTask {

    private static final Logger LOG = LoggerFactory.getLogger(PddaBaisControllerBean.class);
    private static final String LOG_CALLED = " called";
    private static final String TWO_PARAMS = "{}{}";

    private SftpService sftpService;
    
    private final Lock instanceLock = new ReentrantLock();

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
    public void doTask() {
        String methodName = "doTask()";
        LOG.debug(TWO_PARAMS, methodName, LOG_CALLED);

        try {
            instanceLock.lock();
            getSftpService().processBaisMessages(0);
        } finally {
            instanceLock.unlock();
        }
        
    }


    SftpService getSftpService() {
        if (sftpService == null) {
            sftpService = new SftpService(getEntityManager());
        }
        return sftpService;
    }
}
