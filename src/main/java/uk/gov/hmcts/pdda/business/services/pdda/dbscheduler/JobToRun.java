package uk.gov.hmcts.pdda.business.services.pdda.dbscheduler;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;

public class JobToRun {
    
    private static final Logger LOG = LoggerFactory.getLogger(JobToRun.class);
    
    private String schema = "pdda";
    private Duration timeout = Duration.ofSeconds(30);
    
    private String jobName;
    
    private LocalDateTime lastRunTime;
    
    private String procedureName;
    
    private Integer numParamsInProcedure;
    
    private String[] procedureParams;
    
    private String jobStatus;
    
    public JobToRun() {
        // Default constructor
    }

    public JobToRun(String jobName, LocalDateTime lastRunTime) {
        this.jobName = jobName;
        this.lastRunTime = lastRunTime;
        this.procedureName = jobName; // Assuming procedure name is same as job name
        this.jobStatus = "UNKNOWN"; // Default status
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public LocalDateTime getLastRunTime() {
        return lastRunTime;
    }

    public void setLastRunTime(LocalDateTime lastRunTime) {
        this.lastRunTime = lastRunTime;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String jobProcedureName) {
        this.procedureName = jobProcedureName;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }
    
    public Integer getNumParamsInProcedure() {
        return numParamsInProcedure;
    }
    
    public void setNumParamsInProcedure(Integer numParamsInProcedure) {
        this.numParamsInProcedure = numParamsInProcedure;
    }
    
    @SuppressWarnings("PMD.MethodReturnsInternalArray")
    public String[] getProcedureParams() {
        return procedureParams;
    }
    
    @SuppressWarnings("PMD")
    public void setProcedureParams(String[] procedureParams) {
        this.procedureParams = procedureParams;
    }
    
    public Duration getTimeout() {
        return timeout;
    }
    
    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }
    
    public String getSchema() {
        return schema;
    }
    
    public void setSchema(String schema) {
        this.schema = schema;
    }
    
    @Override
    public String toString() {
        return "JobToRun [jobName=" + jobName + ", lastRunTime=" + lastRunTime
            + ", jobProcedureName=" + procedureName + ", jobStatus=" + jobStatus
            + ", numParamsInProcedure=" + numParamsInProcedure
            + ", procedureParams=" + String.join(",", procedureParams) + "]";
    }

    /**
     * Run the job.
     * This jobs will be a stored procedure in the database.
     */
    @SuppressWarnings("PMD")
    public boolean runJob(EntityManager entityManager) {
        // We have details of the job to run
        // Call the stored procedure using the EntityManager
        try {
            var storedProcedure = entityManager.createStoredProcedureQuery(procedureName);
            // Register parameters
            if (procedureParams != null) {
                for (int i = 0; i < procedureParams.length; i++) {
                    storedProcedure.registerStoredProcedureParameter(i + 1, String.class, 
                        jakarta.persistence.ParameterMode.IN);
                    storedProcedure.setParameter(i + 1, procedureParams[i]);
                }
            }
            // Execute the stored procedure
            storedProcedure.execute();
            this.jobStatus = "SUCCESS";
            return true;
        } catch (Exception e) {
            this.jobStatus = "FAILED";
            // Log the exception
            LOG.error("Error executing job " + jobName + ": " + e.getMessage());
            return false;
        }
    }
    
}
