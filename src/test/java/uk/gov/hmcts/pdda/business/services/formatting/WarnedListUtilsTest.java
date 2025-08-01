package uk.gov.hmcts.pdda.business.services.formatting;

import org.eclipse.tags.shaded.org.apache.xpath.jaxp.XPathExpressionImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled.DocumentUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**

 * Title: WarnedListUtils Test.


 * Description:


 * Copyright: Copyright (c) 2024


 * Company: CGI

 * @author Luke Gittins
 */

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WarnedListUtilsTest {

    private static final String NOT_TRUE = "Result is Not True";

    private static final String IWP = "IWP";

    private static final String BASE_NODES =
        "<cs:CourtList><cs:WithFixedDate><cs:FixedDate>2</cs:FixedDate></cs:WithFixedDate></cs:CourtList>";
    private static final String CHILD_NODES =
        "<cs:CourtList><cs:FixedDate>1</cs:FixedDate></cs:CourtList>";
    private static final String CPP_LIST_NODES = "<cs:CppListNode></cs:CppListNode>";

    @Mock
    private XPathExpressionImpl mockXPathExpressionImpl;

    @Mock
    private Node mockNode;

    @Test
    void testIsWarnedListChildNodeValid()
        throws SAXException, IOException, ParserConfigurationException {
        // Setup
        Node node = getDummyDoc(BASE_NODES).getFirstChild().getChildNodes().item(0);
        Node nodeToMerge = getDummyDoc(CHILD_NODES).getFirstChild().getChildNodes().item(0);
        // Run & Check
        assertTrue(WarnedListUtils.isWarnedListChildNodeValid(node, nodeToMerge, true, false, IWP,
            false, true), NOT_TRUE);
    }

    @Test
    void testMergeBlankWarnedList() throws SAXException, IOException, ParserConfigurationException {
        // Setup
        List<Node> nodes = new ArrayList<>();
        List<Node> nodesToMerge = new ArrayList<>();
        Document baseDoc = getDummyDoc(BASE_NODES);
        Node node = baseDoc.getFirstChild().getChildNodes().item(0);
        nodes.add(node);
        Node nodeToMerge = getDummyDoc(CHILD_NODES).getFirstChild().getChildNodes().item(0);
        nodesToMerge.add(nodeToMerge);
        String[] nodeMatchArray = {""};
        boolean result = true;
        // Run & Check
        WarnedListUtils.mergeBlankWarnedList(baseDoc, nodes, nodesToMerge, nodeMatchArray, IWP,
            true, false);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testMergeWarnedNodes()
        throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
        // Setup
        Mockito.mockStatic(MergeDocumentUtils.class);
        Mockito.mockStatic(MergeComparisonUtils.class);
        Mockito.mockStatic(MergeNodeUtils.class);

        List<Node> nodes = new ArrayList<>();
        List<Node> nodesToMerge = new ArrayList<>();

        Document baseDoc = getDummyDoc(BASE_NODES);
        Node node = baseDoc.getFirstChild().getChildNodes().item(0);
        nodes.add(node);
        Node nodeToMerge = getDummyDoc(CHILD_NODES).getFirstChild().getChildNodes().item(0);
        nodesToMerge.add(nodeToMerge);
        XPathExpression[] exception = {mockXPathExpressionImpl};
        Document[] documents = {baseDoc, baseDoc};
        Mockito.when(MergeDocumentUtils.getNodesToMerge2(exception, documents)).thenReturn(nodes);
        String[] nodeMatchArray = {""};
        Mockito.when(MergeComparisonUtils.compareNodes(nodeMatchArray, mockNode, nodes.get(0), IWP,
            Boolean.TRUE, Boolean.TRUE)).thenReturn(0);
        Node cppListNode = getDummyDoc(CPP_LIST_NODES).getDocumentElement();

        boolean result = true;
        // Run
        WarnedListUtils.mergeWarnedNodes(baseDoc, nodes, nodesToMerge, cppListNode, exception,
            nodeMatchArray, IWP, true, false, documents);
        // Check
        assertTrue(result, NOT_TRUE);
        Mockito.clearAllCaches();
    }


    private Document getDummyDoc(String xml)
        throws SAXException, IOException, ParserConfigurationException {
        return DocumentUtils.createInputDocument(xml);
    }
}
