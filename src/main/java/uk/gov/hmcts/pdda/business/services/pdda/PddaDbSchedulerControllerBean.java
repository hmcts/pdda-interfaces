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
import uk.gov.hmcts.pdda.business.services.pdda.db.ProcedureExecutor;
import uk.gov.hmcts.pdda.business.services.pdda.dbscheduler.JobToRun;
import uk.gov.hmcts.pdda.business.services.pdda.dbscheduler.PddaDbSchedulerHelper;
import uk.gov.hmcts.pdda.config.SpringContext;

import java.util.List;

/**

 * Title: Clear Down Controller Bean.


 * Description:


 * Copyright: Copyright (c) 2024


 * Company: CGI

 * @author Nathan Toft
 */
@Stateless
@Service
@Transactional
@LocalBean
@ApplicationException(rollback = true)
public class PddaDbSchedulerControllerBean extends AbstractControllerBean implements RemoteTask {

    private static final Logger LOG = LoggerFactory.getLogger(PddaDbSchedulerControllerBean.class);

    private PddaDbSchedulerHelper pddaDbSchedulerHelper;
    
    // REQUIRED by the legacy scheduler (reflective new with EntityManager)
    public PddaDbSchedulerControllerBean(EntityManager entityManager) {
        super(entityManager);
    }
    
    public PddaDbSchedulerControllerBean() {
        super();
    }

    /**
     * Implementation of RemoteTask so that this process is called by the timer process. This scheduled
     * job resets the IWP data.

     */
    @Override
    public void doTask() {
        LOG.debug("PddaDbScheduler -- doTask() - entered");
        List<JobToRun> jobsToRun = getJobsToRun();
        if (jobsToRun != null && !jobsToRun.isEmpty()) {
            LOG.debug("There are {} jobs to run", jobsToRun.size());
            // There are jobs to run, so loop through and run them
            boolean success = getPddaDbSchedulerHelper().runDbSchedulerJobs(jobsToRun);
            if (success) {
                LOG.debug("All jobs completed successfully");
            } else {
                LOG.error("One or more jobs failed to complete successfully - see logs for details");
            }
        }
        LOG.debug("PddaDbScheduler -- doTask() - exited");
    }

    /**
     * Get the list of DB jobs to run.
     */
    @SuppressWarnings("PMD")
    public List<JobToRun> getJobsToRun() {
        LOG.debug("Calling getJobsToRun()");
        List<JobToRun> jobs = getPddaDbSchedulerHelper().getJobsToRun();
        return jobs;
    }
    
    /**
     * Returns a reference to the pddaDlNotifierHelper object.
     * @return pddaDlNotifierHelper
     */
    private PddaDbSchedulerHelper getPddaDbSchedulerHelper() {
        if (pddaDbSchedulerHelper == null) {

            // Fetch the Spring-managed ProcedureExecutor at runtime
            ProcedureExecutor procedureExecutor = SpringContext.getBean(ProcedureExecutor.class);
            
            pddaDbSchedulerHelper = new PddaDbSchedulerHelper(EntityManagerUtil.getEntityManager(), procedureExecutor);
        }
        return pddaDbSchedulerHelper;
    }
}
