package uk.gov.hmcts.pdda.business.entities.xhbdefendant;

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
import uk.gov.hmcts.DummyDefendantUtil;
import uk.gov.hmcts.pdda.business.entities.AbstractRepositoryTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class XhbDefendantRepositoryTest extends AbstractRepositoryTest<XhbDefendantDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbDefendantRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbDefendantRepository getClassUnderTest() {
        return classUnderTest;
    }

    @BeforeEach
    void setup() {
        classUnderTest = new XhbDefendantRepository(mockEntityManager);
    }

    @Test
    void testFindByIdSuccess() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            XhbDefendantDao dummyDao = getDummyDao();
            Mockito.when(mockEntityManager.find(XhbDefendantDao.class, getDummyId()))
                .thenReturn(dummyDao);

            boolean result = runFindByIdTest(dummyDao);
            assertTrue(result, NOT_TRUE);
        }
    }


    @Test
    void testFindByDefendantName() {
        List<XhbDefendantDao> list = new ArrayList<>();
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);

        XhbDefendantDao dao = getDummyDao();
        Optional<XhbDefendantDao> result =
            classUnderTest.findByDefendantName(dao.getCourtId(), dao.getFirstName(),
                dao.getMiddleName(), dao.getSurname(), dao.getGender(), dao.getDateOfBirth());
        assertNotNull(result, NOTNULLRESULT);
    }

    @Override
    protected XhbDefendantDao getDummyDao() {
        XhbDefendantDao result = DummyDefendantUtil.getXhbDefendantDao();
        assertNotNull(result.getPrimaryKey(), NOTNULLRESULT);
        return new XhbDefendantDao(result);
    }

}
