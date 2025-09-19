package uk.gov.hmcts.pdda.business.services.pdda.dbscheduler;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.pdda.business.AbstractControllerBean;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.services.pdda.db.ProcedureExecutor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
public class PddaDbSchedulerHelper extends AbstractControllerBean {
    
    private static final Logger LOG = LoggerFactory.getLogger(PddaDbSchedulerHelper.class);

    private XhbConfigPropRepository xhbConfigPropRepository;
    
    private final ProcedureExecutor procedureExecutor;
    
    // Optional defaults (used only if JobToRun has schema/timeout setters)
    @Value("${app.db.schema:public}")
    private String defaultSchema;

    @Value("${app.db.proc-timeout-ms:30000}")
    private int defaultTimeoutMs;

    public PddaDbSchedulerHelper(EntityManager entityManager, ProcedureExecutor procedureExecutor) {
        super(entityManager);
        this.procedureExecutor = procedureExecutor;
    }

    /**
     * Get the jobs to run from the database, details are in XHB_CONFIG_PROP.
     * The details of the jobs are (originally) stored in the following format
     * (pn=property_name, pv=property_value):
     * -- pn='db_scheduler_jobs', pv='clear_audit_tables,clear_obsolete_messages,clear_old_records,
     * nullify_live_display_fields'
     * -- pn='job__clear_audit_tables__lastruntime', pv='01/01/2025 00:00'
     * -- pn='job__clear_obsolete_messages__lastruntime', pv='01/01/2025 00:00'
     * -- pn='job__clear_old_records__lastruntime', pv='01/01/2025 00:00'
     * -- pn='job__nullify_live_display_fields__lastruntime', pv='01/01/2025 00:00'
     * -- pn='job__clear_audit_tables__numparams', pv='2'
     * -- pn='job__clear_obsolete_messages__numparams', pv='2'
     * -- pn='job__clear_old_records__numparams', pv='2'
     * -- pn='job__nullify_live_display_fields__numparams', pv='0'
     * -- pn='job__clear_audit_tables__param1', pv='NON_PROD'
     * -- pn='job__clear_audit_tables__param2', pv='5000'
     * -- pn='job__clear_obsolete_messages__param1', pv='Error obsoleting message'
     * -- pn='job__clear_obsolete_messages__param2', pv='5000'
     * -- pn='job__clear_old_records__param1', pv='30'
     * -- pn='job__clear_old_records__param2', pv='100'
     * @return List of jobs to run
     */
    @SuppressWarnings("PMD")
    public List<JobToRun> getJobsToRun() {
        LOG.debug("getJobsToRun() - entered");
        List<JobToRun> jobsToRun = null;
        
        // Step 1: Get the jobs to run
        if (xhbConfigPropRepository == null) {
            xhbConfigPropRepository = new XhbConfigPropRepository(getEntityManager());
        }
        List<XhbConfigPropDao> jobs = xhbConfigPropRepository.findByPropertyNameSafe("db_scheduler_jobs");
        if (jobs == null || jobs.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Step 2: Print out the jobs found, we expect only one record
        for (XhbConfigPropDao job : jobs) { // This loop should only run once
            LOG.debug("Job to run: {}", job.getPropertyValue());
            
            // Now split the property value into individual job names
            String[] jobNames = job.getPropertyValue().split(",");
        
            // Step 3: For each job, get the last run time and check if it is to be run now
            LocalDateTime lastRunTime = null;
            for (String jobName : jobNames) {
                jobName = jobName.trim();
                if (!jobName.isEmpty()) {
                    LOG.debug("Processing job: {}", jobName);
                    // Fetch last run time
                    List<XhbConfigPropDao> lastRunTimeList = xhbConfigPropRepository
                        .findByPropertyNameSafe("job__" + jobName + "__lastruntime");
                    String lastRunTimeAsString = (lastRunTimeList != null && !lastRunTimeList.isEmpty())
                        ? lastRunTimeList.get(0).getPropertyValue() : null;
                    lastRunTime = lastRunTimeAsString != null
                        ? LocalDateTime.parse(lastRunTimeAsString, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                        : null;
                    LOG.debug("Last run time for {}: {}", jobName, lastRunTime);
                }
                
                // Step 4: Check if lastRunTime is null or empty, or if it's not today's date
                // If so, we consider it due to run now, so add it to the jobsToRun list
                boolean dueToRun = (lastRunTime == null)
                    || lastRunTime.toLocalDate().isBefore(LocalDateTime.now().toLocalDate());
                
                if (dueToRun) {
                    LOG.debug("Job {} is due to run", jobName);
                    if (jobsToRun == null) {
                        jobsToRun = new java.util.ArrayList<>();
                    }
                    jobsToRun.add(new JobToRun(jobName, lastRunTime));
                } else {
                    LOG.debug("Job {} is not due to run", jobName);
                }
            }
        }
        
        return jobsToRun;
    }

    
    /**
     * Fetch job details from the database and populate the JobToRun object.
     * @param job JobToRun object to populate
     * @return String array of parameters for the job
     */
    @SuppressWarnings("PMD")
    private JobToRun getJobDetails(JobToRun job) {
        LOG.debug("getJobDetails() - entered for job: {}", job.getJobName());
        
        String jobName = job.getJobName();
        // 1. Fetch number of parameters
        List<XhbConfigPropDao> numParamsList = xhbConfigPropRepository
            .findByPropertyNameSafe("job__" + jobName + "__numparams");
        int numParams = 0;
        if (numParamsList != null && !numParamsList.isEmpty()) {
            try {
                numParams = Integer.parseInt(numParamsList.get(0).getPropertyValue());
            } catch (NumberFormatException e) {
                LOG.error("Invalid number format for numparams of job {}", jobName);
            }
        }
        job.setNumParamsInProcedure(numParams);
        LOG.debug("Number of parameters: {}", numParams);
        
        // Parameters in positional order
        String[] params = new String[numParams];
        for (int i = 1; i <= numParams; i++) {
            List<XhbConfigPropDao> paramList =
                xhbConfigPropRepository.findByPropertyNameSafe("job__" + jobName + "__param" + i);
            params[i - 1] = (paramList != null && !paramList.isEmpty())
                    ? paramList.get(0).getPropertyValue()
                    : null;
            LOG.debug("Parameter {}: {}", i, params[i - 1]);
        }
        job.setProcedureParams(params);
        
        // Ensure the Procedure name seen by executor is set (your names are snake_case already)
        job.setProcedureName(jobName);
        job.setSchema("pdda_housekeeping_pkg"); // Default schema for housekeeping jobs
        
        // Optional: if your JobToRun has these setters, apply defaults so executor can pick them up
        try {
            // If you added these methods to JobToRun in your project:
            job.getClass().getMethod("setSchema", String.class).invoke(job, defaultSchema);
            job.getClass().getMethod("setTimeout", Duration.class).invoke(job, Duration.ofMillis(defaultTimeoutMs));
        } catch (ReflectiveOperationException ignored) {
            // No-op: your JobToRun might not have schema/timeout; ProcedureExecutor uses sensible defaults.
        }
        
        // Safe log of params
        LOG.debug("Parameters array: {}", (params == null ? "<none>" : String.join(",", params)));
        
        return job;
    }
    
    
    /**
     * Run the jobs passed in.
     * @param jobsToRun List of jobs to run
     * @return true if all jobs ran successfully, false if one or more jobs failed
     */
    public boolean runDbSchedulerJobs(List<JobToRun> jobsToRun) {
        LOG.debug("runDbSchedulerJobs() - entered");
        
        if (jobsToRun == null || jobsToRun.isEmpty()) {
            LOG.debug("No jobs to run");
            return true;
        }
        
        boolean allSuccessful = true;
        for (JobToRun job : jobsToRun) {
            
            String jobName = job.getJobName();
            LOG.debug("Getting job details for: {}", jobName);
            
            getJobDetails(job); // Update job with full details
            
            LOG.debug("Running job: {}", jobName);
            boolean success = procedureExecutor.execute(job);
            
            if (success) {
                LOG.debug("Job {} completed successfully", jobName);
                updateLastRunTime(jobName,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            } else {
                LOG.error("Job {} failed to complete successfully", jobName);
                allSuccessful = false;
            }
        }
        
        return allSuccessful;
    }

    /**
     * Update the last run time for the job in the database.
     * @param jobName Name of the job
     * @param nowAsString Current time as string in format dd/MM/yyyy HH:mm
     * @return true if update was successful, false otherwise
     */
    private boolean updateLastRunTime(String jobName, String nowAsString) {
        List<XhbConfigPropDao> lastRunTimeList = xhbConfigPropRepository
            .findByPropertyNameSafe("job__" + jobName + "__lastruntime");
        XhbConfigPropDao lastRunTimeProp;
        if (lastRunTimeList == null || lastRunTimeList.isEmpty()) {
            // No existing record, this is wrong but we will create one
            LOG.warn("No existing last run time record for {}, creating new one", jobName);
            lastRunTimeProp = new XhbConfigPropDao();
            lastRunTimeProp.setPropertyName("job__" + jobName + "__lastruntime");
        } else {
            lastRunTimeProp = lastRunTimeList.get(0);
        }
        lastRunTimeProp.setPropertyValue(nowAsString);
        try {
            xhbConfigPropRepository.save(lastRunTimeProp);
            LOG.debug("Updated last run time for {} to {}", jobName, nowAsString);
            return true;
        } catch (Exception e) {
            LOG.error("Failed to update last run time for {}: {}", jobName, e.getMessage());
            return false;
        }
    }
}
