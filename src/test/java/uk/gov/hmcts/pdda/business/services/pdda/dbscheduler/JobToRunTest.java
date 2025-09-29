package uk.gov.hmcts.pdda.business.services.pdda.dbscheduler;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("PMD")
class JobToRunTest {

    @Test
    void constructor_defaults_and_allArgs_areSet() {
        // default ctor
        JobToRun def = new JobToRun();
        assertEquals("pdda", def.getSchema());
        assertEquals(Duration.ofSeconds(30), def.getTimeout());
        assertNull(def.getJobStatus()); // not set by default ctor

        // (jobName, lastRunTime) ctor
        LocalDateTime now = LocalDateTime.now();
        JobToRun j = new JobToRun("clear_audit_tables", now);
        assertEquals("clear_audit_tables", j.getJobName());
        assertEquals(now, j.getLastRunTime());
        // procedureName mirrors jobName per implementation
        assertEquals("clear_audit_tables", j.getProcedureName());
        assertEquals("UNKNOWN", j.getJobStatus());
    }

    @Test
    void runJob_success_withParams_registersParams_andExecutes() {
        EntityManager em = mock(EntityManager.class);
        StoredProcedureQuery spq = mock(StoredProcedureQuery.class);
        when(em.createStoredProcedureQuery("my_proc")).thenReturn(spq);

        // StoredProcedureQuery has fluent API; return same mock for chaining
        when(spq.registerStoredProcedureParameter(anyInt(), eq(String.class), eq(ParameterMode.IN))).thenReturn(spq);
        when(spq.setParameter(anyInt(), any())).thenReturn(spq);

        JobToRun job = new JobToRun("my_proc", null);
        job.setProcedureParams(new String[] {"A", "B"});

        boolean result = job.runJob(em);

        assertTrue(result);
        assertEquals("SUCCESS", job.getJobStatus());
        verify(em).createStoredProcedureQuery("my_proc");
        verify(spq).registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
        verify(spq).registerStoredProcedureParameter(2, String.class, ParameterMode.IN);
        verify(spq).setParameter(1, "A");
        verify(spq).setParameter(2, "B");
        verify(spq).execute();
    }

    @Test
    void runJob_success_withNoParams_executesWithoutRegistration() {
        EntityManager em = mock(EntityManager.class);
        StoredProcedureQuery spq = mock(StoredProcedureQuery.class);
        when(em.createStoredProcedureQuery("no_params_proc")).thenReturn(spq);

        JobToRun job = new JobToRun("no_params_proc", null);
        job.setProcedureParams(null); // explicit to show branch

        boolean result = job.runJob(em);

        assertTrue(result);
        assertEquals("SUCCESS", job.getJobStatus());
        verify(em).createStoredProcedureQuery("no_params_proc");
        verify(spq, never()).registerStoredProcedureParameter(anyInt(), any(), any());
        verify(spq, never()).setParameter(anyInt(), any());
        verify(spq).execute();
    }

    @Test
    void runJob_failure_setsStatusFailed_andReturnsFalse() {
        EntityManager em = mock(EntityManager.class);
        when(em.createStoredProcedureQuery("boom")).thenThrow(new RuntimeException("kaboom"));

        JobToRun job = new JobToRun("boom", null);

        boolean result = job.runJob(em);

        assertFalse(result);
        assertEquals("FAILED", job.getJobStatus());
    }

    @Test
    void toString_withParams_isSafe_andContainsKeyFields() {
        JobToRun job = new JobToRun("proc", LocalDateTime.of(2025, 1, 1, 0, 0));
        job.setProcedureParams(new String[] {"X", "Y"});
        String s = job.toString();
        assertTrue(s.contains("proc"));
        assertTrue(s.contains("X,Y"));
    }

    @Test
    void toString_withNullParams_currentlyThrowsNpe() {
        // NOTE: JobToRun.toString() calls String.join on procedureParams without a null check.
        // This test documents current behaviour. If you make toString null-safe later, flip this.
        JobToRun job = new JobToRun("proc", null);
        job.setProcedureParams(null);
        assertThrows(NullPointerException.class, job::toString);
    }
}
