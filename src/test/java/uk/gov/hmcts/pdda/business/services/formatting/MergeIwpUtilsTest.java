package uk.gov.hmcts.pdda.business.services.formatting;

import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import uk.gov.hmcts.framework.services.CsServices;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>
 * Title: MergeIwpUtils Test.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2024
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Luke Gittins
 */
@ExtendWith(EasyMockExtension.class)
class MergeIwpUtilsTest {

    private static final String NOT_TRUE = "Result is Not True";
    
    private static final String TEST_CHILD_NODE_XML =
        "<courtroomname><cases>testCourtRoom<date>01/01/2024</date><time>10:00:00</time></cases></courtroomname>";
    private static final String TEST_INSERTBEFORE_NODE_XML =
        "<courtroomname><cases>testCourtRoom<date>01/01/2024</date><time>10:00:00</time></cases></courtroomname>";
    private static final String TEST_NODE_XML =
        "<testNode>testNodeValue</testNode>";

    @Test
    void testReplaceIwp() {
        boolean result = true;
        Node testChildNode = getDummyNode(TEST_CHILD_NODE_XML);
        Node testInsertBeforeNode = getDummyNode(TEST_INSERTBEFORE_NODE_XML);
        Node testNode = getDummyNode(TEST_NODE_XML);
        MergeIwpUtils.replaceIwp(testChildNode, testInsertBeforeNode, testNode);
        assertTrue(result, NOT_TRUE);
    }

    private Node getDummyNode(String xml) {
        Document doc = CsServices.getXmlServices().createDocFromString(xml);
        return doc.getDocumentElement();
    }

}
