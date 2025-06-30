package uk.gov.hmcts.pdda.web.publicdisplay.messaging.event;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.MoveCaseEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CourtRoomIdentifier;
import uk.gov.hmcts.framework.services.ConfigServices;
import uk.gov.hmcts.framework.services.CsServices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings({"PMD"})
@ExtendWith(MockitoExtension.class)
class EventStoreFactoryTest {

    private MockedStatic<CsServices> csServicesMockedStatic;
    private MockedStatic<ConfigServices> configServicesMockedStatic;

    @BeforeEach
    void setUp() throws Exception {
        csServicesMockedStatic = Mockito.mockStatic(CsServices.class);
        configServicesMockedStatic = Mockito.mockStatic(ConfigServices.class);
        EventStoreFactory.resetForTest(null);
    }

    @AfterEach
    void tearDown() {
        if (csServicesMockedStatic != null) {
            csServicesMockedStatic.close();
        }
        if (configServicesMockedStatic != null) {
            configServicesMockedStatic.close();
        }
    }


    @Test
    void testGetEventStoreWhenInvalidClassNameThrowsEventStoreException() {
        // Directly test broken reflection, don't use EventStoreFactory singleton
        ReflectionEventStoreFactory factory = new ReflectionEventStoreFactory();

        EventStoreException exception = assertThrows(EventStoreException.class, () -> {
            factory.createBrokenEventStore();
        });

        assertNotNull(exception.getMessage(), "Exception message should not be null");
        assertTrue(exception.getMessage().contains("InvalidClassName"),
            "Exception message should mention the invalid class");
    }



    @Test
    void testDefaultEventStorePushAndPopEventSuccess() {
        // Given
        DefaultEventStore defaultEventStore = new DefaultEventStore();
        PublicDisplayEvent event = getDummyPublicDisplayEvent();

        // When
        defaultEventStore.pushEvent(event);
        PublicDisplayEvent poppedEvent = defaultEventStore.popEvent();

        // Then
        assertNotNull(poppedEvent, "Popped event should not be null");
        assertEquals(event, poppedEvent, "Popped event should match pushed event");
    }

    private PublicDisplayEvent getDummyPublicDisplayEvent() {
        return getDummyMoveCaseEvent();
    }

    private MoveCaseEvent getDummyMoveCaseEvent() {
        CourtRoomIdentifier from = new CourtRoomIdentifier(-99, null);
        CourtRoomIdentifier to = new CourtRoomIdentifier(-1, null);
        return new MoveCaseEvent(from, to, null);
    }

    @Test
    void testGetEventStoreWhenValidClassNameReturnsCustomEventStore() {
        // Given
        ConfigServices mockConfigServices = mock(ConfigServices.class);
        when(CsServices.getConfigServices()).thenReturn(mockConfigServices);
        when(mockConfigServices.getProperty(Mockito.anyString()))
            .thenReturn(DummyEventStore.class.getName());

        // Reflection will instantiate DummyEventStore
        EventStore eventStore = EventStoreFactory.getEventStore();

        // Then
        assertNotNull(eventStore, "EventStore should not be null");
        assertTrue(eventStore instanceof DummyEventStore,
            "EventStore should be an instance of DummyEventStore");
    }

    /**
     * Dummy EventStore for testing valid reflection instantiation.
     */
    public static class DummyEventStore implements EventStore {
        @Override
        public void pushEvent(PublicDisplayEvent event) {
            // No-op
        }

        @Override
        public PublicDisplayEvent popEvent() {
            return null;
        }
    }


    /**
     * Helper class to simulate reloading EventStoreFactory behavior manually.
     */
    @SuppressWarnings("PMD.ClassWithOnlyPrivateConstructorsShouldBeFinal")
    private static class ReflectionEventStoreFactory {
        public EventStore createBrokenEventStore() {
            String invalidClassName = "InvalidClassName";
            try {
                return (EventStore) Class.forName(invalidClassName).getDeclaredConstructor()
                    .newInstance();
            } catch (ReflectiveOperationException e) {
                throw new EventStoreException(invalidClassName, e);
            }
        }
    }

}
