package uk.gov.hmcts.pdda.courtlog.vos;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD")
class CourtLogSubscriptionValueTest {

    @Mock
    private CourtLogViewValue courtLogViewValue;

    @Test
    void toString_includesAllFields_whenPopulated() {
        // Arrange
        when(courtLogViewValue.toString()).thenReturn("CourtLogViewValue[id=123]");
        CourtLogSubscriptionValue value = new CourtLogSubscriptionValue();
        value.setHearingId(10);
        value.setCourtSiteId(20);
        value.setCourtRoomId(30);
        value.setPnEventType(40);
        value.setCourtUrn("URN-ABC");
        value.setCourtLogViewValue(courtLogViewValue);

        // Act
        String actual = value.toString();

        // Assert
        String expected = "CourtLogSubscriptionValue["
                + "hearingId=10"
                + ", courtSiteId=20"
                + ", courtRoomId=30"
                + ", pnEventType=40"
                + ", courtUrn=URN-ABC"
                + ", courtLogViewValue=CourtLogViewValue[id=123]"
                + "]";
        assertEquals(expected, actual);
    }

    @Test
    void toString_handlesNulls_whenUninitialized() {
        // Arrange
        CourtLogSubscriptionValue value = new CourtLogSubscriptionValue();

        // Act
        String actual = value.toString();

        // Assert
        String expected = "CourtLogSubscriptionValue["
                + "hearingId=null"
                + ", courtSiteId=null"
                + ", courtRoomId=null"
                + ", pnEventType=null"
                + ", courtUrn=null"
                + ", courtLogViewValue=null"
                + "]";
        assertEquals(expected, actual);
    }
}
