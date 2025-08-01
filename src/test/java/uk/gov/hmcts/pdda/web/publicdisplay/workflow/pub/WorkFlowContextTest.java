package uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.pdda.web.publicdisplay.configuration.DisplayConfigurationReader;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WorkFlowContextTest {

    private final DisplayConfigurationReader mockDisplayConfigurationReader =
        Mockito.mock(DisplayConfigurationReader.class);
    private final WorkFlowContext classUnderTest = WorkFlowContext.newInstance(mockDisplayConfigurationReader);

    @BeforeAll
    public static void setUp() {
    }

    @AfterAll
    public static void tearDown() {
    }

    @Test
    void testWorkFlowContext() {
        classUnderTest.setDataContext(classUnderTest.getDataContext());
        assertNotNull(classUnderTest.getDisplayConfigurationReader(), "Result is Null");
    }
}
