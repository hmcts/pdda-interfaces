package uk.gov.hmcts.pdda.business.services.formatting;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MergeNodeUtils {

    private MergeNodeUtils() {
    }
    
    public static Map<String, String> getNodeMapValues(final List<String> nodeNames, final Node node) {
        return getNodeMapValues(new LinkedHashMap<>(), nodeNames, node);
    }

    private static Map<String, String> getNodeMapValues(final Map<String, String> map, final List<String> nodeNames,
        final Node node) {
        Map<String, String> results = map;
        // Check ChildNodes
        if (node.hasChildNodes()) {
            for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                Node childNode = node.getChildNodes().item(i);
                results.putAll(getNodeMapValues(results, nodeNames, childNode));
            }
        }
        // Check this Node
        if (node.getNodeType() == Node.ELEMENT_NODE && nodeNames.contains(node.getNodeName())
            && results.get(node.getNodeName()) == null) {
            // Add the Name and the value to the map of values to match on
            results.put(node.getNodeName(), node.getTextContent());
        }
        return results;
    }

    public static Node getParentNodeByType(final Node node, String nodeName) {
        Node result = null;
        if (node != null) {
            if (node.getNodeType() == Node.ELEMENT_NODE && nodeName.equals(node.getNodeName())) {
                result = node;
                return result;
            }
            if (node.getParentNode() != null && Node.ELEMENT_NODE == node.getNodeType()) {
                result = getParentNodeByType(node.getParentNode(), nodeName);
            }
        }
        return result;
    }

    public static Node getParentNodeByType(Node nodeToMergeAtParentLevel, Node node) {
        if (getParentNodeByType(nodeToMergeAtParentLevel, FirmTag.RESERVE_LIST.value) != null) {
            return node.getParentNode();
        }
        return node;
    }
    
    public static List<Node> getParentNodes(List<Node> nodes) {
        List<Node> parentNodes = new ArrayList<>();
        for (Node node : nodes) {
            // get all parent nodes
            Node parent = node.getParentNode();
            if (!parentNodes.contains(parent)) {
                parentNodes.add(parent);
            }
        }
        return parentNodes;
    }
    
    public static Node getParentNodeToMerge(Document baseDocument, Node nodeToMerge) {
        Node parentNodeToMerge = nodeToMerge.getParentNode();
        return baseDocument.importNode(parentNodeToMerge, true);
    }

    public static boolean containsFixedDateFlag(Node node) {
        return node.getNodeName().contains(WarnedTag.WITH_FIXED_DATE.value)
            || node.getNodeName().contains(WarnedTag.WITHOUT_FIXED_DATE.value);
    }
    
    public static void removeMergedNodes(List<Node> nodesToMerge, List<Node> nodesMerged) {
        for (int nodeNo = nodesToMerge.size(); nodeNo > 0; nodeNo--) {
            Node nodeToMerge = nodesToMerge.get(nodeNo - 1);
            if (nodesMerged.contains(nodeToMerge)) {
                nodesToMerge.remove(nodeNo - 1);
            }
        }
    }
    
    public static void setNodeAttribute(Node node, String attributeName, String attributeValue) {
        ((Element) node).setAttribute(attributeName, attributeValue);
    }

    public static String getNodeAttribute(Node node, String attributeName) {
        return ((Element) node).getAttribute(attributeName);
    }
    
    public static boolean hasChildNode(Node node, String nodeName) {
        return !getNodeMapValues(Arrays.asList(nodeName), node).isEmpty();
    }
}
