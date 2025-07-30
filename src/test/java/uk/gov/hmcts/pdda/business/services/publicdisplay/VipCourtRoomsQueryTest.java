package uk.gov.hmcts.pdda.business.services.publicdisplay;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("PMD")
class VipCourtRoomsQueryTest {

    private static final String NOTNULL = "Result is Null";
    private static final String TRUE = "Result is not True";
    private static final String CLEAR_REPOSITORIES_MESSAGE =
        "Repository should be null after clearRepositories()";

    @Mock
    private XhbCourtRoomRepository mockXhbCourtRoomRepository;

    @Mock
    protected EntityManager mockEntityManager;

    private VipCourtRoomsQuery classUnderTestMultiSite;
    private VipCourtRoomsQuery classUnderTestSingleSite;

    @BeforeEach
    void setupMocks() {
        Query mockQuery = Mockito.mock(Query.class);
        when(mockQuery.setParameter(Mockito.anyString(), Mockito.any())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(new ArrayList<>());

        when(mockXhbCourtRoomRepository.getEntityManager()).thenReturn(mockEntityManager);
        when(mockEntityManager.createNamedQuery(Mockito.anyString())).thenReturn(mockQuery);

        classUnderTestMultiSite = new TestableVipCourtRoomsQuery(mockEntityManager, true, mockXhbCourtRoomRepository);
        classUnderTestSingleSite = new TestableVipCourtRoomsQuery(mockEntityManager, false, mockXhbCourtRoomRepository);

    }


    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testGetDataNoListEmptyMultiSite() {
        boolean result = testGetData(new ArrayList<>(), true);
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataMultiSite() {
        List<XhbCourtRoomDao> xhbCourtRoomDaos = new ArrayList<>();
        xhbCourtRoomDaos.add(DummyCourtUtil.getXhbCourtRoomDao());
        boolean result = testGetData(xhbCourtRoomDaos, true);
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataNoListEmptySingleSite() {
        boolean result = testGetData(new ArrayList<>(), false);
        assertTrue(result, TRUE);
    }

    @Test
    void testGetDataSingleSite() {
        List<XhbCourtRoomDao> xhbCourtRoomDaos = new ArrayList<>();
        xhbCourtRoomDaos.add(DummyCourtUtil.getXhbCourtRoomDao());
        boolean result = testGetData(xhbCourtRoomDaos, false); // FIXED
        assertTrue(result, TRUE);
    }


    private boolean testGetData(List<XhbCourtRoomDao> xhbCourtRoomDaos, boolean isMultiSite) {
        // Setup
        Integer courtId = 81;

        // Expects
        if (isMultiSite) {
            when(mockXhbCourtRoomRepository.findVipMultiSite(Mockito.isA(Integer.class)))
                .thenReturn(xhbCourtRoomDaos);
        } else {
            when(mockXhbCourtRoomRepository.findVipMNoSite(Mockito.isA(Integer.class)))
                .thenReturn(xhbCourtRoomDaos);
        }

        // Run
        XhbCourtRoomDao[] results;
        if (isMultiSite) {
            results = classUnderTestMultiSite.getData(courtId);
        } else {
            results = classUnderTestSingleSite.getData(courtId);
        }
        assertNotNull(results, NOTNULL);
        return true;
    }


    @SuppressWarnings({"PMD.UseExplicitTypes", "PMD.AvoidAccessibilityAlteration"})
    @Test
    void testClearRepositoriesSetsRepositoryToNull() throws Exception {
        // Given
        classUnderTestSingleSite.clearRepositories();

        // Use reflection to check the private field
        var field = VipCourtRoomsQuery.class.getDeclaredField("xhbCourtRoomRepository");
        field.setAccessible(true);
        Object repository = field.get(classUnderTestSingleSite);

        // Then
        assertTrue(repository == null, CLEAR_REPOSITORIES_MESSAGE);
    }
    
    
    private static class TestableVipCourtRoomsQuery extends VipCourtRoomsQuery {
        private final XhbCourtRoomRepository repositoryOverride;

        TestableVipCourtRoomsQuery(EntityManager em, boolean multiSite, XhbCourtRoomRepository repo) {
            super(em, multiSite, repo);
            this.repositoryOverride = repo;
        }

        // Not @Override — this hides the original private method
        public XhbCourtRoomRepository getXhbCourtRoomRepository() {
            return repositoryOverride;
        }
    }
    
}


