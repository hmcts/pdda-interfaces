package uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.EventType;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine.rules.RuleFlyweightPool;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RulesConfigurationHandlerTest {

    private static final String TRUE = "Result is not True";
    
    private static final String URI = "uri";
    
    private static final String LOCALNAME = "localName";

    private final Attributes mockAttributes = Mockito.mock(Attributes.class);
    private final RuleFlyweightPool mockRuleFlyweightPool = Mockito.mock(RuleFlyweightPool.class);
    private final EventType mockEventType = Mockito.mock(EventType.class);

    @InjectMocks
    private RulesConfigurationHandler handlerUnderTest = new RulesConfigurationHandler();

    @BeforeEach
    public void setUp() {
        Mockito.mockStatic(RuleFlyweightPool.class);
        Mockito.mockStatic(EventType.class);
        Mockito.mockStatic(DisplayDocumentType.class);
    }

    @AfterEach
    public void tearDown() {
        Mockito.clearAllCaches();
    }

    @Test
    void testRulesConfigurationHandlerRuleRef() throws SAXException {
        Mockito.when(RuleFlyweightPool.getInstance()).thenReturn(mockRuleFlyweightPool);
        handlerUnderTest = new RulesConfigurationHandler();
        boolean result = false;
        try {
            handlerUnderTest.startElement(URI, LOCALNAME, "RuleRef", mockAttributes);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testRulesConfigurationHandlerEvent() throws SAXException {
        Mockito.when(EventType.getEventType(Mockito.isA(String.class))).thenReturn(mockEventType);
        boolean result = false;
        try {
            handlerUnderTest.startElement(URI, LOCALNAME, "Event", mockAttributes);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testRulesConfigurationHandlerConditionalDocument() throws SAXException {
        Mockito.when(DisplayDocumentType.getDisplayDocumentTypes(Mockito.isA(String.class)))
            .thenReturn(new DisplayDocumentType[] {});
        boolean result = false;
        try {
            handlerUnderTest.startElement(URI, LOCALNAME, "ConditionalDocument", mockAttributes);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testRulesConfigurationHandlerRule() throws SAXException {
        Mockito.when(RuleFlyweightPool.getInstance()).thenReturn(mockRuleFlyweightPool);
        boolean result = false;
        try {
            handlerUnderTest.startElement(URI, LOCALNAME, "Rule", mockAttributes);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }
}
