package uk.gov.hmcts.pdda.business;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbinternethtml.XhbInternetHtmlRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

/**
 * Extra branch coverage for AbstractControllerBean.
 */
@SuppressWarnings("PMD")
class AbstractControllerBeanBranchesTest {

    /** Test double that lets us flip EM/txn state. */
    static class TestableAbstractControllerBean extends AbstractControllerBean {
        private final EntityManager em;
        private boolean emActive = true;
        private boolean txnActive = false;

        TestableAbstractControllerBean(EntityManager em) {
            this.em = em;
        }

        void setEntityManagerActive(boolean active) {
            this.emActive = active;
        }

        void setTransactionActive(boolean active) {
            this.txnActive = active;
        }

        @Override
        protected EntityManager getEntityManager() {
            // Use the stable EM supplied by the test
            return em;
        }

        @Override
        protected boolean isEntityManagerActive() {
            return emActive;
        }

        @Override
        protected boolean isTransactionActive() {
            return txnActive;
        }
    }

    @Test
    void getXhbInternetHtmlRepository_firstCall_createsRepository() {
        TestableAbstractControllerBean bean =
            new TestableAbstractControllerBean(mock(EntityManager.class));
        // First call: repo is null -> path in the screenshot executes
        XhbInternetHtmlRepository repo = bean.getXhbInternetHtmlRepository();
        assertNotNull(repo, "Should create repository on first call");
    }

    @Test
    void getXhbInternetHtmlRepository_recreates_whenEntityManagerBecomesInactive() {
        TestableAbstractControllerBean bean =
            new TestableAbstractControllerBean(mock(EntityManager.class));

        // 1) create once with active EM
        XhbInternetHtmlRepository first = bean.getXhbInternetHtmlRepository();
        assertNotNull(first);

        // 2) flip EM to inactive; method should build a NEW instance
        bean.setEntityManagerActive(false);
        XhbInternetHtmlRepository second = bean.getXhbInternetHtmlRepository();

        assertNotSame(first, second,
            "Should re-instantiate repository when EntityManager is inactive");
    }

    @Test
    void getXhbCourtRepository_doesNotCreateWhileTransactionActive() {
        TestableAbstractControllerBean bean =
            new TestableAbstractControllerBean(mock(EntityManager.class));

        // Block creation by simulating an active transaction
        bean.setTransactionActive(true);

        // Because the repo is null AND txn is active, the 'if' guard fails and method returns null
        XhbCourtRepository repo = bean.getXhbCourtRepository();
        assertNull(repo, "Court repository must not be created while a transaction is active");

        // Now allow creation and verify it appears
        bean.setTransactionActive(false);
        XhbCourtRepository created = bean.getXhbCourtRepository();
        assertNotNull(created, "Court repository should be created when transaction is not active");
    }
}
