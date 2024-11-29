package uk.gov.hmcts.pdda.business.entities.xhbcase;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyCaseUtil;
import uk.gov.hmcts.pdda.business.entities.AbstractRepositoryTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("PMD.TestClassWithoutTestCases")
class XhbCaseRepositoryTest extends AbstractRepositoryTest<XhbCaseDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbCaseRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbCaseRepository getClassUnderTest() {
        if (classUnderTest == null) {
            classUnderTest = new XhbCaseRepository(getEntityManager());
        }
        return classUnderTest;
    }

    @Test
    void testFindByNumberTypeAndCourt() {
        List<XhbCaseDao> list = new ArrayList<>();
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);

        XhbCaseDao dao = getDummyDao();
        Optional<XhbCaseDao> result = classUnderTest.findByNumberTypeAndCourt(dao.getCourtId(),
            dao.getCaseType(), dao.getCaseNumber());
        assertNotNull(result, NOTNULL);
    }

    @Override
    protected XhbCaseDao getDummyDao() {
        return DummyCaseUtil.getXhbCaseDao();
    }

}
