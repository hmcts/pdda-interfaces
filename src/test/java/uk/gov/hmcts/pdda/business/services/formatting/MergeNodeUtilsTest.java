package uk.gov.hmcts.pdda.business.services.formatting;

import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled.DocumentUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**

 * Title: MergeNodeUtils Test.


 * Description:


 * Copyright: Copyright (c) 2024


 * Company: CGI

 * @author Luke Gittins
 */
@ExtendWith(EasyMockExtension.class)
class MergeNodeUtilsTest {

    private static final String NULL = "Result is Null";
    private static final String NOT_NULL = "Result is Not Null";
    private static final String NOT_TRUE = "Result is Not True";
    private static final String NOT_EQUAL = "Result is Not Equal";

    private static final String BASE_XML =
        "<cs:CourtList><cs:ReserveList>TestCourtHouse</cs:ReserveList></cs:CourtList>";

    private static final String TEST = "TEST";
    private static final String TEST_VALUE = "TEST_VALUE";
    
    @Test
    void testGetParentNodeByType() throws SAXException, IOException, ParserConfigurationException {
        // Setup
        Node nodeToMergeAtParentLevel =
            getDummyDoc(BASE_XML).getFirstChild().getChildNodes().item(0);
        Node node = getDummyDoc(BASE_XML).getFirstChild().getChildNodes().item(0);
        // Run & Check
        assertNotNull(MergeNodeUtils.getParentNodeByType(nodeToMergeAtParentLevel, node), NULL);
    }

    @Test
    void testGetParentNodeByTypeNull()
        throws SAXException, IOException, ParserConfigurationException {
        // Setup
        String xmlParentLevel = "<cs:CourtList></cs:CourtList>";
        String xmlNode = "<cs:CourtList></cs:CourtList>";

        Node nodeToMergeAtParentLevel =
            getDummyDoc(xmlParentLevel).getFirstChild().getChildNodes().item(0);
        Node node = getDummyDoc(xmlNode).getFirstChild().getChildNodes().item(0);
        // Run & Check
        assertNull(MergeNodeUtils.getParentNodeByType(nodeToMergeAtParentLevel, node), NOT_NULL);
    }

    @Test
    void testGetParentNodes() throws SAXException, IOException, ParserConfigurationException {
        // Setup
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            nodes.add(getDummyDoc(BASE_XML).getFirstChild().getChildNodes().item(0));
        }
        // Run & Check
        assertNotNull(MergeNodeUtils.getParentNodes(nodes), NULL);
    }

    @Test
    void testGetParentNodeToMerge() throws SAXException, IOException, ParserConfigurationException {
        // Setup
        Document baseDocument = getDummyDoc(BASE_XML);
        Node node = getDummyDoc(BASE_XML).getFirstChild().getChildNodes().item(0);
        // Run & Check
        assertNotNull(MergeNodeUtils.getParentNodeToMerge(baseDocument, node), NULL);
    }
    
    @Test
    void testContainsFixedDateFlagWith() throws SAXException, IOException, ParserConfigurationException {
        // Setup
        String xml = "<cs:CourtList><cs:WithFixedDate>Test</cs:WithFixedDate></cs:CourtList>";
        Node node = getDummyDoc(xml).getFirstChild().getChildNodes().item(0);
        // Run & Check
        assertTrue(MergeNodeUtils.containsFixedDateFlag(node), NOT_TRUE);
    }
    
    @Test
    void testContainsFixedDateFlagWithout() throws SAXException, IOException, ParserConfigurationException {
        // Setup
        String xml = "<cs:CourtList><cs:WithoutFixedDate>Test</cs:WithoutFixedDate></cs:CourtList>";
        Node node = getDummyDoc(xml).getFirstChild().getChildNodes().item(0);
        // Run & Check
        assertTrue(MergeNodeUtils.containsFixedDateFlag(node), NOT_TRUE);
    }
    
    @Test
    void testRemoveMergedNodes() throws SAXException, IOException, ParserConfigurationException {
        // Setup
        List<Node> nodesToMerge = new ArrayList<>();
        List<Node> nodesMerged = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Node baseNode = getDummyDoc(BASE_XML).getFirstChild().getChildNodes().item(0);
            nodesToMerge.add(baseNode);
            nodesMerged.add(baseNode);
        }
        boolean result = true;
        // Run
        MergeNodeUtils.removeMergedNodes(nodesToMerge, nodesMerged);
        // Check
        assertTrue(result, NOT_TRUE);
    }
    
    @Test
    void testGetAndSetNodeAttribute() throws SAXException, IOException, ParserConfigurationException {
        // Setup
        Node node = getDummyDoc(BASE_XML).getFirstChild().getChildNodes().item(0);
        // Run
        MergeNodeUtils.setNodeAttribute(node, TEST, TEST_VALUE);
        String result = MergeNodeUtils.getNodeAttribute(node, TEST);
        // Check
        assertEquals(TEST_VALUE, result, NOT_EQUAL);
    }

    private Document getDummyDoc(String xml)
        throws SAXException, IOException, ParserConfigurationException {
        return DocumentUtils.createInputDocument(xml);
    }
}
