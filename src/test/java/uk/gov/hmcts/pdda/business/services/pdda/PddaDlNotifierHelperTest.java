package uk.gov.hmcts.pdda.business.services.pdda;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.DummyPdNotifierUtil;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbpddadlnotifier.XhbPddaDlNotifierDao;
import uk.gov.hmcts.pdda.business.entities.xhbpddadlnotifier.XhbPddaDlNotifierRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * <p>
 * Title: PDDA Dl Notifier Test.
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
@ExtendWith(MockitoExtension.class)
class PddaDlNotifierHelperTest {

    private static final String YES = "Y";
    private static final String NO = "N";
    private static final String NOT_TRUE = "Expected true";
    private static final String NOT_FALSE = "Expected false";
    private static final DateTimeFormatter DL_NOTIFIER_EXECUTION_TIME_FORMAT =
        DateTimeFormatter.ofPattern("HH:mm");
    private static final String DL_NOTIFIER_EXECUTION_TIME = "DL_NOTIFIER_EXECUTION_TIME";

    @Mock
    private EntityManager mockEntityManager;
    @Mock
    private XhbConfigPropRepository mockXhbConfigPropRepository;
    @Mock
    private XhbCourtRepository mockXhbCourtRepository;
    @Mock
    private XhbPddaDlNotifierRepository mockXhbPddaDlNotifierRepository;
    @Mock
    private Environment mockEnvironment;
    @Mock
    private Query mockQuery;

    private PddaDlNotifierHelper helper;

    @BeforeEach
    void setUp() {
        helper = new PddaDlNotifierHelper(mockEntityManager, mockXhbConfigPropRepository,
            mockEnvironment) {
            @Override
            protected boolean isEntityManagerActive() {
                return true;
            }

            @Override
            protected EntityManager getEntityManager() {
                return mockEntityManager;
            }

            @Override
            protected XhbPddaDlNotifierRepository getPddaDlNotifierRepository() {
                return mockXhbPddaDlNotifierRepository;
            }

            @Override
            protected XhbCourtRepository getCourtRepository() {
                return mockXhbCourtRepository;
            }
        };
    }

