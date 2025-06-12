package uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound;

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
import uk.gov.hmcts.DummyPdNotifierUtil;
import uk.gov.hmcts.pdda.business.entities.AbstractRepositoryTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class XhbCppStagingInboundRepositoryTest extends AbstractRepositoryTest<XhbCppStagingInboundDao> {

    private static final Integer BYVALIDATIONANDPROCESSINGSTATUS = 1;
    private static final Integer UNRESPONDED = 6;

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbCppStagingInboundRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbCppStagingInboundRepository getClassUnderTest() {
        return classUnderTest;
    }

    @BeforeEach
    void setup() {
        classUnderTest = new XhbCppStagingInboundRepository(mockEntityManager);
    }

    @Test
    void testFindByIdSuccess() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            XhbCppStagingInboundDao dummyDao = getDummyDao();
            Mockito.when(mockEntityManager.find(XhbCppStagingInboundDao.class, getDummyId()))
                .thenReturn(dummyDao);

            boolean result = runFindByIdTest(dummyDao);
            assertTrue(result, NOT_TRUE);
        }
    }

    @Test
    void testFindNextDocumentByValidationAndProcessingStatus() {
        boolean result = testFind(getDummyDao(), BYVALIDATIONANDPROCESSINGSTATUS);
        assertTrue(result, NOT_TRUE);
        result = testFind(null, BYVALIDATIONANDPROCESSINGSTATUS);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindUnrespondedCppMessages() {
        boolean result = testFind(getDummyDao(), UNRESPONDED);
        assertTrue(result, NOT_TRUE);
        result = testFind(null, UNRESPONDED);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testFind(XhbCppStagingInboundDao dao, Integer whichTest) {
        List<XhbCppStagingInboundDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        List<XhbCppStagingInboundDao> result = null;
        if (BYVALIDATIONANDPROCESSINGSTATUS.equals(whichTest)) {
            Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
            Mockito.when(mockQuery.getResultList()).thenReturn(list);
            result = getClassUnderTest()
                .findNextDocumentByValidationAndProcessingStatus(getDummyDao().getTimeLoaded(),
                    getDummyDao().getValidationStatus(), getDummyDao().getProcessingStatus());
        } else if (UNRESPONDED.equals(whichTest)) {
            Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
            Mockito.when(mockQuery.getResultList()).thenReturn(list);
            result = getClassUnderTest().findUnrespondedCppMessages();
        }
        assertNotNull(result, NOTNULLRESULT);
        if (dao != null) {
            assertSame(dao, result.get(0), NOTSAMERESULT);
        } else {
            assertSame(0, result.size(), NOTSAMERESULT);
        }
        return true;
    }

    @Override
    protected XhbCppStagingInboundDao getDummyDao() {
        return DummyPdNotifierUtil.getXhbCppStagingInboundDao();

    }

}
