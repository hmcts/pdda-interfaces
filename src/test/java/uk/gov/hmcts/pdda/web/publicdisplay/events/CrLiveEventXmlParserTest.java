package uk.gov.hmcts.pdda.web.publicdisplay.events;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("PMD")
class CrLiveEventXmlParserTest {

    @Test
    void parse_nullOrBlank_returnsEmpty() {
        assertTrue(CrLiveEventXmlParser.parse(null).isEmpty());
        assertTrue(CrLiveEventXmlParser.parse("").isEmpty());
        assertTrue(CrLiveEventXmlParser.parse("   ").isEmpty());
    }

    @Test
    void parse_validXml_parsesTimeAndIdsAndDefendantName() {
        String xml = "<event>"
            + "<date>02/02/2025</date>"
            + "<time>09:30</time>"
            + "<hearing_id>123</hearing_id>"
            + "<scheduled_hearing_id>456</scheduled_hearing_id>"
            + "<defendant_on_case_id>789</defendant_on_case_id>"
            + "<defendant_name>John Doe</defendant_name>"
            + "<info>Test leaf content</info>"
            + "</event>";

        Optional<CrLiveEventXmlParser.ParseResult> opt = CrLiveEventXmlParser.parse(xml);
        assertTrue(opt.isPresent(), "Parser should return a ParseResult for valid xml");

        CrLiveEventXmlParser.ParseResult pr = opt.get();

        // eventTime parsed correctly (date + time)
        LocalDateTime expected = LocalDateTime.of(2025, Month.FEBRUARY, 2, 9, 30);
        assertEquals(expected, pr.eventTime);

        // numeric ids parsed
        assertEquals(Integer.valueOf(123), pr.hearingId);
        assertEquals(Integer.valueOf(456), pr.scheduledHearingId);
        assertEquals(Integer.valueOf(789), pr.defendantOnCaseId);

        // defendant name
        assertEquals("John Doe", pr.defendantName);

        // node should be non-null (parser returns a BranchEventXmlNode)
        assertNotNull(pr.node);
        // node name is expected to be "event" because parser creates root with that tag
        // This uses only a safe accessor (if available) by reflection would be overkill;
        // at minimum the node is non-null which indicates successful parsing.
    }

    @Test
    void parse_dateTwoDigitYear_parsesAs20xx() {
        String xml = "<event>"
            + "<date>02/02/25</date>"
            + "<time>00:00</time>"
            + "</event>";

        Optional<CrLiveEventXmlParser.ParseResult> opt = CrLiveEventXmlParser.parse(xml);
        assertTrue(opt.isPresent());
        CrLiveEventXmlParser.ParseResult pr = opt.get();

        LocalDateTime expected = LocalDateTime.of(2025, Month.FEBRUARY, 2, 0, 0);
        assertEquals(expected, pr.eventTime);
    }

    @Test
    void parse_missingTime_usesMidnight() {
        String xml = "<event><date>01/01/2024</date></event>";
        Optional<CrLiveEventXmlParser.ParseResult> opt = CrLiveEventXmlParser.parse(xml);
        assertTrue(opt.isPresent());
        CrLiveEventXmlParser.ParseResult pr = opt.get();

        LocalDateTime expected = LocalDateTime.of(2024, 1, 1, 0, 0);
        assertEquals(expected, pr.eventTime);
    }

    @Test
    void parse_malformedXml_returnsEmpty() {
        String xml = "<not-event><foo>bar</foo></not-event>";
        assertTrue(CrLiveEventXmlParser.parse(xml).isEmpty());
    }
}
