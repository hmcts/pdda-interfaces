package uk.gov.hmcts.pdda.web.publicdisplay.events;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayDao;
import uk.gov.hmcts.pdda.business.services.pdda.data.RepositoryHelper;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.AllCaseStatusValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.PublicDisplayValue;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD")
class CrLiveEventHelperTest {

    // --- Reflection helpers -----------------------------------------------------

    private static void injectRepositoryHelper(RepositoryHelper helper) throws Exception {
        Field f = CrLiveEventHelper.class.getDeclaredField("repositoryHelper");
        f.setAccessible(true);
        f.set(null, helper);
    }

    private static void clearRepositoryHelper() throws Exception {
        Field f = CrLiveEventHelper.class.getDeclaredField("repositoryHelper");
        f.setAccessible(true);
        f.set(null, null);
    }

    /** Read the raw eventTime field — getter always synthesizes a value. */
    private static LocalDateTime getRawEventTime(PublicDisplayValue v) throws Exception {
        Field f = PublicDisplayValue.class.getDeclaredField("eventTime");
        f.setAccessible(true);
        return (LocalDateTime) f.get(v);
    }

    /** Read the raw event field. */
    private static Object getRawEvent(PublicDisplayValue v) throws Exception {
        Field f = PublicDisplayValue.class.getDeclaredField("event");
        f.setAccessible(true);
        return f.get(v);
    }

    @AfterEach
    void cleanup() throws Exception {
        clearRepositoryHelper();
    }

    // --- Tests -----------------------------------------------------------------

    @Test
    void populateEventIfPresent_noRepositoryEntry_doesNothing() throws Exception {

        RepositoryHelper helper = Mockito.mock(RepositoryHelper.class);
        var repo = Mockito.mock(
            uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayRepository.class);

        Mockito.when(helper.getXhbCrLiveDisplayRepository()).thenReturn(repo);
        Mockito.when(repo.findByCourtRoomSafe(anyInt())).thenReturn(Optional.empty());

        injectRepositoryHelper(helper);

        AllCaseStatusValue v = new AllCaseStatusValue();
        v.setCourtRoomId(10);
        v.setEvent(null);
        v.setEventTime(null);

        CrLiveEventHelper.populateEventIfPresent(v);

        assertNull(getRawEvent(v), "No repository entry → event must remain null");
        assertNull(getRawEventTime(v), "No repository entry → eventTime must remain null");
    }


    @Test
    void populateEventIfPresent_nonMatchingScheduledHearing_doesNotAttach() throws Exception {

        String xml = """
                <event>
                    <date>24/11/2025</date>
                    <time>00:01</time>
                    <scheduled_hearing_id>999</scheduled_hearing_id>
                </event>
                """;

        XhbCrLiveDisplayDao dao = Mockito.mock(XhbCrLiveDisplayDao.class);
        Mockito.when(dao.getStatus()).thenReturn(xml);

        var repo = Mockito.mock(
            uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayRepository.class);

        Mockito.when(repo.findByCourtRoomSafe(anyInt())).thenReturn(Optional.of(dao));

        RepositoryHelper helper = Mockito.mock(RepositoryHelper.class);
        Mockito.when(helper.getXhbCrLiveDisplayRepository()).thenReturn(repo);

        injectRepositoryHelper(helper);

        AllCaseStatusValue v = new AllCaseStatusValue();
        v.setCourtRoomId(10);
        v.setScheduledHearingId(123); // DOES NOT MATCH xml(999)
        v.setEvent(null);
        v.setEventTime(null);

        CrLiveEventHelper.populateEventIfPresent(v);

        assertNull(getRawEvent(v), "Scheduled hearing mismatch → event must remain null");
        assertNull(getRawEventTime(v), "Scheduled hearing mismatch → eventTime must remain null");
    }


    @Test
    void populateEventIfPresent_matchingScheduledHearing_attaches() throws Exception {

        String xml = """
                <event>
                    <date>24/11/2025</date>
                    <time>00:01</time>
                    <scheduled_hearing_id>555</scheduled_hearing_id>
                </event>
                """;

        XhbCrLiveDisplayDao dao = Mockito.mock(XhbCrLiveDisplayDao.class);
        Mockito.when(dao.getStatus()).thenReturn(xml);

        var repo = Mockito.mock(
            uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayRepository.class);

        Mockito.when(repo.findByCourtRoomSafe(anyInt())).thenReturn(Optional.of(dao));

        RepositoryHelper helper = Mockito.mock(RepositoryHelper.class);
        Mockito.when(helper.getXhbCrLiveDisplayRepository()).thenReturn(repo);

        injectRepositoryHelper(helper);

        AllCaseStatusValue v = new AllCaseStatusValue();
        v.setCourtRoomId(10);
        v.setScheduledHearingId(555); // MATCHES xml
        v.setEvent(null);
        v.setEventTime(null);

        CrLiveEventHelper.populateEventIfPresent(v);

        assertNotNull(getRawEvent(v), "Matching ID → event should be attached");
        assertNotNull(getRawEventTime(v), "Matching ID → eventTime should be attached");
    }
}
