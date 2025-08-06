package uk.gov.hmcts.pdda.business.services.formatting;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Arrays;
import java.util.List;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

/**
 * WarnedListMergeUtils.
 */
public final class WarnedListUtils {

    private WarnedListUtils() {

    }

    public static String[] getNodePositionArray() {
        return new String[] {WarnedTag.FIXED_DATE.value, WarnedTag.CASE_NUMBER.value};
    }

    public static boolean isWithFixedDateToMerge(Node node, boolean isWarnedList) {
        return isWarnedList
            && !MergeNodeUtils.getNodeMapValues(Arrays.asList(WarnedTag.WITH_FIXED_DATE.value), node).isEmpty();
    }

    public static boolean isWithoutFixedDateToMerge(Node node, boolean isWarnedList) {
        return isWarnedList
            && !MergeNodeUtils.getNodeMapValues(Arrays.asList(WarnedTag.WITHOUT_FIXED_DATE.value), node).isEmpty();
    }

    public static boolean isWarnedListChildNodeValid(Node childNode, Node childNodeToMerge,
        boolean isWithFixedDateToMerge, boolean isWithoutFixedDateToMerge, String mergeType, boolean isFirmList,
        boolean isWarnedList) {
        boolean isWithFixedDateChild = isWithFixedDateToMerge(childNode, isWarnedList);
        boolean isWithoutFixedDateChild = isWithoutFixedDateToMerge(childNode, isWarnedList);

        // We've passed the point to merge, so merge here
        return isWithFixedDateToMerge && isWithoutFixedDateChild
            || (isWithFixedDateToMerge && isWithFixedDateChild || isWithoutFixedDateToMerge && isWithoutFixedDateChild)
                && MergeComparisonUtils.getNodePositionForSorting(childNode, childNodeToMerge, getNodePositionArray(),
                    mergeType, isFirmList, isWarnedList) < 0;
    }

    public static void mergeBlankWarnedList(Document baseDocument, List<Node> nodes, List<Node> nodesToMerge,
        String[] nodeMatchArray, String mergeType, boolean isFirmList, boolean isWarnedList) {
        // loop over all nodes
        for (Node node : nodes) {
            // loop over all nodes to merge to find the correct court list to merge into
            for (Node nodeToMerge : nodesToMerge) {
                if (MergeComparisonUtils.compareNodes(nodeMatchArray, node, nodeToMerge.getParentNode(), mergeType,
                    isFirmList, isWarnedList) == 0) {
                    // insert this node after last child.
                    Node childNodeToMerge = baseDocument.importNode(nodeToMerge, true);
                    node.appendChild(childNodeToMerge);
                }
            }
        }
    }

    public static void mergeWarnedNodes(Document baseDocument, List<Node> parentNodes, List<Node> nodesToMerge,
        Node cppListNodesInXhibit, XPathExpression[] listRootNodeExpression, String[] nodeMatchArray, String mergeType,
        boolean isFirmList, boolean isWarnedList, Document... documents) throws XPathExpressionException {
        // if its not in the parent node it means it's blank and doesn't have
        // any
        // with/withouts so in this case we need to manually add
        // it in.
        if (!parentNodes.contains(cppListNodesInXhibit)) {
            for (Node nodeToMerge2 : MergeDocumentUtils.getNodesToMerge2(listRootNodeExpression, documents)) {
                if (MergeComparisonUtils.compareNodes(nodeMatchArray, cppListNodesInXhibit, nodeToMerge2, mergeType,
                    isFirmList, isWarnedList) == 0) {
                    // get all with/withouts of cpp
                    mergeWarnedNode(baseDocument, cppListNodesInXhibit, nodeToMerge2.getChildNodes(),
                        nodesToMerge);
                }
            }
        }
    }

    private static void mergeWarnedNode(Document baseDocument, Node cppListNodesInXhibit, NodeList allWithWithoutCpp,
        List<Node> nodesToMerge) {
        for (int i2 = 0; i2 < allWithWithoutCpp.getLength(); i2++) {
            // insert all with/withouts below the last child node of
            // xhibit
            Node childNodeToMerge = allWithWithoutCpp.item(i2);
            if (MergeNodeUtils.containsFixedDateFlag(childNodeToMerge)) {
                // insert this node after last child.
                Node tempChildNodeToMerge = baseDocument.importNode(childNodeToMerge, true);
                cppListNodesInXhibit.appendChild(tempChildNodeToMerge);
                if (nodesToMerge.contains(childNodeToMerge)) {
                    nodesToMerge.remove(childNodeToMerge);
                }

            }
        }
    }
}
