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
class AppendEventsTwentyNineHundredTenUtilsTest {

    @Test
    void appendEvent20910_whenDefOnCaseIdIsNull_appendsDefenceAndCaseClosedOnly() {
        // Arrange
        StringBuilder buffer = new StringBuilder();
        BranchEventXmlNode mockNode = Mockito.mock(BranchEventXmlNode.class);
        TranslationBundle mockBundle = Mockito.mock(TranslationBundle.class);
        Collection<DefendantName> emptyNames = Collections.emptyList();

        try (MockedStatic<RendererUtils> rendererUtils = Mockito.mockStatic(RendererUtils.class)) {
            // Force null to hit the branch
            rendererUtils.when(() -> RendererUtils.getDefendantOnCaseId(any(BranchEventXmlNode.class)))
                         .thenReturn(null);

            try (MockedStatic<TranslationUtils> translationUtils = Mockito.mockStatic(TranslationUtils.class)) {
                // Stub translations used in this method
                translationUtils.when(() -> TranslationUtils.translate(eq(mockBundle), eq("Defence")))
                                .thenReturn("Defence");
                translationUtils.when(() -> TranslationUtils.translate(eq(mockBundle), eq("Case_Closed")))
                                .thenReturn("Case closed");

                try (MockedStatic<AppendUtils> appendUtils = Mockito.mockStatic(AppendUtils.class)) {
                    // Make AppendUtils.append actually append to the provided StringBuilder
                    appendUtils.when(() -> AppendUtils.append(any(StringBuilder.class), any(String.class)))
                               .thenAnswer(invocation -> {
                                   StringBuilder sb = invocation.getArgument(0);
                                   String s = invocation.getArgument(1);
                                   sb.append(s);
                                   return null;
                               });

                    // Act
                    AppendEventsTwentyNineHundredTenUtils.appendEvent20910(buffer, mockNode, mockBundle, emptyNames);

                    // Assert - when defOnCaseId is null, name is skipped and we get: "Defence<space>Case closed"
                    assertEquals("Defence Case closed", buffer.toString());
                }
            }
        }
    }
}
