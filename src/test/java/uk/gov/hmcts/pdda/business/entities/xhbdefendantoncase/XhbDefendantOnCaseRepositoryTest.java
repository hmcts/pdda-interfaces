package uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase;

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
import uk.gov.hmcts.framework.jdbc.core.PddaRow;
import uk.gov.hmcts.pdda.business.entities.AbstractRepositoryTest;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.DefendantName;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class XhbDefendantOnCaseRepositoryTest extends AbstractRepositoryTest<XhbDefendantOnCaseDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbDefendantOnCaseRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbDefendantOnCaseRepository getClassUnderTest() {
        return classUnderTest;
    }

    @BeforeEach
    void setup() {
        classUnderTest = new XhbDefendantOnCaseRepository(mockEntityManager);
    }

    @Test
    void testFindByIdSuccess() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            XhbDefendantOnCaseDao dummyDao = getDummyDao();
            Mockito.when(mockEntityManager.find(XhbDefendantOnCaseDao.class, getDummyId()))
                .thenReturn(dummyDao);

            boolean result = runFindByIdTest(dummyDao);
            assertTrue(result, NOT_TRUE);
        }
    }

    @Override
    protected XhbDefendantOnCaseDao getDummyDao() {
        return DummyDefendantUtil.getXhbDefendantOnCaseDao();
    }
    
    @Test
    void testDefandantName() {
        PddaRow mockPddaRow = Mockito.mock(PddaRow.class);
        DefendantName result = new DefendantName(mockPddaRow);
        assertNotNull(result, NOTNULLRESULT);
    }

}
