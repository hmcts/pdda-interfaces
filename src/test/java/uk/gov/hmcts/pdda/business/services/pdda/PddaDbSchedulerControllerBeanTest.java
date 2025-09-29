package uk.gov.hmcts.pdda.business.services.pdda;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.pdda.business.services.pdda.db.ProcedureExecutor;
import uk.gov.hmcts.pdda.business.services.pdda.dbscheduler.JobToRun;
import uk.gov.hmcts.pdda.business.services.pdda.dbscheduler.PddaDbSchedulerHelper;
import uk.gov.hmcts.pdda.config.SpringContext;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for PddaDbSchedulerControllerBean.
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD")
class PddaDbSchedulerControllerBeanTest {

    private PddaDbSchedulerControllerBean bean;

    private PddaDbSchedulerHelper helperMock;
    private EntityManager entityManagerMock;

    @BeforeEach
    void setUp() {
        bean = Mockito.spy(new PddaDbSchedulerControllerBean());
        helperMock = mock(PddaDbSchedulerHelper.class);
        entityManagerMock = mock(EntityManager.class);
    }

    private void injectHelper(PddaDbSchedulerControllerBean target, PddaDbSchedulerHelper helper) {
        try {
            Field f = PddaDbSchedulerControllerBean.class.getDeclaredField("pddaDbSchedulerHelper");
            f.setAccessible(true);
            f.set(target, helper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void doTask_whenJobsNull_doesNotRunAnything() {
        doReturn(null).when(bean).getJobsToRun();
        bean.doTask();
        verifyNoInteractions(helperMock);
    }

    @Test
    void doTask_whenJobsEmpty_doesNotRunAnything() {
        doReturn(List.of()).when(bean).getJobsToRun();
        bean.doTask();
        verifyNoInteractions(helperMock);
    }

    @Test
    void doTask_whenJobsPresent_andRunSuccess_logsSuccessPath() {
        List<JobToRun> jobs = List.of(new JobToRun("TEST_JOB", LocalDateTime.now()));
        doReturn(jobs).when(bean).getJobsToRun();
        injectHelper(bean, helperMock);
        when(helperMock.runDbSchedulerJobs(jobs)).thenReturn(true);

        bean.doTask();

        verify(helperMock).runDbSchedulerJobs(jobs);
    }

    @Test
    void doTask_whenJobsPresent_andRunFailure_logsErrorPath() {
        List<JobToRun> jobs = List.of(new JobToRun("TEST_JOB", LocalDateTime.now()));
        doReturn(jobs).when(bean).getJobsToRun();
        injectHelper(bean, helperMock);
        when(helperMock.runDbSchedulerJobs(jobs)).thenReturn(false);

        bean.doTask();

        verify(helperMock).runDbSchedulerJobs(jobs);
    }

    @Test
    void getJobsToRun_delegatesToHelper_andReturnsList() {
        List<JobToRun> jobs = new ArrayList<>();
        jobs.add(new JobToRun("A", LocalDateTime.now()));
        injectHelper(bean, helperMock);
        when(helperMock.getJobsToRun()).thenReturn(jobs);

        List<JobToRun> result = bean.getJobsToRun();

        assertSame(jobs, result);
        verify(helperMock).getJobsToRun();
    }

    @Test
    void constructor_withEntityManager_doesNotThrow() {
        assertDoesNotThrow(() -> new PddaDbSchedulerControllerBean(entityManagerMock));
    }

    @Test
    void lazyInit_buildsHelperUsingSpringContextAndEntityManagerUtil() {
        ProcedureExecutor procExecMock = mock(ProcedureExecutor.class);

        try (MockedStatic<SpringContext> springMock = Mockito.mockStatic(SpringContext.class);
             MockedStatic<EntityManagerUtil> emUtilMock = Mockito.mockStatic(EntityManagerUtil.class);
             MockedConstruction<PddaDbSchedulerHelper> helperCtor =
                     Mockito.mockConstruction(PddaDbSchedulerHelper.class, (mock, context) -> {
                         when(mock.getJobsToRun()).thenReturn(List.of());
                         when(mock.runDbSchedulerJobs(anyList())).thenReturn(true);
                     })) {

            springMock.when(() -> SpringContext.getBean(ProcedureExecutor.class)).thenReturn(procExecMock);
            emUtilMock.when(EntityManagerUtil::getEntityManager).thenReturn(entityManagerMock);

            PddaDbSchedulerControllerBean freshBean = new PddaDbSchedulerControllerBean();
            List<JobToRun> result = freshBean.getJobsToRun();

            List<PddaDbSchedulerHelper> constructed = helperCtor.constructed();
            org.junit.jupiter.api.Assertions.assertEquals(1, constructed.size());
            verify(constructed.get(0)).getJobsToRun();
            org.junit.jupiter.api.Assertions.assertNotNull(result);
        }
    }
}
