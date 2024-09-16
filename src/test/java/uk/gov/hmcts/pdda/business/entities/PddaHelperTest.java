package uk.gov.hmcts.pdda.business.entities;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.pdda.business.entities.xhbconfiguredpublicnotice.XhbConfiguredPublicNoticeDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PddaHelperTest {

    private static final String NOTNULL = "Result is Null";

    @Mock
    private EntityManager mockEntityManager;


    @BeforeAll
    public static void setUp() {
        Mockito.mockStatic(EntityManagerUtil.class);
    }

    @AfterAll
    public static void tearDown() {
        Mockito.clearAllCaches();
    }

    @Test
    void testXcpnFindByPrimaryKey() {
        // Setup
        Integer id = 1;
        // Expects
        Mockito.when(EntityManagerUtil.getEntityManager()).thenReturn(mockEntityManager);
        // Run
        Optional<XhbConfiguredPublicNoticeDao> result = PddaEntityHelper.xcpnFindByPrimaryKey(id);
        // Checks
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testXcstFindByPrimaryKey() {
        // Setup
        Integer id = 1;
        // Expects
        Mockito.when(EntityManagerUtil.getEntityManager()).thenReturn(mockEntityManager);
        // Run
        Optional<XhbCourtSiteDao> result = PddaEntityHelper.xcstFindByPrimaryKey(id);
        // Checks
        assertNotNull(result, NOTNULL);
    }

}
