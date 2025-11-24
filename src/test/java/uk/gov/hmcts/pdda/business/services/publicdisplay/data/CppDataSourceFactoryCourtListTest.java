package uk.gov.hmcts.pdda.business.services.publicdisplay.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.CourtListValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.DefendantName;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for dedupeByCoreIdentityCourtList private helper.
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD")
class CppDataSourceFactoryCourtListTest {

    // Helper: best-effort setter (small subset of your big helper from ExtendedTest)
    private void setPropertyBestEffort(Object target, String propNameCamelCase, Object value) {
        String setterName = "set" + Character.toUpperCase(propNameCamelCase.charAt(0)) + propNameCamelCase.substring(1);
        try {
            Method setter = findMethodRecursive(target.getClass(), setterName,
                value == null ? new Class<?>[] {Object.class} : new Class<?>[] {value.getClass()});
            if (setter != null) {
                setter.setAccessible(true);
                setter.invoke(target, value);
                return;
            }
        } catch (Exception ignored) {
            // fall back to trying fields is possible in your suite; this helper keeps things small
        }
        // attempt a few typical setter signatures (String/Object)
        try {
            Method setterObj = findMethodRecursive(target.getClass(), setterName, Object.class);
            if (setterObj != null) {
                setterObj.setAccessible(true);
                setterObj.invoke(target, value);
                return;
            }
        } catch (Exception ignored) {
            // give up quietly for this small helper
        }
    }

    private Method findMethodRecursive(Class<?> cls, String name, Class<?>... paramTypes) {
        Class<?> cur = cls;
        while (cur != null) {
            try {
                return cur.getDeclaredMethod(name, paramTypes);
            } catch (NoSuchMethodException e) {
                cur = cur.getSuperclass();
            }
        }
        return null;
    }

    @Test
    void dedupeByCoreIdentityCourtList_prefersCaseKey_and_removesExactDuplicates() throws Exception {
        // Create two CourtListValue instances representing the same logical identity
        CourtListValue a = new CourtListValue();
        CourtListValue b = new CourtListValue();

        // Give both the same caseNumber (strict grouping)
        setPropertyBestEffort(a, "caseNumber", "CASE-CL-1");
        setPropertyBestEffort(b, "caseNumber", "CASE-CL-1");

        // Same listCourtRoomId and notBeforeTime so strict CASE key will match
        setPropertyBestEffort(a, "listCourtRoomId", 55);
        setPropertyBestEffort(b, "listCourtRoomId", 55);

        // notBeforeTime as string or Date - try string
        setPropertyBestEffort(a, "notBeforeTime", "2025-11-21T09:00");
        setPropertyBestEffort(b, "notBeforeTime", "2025-11-21T09:00");

        // Defendant names identical (equal content)
        List<DefendantName> defsA = new ArrayList<>();
        defsA.add(new DefendantName("Sam", null, "Smith", false));
        setPropertyBestEffort(a, "defendantNames", defsA);

        List<DefendantName> defsB = new ArrayList<>();
        defsB.add(new DefendantName("Sam", null, "Smith", false));
        setPropertyBestEffort(b, "defendantNames", defsB);

        // Also create an exact-equals duplicate object (simulate equals() returning true)
        CourtListValue exactSameAsA = a; // same reference -> will be filtered as duplicate

        // Build input with duplicates and verify deduplication reduces to single entry
        List<CourtListValue> input = new ArrayList<>(Arrays.asList(a, b, exactSameAsA));

        Method m = CppDataSourceFactory.class.getDeclaredMethod("dedupeByCoreIdentityCourtList", List.class);
        m.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<CourtListValue> out = (List<CourtListValue>) m.invoke(null, input);

        assertNotNull(out);
        // Expect only one group for this caseNumber
        assertEquals(1, out.size(), "Expected grouping by CASE key to produce a single representative");

        // Now assert that an item for this caseNumber is present
        CourtListValue chosen = out.get(0);
        // try to read the caseNumber using the getter reflectively (invoke)
        Method getCase = findMethodRecursive(chosen.getClass(), "getCaseNumber");
        assertNotNull(getCase, "getCaseNumber getter should exist");
        Object caseVal = getCase.invoke(chosen);
        assertEquals("CASE-CL-1", String.valueOf(caseVal));
    }

    @Test
    void dedupeByCoreIdentityCourtList_usesFallback_whenCaseMissing_and_handles_judgePreference() throws Exception {
        // Two entries lacking caseNumber -> fallback grouping by site/room/listCourtRoomId/notBefore/defendantNames
        CourtListValue e1 = new CourtListValue();
        CourtListValue e2 = new CourtListValue();

        // same site + crest room (use setters if present)
        setPropertyBestEffort(e1, "courtSiteCode", "SITE-CL");
        setPropertyBestEffort(e2, "courtSiteCode", "SITE-CL");
        setPropertyBestEffort(e1, "crestCourtRoomNo", 3);
        setPropertyBestEffort(e2, "crestCourtRoomNo", 3);

        // identical listCourtRoomId + notBefore + defendant names
        setPropertyBestEffort(e1, "listCourtRoomId", 100L);
        setPropertyBestEffort(e2, "listCourtRoomId", 100L);
        setPropertyBestEffort(e1, "notBeforeTime", "2025-11-21T10:00");
        setPropertyBestEffort(e2, "notBeforeTime", "2025-11-21T10:00");

        List<DefendantName> listA = new ArrayList<>();
        listA.add(new DefendantName("Pat", null, "Jones", false));
        setPropertyBestEffort(e1, "defendantNames", listA);

        List<DefendantName> listB = new ArrayList<>();
        listB.add(new DefendantName("Pat", null, "Jones", false));
        setPropertyBestEffort(e2, "defendantNames", listB);

        // Give e2 a judge to ensure preference of judge is respected
        setPropertyBestEffort(e2, "judgeName",
            new uk.gov.hmcts.pdda.common.publicdisplay.renderdata.JudgeName("Judge", "X"));

        List<CourtListValue> input = new ArrayList<>();
        input.add(e1);
        input.add(e2);

        Method m = CppDataSourceFactory.class.getDeclaredMethod("dedupeByCoreIdentityCourtList", List.class);
        m.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<CourtListValue> out = (List<CourtListValue>) m.invoke(null, input);

        assertNotNull(out);
        assertEquals(1, out.size(), "Expected fallback grouping to dedupe to 1 representative");

        // Ensure surviving representative has a judge (preference)
        CourtListValue chosen = out.get(0);
        Method getJudge = findMethodRecursive(chosen.getClass(), "getJudgeName");
        if (getJudge != null) {
            Object judge = getJudge.invoke(chosen);
            assertNotNull(judge, "Expected the chosen representative to have a non-null judge");
        }
    }
}
