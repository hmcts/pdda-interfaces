package uk.gov.hmcts.pdda.business.entities.xhbrefhearingtype;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.pdda.business.entities.AbstractRepositoryTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("PMD.TestClassWithoutTestCases")
class XhbRefHearingTypeRepositoryTest extends AbstractRepositoryTest<XhbRefHearingTypeDao> {

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private XhbRefHearingTypeRepository classUnderTest;

    @Override
    protected EntityManager getEntityManager() {
        return mockEntityManager;
    }

    @Override
    protected XhbRefHearingTypeRepository getClassUnderTest() {
        if (classUnderTest == null) {
            classUnderTest = new XhbRefHearingTypeRepository(getEntityManager());
        }
        return classUnderTest;
    }

    @Override
    protected XhbRefHearingTypeDao getDummyDao() {
        Integer refHearingTypeId = getDummyId();
        String hearingTypeCode = "hearingTypeCode";
        String hearingTypeDesc = "hearingTypeDesc";
        String category = "category";
        Integer seqNo = -1;
        Integer listSequence = -1;
        Integer courtId = -1;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;
        String obsInd = "N";
        XhbRefHearingTypeDao result = new XhbRefHearingTypeDao();
        result.setRefHearingTypeId(refHearingTypeId);
        result.setHearingTypeCode(hearingTypeCode);
        result.setHearingTypeDesc(hearingTypeDesc);
        result.setCategory(category);
        result.setSeqNo(seqNo);
        result.setListSequence(listSequence);
        result.setCourtId(courtId);
        result.setObsInd(obsInd);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        refHearingTypeId = result.getPrimaryKey();
        assertNotNull(refHearingTypeId, NOTNULL);
        return new XhbRefHearingTypeDao(result);
    }

}
