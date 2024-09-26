package uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.EventType;
import uk.gov.hmcts.DummyEventUtil;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentTypeUtils;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.exceptions.UnrecognizedEventException;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine.exceptions.RulesConfigurationException;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine.rules.DefendantNameChangedRule;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine.rules.Rule;

import java.util.Locale;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RulesConfigurationTest {

    private final SAXParserFactory mockSaxParserFactory = Mockito.mock(SAXParserFactory.class);
    private final SAXParser mockSaxParser = Mockito.mock(SAXParser.class);
    private final XMLReader mockXmlReader = Mockito.mock(XMLReader.class);
    private final RulesConfigurationHandler mockRulesConfigurationHandler =
        Mockito.mock(RulesConfigurationHandler.class);

    @InjectMocks
    private final RulesConfiguration classUnderTest = getClassUnderTest();

    @BeforeAll
    public static void setUp() {
        Mockito.mockStatic(SAXParserFactory.class);
    }

    @AfterAll
    public static void tearDown() {
        Mockito.clearAllCaches();
    }

    private RulesConfiguration getClassUnderTest() {
        RulesConfiguration result = null;
        try {
            Mockito.when(SAXParserFactory.newInstance()).thenReturn(mockSaxParserFactory);
            Mockito.when(mockSaxParserFactory.newSAXParser()).thenReturn(mockSaxParser);
            Mockito.when(mockSaxParser.getXMLReader()).thenReturn(mockXmlReader);
            result = new RulesConfiguration(mockRulesConfigurationHandler);
        } catch (ParserConfigurationException | SAXException e) {
            // Will never happen on mocked items
            fail(e);
        }
        return result;
    }

    @Test
    void testNewConfiguration() {
        @SuppressWarnings("static-access")
        RulesConfiguration result = classUnderTest.newConfiguration();
        assertNotNull(result, "Result is Null");
    }

    @Test
    void testGetConditionalDocumentsForEvent() throws SAXException {
        Mockito.when(mockRulesConfigurationHandler.getEventMappings())
            .thenReturn(getDummyEventMappings());
        classUnderTest.loadConfiguration();
        ConditionalDocument[] results =
            classUnderTest.getConditionalDocumentsForEvent(getDummyEventType());
        assertNotNull(results, "Result is Null");
        assertTrue(results[0].isDocumentValidForEvent(getDummyPublicDisplayEvent()),
            "Result is not True");
        // Test the invalid type
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            assertFalse(results[0].isDocumentValidForEvent(DummyEventUtil.getAddCaseEvent()),
                "Result is not False");
        });
    }

    @Test
    void testUnrecognizedEventException() {
        Assertions.assertThrows(UnrecognizedEventException.class, () -> {
            classUnderTest.loadConfiguration();
            classUnderTest.getConditionalDocumentsForEvent(null);
        });
    }

    @Test
    void testRulesConfigurationException() {
        Assertions.assertThrows(RulesConfigurationException.class, () -> {
            throw new RulesConfigurationException("Test");
        });
        Assertions.assertThrows(RulesConfigurationException.class, () -> {
            throw new RulesConfigurationException("Test");
        });
    }

    @Test
    void testEventMappingsGetConditionalDocumentsForEventFailure() {
        Assertions.assertThrows(RulesConfigurationException.class, () -> {
            EventMappings eventMappings = getDummyEventMappings();
            eventMappings.getConditionalDocumentsForEvent(null);
        });
    }

    protected PublicDisplayEvent getDummyPublicDisplayEvent() {
        return DummyEventUtil.getMoveCaseEvent();
    }

    private EventType getDummyEventType() {
        return EventType.getEventType(EventType.MOVE_CASE_EVENT);
    }

    private EventMappings getDummyEventMappings() {
        EventMappings result = new EventMappings();
        result.putConditionalDocumentsForEvent(getDummyEventType(), getDummyConditionalDocument());
        return result;
    }

    private ConditionalDocument getDummyConditionalDocument() {
        DisplayDocumentType[] docTypes = {getDummyDisplayDocumentType("DailyList")};
        Rule[] rules = {new DefendantNameChangedRule()};
        return new ConditionalDocument(docTypes, rules,
            DummyEventUtil.getMoveCaseEvent().getEventType());
    }

    private DisplayDocumentType getDummyDisplayDocumentType(String descriptionCode) {
        Locale locale = Locale.UK;
        return DisplayDocumentTypeUtils.getDisplayDocumentType(descriptionCode,
            locale.getLanguage(), locale.getCountry());
    }
}
