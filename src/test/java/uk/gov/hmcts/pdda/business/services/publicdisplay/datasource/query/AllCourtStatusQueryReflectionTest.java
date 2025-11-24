package uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.AllCourtStatusValue;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SuppressWarnings("PMD")
class AllCourtStatusQueryReflectionTest {

    /**
     * Verify numeric-extraction logic used by the sorting helper.
     * - number in courtRoomName (first numeric token)
     * - fallback to crestCourtRoomNo
     * - fallback to courtRoomId
     * - no numeric info -> null
     */
    @Test
    void integerFromCourtRoomName_extractsCorrectly() throws Exception {
        Method m = AllCourtStatusQuery.class.getDeclaredMethod("integerFromCourtRoomName",
            AllCourtStatusValue.class);
        m.setAccessible(true);

        AllCourtStatusValue v1 = new AllCourtStatusValue();
        v1.setCourtRoomName("Court 6 - Main");
        Integer r1 = (Integer) m.invoke(null, v1);
        assertNotNull(r1);
        assertEquals(6, r1.intValue(), "Should extract 6 from 'Court 6 - Main'");

        AllCourtStatusValue v2 = new AllCourtStatusValue();
        v2.setCourtRoomName("RoomNo: X"); // no digits
        v2.setCrestCourtRoomNo(Integer.valueOf(42));
        Integer r2 = (Integer) m.invoke(null, v2);
        assertNotNull(r2);
        assertEquals(42, r2.intValue(), "Should fall back to crestCourtRoomNo when name has no digits");

        AllCourtStatusValue v3 = new AllCourtStatusValue();
        // no name, no crest -> fallback to courtRoomId
        v3.setCourtRoomId(Integer.valueOf(123));
        Integer r3 = (Integer) m.invoke(null, v3);
        assertNotNull(r3);
        assertEquals(123, r3.intValue(), "Should fall back to courtRoomId when crest/name absent");

        AllCourtStatusValue v4 = new AllCourtStatusValue();
        // completely empty -> null
        Integer r4 = (Integer) m.invoke(null, v4);
        assertNull(r4, "Should return null when no numeric info available");
    }
}
