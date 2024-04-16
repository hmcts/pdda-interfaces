package uk.gov.hmcts.pdda.common.publicdisplay.jms;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("PMD.TestClassWithoutTestCases")
class PublicDisplayNotifierTest {
    /** This will need modifying now that PDDA no longer uses ActiveMQ, 
     * remove the PMD.TestClassWithoutTestCases once this has been done. **/

    /*@Mock
    private TopicConnection mockTopicConnection;

    @Mock
    private TopicSession mockTopicSession;

    @Mock
    private ActiveMQObjectMessage mockActiveMqObjectMessage;

    @Mock
    private Topic mockTopic;

    @Mock
    private TopicPublisher mockTopicPublisher;

    @Mock
    private InitializationService mockInitializationService;

    @InjectMocks
    private final PublicDisplayNotifier classUnderTest = new PublicDisplayNotifier();

    @BeforeEach
    public void setup() throws Exception {
        Mockito.mockStatic(InitializationService.class);
    }

    @AfterEach
    public void teardown() throws Exception {
        Mockito.clearAllCaches();
    }

    @Test
    void testSendMessage() throws JMSException {
        // Setup
        PublicDisplayEvent publicDisplayEvent = getDummyPublicDisplayEvent();
        // Expects
        Mockito.when(mockTopicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE))
            .thenReturn(mockTopicSession);
        Mockito.when(mockTopicSession.createObjectMessage(Mockito.isA(Serializable.class)))
            .thenReturn(mockActiveMqObjectMessage);
        Mockito.when(mockActiveMqObjectMessage.getObject()).thenReturn(publicDisplayEvent);
        Mockito.when(InitializationService.getInstance()).thenReturn(mockInitializationService);
        Mockito.when(InitializationService.getInstance().getTopic()).thenReturn(mockTopic);
        Mockito.when(mockTopicSession.createPublisher(mockTopic)).thenReturn(mockTopicPublisher);
        // Run
        classUnderTest.sendMessage(publicDisplayEvent);
        assertNotNull(publicDisplayEvent, "Result is Null");
    }

    private PublicDisplayEvent getDummyPublicDisplayEvent() {
        return getDummyMoveCaseEvent();
    }

    private MoveCaseEvent getDummyMoveCaseEvent() {
        CourtRoomIdentifier from = new CourtRoomIdentifier(Integer.valueOf(-99), null);
        CourtRoomIdentifier to = new CourtRoomIdentifier(Integer.valueOf(-1), null);
        from.setCourtId(from.getCourtId());
        from.setCourtRoomId(from.getCourtRoomId());
        return new MoveCaseEvent(from, to, null);
    }*/
}
