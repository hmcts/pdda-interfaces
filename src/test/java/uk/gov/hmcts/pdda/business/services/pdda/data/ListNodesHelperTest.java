package uk.gov.hmcts.pdda.business.services.pdda.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import uk.gov.hmcts.pdda.business.services.formatting.MergeDocumentUtils;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled.DocumentUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings({"static-access", "PMD.ExcessiveImports", "PMD.CouplingBetweenObjects",
    "PMD.TooManyMethods"})
class ListNodesHelperTest {

    private static final String TRUE = "Result is False";
    private static final String DAILY_LIST = "DailyList";
    private static final String DEFENDANTS_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<cs:DailyList>" + "<cs:Defendants>\n"
            + "<cs:Defendant>\n" + "<cs:PersonalDetails><cs:Name>"
            + "<apd:CitizenNameForename>John</apd:CitizenNameForename>"
            + "<apd:CitizenNameForename>Fitzgerald</apd:CitizenNameForename>"
            + "<apd:CitizenNameSurname>Kennedy</apd:CitizenNameSurname>"
            + "</cs:Name>"
            + "</cs:PersonalDetails></cs:Defendant></cs:Defendants>" + "</cs:DailyList>";


    @InjectMocks
    private final ListNodesHelper classUnderTest = new ListNodesHelper();

    @Test
    void testProcessNodes() {
        boolean result = false;
        try {
            Document document = DocumentUtils.createInputDocument(DEFENDANTS_XML);
            String[] rootNodes = {DAILY_LIST};
            List<Node> nodes = MergeDocumentUtils
                .getNodeList(MergeDocumentUtils.getRootNodeExpressionArray(rootNodes), document);
            classUnderTest.processNodes(nodes);
            result = true;
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
        assertTrue(result, TRUE);
    }
}
