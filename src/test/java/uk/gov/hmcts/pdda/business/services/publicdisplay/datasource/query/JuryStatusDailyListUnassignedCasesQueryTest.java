package uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query;

import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>
 * Title: JuryStatusDailyListUnassignedCaseQuery Test.
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
class JuryStatusDailyListUnassignedCasesQueryTest extends JuryStatusDailyListQueryTest {

    @Override
    protected JuryStatusDailyListUnassignedCasesQuery getClassUnderTest() {
        return new JuryStatusDailyListUnassignedCasesQuery(mockEntityManager, mockXhbCaseRepository,
            mockXhbCaseReferenceRepository, mockXhbHearingListRepository, mockXhbSittingRepository,
            mockXhbScheduledHearingRepository, mockXhbCourtSiteRepository, mockXhbCourtRoomRepository,
            mockXhbSchedHearingDefendantRepository, mockXhbHearingRepository, mockXhbDefendantOnCaseRepository,
            mockXhbDefendantRepository, mockXhbCourtLogEntryRepository, mockXhbRefHearingTypeRepository,
            mockXhbRefJudgeRepository);
    }

    @Test
    @Override
    void testDefaultConstructor() {
        boolean result = false;
        try {
            classUnderTest = new JuryStatusDailyListUnassignedCasesQuery(mockEntityManager);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }
}
