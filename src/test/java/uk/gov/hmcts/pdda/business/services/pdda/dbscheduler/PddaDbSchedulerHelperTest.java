package uk.gov.hmcts.pdda.business.services.pdda.dbscheduler;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.services.pdda.db.ProcedureExecutor;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


@SuppressWarnings("PMD")
class PddaDbSchedulerHelperTest {

    private PddaDbSchedulerHelper helper;
    private ProcedureExecutor executor;
    private EntityManager em;
    private XhbConfigPropRepository repo;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @BeforeEach
    void setUp() throws Exception {
        em = mock(EntityManager.class);
        executor = mock(ProcedureExecutor.class);
        repo = mock(XhbConfigPropRepository.class);

        helper = new PddaDbSchedulerHelper(em, executor);
        injectRepo(helper, repo);
    }

    private static void injectRepo(PddaDbSchedulerHelper target, XhbConfigPropRepository repository) throws Exception {
        Field f = PddaDbSchedulerHelper.class.getDeclaredField("xhbConfigPropRepository");
        f.setAccessible(true);
        f.set(target, repository);
    }

    private static XhbConfigPropDao dao(String name, String value) {
        XhbConfigPropDao d = new XhbConfigPropDao();
        d.setPropertyName(name);
        d.setPropertyValue(value);
        return d;
    }

    @Test
    void getJobsToRun_returnsEmpty_whenNoJobsProperty() {
        when(repo.findByPropertyNameSafe("db_scheduler_jobs")).thenReturn(List.of());

        List<JobToRun> jobs = helper.getJobsToRun();

        assertNotNull(jobs);
        assertTrue(jobs.isEmpty());
        verify(repo).findByPropertyNameSafe("db_scheduler_jobs");
    }

    @Test
    void getJobsToRun_returnsOnlyJobsDueTodayLogic() {
        // jobs list prop with two jobs
        when(repo.findByPropertyNameSafe("db_scheduler_jobs"))
                .thenReturn(List.of(dao("db_scheduler_jobs", "clear_audit_tables, clear_obsolete_messages")));

        // last runtime for clear_audit_tables = yesterday -> due
        LocalDateTime yesterday = LocalDate.now().minusDays(1).atTime(10, 0);
        when(repo.findByPropertyNameSafe("job__clear_audit_tables__lastruntime"))
                .thenReturn(List.of(dao("job__clear_audit_tables__lastruntime", FMT.format(yesterday))));

        // last runtime for clear_obsolete_messages = today -> NOT due
        LocalDateTime today = LocalDate.now().atTime(9, 30);
        when(repo.findByPropertyNameSafe("job__clear_obsolete_messages__lastruntime"))
                .thenReturn(List.of(dao("job__clear_obsolete_messages__lastruntime", FMT.format(today))));

        List<JobToRun> jobs = helper.getJobsToRun();

        assertNotNull(jobs);
        assertEquals(1, jobs.size(), "Only yesterday's job should be due");
        assertEquals("clear_audit_tables", jobs.get(0).getJobName());
        // constructor passes the parsed lastRunTime through:
        assertNotNull(jobs.get(0)); // basic sanity
    }

    @Test
    void runDbSchedulerJobs_returnsTrue_whenNullOrEmpty() {
        assertTrue(helper.runDbSchedulerJobs(null));
        assertTrue(helper.runDbSchedulerJobs(List.of()));
        verifyNoInteractions(executor);
        // No updates should occur
        verify(repo, never()).save(any());
    }

    @Test
    void runDbSchedulerJobs_success_updatesLastRunTime_andReturnsTrue() {
        // Arrange a single job; last run time doesn't really matter for run path
        JobToRun job = new JobToRun("clear_audit_tables", null);

        // getJobDetails() lookups:
        when(repo.findByPropertyNameSafe("job__clear_audit_tables__numparams"))
                .thenReturn(List.of(dao("job__clear_audit_tables__numparams", "2")));
        when(repo.findByPropertyNameSafe("job__clear_audit_tables__param1"))
                .thenReturn(List.of(dao("job__clear_audit_tables__param1", "NON_PROD")));
        when(repo.findByPropertyNameSafe("job__clear_audit_tables__param2"))
                .thenReturn(List.of(dao("job__clear_audit_tables__param2", "5000")));

        // updateLastRunTime() first reads existing lastruntime (present)
        XhbConfigPropDao lastRunDao = dao("job__clear_audit_tables__lastruntime", "01/01/2025 00:00");
        when(repo.findByPropertyNameSafe("job__clear_audit_tables__lastruntime"))
                .thenReturn(List.of(lastRunDao));

        when(executor.execute(any(JobToRun.class))).thenReturn(true);

        // Act
        boolean result = helper.runDbSchedulerJobs(List.of(job));

        // Assert: executor called, save called with updated value, result true
        assertTrue(result);
        verify(executor, times(1)).execute(any(JobToRun.class));

        ArgumentCaptor<XhbConfigPropDao> saveCaptor = ArgumentCaptor.forClass(XhbConfigPropDao.class);
        verify(repo, atLeastOnce()).save(saveCaptor.capture());
        XhbConfigPropDao saved = saveCaptor.getValue();
        assertEquals("job__clear_audit_tables__lastruntime", saved.getPropertyName());
        assertNotNull(saved.getPropertyValue());
        // ensure format matches dd/MM/yyyy HH:mm (basic check on length/shape)
        assertEquals(16, saved.getPropertyValue().length());
    }

    @Test
    void runDbSchedulerJobs_failure_doesNotUpdateLastRunTime_andReturnsFalse() {
        JobToRun job = new JobToRun("clear_old_records", null);

        // getJobDetails() queries for num params
        when(repo.findByPropertyNameSafe("job__clear_old_records__numparams"))
                .thenReturn(List.of(dao("job__clear_old_records__numparams", "1")));
        when(repo.findByPropertyNameSafe("job__clear_old_records__param1"))
                .thenReturn(List.of(dao("job__clear_old_records__param1", "30")));

        when(executor.execute(any(JobToRun.class))).thenReturn(false);

        boolean result = helper.runDbSchedulerJobs(List.of(job));

        assertFalse(result);
        verify(executor, times(1)).execute(any(JobToRun.class));
        // Because job failed, last run time must NOT be updated
        verify(repo, never()).save(argThat(prop ->
                "job__clear_old_records__lastruntime".equals(prop.getPropertyName())));
    }

    @Test
    void runDbSchedulerJobs_success_createsLastRunTimeRecord_whenMissing() {
        JobToRun job = new JobToRun("nullify_live_display_fields", null);

        // getJobDetails() with zero params and an invalid numparams value to hit the error path
        when(repo.findByPropertyNameSafe("job__nullify_live_display_fields__numparams"))
                .thenReturn(List.of(dao("job__nullify_live_display_fields__numparams", "not-a-number")));

        // No existing lastruntime -> helper should create one and then save it
        when(repo.findByPropertyNameSafe("job__nullify_live_display_fields__lastruntime"))
                .thenReturn(List.of()); // missing

        when(executor.execute(any(JobToRun.class))).thenReturn(true);

        boolean result = helper.runDbSchedulerJobs(List.of(job));

        assertTrue(result);
        // verify we saved a newly constructed DAO with the correct property name
        verify(repo).save(argThat(prop ->
                "job__nullify_live_display_fields__lastruntime".equals(prop.getPropertyName())
                        && prop.getPropertyValue() != null
                        && prop.getPropertyValue().length() == 16
        ));
    }
}
