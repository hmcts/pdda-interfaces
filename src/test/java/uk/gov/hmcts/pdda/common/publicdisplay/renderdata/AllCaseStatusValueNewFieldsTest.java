package uk.gov.hmcts.pdda.common.publicdisplay.renderdata;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("PMD")
class AllCaseStatusValueNewFieldsTest {

    @Test
    void scheduledAndHearingIds_gettersAndSetters_and_isListedInThisCourtRoom() {
        AllCaseStatusValue v = new AllCaseStatusValue();

        // new fields
        v.setScheduledHearingId(Integer.valueOf(555));
        v.setDefendantOnCaseId(Integer.valueOf(777));
        v.setHearingId(Integer.valueOf(888));

        assertEquals(Integer.valueOf(555), v.getScheduledHearingId());
        assertEquals(Integer.valueOf(777), v.getDefendantOnCaseId());
        assertEquals(Integer.valueOf(888), v.getHearingId());

        // isListedInThisCourtRoom depends on listCourtRoomId and courtRoomId
        v.setListCourtRoomId(Integer.valueOf(10));
        // ensure courtRoomId different -> should be false
        v.setCourtRoomId(Integer.valueOf(20));
        assertFalse(v.isListedInThisCourtRoom(), "Different list vs room id -> not listed in this room");

        // make them equal -> should be true
        v.setCourtRoomId(Integer.valueOf(10));
        assertTrue(v.isListedInThisCourtRoom(), "Equal list vs room id -> listed in this room");
    }
}
