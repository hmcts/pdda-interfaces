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
class AppendEventsTwentySixHundredUtilsTest {

    @Test
    void appendEvent20606_whenDefOnCaseIdIsNull_appendsAppellantAndCaseOpenedOnly() {
        // Arrange
        StringBuilder buffer = new StringBuilder();
        BranchEventXmlNode mockNode = Mockito.mock(BranchEventXmlNode.class);
        TranslationBundle mockBundle = Mockito.mock(TranslationBundle.class);
        Collection<DefendantName> emptyNames = Collections.emptyList();

        try (MockedStatic<RendererUtils> rendererUtils = Mockito.mockStatic(RendererUtils.class)) {
            // Force null to exercise the branch
            rendererUtils.when(() -> RendererUtils.getDefendantOnCaseId(any(BranchEventXmlNode.class)))
                         .thenReturn(null);

            try (MockedStatic<TranslationUtils> translationUtils = Mockito.mockStatic(TranslationUtils.class)) {
                // Stub translations used in the method
                translationUtils.when(() -> TranslationUtils.translate(eq(mockBundle), eq("Appellant")))
                                .thenReturn("Appellant");
                translationUtils.when(() -> TranslationUtils.translate(eq(mockBundle), eq("Case_Opened")))
                                .thenReturn("Case opened");

                try (MockedStatic<AppendUtils> appendUtils = Mockito.mockStatic(AppendUtils.class)) {
                    appendUtils.when(() -> AppendUtils.append(any(StringBuilder.class), any(String.class)))
                               .thenAnswer(invocation -> {
                                   StringBuilder sb = invocation.getArgument(0);
                                   String s = invocation.getArgument(1);
                                   sb.append(s);
                                   return null;
                               });

                    // Act
                    AppendEventsTwentySixHundredUtils.appendEvent20606(buffer, mockNode, mockBundle, emptyNames);

                    // Assert - when defOnCaseId is null, we expect "Appellant<space><space>Case opened"
                    // Note: implementation appends Appellant, SPACE, then later a SPACE, then Case_Opened
                    assertEquals("Appellant  Case opened", buffer.toString());
                }
            }
        }
    }
}
