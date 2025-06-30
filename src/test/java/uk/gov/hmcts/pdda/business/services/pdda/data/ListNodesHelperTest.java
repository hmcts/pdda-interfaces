package uk.gov.hmcts.pdda.business.services.pdda.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings({"static-access", "PMD"})
class ListNodesHelperTest {

    private static final String TRUE = "Result is False";
    private static final String LISTHEADER_XML = "<cs:ListHeader>"
        + "<cs:ListCategory>Criminal</cs:ListCategory>" + "<cs:StartDate>2024-10-01</cs:StartDate>"
        + "<cs:EndDate>2024-10-01</cs:EndDate>" + "<cs:Version>DRAFT 1</cs:Version>"
        + "<cs:PublishedTime>2024-09-30T08:36:19.000</cs:PublishedTime>" + "</cs:ListHeader>";
    private static final String DEFENDANT_XML = "<cs:Defendants><cs:Defendant><cs:PersonalDetails>"
        + "<cs:Name>" + "<apd:CitizenNameForename>John</apd:CitizenNameForename>"
        + "<apd:CitizenNameForename>Fitzgerald</apd:CitizenNameForename>"
        + "<apd:CitizenNameSurname>Kennedy</apd:CitizenNameSurname>" + "</cs:Name>"
        + "</cs:PersonalDetails></cs:Defendant></cs:Defendants>";
    private static final String HEARING_XML =
        "<cs:Hearings><cs:Hearing>" + "<cs:HearingDetails HearingType=\"TPH\">"
            + "<cs:HearingDescription>Trial (Part Heard)</cs:HearingDescription>"
            + "<cs:HearingDate>2024-10-01</cs:HearingDate>" + "</cs:HearingDetails>"
            + "<cs:TimeMarkingNote>SITTING AT  10:00 am</cs:TimeMarkingNote>"
            + "<cs:CaseNumber>T87654321</cs:CaseNumber>" + DEFENDANT_XML
            + "</cs:Hearing></cs:Hearings>";
    private static final String SITTING_XML =
        "<cs:Sittings><cs:Sitting>" + "<cs:CourtRoomNumber>1</cs:CourtRoomNumber>"
            + "<cs:SittingAt>10:00:00</cs:SittingAt>" + HEARING_XML + "</cs:Sitting></cs:Sittings>";
    private static final String COURTLIST_XML = "<cs:CourtLists><cs:CourtList><cs:CourtHouse>"
        + "<cs:CourtHouseCode>404</cs:CourtHouseCode>"
        + "<cs:CourtHouseName>BIRMINGHAM</cs:CourtHouseName>" + "</cs:CourtHouse>" + SITTING_XML
        + "</cs:CourtList></cs:CourtLists>";
    private static final String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<cs:DailyList>" + LISTHEADER_XML + COURTLIST_XML + "</cs:DailyList>";


    @InjectMocks
    private final ListNodesHelper classUnderTest =
        new ListNodesHelper(Mockito.mock(ListObjectHelper.class));

    @Test
    void testDefaultConstructor() {
        boolean result = false;
        try {
            new ListNodesHelper();
            result = true;
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testProcessClobData() {
        boolean result = false;
        try {
            classUnderTest.processClobData(XML);
            result = true;
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
        assertTrue(result, TRUE);
    }
}
