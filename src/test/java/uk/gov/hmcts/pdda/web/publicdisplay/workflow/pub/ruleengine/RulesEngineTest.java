package uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.MoveCaseEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CaseChangeInformation;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CourtRoomIdentifier;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.EventType;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentTypeUtils;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine.rules.DefendantNameChangedRule;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine.rules.Rule;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SuppressWarnings("PMD")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RulesEngineTest {

    private final RulesConfiguration mockRulesConfiguration = Mockito.mock(RulesConfiguration.class);

    @InjectMocks
    private final RulesEngine classUnderTest = getClassUnderTest();

    @BeforeAll
    public static void setUp() {
        Mockito.mockStatic(RulesConfiguration.class);
    }

    @AfterAll
    public static void tearDown() {
        Mockito.clearAllCaches();
    }

    private RulesEngine getClassUnderTest() {
        Mockito.when(RulesConfiguration.newConfiguration()).thenReturn(mockRulesConfiguration);
        return RulesEngine.getInstance();
    }

    @Test
    void testGetDisplayDocumentTypesForEvent() {
        Mockito.when(mockRulesConfiguration.getConditionalDocumentsForEvent(Mockito.isA(EventType.class)))
            .thenReturn(new ConditionalDocument[] {getDummyConditionalDocument()});
        DocumentsForEvent result = classUnderTest.getDisplayDocumentTypesForEvent(getDummyPublicDisplayEvent());
        assertNotNull(result, "Result is Null");
        assertNotNull(result.getDisplayDocumentTypes(), "Result is Null");
    }

    private PublicDisplayEvent getDummyPublicDisplayEvent() {
        return getDummyMoveCaseEvent();
    }

    private MoveCaseEvent getDummyMoveCaseEvent() {
        CourtRoomIdentifier from = new CourtRoomIdentifier(-99, null);
        CourtRoomIdentifier to = new CourtRoomIdentifier(-1, null);
        from.setCourtId(from.getCourtId());
        from.setCourtRoomId(from.getCourtRoomId());
        CaseChangeInformation caseChangeInformation = new CaseChangeInformation(true);
        MoveCaseEvent result = new MoveCaseEvent(from, to, caseChangeInformation);
        result.setFromCourtRoomIdentifier(from);
        result.setToCourtRoomIdentifier(to);
        return result;
    }

    private ConditionalDocument getDummyConditionalDocument() {
        DisplayDocumentType[] docTypes = {getDummyDisplayDocumentType("DailyList")};
        Rule[] rules = {new DefendantNameChangedRule()};
        return new ConditionalDocument(docTypes, rules, getDummyMoveCaseEvent().getEventType());
    }

    private DisplayDocumentType getDummyDisplayDocumentType(String descriptionCode) {
        Locale locale = Locale.UK;
        return DisplayDocumentTypeUtils.getDisplayDocumentType(descriptionCode, locale.getLanguage(),
            locale.getCountry());
    }
}
