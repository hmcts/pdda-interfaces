package uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.impl;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.ConfigurationChangeEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.types.configuration.CourtConfigurationChange;
import uk.gov.hmcts.pdda.web.publicdisplay.configuration.DisplayConfigurationReader;
import uk.gov.hmcts.pdda.web.publicdisplay.types.RenderChanges;
import uk.gov.hmcts.pdda.web.publicdisplay.types.document.DisplayDocument;
import uk.gov.hmcts.pdda.web.publicdisplay.types.rotationset.DisplayRotationSet;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.WorkFlowContext;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings("PMD")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ConfigurationChangeWorkFlowTest {

    private static final String TRUE = "Result is not True";

    private final WorkFlowContext mockWorkFlowContext = Mockito.mock(WorkFlowContext.class);
    private final DisplayConfigurationReader mockDisplayConfigurationReader =
        Mockito.mock(DisplayConfigurationReader.class);
    private final RenderChanges mockRenderChanges = Mockito.mock(RenderChanges.class);
    private final ConfigurationChangeWorkFlow classUnderTest = getClassUnderTest();

    @BeforeAll
    public static void setUp() {
    }

    @AfterAll
    public static void tearDown() {
    }

    private ConfigurationChangeWorkFlow getClassUnderTest() {
        Mockito.when(mockWorkFlowContext.getDisplayConfigurationReader()).thenReturn(mockDisplayConfigurationReader);
        Mockito.when(mockDisplayConfigurationReader.getRenderChanges(Mockito.isA(CourtConfigurationChange.class)))
            .thenReturn(mockRenderChanges);
        return new ConfigurationChangeWorkFlow(mockWorkFlowContext, getDummyConfigurationChangeEvent());

    }

    @Test
    void testProcess() {
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

    private ConfigurationChangeEvent getDummyConfigurationChangeEvent() {
        return new ConfigurationChangeEvent(getDummyCourtConfigurationChange());
    }

    private CourtConfigurationChange getDummyCourtConfigurationChange() {
        return new CourtConfigurationChange(81);
    }
}
