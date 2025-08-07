package uk.gov.hmcts.pdda.business.services.formatting;

import org.w3c.dom.Node;
import uk.gov.hmcts.pdda.business.services.formatting.FirmListXmlMergeUtils.Attribute;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class FirmListUtils {
    
    private static final String SITTINGDATE = "SittingDate";

    private FirmListUtils() {
    }
    
    public static String[] getNodePositionArray() {
        return new String[] {Tag.COURTROOM_NUMBER.value, FirmTag.HEARING_DATE.value, Tag.SITTING_AT.value};
    }
    
    public static boolean isReservedNode(final Node node) {
        return MergeNodeUtils.getParentNodeByType(node, FirmTag.RESERVE_LIST.value) != null;
    }
    
    private static String getSittingAttribute(Node node) {
        if (node.hasAttributes()) {
            return node.getAttributes().getNamedItem(SITTINGDATE).getNodeValue();
        } else {
            return null;
        }
    }
    
    public static String getUniqueKey(Node node) {
        String[] args = {FirmTag.HEARING_DATE.value};
        return MergeNodeUtils.getNodeMapValues(Arrays.asList(args), node).get(FirmTag.HEARING_DATE.value);
    }
    
    public static String getSittingDateFromCourtNode(Node courtListNode) {
        String sittingDate = null;
        if (courtListNode != null && Node.ELEMENT_NODE == courtListNode.getNodeType()) {
            String[] args = {FirmTag.HEARING_DATE.value};
            List<String> mapArray = Arrays.asList(args);
            sittingDate = MergeNodeUtils.getNodeAttribute(courtListNode, Attribute.SITTING_DATE.value);
            if (sittingDate == null) {
                Map<String, String> map = MergeNodeUtils.getNodeMapValues(mapArray, courtListNode);
                if (!map.isEmpty()) {
                    sittingDate = map.get(FirmTag.HEARING_DATE.value);
                }
            }
        }
        return sittingDate;
    }
    
    public static Node getCourtNodeNodeBeforeInsert(Node courtListsNode, Node courtNodeToMerge) {
        Node result = null;
        String sittingDateToMerge = getSittingDateFromCourtNode(courtNodeToMerge);
        if (sittingDateToMerge != null) {
            for (int nodeNo = 0; nodeNo < courtListsNode.getChildNodes().getLength(); nodeNo++) {
                Node courtListNode = courtListsNode.getChildNodes().item(nodeNo);
                String sittingDate = getSittingDateFromCourtNode(courtListNode);
                if (sittingDate != null && sittingDateToMerge.compareTo(sittingDate) < 0) {
                    result = courtListNode;
                    break;
                }
            }
        }
        return result;
    }
    
    public static Node cloneSiblingNode(Node sittingNode, String tagName) {
        Node parentNode = sittingNode.getParentNode();
        Node result = parentNode.cloneNode(false);
        for (int i = 0; i < parentNode.getChildNodes().getLength(); i++) {
            Node childNode = parentNode.getChildNodes().item(i);
            boolean isMatch = MergeNodeUtils.getNodeMapValues(Arrays.asList(tagName), childNode).get(tagName) != null;
            if (isMatch) {
                result.appendChild(childNode);
            }
        }
        return result;
    }
    
    public static String getNodeUniqueKey(String nodeUniqueKey, String nodeToMergeUniqueKey) {
        if (nodeToMergeUniqueKey != null && !nodeToMergeUniqueKey.equals(nodeUniqueKey)
            || nodeUniqueKey == null) {
            return nodeToMergeUniqueKey;
        }
        return nodeUniqueKey;
    }
    
    /**
     * Sorts the court list items depending on their sitting date.
     * 
     * @param results original array
     * @return the sorted results
     */
    public static List<Node> sortCourtLists(List<Node> results) {

        Comparator<Node> courtListComparator = new Comparator<>() {
            @Override
            public int compare(Node o1, Node o2) {
                String o1Date = getSittingAttribute(o1);
                String o2Date = getSittingAttribute(o2);
                if (o1Date != null && o2Date != null) {
                    return o1Date.compareTo(o2Date);
                } else if (o2Date == null) {
                    return -1;
                } else {
                    return 1;
                }
            }
        };

        Collections.sort(results, courtListComparator);
        return results;
    }
}
