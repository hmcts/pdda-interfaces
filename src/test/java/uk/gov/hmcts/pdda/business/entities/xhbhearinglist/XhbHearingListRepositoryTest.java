package uk.gov.hmcts.pdda.business.entities.xhbhearinglist;

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
import uk.gov.hmcts.DummyHearingUtil;
import uk.gov.hmcts.pdda.business.entities.AbstractRepositoryTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class XhbHearingListRepositoryTest extends AbstractRepositoryTest<XhbHearingListDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbHearingListRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbHearingListRepository getClassUnderTest() {
        return classUnderTest;
    }

    @BeforeEach
    void setup() {
        classUnderTest = new XhbHearingListRepository(mockEntityManager);
    }

    @Test
    void testFindByIdSuccess() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            XhbHearingListDao dummyDao = getDummyDao();
            Mockito.when(mockEntityManager.find(XhbHearingListDao.class, getDummyId()))
                .thenReturn(dummyDao);

            boolean result = runFindByIdTest(dummyDao);
            assertTrue(result, NOT_TRUE);
        }
    }

    @Test
    void testFindByCourtIdStatusAndDate() {
        List<XhbHearingListDao> list = new ArrayList<>();
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);

        XhbHearingListDao dao = getDummyDao();
        Optional<XhbHearingListDao> result = classUnderTest
            .findByCourtIdStatusAndDate(dao.getCourtId(), dao.getStatus(), dao.getStartDate());
        assertNotNull(result, NOTNULLRESULT);
    }

    @Override
    protected XhbHearingListDao getDummyDao() {
        XhbHearingListDao result = DummyHearingUtil.getXhbHearingListDao();
        assertNotNull(result.getPrimaryKey(), NOTNULLRESULT);
        return new XhbHearingListDao(result);
    }

}
