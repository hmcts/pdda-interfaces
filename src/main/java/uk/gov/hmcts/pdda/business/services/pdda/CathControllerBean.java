package uk.gov.hmcts.pdda.business.services.pdda;

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

/**
 * <p>
 * Title: Cath Controller Bean.
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
 * @author Nathan Toft
 */
@Stateless
@Service
@Transactional
@LocalBean
@ApplicationException(rollback = true)
public class CathControllerBean extends AbstractControllerBean implements RemoteTask {

    private static final Logger LOG = LoggerFactory.getLogger(CathControllerBean.class);
    private static final String LOG_CALLED = " called";
    private static final String TWO_PARAMS = "{}{}";
    
    private CathHelper cathHelper;

    public CathControllerBean(EntityManager entityManager) {
        super(entityManager);
    }

    public CathControllerBean() {
        super();
    }

    /**
     * Implementation of RemoteTask so that this process is called by the timer process. This scheduled
     * job sends documents to CaTH.
     * 
     */
    @Override
    public void doTask() {
        String methodName = "doTask()";
        LOG.debug(TWO_PARAMS, methodName, LOG_CALLED);
        getCathHelper().processDocuments();
        getCathHelper().processFailedDocuments();
    }
    
    /**
     * Returns a reference to the cathHelper object.
     * 
     * @return cathHelper
     */
    private CathHelper getCathHelper() {
        if (cathHelper == null) {
            cathHelper = new CathHelper(EntityManagerUtil.getEntityManager(), getXhbXmlDocumentRepository());
        }
        return cathHelper;
    }
}
