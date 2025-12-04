package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundle;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.DefendantName;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.BranchEventXmlNode;

import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SuppressWarnings("PMD")
class AppendEventsTwentyOneHundredToThirtyUtilsTest {

    @Test
    void appendEvent30600_whenDefOnCaseIdIsNull_appendsHearingFinishedTranslation() {
        // Arrange
        StringBuilder buffer = new StringBuilder();
        BranchEventXmlNode mockNode = Mockito.mock(BranchEventXmlNode.class);
        TranslationBundle mockBundle = Mockito.mock(TranslationBundle.class);
        Collection<DefendantName> emptyNames = Collections.emptyList();

        // Stub static RendererUtils.getDefendantOnCaseId(...) to return null
        try (MockedStatic<RendererUtils> rendererUtils = Mockito.mockStatic(RendererUtils.class)) {
            rendererUtils.when(() -> RendererUtils.getDefendantOnCaseId(any(BranchEventXmlNode.class)))
                         .thenReturn(null);

            // Stub TranslationUtils.translate(...) to return the expected string for the "Hearing_finished" key
            try (MockedStatic<TranslationUtils> translationUtils = Mockito.mockStatic(TranslationUtils.class)) {
                translationUtils.when(() -> TranslationUtils.translate(eq(mockBundle), eq("Hearing_finished")))
                                .thenReturn("Hearing finished");

                // Stub AppendUtils.append(buffer, text) to actually append to the buffer
                try (MockedStatic<AppendUtils> appendUtils = Mockito.mockStatic(AppendUtils.class)) {
                    appendUtils.when(() -> AppendUtils.append(any(StringBuilder.class), any(String.class)))
                               .thenAnswer(invocation -> {
                                   StringBuilder sb = invocation.getArgument(0);
                                   String s = invocation.getArgument(1);
                                   sb.append(s);
                                   return null;
                               });

                    // Act
                    AppendEventsTwentyOneHundredToThirtyUtils.appendEvent30600(buffer, mockNode,
                        mockBundle, emptyNames);

                    // Assert - branch where defOnCaseId == null should append the "Hearing finished" translation
                    assertEquals("Hearing finished", buffer.toString());
                }
            }
        }
    }
}
