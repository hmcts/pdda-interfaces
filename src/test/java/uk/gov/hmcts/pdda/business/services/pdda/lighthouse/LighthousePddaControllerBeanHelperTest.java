package uk.gov.hmcts.pdda.business.services.pdda.lighthouse;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundRepository;
import uk.gov.hmcts.pdda.business.entities.xhbinternethtml.XhbInternetHtmlRepository;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for LighthousePddaControllerBeanHelper using Mockito.
 */
@ExtendWith(MockitoExtension.class)
class LighthousePddaControllerBeanHelperTest {

    @Mock
    private EntityManager entityManager;

    private TestableHelper helper;

    @BeforeEach
    void setUp() {
        helper = spy(new TestableHelper(entityManager));
    }

    /**
     * A small testable subclass that lets us control the AbstractControllerBean hooks
     * and expose them for Mockito verification.
     */
    private static class TestableHelper extends LighthousePddaControllerBeanHelper {
        private boolean active = true;
        private final EntityManager em;

        TestableHelper(EntityManager em) {
            this.em = em;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        @Override
        public boolean isEntityManagerActive() {
            return active;
        }

        // Increase visibility so Mockito can verify invocations.
        @Override
        public EntityManager getEntityManager() {
            return em;
        }
    }

    @Test
    void clearRepositories_shouldNullAllRepositories() {
        // Arrange: populate the protected fields with mocks
        helper.xhbPddaMessageRepository = mock(XhbPddaMessageRepository.class);
        helper.xhbCppStagingInboundRepository = mock(XhbCppStagingInboundRepository.class);
        helper.xhbInternetHtmlRepository = mock(XhbInternetHtmlRepository.class);

        // Act
        helper.clearRepositories();

        // Assert
        assertNull(helper.xhbPddaMessageRepository, "xhbPddaMessageRepository should be null after clearRepositories()");
        assertNull(helper.xhbCppStagingInboundRepository, "xhbCppStagingInboundRepository should be null after clearRepositories()");
        assertNull(helper.xhbInternetHtmlRepository, "xhbInternetHtmlRepository should be null after clearRepositories()");
    }

    @Test
    void getXhbPddaMessageRepository_shouldCreateOnce_whenEntityManagerActive() {
        // Arrange
        ((TestableHelper) helper).setActive(true);

        // Act
        XhbPddaMessageRepository first = helper.getXhbPddaMessageRepository();
        XhbPddaMessageRepository second = helper.getXhbPddaMessageRepository();

        // Assert: same instance when EM is active (cached)
        assertNotNull(first, "Repository should be created");
        assertSame(first, second, "Repository should be reused when EntityManager is active");

        // getEntityManager() should have been called only for the initial creation
        verify(helper, times(1)).getEntityManager();
    }

    @Test
    void getXhbPddaMessageRepository_shouldRecreate_whenEntityManagerNotActive() {
        // Arrange: start active to create the initial instance
        ((TestableHelper) helper).setActive(true);
        XhbPddaMessageRepository initial = helper.getXhbPddaMessageRepository();
        assertNotNull(initial);

        // Now flip to inactive which should force a new instance on next call
        ((TestableHelper) helper).setActive(false);

        // Act
        XhbPddaMessageRepository recreated = helper.getXhbPddaMessageRepository();

        // Assert: a different instance is produced when EM is not active
        assertNotNull(recreated);
        assertNotSame(initial, recreated, "Repository should be recreated when EntityManager is not active");

        // getEntityManager() should have been called twice in total (initial + recreation)
        verify(helper, times(2)).getEntityManager();
    }
}
