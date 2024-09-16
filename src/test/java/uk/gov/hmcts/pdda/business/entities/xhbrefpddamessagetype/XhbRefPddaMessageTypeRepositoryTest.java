package uk.gov.hmcts.pdda.business.entities.xhbrefpddamessagetype;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
class XhbRefPddaMessageTypeRepositoryTest extends AbstractRepositoryTest<XhbRefPddaMessageTypeDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbRefPddaMessageTypeRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbRefPddaMessageTypeRepository getClassUnderTest() {
        if (classUnderTest == null) {
            classUnderTest = new XhbRefPddaMessageTypeRepository(getEntityManager());
        }
        return classUnderTest;
    }

    @Test
    void testFindByMessageTypeSuccess() {
        boolean result = testFindByMessageType(getDummyDao());
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindByMessageTypeFailure() {
        boolean result = testFindByMessageType(null);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testFindByMessageType(XhbRefPddaMessageTypeDao dao) {
        List<XhbRefPddaMessageTypeDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);
        List<XhbRefPddaMessageTypeDao> result =
            getClassUnderTest().findByMessageType(getDummyDao().getPddaMessageType());
        assertNotNull(result, NOTNULL);
        if (dao != null) {
            assertSame(dao, result.get(0), SAME);
        } else {
            assertSame(0, result.size(), SAME);
        }
        return true;
    }

    @Override
    protected XhbRefPddaMessageTypeDao getDummyDao() {
        Integer pddaMessageTypeId = getDummyId();
        String pddaMessageType = "pddaMessageType";
        String pddaMessageDescription = "pddaMessageDescription";
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;
        String obsInd = "N";
        XhbRefPddaMessageTypeDao result = new XhbRefPddaMessageTypeDao(pddaMessageTypeId, pddaMessageType,
            pddaMessageDescription, lastUpdateDate, creationDate, lastUpdatedBy, createdBy, version, obsInd);

        return new XhbRefPddaMessageTypeDao(result);
    }

}
