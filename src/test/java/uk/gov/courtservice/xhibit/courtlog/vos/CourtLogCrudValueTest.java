package uk.gov.courtservice.xhibit.courtlog.vos;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("PMD")
class CourtLogCrudValueTest {

    @Test
    void testDefaultConstructorInitializesEmptyPropertyMap() {
        CourtLogCrudValue value = new CourtLogCrudValue();
        assertNotNull(value.getPropertyMap());
        assertTrue(value.getPropertyMap().isEmpty());
    }

    @Test
    void testVersionConstructorInitializesSuccessfully() {
        CourtLogCrudValue value = new CourtLogCrudValue(5);
        assertNotNull(value);
        assertTrue(value.getPropertyMap().isEmpty());
    }

    @Test
    void testSetAndGetPropertyWorksCorrectly() {
        CourtLogCrudValue value = new CourtLogCrudValue();
        value.setProperty("key1", "value1");

        assertEquals("value1", value.getProperty("key1"));
        assertEquals(1, value.getPropertyMap().size());
    }

    @Test
    void testSetPropertyMapReplacesExistingEntries() {
        CourtLogCrudValue value = new CourtLogCrudValue();

        Map<String, Object> initial = new ConcurrentHashMap<>();
        initial.put("oldKey", "oldValue");
        value.setPropertyMap(initial);

        Map<String, Object> updated = new ConcurrentHashMap<>();
        updated.put("newKey", "newValue");
        value.setPropertyMap(updated);

        Map<String, Object> result = value.getPropertyMap();
        assertEquals(1, result.size());
        assertEquals("newValue", result.get("newKey"));
        assertFalse(result.containsKey("oldKey"));
    }

    @Test
    void testGetPropertyMapReturnsUnmodifiableMap() {
        CourtLogCrudValue value = new CourtLogCrudValue();
        value.setProperty("a", "b");

        Map<String, Object> readOnly = value.getPropertyMap();
        assertThrows(UnsupportedOperationException.class, () -> readOnly.put("x", "y"));
    }

    @Test
    void testGetPropertyReturnsNullWhenKeyMissing() {
        CourtLogCrudValue value = new CourtLogCrudValue();
        assertNull(value.getProperty("nonexistent"));
    }

    @Test
    void testSetInCourtAndIsInCourtWorkAsExpected() {
        CourtLogCrudValue value = new CourtLogCrudValue();
        value.setInCourt(true);
        assertTrue(value.isInCourt());

        value.setInCourt(false);
        assertFalse(value.isInCourt());
    }

    @Test
    void testSetAndGetEntryFreeTextWorkAsExpected() {
        CourtLogCrudValue value = new CourtLogCrudValue();
        value.setEntryFreeText("Test free text");
        assertEquals("Test free text", value.getEntryFreeText());
    }

    @Test
    void testSetPropertyMapRejectsNullMap() {
        CourtLogCrudValue value = new CourtLogCrudValue();
        assertThrows(NullPointerException.class, () -> value.setPropertyMap(null));
    }

    @Test
    void testSetPropertyRejectsNullKey() {
        CourtLogCrudValue value = new CourtLogCrudValue();
        assertThrows(NullPointerException.class, () -> value.setProperty(null, "value"));
    }
}
