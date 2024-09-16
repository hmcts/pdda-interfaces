package uk.gov.hmcts.pdda.web.publicdisplay.messaging.event;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.MoveCaseEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CourtRoomIdentifier;
import uk.gov.hmcts.framework.services.ConfigServices;
import uk.gov.hmcts.framework.services.CsServices;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EventStoreFactoryTest {

    private static final String TRUE = "Result is not True";

    @Mock
    private CsServices mockCsServices;

    @Mock
    private ConfigServices mockConfigServices;

    @Mock
    private EventStore mockEventStore;

    @InjectMocks
    private final DefaultEventStore eventStoreUnderTest = new DefaultEventStore();

    @BeforeAll
    public static void setUp() {
        Mockito.mockStatic(CsServices.class);
        Mockito.mockStatic(ConfigServices.class);
    }

    @AfterAll
    public static void tearDown() {
        Mockito.clearAllCaches();
    }

    @Test
    void testDefaultConstructor() {
        boolean result;
        new EventStoreFactory();
        result = true;

        assertTrue(result, TRUE);
    }

    @Test
    void testGetEventStoreSuccess() {
        Mockito.when(CsServices.getConfigServices()).thenReturn(mockConfigServices);
        Mockito.when(mockConfigServices.getProperty(Mockito.isA(String.class))).thenReturn(null);
        boolean result = false;
        try {
            EventStoreFactory.getEventStore();
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }

        assertTrue(result, TRUE);
    }

    @Test
    void testGetEventStoreFailure() {
        Assertions.assertThrows(EventStoreException.class, () -> {
            Mockito.when(CsServices.getConfigServices()).thenReturn(mockConfigServices);
            Mockito.when(mockConfigServices.getProperty(Mockito.isA(String.class))).thenReturn("DefaultEventStore()");
            EventStoreFactory.getEventStore();
        });
    }

    @Test
    void testDefaultEventStorePushEvent() {
        boolean result;
        PublicDisplayEvent publicDisplayEvent = getDummyPublicDisplayEvent();
        eventStoreUnderTest.pushEvent(publicDisplayEvent);
        eventStoreUnderTest.popEvent();
        result = true;

        assertTrue(result, TRUE);
    }

    private PublicDisplayEvent getDummyPublicDisplayEvent() {
        return getDummyMoveCaseEvent();
    }

    private MoveCaseEvent getDummyMoveCaseEvent() {
        CourtRoomIdentifier from = new CourtRoomIdentifier(-99, null);
        CourtRoomIdentifier to = new CourtRoomIdentifier(-1, null);
        from.setCourtId(from.getCourtId());
        from.setCourtRoomId(from.getCourtRoomId());
        return new MoveCaseEvent(from, to, null);
    }
}
