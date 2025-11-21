package uk.gov.hmcts.pdda.business.services.publicdisplay.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Mockito-based JUnit tests for CppDataSourceFactory.
 */
@SuppressWarnings("PMD")
@ExtendWith(MockitoExtension.class)
class CppDataSourceFactoryTest {

    @Test
    void getDataSource_nullShortName_returnsNull() {
        assertNull(CppDataSourceFactory.getDataSource(null, new Date(), 1));
    }

    @Test
    void postProcessing_nullShortName_returnsEmptyCollection() {
        Collection<String> input = Arrays.asList("one", "two");
        Collection<?> result = CppDataSourceFactory.postProcessing(null, input);
        assertNotNull(result);
        assertTrue(result.isEmpty(), "Expected empty collection for null shortName");
    }

    @Test
    void postProcessing_unknownShortName_returnsEmptyCollection() {
        Collection<String> input = Arrays.asList("one", "two");
        Collection<?> result = CppDataSourceFactory.postProcessing("THIS_IS_NOT_A_VALID_TYPE", input);
        assertNotNull(result);
        assertTrue(result.isEmpty(), "Expected empty collection for unknown shortName");
    }

    @Test
    void private_getSortedList_sortsWhenMoreThanOneElement() throws Exception {
        List<String> data = new ArrayList<>(Arrays.asList("banana", "apple", "cherry"));

        Method getSortedList = CppDataSourceFactory.class.getDeclaredMethod("getSortedList", List.class);
        getSortedList.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<String> returned = (List<String>) getSortedList.invoke(null, data);

        assertEquals(Arrays.asList("apple", "banana", "cherry"), returned);
    }

    @Test
    void private_findAllObjects_findsAllEqualElements() throws Exception {
        List<String> data = new ArrayList<>(Arrays.asList("a", "b", "a", "c", "a"));

        Method findAllObjects = CppDataSourceFactory.class.getDeclaredMethod("findAllObjects", Object.class,
            List.class);
        findAllObjects.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<String> matches = (List<String>) findAllObjects.invoke(null, "a", data);

        assertNotNull(matches);
        assertEquals(3, matches.size());
        for (String s : matches) {
            assertEquals("a", s);
        }
    }

    @Test
    void private_getCourtRoomNumberFromName_parsesNumberOrDefaultsToOne() throws Exception {
        Method getCourtRoomNumberFromName = CppDataSourceFactory.class.getDeclaredMethod("getCourtRoomNumberFromName",
            String.class);
        getCourtRoomNumberFromName.setAccessible(true);

        int parsed = (Integer) getCourtRoomNumberFromName.invoke(null, "Court Room 12");
        assertEquals(12, parsed);

        // Implementation only parses pure numeric tokens like "3". "3A" will not parse -> returns default 1.
        int parsed2 = (Integer) getCourtRoomNumberFromName.invoke(null, "Theatre 3A");
        assertEquals(1, parsed2, "Implementation does not parse '3A' as 3; expects default 1");

        int defaulted = (Integer) getCourtRoomNumberFromName.invoke(null, "Main Court");
        assertEquals(1, defaulted, "Should default to 1 when no number found");
    }

    @Test
    void private_getMatchedCourtDetailValue_and_getMatchedAllCourtStatusValue_workWhenNoMatchingInfo()
        throws Exception {
        Method getMatchedCourtDetailValue = CppDataSourceFactory.class.getDeclaredMethod("getMatchedCourtDetailValue",
            List.class);
        getMatchedCourtDetailValue.setAccessible(true);
        @SuppressWarnings("unchecked")
        Object resultCourtDetail = getMatchedCourtDetailValue.invoke(null, Collections.emptyList());
        assertNull(resultCourtDetail);

        Method getMatchedAllCourtStatusValue = CppDataSourceFactory.class.getDeclaredMethod(
            "getMatchedAllCourtStatusValue", List.class);
        getMatchedAllCourtStatusValue.setAccessible(true);
        @SuppressWarnings("unchecked")
        Object resultAllCourt = getMatchedAllCourtStatusValue.invoke(null, Collections.emptyList());
        assertNull(resultAllCourt);
    }

    // Utility: verify that reflective invocation throws with meaningful message if method
    // signatures change
    private void reflectivelyInvoke(Method method, Object... args) throws Exception {
        try {
            method.setAccessible(true);
            method.invoke(null, args);
        } catch (InvocationTargetException ite) {
            throw ite;
        }
    }
}
