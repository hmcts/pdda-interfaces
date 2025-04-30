package uk.gov.hmcts.config;

import com.pdda.hb.jpa.RepositoryUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for RepositoryUtil.
 *
 * @author harrism
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@TestMethodOrder(OrderAnnotation.class)
class RepositoryUtilTest {

    private static final String FALSE = "Result is True";
    private static final String TRUE = "Result is False";

    @Mock
    private XhbCourtRepository mockRepository;
    
    @Mock
    private EntityManager mockEntityManager;

    /**
     * Teardown.
     */
    @AfterEach
    public void teardown() {
        new LocalRepositoryUtil();
    }

    @Test
    void testIsRepositoryActive() {
        Mockito.when(mockRepository.getEntityManager()).thenReturn(mockEntityManager);
        Mockito.when(mockEntityManager.isOpen()).thenReturn(true);
        
        boolean result = RepositoryUtil.isRepositoryActive(mockRepository);
        assertTrue(result, TRUE);
    }
    
    @Test
    void testIsRepositoryActiveFailure() {
        Mockito.when(mockRepository.getEntityManager()).thenReturn(null);
        
        boolean result = RepositoryUtil.isRepositoryActive(null);
        assertFalse(result, FALSE);
        result = RepositoryUtil.isRepositoryActive(mockRepository);
        assertFalse(result, FALSE);
    }

    protected class LocalRepositoryUtil extends RepositoryUtil {
        LocalRepositoryUtil() {
            super();
        }
    }
}