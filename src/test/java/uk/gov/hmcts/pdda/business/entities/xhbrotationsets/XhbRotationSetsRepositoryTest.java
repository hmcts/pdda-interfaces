package uk.gov.hmcts.pdda.business.entities.xhbrotationsets;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.pdda.business.entities.AbstractRepositoryTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class XhbRotationSetsRepositoryTest extends AbstractRepositoryTest<XhbRotationSetsDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbRotationSetsRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbRotationSetsRepository getClassUnderTest() {
        return classUnderTest;
    }

    @BeforeEach
    void setup() {
        classUnderTest = new XhbRotationSetsRepository(mockEntityManager);
    }

    @Test
    void testFindByIdSuccess() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            XhbRotationSetsDao dummyDao = getDummyDao();
            Mockito.when(mockEntityManager.find(XhbRotationSetsDao.class, getDummyId()))
                .thenReturn(dummyDao);

            boolean result = runFindByIdTest(dummyDao);
            assertTrue(result, NOT_TRUE);
        }
    }

    @Test
    void testFindByCourtIdSuccess() {
        boolean result = testFindByCourtId(getDummyDao());
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindByCourtIdFailure() {
        boolean result = testFindByCourtId(null);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testFindByCourtId(XhbRotationSetsDao dao) {
        List<XhbRotationSetsDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);
        List<XhbRotationSetsDao> result =
            getClassUnderTest().findByCourtId(getDummyDao().getRotationSetId());
        assertNotNull(result, NOTNULLRESULT);
        if (dao != null) {
            assertSame(dao, result.get(0), NOTSAMERESULT);
        } else {
            assertSame(0, result.size(), NOTSAMERESULT);
        }
        return true;
    }

    @Test
    void testSetDataNoUpdates() {
        // Setup
        XhbRotationSetsDao originalDao = getDummyDao();
        XhbRotationSetsDao newDao = originalDao;
        // Run with no updates
        newDao.setData(originalDao);
        // Assert
        assertNotNull(newDao, NOTNULLRESULT);
    }

    @Test
    void testSetDataUpdates() {
        // Setup
        XhbRotationSetsDao originalDao = testSetData(getDummyDao(), false);
        XhbRotationSetsDao newDao = getDummyDao();
        // Check
        newDao.setData(originalDao);
        // Assert
        assertNotNull(newDao, NOTNULLRESULT);
    }

    @Test
    void testSetDataUpdateToNull() {
        // Setup
        XhbRotationSetsDao originalDao = testSetData(getDummyDao(), true);
        XhbRotationSetsDao newDao = getDummyDao();
        // Check
        newDao.setData(originalDao);
        // Assert
        assertNotNull(newDao, NOTNULLRESULT);
    }

    @Test
    void testSetDataUpdateBothNull() {
        // Setup
        XhbRotationSetsDao originalDao = testSetData(getDummyDao(), true);
        XhbRotationSetsDao newDao = testSetData(getDummyDao(), true);
        // Check
        newDao.setData(originalDao);
        // Assert
        assertNotNull(newDao, NOTNULLRESULT);
    }

    private XhbRotationSetsDao testSetData(XhbRotationSetsDao dao, boolean setToNull) {
        if (setToNull) {
            dao.setDescription(null);
            dao.setDefaultYn(null);
            dao.setCreatedBy(null);
            dao.setLastUpdatedBy(null);
            dao.setCreationDate(null);
            dao.setLastUpdateDate(null);
            dao.setVersion(null); 
        } else {
            dao.setDescription(dao.getDescription() + ".");
            dao.setDefaultYn(dao.getDefaultYn() + ".");
            dao.setCreatedBy(dao.getCreatedBy() + ".");
            dao.setLastUpdatedBy(dao.getLastUpdatedBy() + ".");
            dao.setCreationDate(LocalDateTime.now());
            dao.setLastUpdateDate(LocalDateTime.now());
            dao.setVersion(dao.getVersion() + 1);
        }
        return dao;
    }

    @Override
    protected XhbRotationSetsDao getDummyDao() {
        Integer rotationSetsId = getDummyId();
        Integer courtId = -1;
        String description = "description";
        String defaultYn = "defaultYn";
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;
        XhbRotationSetsDao result = new XhbRotationSetsDao();
        result.setRotationSetId(rotationSetsId);
        result.setCourtId(courtId);
        result.setDescription(description);
        result.setDefaultYn(defaultYn);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        rotationSetsId = result.getPrimaryKey();
        assertNotNull(rotationSetsId, NOTNULLRESULT);

        return new XhbRotationSetsDao(result);
    }

}
