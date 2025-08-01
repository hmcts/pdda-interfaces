package uk.gov.hmcts.pdda.business.services.formatting;

import org.w3c.dom.Node;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class MergeComparisonUtils {

    private static final String IWP = "IWP";
    private static final int LESS_THAN = -1;
    private static final int EQUAL_TO = 0;
    private static final int GREATER_THAN = 1;

    private MergeComparisonUtils() {

    }

    public static int compareNode1Null(final String nodeName, boolean isFirmList, boolean isWarnedList) {
        if (isWarnedList && WarnedTag.FIXED_DATE == WarnedTag.fromString(nodeName)) {
            return LESS_THAN;
        } else if (isFirmList && Tag.SITTING_AT == Tag.fromString(nodeName)) {
            return LESS_THAN;
        }
        return GREATER_THAN;
    }

    public static int compareNode2Null(final String nodeName, boolean isFirmList, boolean isWarnedList) {
        if (isWarnedList && WarnedTag.FIXED_DATE == WarnedTag.fromString(nodeName)) {
            return GREATER_THAN;
        } else if (isFirmList && Tag.SITTING_AT == Tag.fromString(nodeName)) {
            return GREATER_THAN;
        }
        return LESS_THAN;
    }

    public static int compareNodes(final String[] filterArray, final Node node1, final Node node2, String mergeType,
        boolean isFirmList, boolean isWarnedList) {
        List<String> nodesToMatchList = Arrays.asList(filterArray);
        return compareNodes(MergeNodeUtils.getNodeMapValues(nodesToMatchList, node1),
            MergeNodeUtils.getNodeMapValues(nodesToMatchList, node2), mergeType, nodesToMatchList, isFirmList,
            isWarnedList);
    }

    public static int compareNodes(Map<String, String> map1, Map<String, String> map2, String mergeType,
        List<String> nodesToMatchList, boolean isFirmList, boolean isWarnedList) {
        int nodeDiff = EQUAL_TO;
        // Check if we have nodes to match

        // Loop through the nodes and check the values match
        for (String nodeName : nodesToMatchList) {
            String node1Value = map1.get(nodeName);
            String node2Value = map2.get(nodeName);
            nodeDiff = compareNodes(nodeName, node1Value, node2Value, IWP.equals(mergeType), nodeDiff, isFirmList,
                isWarnedList);
            if (nodeDiff != EQUAL_TO) {
                nodeDiff = nodeDiff < 0 ? LESS_THAN : GREATER_THAN;
                break;
            }
        }

        return nodeDiff;
    }

    public static int compareNodes(String nodeName, String node1Value, String node2Value, boolean isInternetWebpage,
        int nodeDiff, boolean isFirmList, boolean isWarnedList) {
        if (CompareXmlNodes.compareNumeric(node1Value, node2Value)) {
            return Integer.valueOf(node2Value).compareTo(Integer.valueOf(node1Value));
        } else if (CompareXmlNodes.compareToEmpty(node1Value, node2Value)) {
            return compareNode2Null(nodeName, isFirmList, isWarnedList);
        } else if (CompareXmlNodes.compareToEmpty(node2Value, node1Value)) {
            return compareNode1Null(nodeName, isFirmList, isWarnedList);
        } else if (CompareXmlNodes.isEmpty(node1Value) && CompareXmlNodes.isEmpty(node2Value)) {
            return EQUAL_TO;
        } else if (node1Value != null && node2Value != null) {
            return CompareXmlNodes.compareNodes(nodeName, node1Value, node2Value, isInternetWebpage, isWarnedList);
        }
        return nodeDiff;
    }

    public static int getNodePositionForSorting(final Node node1, final Node node2, String[] nodePositionArray,
        String mergeType, boolean isFirmList, boolean isWarnedList) {
        return compareNodes(nodePositionArray, node1, node2, mergeType, isFirmList, isWarnedList);
    }
}