    @Test
    void testDefaultConstructor() {
        boolean result = true;
        new PddaDlNotifierHelper(mockEntityManager);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testClearRepositoriesShouldResetCachedRepositories() {
        PddaDlNotifierHelper localHelper = new PddaDlNotifierHelper(mockEntityManager,
            mockXhbConfigPropRepository, mockEnvironment) {
            @Override
            protected boolean isEntityManagerActive() {
                return true;
            }

            @Override
            protected EntityManager getEntityManager() {
                return mockEntityManager;
            }
        };

        // Get initial instances (cached)
        XhbPddaDlNotifierRepository initialNotifierRepo = localHelper.getPddaDlNotifierRepository();
        XhbCourtRepository initialCourtRepo = localHelper.getCourtRepository();

        assertNotNull(initialNotifierRepo, "Notifier repository should not be null");
        assertNotNull(initialCourtRepo, "Court repository should not be null");

        // Clear repositories
        localHelper.clearRepositories();

        // Get new instances (should not be the same as before)
        XhbPddaDlNotifierRepository newNotifierRepo = localHelper.getPddaDlNotifierRepository();
        XhbCourtRepository newCourtRepo = localHelper.getCourtRepository();

        assertNotNull(newNotifierRepo, "Notifier repository should not be null after clearing");
        assertNotNull(newCourtRepo, "Court repository should not be null after clearing");
        assertNotSame(initialNotifierRepo, newNotifierRepo,
            "Notifier repository should have been cleared and re-instantiated.");
        assertNotSame(initialCourtRepo, newCourtRepo,
            "Court repository should have been cleared and re-instantiated.");
    }


    @Test
    void testIsDailyNotifierRequiredSuccess() {
        String minuteAgo =
            LocalDateTime.now().minusMinutes(1).format(DL_NOTIFIER_EXECUTION_TIME_FORMAT);
        when(mockXhbConfigPropRepository.findByPropertyNameSafe(DL_NOTIFIER_EXECUTION_TIME))
            .thenReturn(List.of(new XhbConfigPropDao(1, DL_NOTIFIER_EXECUTION_TIME, minuteAgo)));
        when(mockXhbConfigPropRepository.findByPropertyNameSafe("PDDA_SWITCHER"))
            .thenReturn(List.of(new XhbConfigPropDao(2, "PDDA_SWITCHER", "1")));

        boolean result = helper.isDailyNotifierRequired();
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testIsDailyNotifierRequiredFailure() {
        when(mockXhbConfigPropRepository.findByPropertyNameSafe(DL_NOTIFIER_EXECUTION_TIME))
            .thenReturn(List.of(new XhbConfigPropDao(1, DL_NOTIFIER_EXECUTION_TIME, "Invalid")));

        boolean result = helper.isDailyNotifierRequired();
        assertFalse(result, NOT_FALSE);
    }

    @Test
    void testRunDailyListNotifierSuccess() {
        LocalDateTime now = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);

        XhbCourtDao court1 = DummyCourtUtil.getXhbCourtDao(100, "Court1");
        XhbCourtDao court2 = DummyCourtUtil.getXhbCourtDao(200, "Court2");
        court1.setObsInd(null);
        court2.setObsInd(NO);
        List<XhbCourtDao> courtList = List.of(court1, court2);

        XhbPddaDlNotifierDao newDao = DummyPdNotifierUtil.getXhbPddaDlNotifierDao(200, now);
        when(mockXhbCourtRepository.findAll()).thenReturn(courtList);
        when(mockXhbPddaDlNotifierRepository.findByCourtAndLastRunDate(anyInt(), any()))
            .thenReturn(List.of(new XhbPddaDlNotifierDao())) // court 100 – no notifier
            .thenReturn(List.of(newDao)) // re-fetch after try block
            .thenReturn(List.of(newDao)); // second court
        when(mockXhbPddaDlNotifierRepository.update(any())).thenReturn(Optional.of(newDao));

        helper.runDailyListNotifier();

        verify(mockXhbPddaDlNotifierRepository, times(2)).update(any());
    }

    @Test
    void testGetPddaDlNotifierRepositoryWhenSameEmShouldReturnSameInstance() {
        PddaDlNotifierHelper localHelper = new PddaDlNotifierHelper(mockEntityManager,
            mockXhbConfigPropRepository, mockEnvironment) {
            @Override
            protected boolean isEntityManagerActive() {
                return true;
            }

            @Override
            protected EntityManager getEntityManager() {
                return mockEntityManager;
            }
        };

        XhbPddaDlNotifierRepository repo1 = localHelper.getPddaDlNotifierRepository();
        XhbPddaDlNotifierRepository repo2 = localHelper.getPddaDlNotifierRepository();

        assertSame(repo1, repo2, "Repositories should be the same instance");
    }

    @Test
    void testGetPddaDlNotifierRepositoryWhenInactiveEmShouldRecreate() {
        PddaDlNotifierHelper localHelper = new PddaDlNotifierHelper(mockEntityManager,
            mockXhbConfigPropRepository, mockEnvironment) {
            private int counter;

            @Override
            protected boolean isEntityManagerActive() {
                return counter++ > 0; // first call false, second call true
            }

            @Override
            protected EntityManager getEntityManager() {
                return mockEntityManager;
            }
        };

        XhbPddaDlNotifierRepository repo1 = localHelper.getPddaDlNotifierRepository();
        XhbPddaDlNotifierRepository repo2 = localHelper.getPddaDlNotifierRepository();

        assertNotSame(repo1, repo2, "Repositories should not be the same instance");
    }

    @Test
    void testGetCourtRepositoryWhenSameEmShouldReturnSameInstance() {
        PddaDlNotifierHelper localHelper = new PddaDlNotifierHelper(mockEntityManager,
            mockXhbConfigPropRepository, mockEnvironment) {
            @Override
            protected boolean isEntityManagerActive() {
                return true;
            }

            @Override
            protected EntityManager getEntityManager() {
                return mockEntityManager;
            }
        };

        XhbCourtRepository repo1 = localHelper.getCourtRepository();
        XhbCourtRepository repo2 = localHelper.getCourtRepository();

        assertSame(repo1, repo2, "Repositories should be the same instance");
    }

    @Test
    void testGetCourtRepositoryWhenInactiveEmShouldRecreate() {
        PddaDlNotifierHelper localHelper = new PddaDlNotifierHelper(mockEntityManager,
            mockXhbConfigPropRepository, mockEnvironment) {
            private int counter;

            @Override
            protected boolean isEntityManagerActive() {
                return counter++ > 0;
            }

            @Override
            protected EntityManager getEntityManager() {
                return mockEntityManager;
            }
        };

        XhbCourtRepository repo1 = localHelper.getCourtRepository();
        XhbCourtRepository repo2 = localHelper.getCourtRepository();

        assertNotSame(repo1, repo2, "Repositories should not be the same instance");
    }
}
