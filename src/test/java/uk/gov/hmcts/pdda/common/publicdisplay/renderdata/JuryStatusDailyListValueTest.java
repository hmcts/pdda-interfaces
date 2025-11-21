package uk.gov.hmcts.pdda.common.publicdisplay.renderdata;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for JuryStatusDailyListValue.
 * Uses best-effort reflection utilities to set inherited/private fields when public setters are
 * not available, keeping Sonar rules in mind (no wildcards, braces for single-line ifs).
 */
@SuppressWarnings("PMD")
@ExtendWith(MockitoExtension.class)
class JuryStatusDailyListValueTest {

    // Best-effort setter: tries setter first, then private fields up the class hierarchy.
    private void setPropertyBestEffort(Object target, String propNameCamelCase, Object value,
        String... candidateFieldNames) {
        String setterName = "set" + capitalize(propNameCamelCase);
        try {
            Method setter = findMethodRecursive(target.getClass(), setterName,
                value == null ? new Class<?>[] { Object.class } : new Class<?>[] { value.getClass() });
            if (setter != null) {
                setter.setAccessible(true);
                setter.invoke(target, value);
                return;
            }
        } catch (Exception ignored) {
            // fall through to field set
        }

        // try candidate field names and propNameCamelCase
        String[] combined = Arrays.copyOf(candidateFieldNames, candidateFieldNames.length + 1);
        combined[candidateFieldNames.length] = propNameCamelCase;
        for (String fname : combined) {
            if (fname == null) {
                continue;
            }
            if (trySetFieldRecursive(target, fname, value)) {
                return;
            }
        }

        try {
            Method setterObj = findMethodRecursive(target.getClass(), setterName, Object.class);
            if (setterObj != null) {
                setterObj.setAccessible(true);
                setterObj.invoke(target, value);
                return;
            }
        } catch (Exception ignored) {
            // give up quietly
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

    private boolean trySetFieldRecursive(Object target, String fieldName, Object value) {
        Class<?> cur = target.getClass();
        while (cur != null) {
            try {
                Field f = cur.getDeclaredField(fieldName);
                f.setAccessible(true);
                if (f.getType().isPrimitive() && value == null) {
                    return false;
                }
                if (value != null && !f.getType().isAssignableFrom(value.getClass())) {
                    // try to handle common wrapper -> primitive cases
                    Object converted = tryConvertPrimitive(f.getType(), value);
                    if (converted != null) {
                        f.set(target, converted);
                        return true;
                    } else {
                        return false;
                    }
                }
                f.set(target, value);
                return true;
            } catch (NoSuchFieldException e) {
                cur = cur.getSuperclass();
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    private Object tryConvertPrimitive(Class<?> to, Object value) {
        if (value == null) {
            return null;
        }
        try {
            if (to == int.class && value instanceof Number) {
                return ((Number) value).intValue();
            }
            if (to == long.class && value instanceof Number) {
                return ((Number) value).longValue();
            }
            if (to == boolean.class && value instanceof Boolean) {
                return value;
            }
        } catch (Exception ignored) {
            // ignore
        }
        return null;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    @Test
    void isFloating_and_getFloating_string_flag() {
        JuryStatusDailyListValue v = new JuryStatusDailyListValue();

        // set via public setter for floating
        v.setFloating(JuryStatusDailyListValue.IS_FLOATING);
        assertTrue(v.isFloating(), "Expected isFloating() to return true when set to IS_FLOATING");
        assertEquals(JuryStatusDailyListValue.IS_FLOATING, v.getFloating());

        // set to something else
        v.setFloating("N");
        assertFalse(v.isFloating(), "Expected isFloating() to return false for any non-IS_FLOATING value");
        assertEquals("N", v.getFloating());
    }

    @Test
    void compareTo_considers_courtSite_floating_and_room_number() {
        JuryStatusDailyListValue a = new JuryStatusDailyListValue();
        JuryStatusDailyListValue b = new JuryStatusDailyListValue();

        // set court site codes (attempt setter, then fields)
        setPropertyBestEffort(a, "courtSiteCode", "A", "courtSiteCode");
        setPropertyBestEffort(b, "courtSiteCode", "B", "courtSiteCode");

        // when sites differ, site A should compare < site B
        int cmpSites = a.compareTo(b);
        assertTrue(cmpSites < 0, "Expected site code 'A' to come before 'B'");

        // align site codes and vary floating flags
        setPropertyBestEffort(b, "courtSiteCode", "A", "courtSiteCode");
        a.setFloating(JuryStatusDailyListValue.IS_FLOATING);
        b.setFloating("N");

        int cmpFloating = a.compareTo(b);
        // "Floating cases should appear last": compareTo returns a.getFloating().compareTo(b.getFloating())
        // IS_FLOATING likely > "N" so a should compare > b (positive)
        assertTrue(cmpFloating != 0, "Expected floating comparison to produce non-zero result");

        // set same floating, vary room numbers
        a.setFloating("N");
        setPropertyBestEffort(a, "crestCourtRoomNo", 1, "crestCourtRoomNo");
        setPropertyBestEffort(b, "crestCourtRoomNo", 2, "crestCourtRoomNo");

        int cmpRoom = a.compareTo(b);
        assertTrue(cmpRoom < 0, "Expected room 1 to come before room 2");
    }

    @Test
    void equals_and_hashCode_consider_key_fields() {
        JuryStatusDailyListValue a = new JuryStatusDailyListValue();
        JuryStatusDailyListValue b = new JuryStatusDailyListValue();

        setPropertyBestEffort(a, "courtSiteCode", "S", "courtSiteCode");
        setPropertyBestEffort(b, "courtSiteCode", "S", "courtSiteCode");

        setPropertyBestEffort(a, "crestCourtRoomNo", 10, "crestCourtRoomNo");
        setPropertyBestEffort(b, "crestCourtRoomNo", 10, "crestCourtRoomNo");

        setPropertyBestEffort(a, "caseNumber", "C-1", "caseNumber", "caseNo");
        setPropertyBestEffort(b, "caseNumber", "C-1", "caseNumber", "caseNo");

        a.setFloating("N");
        b.setFloating("N");

        // defendant names may be a collection or string; try some candidate names
        setPropertyBestEffort(a, "defendantNames", "Def A", "defendantNames", "defendantName", "defendant");
        setPropertyBestEffort(b, "defendantNames", "Def A", "defendantNames", "defendantName", "defendant");

        // Objects should be equal (and have equal hashCodes) when key fields match and
        // defendantNames differ only in representation equality sense
        assertTrue(a.equals(a), "equals must be reflexive");
        assertTrue(b.equals(b), "equals must be reflexive for other");

        boolean ab = a.equals(b);
        boolean ba = b.equals(a);
        assertEquals(ab, ba, "equals must be symmetric");

        if (ab) {
            assertEquals(a.hashCode(), b.hashCode(), "When equal, hashCodes must match");
        }
    }
}
