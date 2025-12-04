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
class AppendEventsTwentyNineHundredUtilsTest {

    @Test
    void appendEvent20906_whenDefOnCaseIdIsNull_appendsDefenceAndCaseOpenedTranslationOnly() {
        // Arrange
        StringBuilder buffer = new StringBuilder();
        BranchEventXmlNode mockNode = Mockito.mock(BranchEventXmlNode.class);
        TranslationBundle mockBundle = Mockito.mock(TranslationBundle.class);
        Collection<DefendantName> emptyNames = Collections.emptyList();

        try (MockedStatic<RendererUtils> rendererUtils = Mockito.mockStatic(RendererUtils.class)) {
            // force null to exercise the branch
            rendererUtils.when(() -> RendererUtils.getDefendantOnCaseId(any(BranchEventXmlNode.class)))
                         .thenReturn(null);

            try (MockedStatic<TranslationUtils> translationUtils = Mockito.mockStatic(TranslationUtils.class)) {
                // stub translations used in this method
                translationUtils.when(() -> TranslationUtils.translate(eq(mockBundle), eq("Defence")))
                                .thenReturn("Defence");
                translationUtils.when(() -> TranslationUtils
                    .translate(eq(mockBundle), eq("Case_Opened"), eq("Defence")))
                                .thenReturn("Case opened - Defence");

                try (MockedStatic<AppendUtils> appendUtils = Mockito.mockStatic(AppendUtils.class)) {
                    // implement AppendUtils.append to actually append to the StringBuilder
                    appendUtils.when(() -> AppendUtils.append(any(StringBuilder.class), any(String.class)))
                               .thenAnswer(invocation -> {
                                   StringBuilder sb = invocation.getArgument(0);
                                   String s = invocation.getArgument(1);
                                   sb.append(s);
                                   return null;
                               });

                    // Act
                    AppendEventsTwentyNineHundredUtils.appendEvent20906(buffer, mockNode, mockBundle, emptyNames);

                    // Assert - when defOnCaseId is null, we should have: "<Defence><space><Case opened - Defence>"
                    assertEquals("Defence Case opened - Defence", buffer.toString());
                }
            }
        }
    }

    @Test
    void appendEvent20909_whenDefOnCaseIdIsNull_appendsDefenceClosingSpeechOnly() {
        // Arrange
        StringBuilder buffer = new StringBuilder();
        BranchEventXmlNode mockNode = Mockito.mock(BranchEventXmlNode.class);
        TranslationBundle mockBundle = Mockito.mock(TranslationBundle.class);
        Collection<DefendantName> emptyNames = Collections.emptyList();

        try (MockedStatic<RendererUtils> rendererUtils = Mockito.mockStatic(RendererUtils.class)) {
            // force null to exercise the branch
            rendererUtils.when(() -> RendererUtils.getDefendantOnCaseId(any(BranchEventXmlNode.class)))
                         .thenReturn(null);

            try (MockedStatic<TranslationUtils> translationUtils = Mockito.mockStatic(TranslationUtils.class)) {
                translationUtils.when(() -> TranslationUtils.translate(eq(mockBundle), eq("Defence_Closing_Speech")))
                                .thenReturn("Defence Closing Speech");

                try (MockedStatic<AppendUtils> appendUtils = Mockito.mockStatic(AppendUtils.class)) {
                    appendUtils.when(() -> AppendUtils.append(any(StringBuilder.class), any(String.class)))
                               .thenAnswer(invocation -> {
                                   StringBuilder sb = invocation.getArgument(0);
                                   String s = invocation.getArgument(1);
                                   sb.append(s);
                                   return null;
                               });

                    // Act
                    AppendEventsTwentyNineHundredUtils.appendEvent20909(buffer, mockNode, mockBundle, emptyNames);

                    // Assert - when defOnCaseId is null, the defendant name branch is
                    // skipped and the closing speech translation is appended
                    assertEquals("Defence Closing Speech", buffer.toString());
                }
            }
        }
    }
}
