package uk.gov.hmcts.pdda.business.entities.xhbcourtellist;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyCourtelUtil;
import uk.gov.hmcts.pdda.business.entities.AbstractRepositoryTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("PMD.TestClassWithoutTestCases")
class XhbCourtelListRepositoryTest extends AbstractRepositoryTest<XhbCourtelListDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbCourtelListRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbCourtelListRepository getClassUnderTest() {
        if (classUnderTest == null) {
            classUnderTest = new XhbCourtelListRepository(getEntityManager());
        }
        return classUnderTest;
    }

    @Override
    protected boolean testfindById(XhbCourtelListDao dao) {
        Mockito.when(getEntityManager().find(getClassUnderTest().getDaoClass(), getDummyLongId())).thenReturn(dao);
        Optional<XhbCourtelListDao> result =
            (Optional<XhbCourtelListDao>) getClassUnderTest().findById(getDummyLongId());
        assertNotNull(result, "Result is Null");
        if (dao != null) {
            assertSame(dao, result.get(), SAME);
        } else {
            assertSame(Optional.empty(), result, SAME);
        }
        return true;
    }

    @Override
    protected XhbCourtelListDao getDummyDao() {
        return DummyCourtelUtil.getXhbCourtelListDao();
    }

}
