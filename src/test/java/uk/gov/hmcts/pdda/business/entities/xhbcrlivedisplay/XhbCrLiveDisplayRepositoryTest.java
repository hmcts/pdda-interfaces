package uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay;


import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyDisplayUtil;
import uk.gov.hmcts.pdda.business.entities.AbstractRepositoryTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("PMD.TestClassWithoutTestCases")
class XhbCrLiveDisplayRepositoryTest extends AbstractRepositoryTest<XhbCrLiveDisplayDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbCrLiveDisplayRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbCrLiveDisplayRepository getClassUnderTest() {
        if (classUnderTest == null) {
            classUnderTest = new XhbCrLiveDisplayRepository(getEntityManager());
        }
        return classUnderTest;
    }

    @Test
    void testFindLiveDisplaysWhereStatusNotNull() {
        List<XhbCrLiveDisplayDao> list = new ArrayList<>();
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);

        List<XhbCrLiveDisplayDao> result = classUnderTest.findLiveDisplaysWhereStatusNotNull();
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testFindByCourtRoom() {
        List<XhbCrLiveDisplayDao> list = new ArrayList<>();
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);

        XhbCrLiveDisplayDao dao = getDummyDao();
        Optional<XhbCrLiveDisplayDao> result =
            classUnderTest.findByCourtRoom(dao.getCourtRoomId());
        assertNotNull(result, NOTNULL);
    }

    @Override
    protected XhbCrLiveDisplayDao getDummyDao() {
        XhbCrLiveDisplayDao result = DummyDisplayUtil.getXhbCrLiveDisplayDao();
        assertNotNull(result.getPrimaryKey(), NOTNULL);
        return new XhbCrLiveDisplayDao(result);
    }

}
