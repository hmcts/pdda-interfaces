package uk.gov.courtservice.xhibit.common.publicdisplay.events.types;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.pdda.courtlog.vos.CourtLogSubscriptionValue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD")
class CaseCourtLogInformationTest {

    @Mock
    private CourtLogSubscriptionValue mockSubscriptionValue;

    @Test
    void toString_includesAllFields_whenPopulated() {
        // Arrange
        when(mockSubscriptionValue.toString()).thenReturn("CourtLogSubscriptionValue[hearingId=55]");
        CaseCourtLogInformation info = new CaseCourtLogInformation(mockSubscriptionValue, true);

        // Act
        String actual = info.toString();

        // Assert
        String expected = "CaseCourtLogInformation["
                + "caseActive=true"
                + ", clSubscriptionValue=CourtLogSubscriptionValue[hearingId=55]"
                + "]";
        assertEquals(expected, actual);
    }

    @Test
    void toString_handlesNullSubscriptionValue() {
        // Arrange
        CaseCourtLogInformation info = new CaseCourtLogInformation(null, false);

        // Act
        String actual = info.toString();

        // Assert
        String expected = "CaseCourtLogInformation["
                + "caseActive=false"
                + ", clSubscriptionValue=null"
                + "]";
        assertEquals(expected, actual);
    }
}
