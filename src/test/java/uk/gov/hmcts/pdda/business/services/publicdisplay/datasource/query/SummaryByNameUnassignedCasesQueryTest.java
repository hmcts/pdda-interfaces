package uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query;

import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**

 * Title: SummaryByNameQueryUnassignedCasesQuery Test.


 * Description:


 * Copyright: Copyright (c) 2023


 * Company: CGI

 * @author Mark Harris
 */
@SuppressWarnings("PMD")
@ExtendWith(EasyMockExtension.class)
class SummaryByNameUnassignedCasesQueryTest extends SummaryByNameQueryTest {

    @Override
    protected SummaryByNameQuery getClassUnderTest() {
        return new SummaryByNameUnassignedCasesQuery(mockEntityManager, mockXhbCaseRepository,
            mockXhbCaseReferenceRepository, mockXhbHearingListRepository, mockXhbSittingRepository,
            mockXhbScheduledHearingRepository, mockXhbCourtSiteRepository, mockXhbCourtRoomRepository,
            mockXhbSchedHearingDefendantRepository, mockXhbHearingRepository, mockXhbDefendantOnCaseRepository,
            mockXhbDefendantRepository, mockXhbCourtLogEntryRepository, mockXhbRefHearingTypeRepository);
    }

    @Test
    @Override
    void testDefaultConstructor() {
        boolean result = false;
        try {
            classUnderTest = new SummaryByNameUnassignedCasesQuery(mockEntityManager);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }
}
