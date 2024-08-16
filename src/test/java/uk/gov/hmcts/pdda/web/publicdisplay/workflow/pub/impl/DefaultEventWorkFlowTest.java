package uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.impl;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.ActivateCaseEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CaseChangeInformation;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CourtRoomIdentifier;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType;
import uk.gov.hmcts.pdda.web.publicdisplay.configuration.DisplayConfigurationReader;
import uk.gov.hmcts.pdda.web.publicdisplay.types.RenderChanges;
import uk.gov.hmcts.pdda.web.publicdisplay.types.document.DisplayDocument;
import uk.gov.hmcts.pdda.web.publicdisplay.types.rotationset.DisplayRotationSet;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.WorkFlowContext;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine.DocumentsForEvent;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine.RulesEngine;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DefaultEventWorkFlowTest {

    private static final String TRUE = "Result is not True";

    private final WorkFlowContext mockWorkFlowContext = Mockito.mock(WorkFlowContext.class);
    private final DisplayConfigurationReader mockDisplayConfigurationReader =
        Mockito.mock(DisplayConfigurationReader.class);
    private final RenderChanges mockRenderChanges = Mockito.mock(RenderChanges.class);
    private final RulesEngine mockRulesEngine = Mockito.mock(RulesEngine.class);

    @InjectMocks
    private final DefaultEventWorkFlow classUnderTest = getClassUnderTest();

    @BeforeAll
    public static void setUp() throws Exception {
        Mockito.mockStatic(RulesEngine.class);
    }

    @AfterAll
    public static void tearDown() throws Exception {
        Mockito.clearAllCaches();
    }

    private DefaultEventWorkFlow getClassUnderTest() {
        ActivateCaseEvent event = getDummyActivateCaseEvent();
        Mockito.when(RulesEngine.getInstance()).thenReturn(mockRulesEngine);
        Mockito.when(mockRulesEngine.getDisplayDocumentTypesForEvent(event)).thenReturn(getDummyDocumentsForEvent());
        return new DefaultEventWorkFlow(mockWorkFlowContext, event);
    }

    @Test
    void testProcess() {
        Mockito.when(mockWorkFlowContext.getDisplayConfigurationReader()).thenReturn(mockDisplayConfigurationReader);
        Mockito.when(mockDisplayConfigurationReader.getRenderChanges(Mockito.isA(DisplayDocumentType[].class),
            Mockito.isA(CourtRoomIdentifier.class))).thenReturn(mockRenderChanges);
        Mockito.when(mockRenderChanges.getDocumentsToStartRendering()).thenReturn(new DisplayDocument[] {});
        Mockito.when(mockRenderChanges.getDocumentsToStopRendering()).thenReturn(new DisplayDocument[] {});
        Mockito.when(mockRenderChanges.getDisplayRotationSetsToStartRendering())
            .thenReturn(new DisplayRotationSet[] {});
        Mockito.when(mockRenderChanges.getDisplayRotationSetsToStopRendering()).thenReturn(new DisplayRotationSet[] {});
        boolean result = false;
        try {
            classUnderTest.process();
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    private CourtRoomIdentifier getDummyCourtRoomIdentifier() {
        return new CourtRoomIdentifier(Integer.valueOf(-99), null);
    }

    private CaseChangeInformation getDummyCaseChangeInformation() {
        return new CaseChangeInformation(false);
    }

    private ActivateCaseEvent getDummyActivateCaseEvent() {
        ActivateCaseEvent result =
            new ActivateCaseEvent(getDummyCourtRoomIdentifier(), getDummyCaseChangeInformation());
        result.setCaseActive(true);
        return result;
    }

    private DocumentsForEvent getDummyDocumentsForEvent() {
        return new DocumentsForEvent();
    }
}
