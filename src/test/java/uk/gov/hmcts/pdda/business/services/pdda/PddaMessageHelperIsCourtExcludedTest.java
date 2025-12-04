package uk.gov.hmcts.pdda.business.services.pdda;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SuppressWarnings({"PMD"})
class PddaMessageHelperIsCourtExcludedTest {

    @Test
    void testIsCourtExcluded_NullDocument() {
        assertFalse(PddaMessageHelper.isCourtExcluded(null, "123"), "Null document should return false");
    }

    @Test
    void testIsCourtExcluded_InvalidFormat() {
        assertFalse(PddaMessageHelper.isCourtExcluded("InvalidDoc_456", "456"),
            "Non-matching format should return false");
    }

    @Test
    void testIsCourtExcluded_NullCourtList() {
        assertFalse(PddaMessageHelper.isCourtExcluded("PublicDisplay_123_filename.xml", null),
            "Null courtsExcluded should return false");
    }

    @Test
    void testIsCourtExcluded_EmptyCourtList() {
        assertFalse(PddaMessageHelper.isCourtExcluded("PublicDisplay_123_filename.xml", ""),
            "Empty courtsExcluded should return false");
    }

    @Test
    void testIsCourtExcluded_ExactMatch() {
        assertTrue(PddaMessageHelper.isCourtExcluded("PublicDisplay_456_otherstuff", "123, 456 ,789"),
            "Matching court code should return true");
    }

    @Test
    void testIsCourtExcluded_NoMatch() {
        assertFalse(PddaMessageHelper.isCourtExcluded("PublicDisplay_999_something", "123,456,789"),
            "Non-matching court code should return false");
    }

    @Test
    void testIsCourtExcluded_WhitespaceTrimmed() {
        assertTrue(PddaMessageHelper.isCourtExcluded("PublicDisplay_001_somefile", " 001 , 002 "),
            "Should trim whitespace and match");
    }

    @Test
    void testIsCourtExcluded_ExtraUnderscores() {
        assertTrue(PddaMessageHelper.isCourtExcluded("PublicDisplay_321__doubleunderscores", "321"),
            "Extra underscores after code shouldn't affect parsing");
    }
    
    @Test
    void testDailyListFormat() {
        assertTrue(PddaMessageHelper.isCourtExcluded("DailyList_456_20251203101501", "456"),
            "DailyList format should be handled");
    }
    
    @Test
    void testFirmListFormat() {
        assertTrue(PddaMessageHelper.isCourtExcluded("FirmList_456_20251203101501", "456"),
            "FirmList format should be handled");
    }
    
    @Test
    void testWarnedListFormat() {
        assertTrue(PddaMessageHelper.isCourtExcluded("WarnedList_456_20251203101501", "456"),
            "WarningList format should be handled");
    }
    
    @Test
    void testWebPageFormat() {
        assertTrue(PddaMessageHelper.isCourtExcluded("WebPage_456_20251203101501", "456"),
            "WebPage format should be handled");
    }
}
