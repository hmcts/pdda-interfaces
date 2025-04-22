package uk.gov.hmcts.pdda.business.entities.xhbcourtlogentry;

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
class XhbCourtLogEntryRepositoryTest extends AbstractRepositoryTest<XhbCourtLogEntryDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbCourtLogEntryRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbCourtLogEntryRepository getClassUnderTest() {
        return classUnderTest;
    }

    @BeforeEach
    void setup() {
        classUnderTest = new XhbCourtLogEntryRepository(mockEntityManager);
    }

    @Test
    void testFindByIdSuccess() {
        try (MockedStatic<EntityManagerUtil> mockedStatic =
            Mockito.mockStatic(EntityManagerUtil.class)) {
            mockedStatic.when(EntityManagerUtil::getEntityManager).thenReturn(mockEntityManager);

            XhbCourtLogEntryDao dummyDao = getDummyDao();
            Mockito.when(mockEntityManager.find(XhbCourtLogEntryDao.class, getDummyId()))
                .thenReturn(dummyDao);

            boolean result = runFindByIdTest(dummyDao);
            assertTrue(result, NOT_TRUE);
        }
    }

    @Test
    void testFindByCaseIdSuccess() {
        boolean result = testFindByCaseId(getDummyDao());
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testFindByCaseIdFailure() {
        boolean result = testFindByCaseId(null);
        assertTrue(result, NOT_TRUE);
    }

    private boolean testFindByCaseId(XhbCourtLogEntryDao dao) {
        List<XhbCourtLogEntryDao> list = new ArrayList<>();
        if (dao != null) {
            list.add(dao);
        }
        Mockito.when(getEntityManager().createNamedQuery(isA(String.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(list);
        List<XhbCourtLogEntryDao> result =
            getClassUnderTest().findByCaseId(getDummyDao().getCaseId());
        assertNotNull(result, "Result is Null");
        if (dao != null) {
            assertSame(dao, result.get(0), "Result is not Same");
        } else {
            assertSame(0, result.size(), "Result is not Same");
        }
        return true;
    }

    @Override
    protected XhbCourtLogEntryDao getDummyDao() {
        Integer entryId = getDummyId();
        Integer caseId = -1;
        Integer defendantOnCaseId = -2;
        Integer defendantOnOffenceId = -3;
        Integer scheduledHearingId = -4;
        Integer eventDescId = -5;
        String logEntryXml = "logEntryXml";
        LocalDateTime dateTime = LocalDateTime.now();
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;


        XhbCourtLogEntryDao result = new XhbCourtLogEntryDao();
        result.setEntryId(entryId);
        result.setCaseId(caseId);
        result.setDefendantOnCaseId(defendantOnCaseId);
        result.setDefendantOnOffenceId(defendantOnOffenceId);
        result.setScheduledHearingId(scheduledHearingId);
        result.setEventDescId(eventDescId);
        result.setLogEntryXml(logEntryXml);
        result.setDateTime(dateTime);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        entryId = result.getPrimaryKey();
        assertNotNull(entryId, NOTSAMERESULT);
        return new XhbCourtLogEntryDao(result);
    }

}
