package uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query;

import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>
 * Title: AllCaseStatusUnassignedCasesQuery Test.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2023
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Mark Harris
 */
@SuppressWarnings("PMD")
@ExtendWith(EasyMockExtension.class)
class AllCaseStatusUnassignedCasesQueryTest extends AllCaseStatusQueryTest {

    @Override
    protected AllCaseStatusQuery getClassUnderTest() {
        return new AllCaseStatusUnassignedCasesQuery(mockEntityManager, mockXhbCaseRepository,
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
            classUnderTest = new AllCaseStatusUnassignedCasesQuery(mockEntityManager);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }
}
