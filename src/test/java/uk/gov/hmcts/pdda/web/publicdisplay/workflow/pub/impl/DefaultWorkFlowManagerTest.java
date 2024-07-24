package uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.impl;

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
import uk.gov.courtservice.xhibit.common.publicdisplay.events.ActivateCaseEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.ConfigurationChangeEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.MoveCaseEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CaseChangeInformation;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CourtRoomIdentifier;
import uk.gov.hmcts.pdda.common.publicdisplay.types.configuration.CourtConfigurationChange;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType;
import uk.gov.hmcts.pdda.web.publicdisplay.configuration.DisplayConfigurationReader;
import uk.gov.hmcts.pdda.web.publicdisplay.types.RenderChanges;
import uk.gov.hmcts.pdda.web.publicdisplay.types.document.DisplayDocument;
import uk.gov.hmcts.pdda.web.publicdisplay.types.rotationset.DisplayRotationSet;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.WorkFlow;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.WorkFlowContext;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.exceptions.UnrecognizedEventException;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine.DocumentsForEvent;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine.RulesEngine;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DefaultWorkFlowManagerTest {

    private static final String TRUE = "Result is not True";
    private final WorkFlowContext mockWorkFlowContext = Mockito.mock(WorkFlowContext.class);
    private final DisplayConfigurationReader mockDisplayConfigurationReader =
        Mockito.mock(DisplayConfigurationReader.class);
    private final RulesEngine mockRulesEngine = Mockito.mock(RulesEngine.class);
    private final RenderChanges mockRenderChanges = Mockito.mock(RenderChanges.class);

    @InjectMocks
    private final DefaultWorkFlowManager classUnderTest = getClassUnderTest();

    @BeforeAll
    public static void setUp() throws Exception {
        Mockito.mockStatic(RulesEngine.class);
    }

    @AfterAll
    public static void tearDown() throws Exception {
        Mockito.clearAllCaches();
    }

    private DefaultWorkFlowManager getClassUnderTest() {
        return new DefaultWorkFlowManager(mockWorkFlowContext);
    }

    @Test
    void testUnrecognizedEventException() {
        Assertions.assertThrows(UnrecognizedEventException.class, () -> {
            throw new UnrecognizedEventException(getDummyMoveCaseEvent());
        });
    }

    @Test
    void testProcess() {
        // Setup
        ActivateCaseEvent event = getDummyActivateCaseEvent();
        // Expects
        Mockito.when(mockWorkFlowContext.getDisplayConfigurationReader()).thenReturn(mockDisplayConfigurationReader);
        Mockito.when(mockDisplayConfigurationReader.getRenderChanges(Mockito.isA(DisplayDocumentType[].class),
            Mockito.isA(CourtRoomIdentifier.class))).thenReturn(mockRenderChanges);
        Mockito.when(RulesEngine.getInstance()).thenReturn(mockRulesEngine);
        Mockito.when(mockRulesEngine.getDisplayDocumentTypesForEvent(event)).thenReturn(getDummyDocumentsForEvent());
        Mockito.when(mockRenderChanges.getDocumentsToStartRendering()).thenReturn(new DisplayDocument[] {});
        Mockito.when(mockRenderChanges.getDocumentsToStopRendering()).thenReturn(new DisplayDocument[] {});
        Mockito.when(mockRenderChanges.getDisplayRotationSetsToStartRendering())
            .thenReturn(new DisplayRotationSet[] {});
        Mockito.when(mockRenderChanges.getDisplayRotationSetsToStopRendering()).thenReturn(new DisplayRotationSet[] {});
        classUnderTest.process(event);
        assertNotNull(event, "Result is Null");
    }

    @SuppressWarnings("static-access")
    @Test
    void testGetInstance() {
        boolean result = false;
        try {
            classUnderTest.getInstance(mockWorkFlowContext);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testGetWorkFlowsForEventDefaultEventWorkFlow() {
        boolean result = false;
        try {
            testGetWorkFlowsForEvent(getDummyActivateCaseEvent());
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testGetWorkFlowsForEventConfigurationChangeWorkFlow() {
        Mockito.when(mockWorkFlowContext.getDisplayConfigurationReader()).thenReturn(mockDisplayConfigurationReader);
        boolean result = false;
        try {
            testGetWorkFlowsForEvent(getDummyConfigurationChangeEvent());
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testGetWorkFlowsForEventMoveCaseWorkFlow() {
        boolean result = false;
        try {
            MoveCaseEvent event = getDummyMoveCaseEvent();
            Mockito.when(RulesEngine.getInstance()).thenReturn(mockRulesEngine);
            Mockito.when(mockRulesEngine.getDisplayDocumentTypesForEvent(event))
                .thenReturn(getDummyDocumentsForEvent());
            testGetWorkFlowsForEvent(event);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    private void testGetWorkFlowsForEvent(PublicDisplayEvent event) {
        WorkFlow[] result = classUnderTest.getWorkFlowsForEvent(event);
        assertNotNull(result, "Result is Null");
    }

    private ActivateCaseEvent getDummyActivateCaseEvent() {
        return new ActivateCaseEvent(getDummyCourtRoomIdentifier(), getDummyCaseChangeInformation());
    }

    private CaseChangeInformation getDummyCaseChangeInformation() {
        return new CaseChangeInformation(false);
    }

    private CourtRoomIdentifier getDummyCourtRoomIdentifier() {
        return new CourtRoomIdentifier(Integer.valueOf(-99), null);
    }

    private ConfigurationChangeEvent getDummyConfigurationChangeEvent() {
        return new ConfigurationChangeEvent(getDummyCourtConfigurationChange());
    }

    private CourtConfigurationChange getDummyCourtConfigurationChange() {
        return new CourtConfigurationChange(81);
    }

    private MoveCaseEvent getDummyMoveCaseEvent() {
        return new MoveCaseEvent(getDummyCourtRoomIdentifier(), getDummyCourtRoomIdentifier(),
            getDummyCaseChangeInformation());
    }

    private DocumentsForEvent getDummyDocumentsForEvent() {
        return new DocumentsForEvent();
    }
}
