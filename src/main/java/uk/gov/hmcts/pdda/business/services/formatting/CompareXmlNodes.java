package uk.gov.hmcts.pdda.business.services.formatting;

import org.w3c.dom.Node;

import java.util.Locale;

public final class CompareXmlNodes {

    private static final String COURT = "Court ";
    private static final String CROWN_COURT = "Crown Court ";
    private static final String COURTHOUSE_NAMEPREFIX = "at ";
    private static final String EMPTY_STRING = "";

    private CompareXmlNodes() {
    }
    
    public static int compareCourtHouseNodes(String node1Value, String node2Value) {
        String node1ValueNew = removeCourtHousePrefix(node1Value);
        String node2ValueNew = removeCourtHousePrefix(node2Value);
        return node2ValueNew.compareTo(node1ValueNew);
    }

    private static String removeCourtHousePrefix(String nodeValue) {
        if (nodeValue.startsWith(COURTHOUSE_NAMEPREFIX)) {
            return nodeValue.replace(COURTHOUSE_NAMEPREFIX, EMPTY_STRING);
        }
        return nodeValue;
    }

    public static int compareNodes(String nodeName, String node1Value,
        String node2Value, boolean isInternetWebpage, boolean isWarnedList) {
        if (isInternetWebpage) {
            return compareIwpNodes(node1Value, node2Value);
        } else if (isWarnedList && Tag.COURTHOUSE_NAME == Tag.fromString(nodeName)) {
            return compareCourtHouseNodes(node1Value, node2Value);
        } else {
            return node2Value.compareTo(node1Value);
        }
    }

    private static int compareIwpNodes(String node1Value, String node2Value) {
        if (node1Value.startsWith(COURT) && node2Value.startsWith(COURT)) {
            return compareCourtNodes(node1Value, node2Value);
        } else if (node1Value.toLowerCase(Locale.getDefault()).startsWith(CROWN_COURT.toLowerCase(Locale.getDefault()))
            && node2Value.toLowerCase(Locale.getDefault()).startsWith(CROWN_COURT.toLowerCase(Locale.getDefault()))) {
            return compareCrownCourtNodes(node1Value, node2Value);
        } else {
            return node2Value.compareTo(node1Value);
        }
    }

    private static int compareCourtNodes(String node1Value, String node2Value) {
        String node1ValueNew = node1Value.replace(COURT, EMPTY_STRING);
        String node2ValueNew = node2Value.replace(COURT, EMPTY_STRING);
        // to stop a number format exception, if they have entered an invalid
        // courtroom it won't
        // get merged
        // anyway so it doesn't really matter about the sorting
        if (isNumeric(node1ValueNew) && isNumeric(node2ValueNew)) {
            return Integer.valueOf(node2ValueNew).compareTo(Integer.valueOf(node1ValueNew));
        } else {
            return node2Value.compareTo(node1Value);
        }
    }

    private static int compareCrownCourtNodes(String node1Value, String node2Value) {
        String node1ValueNew = node1Value.replace(CROWN_COURT, EMPTY_STRING);
        String node2ValueNew = node2Value.replace(CROWN_COURT, EMPTY_STRING);
        // to stop a number format exception, if they have entered an invalid
        // courtroom it won't
        // get merged
        // anyway so it doesn't really matter about the sorting
        if (isNumeric(node1ValueNew) && isNumeric(node2ValueNew)) {
            return Integer.valueOf(node2ValueNew).compareTo(Integer.valueOf(node1ValueNew));
        } else {
            return node2Value.compareTo(node1Value);
        }
    }

    public static boolean compareToEmpty(String populatedNodeValue, String emptyNodeValue) {
        return populatedNodeValue != null && !EMPTY_STRING.equals(populatedNodeValue)
            && isEmpty(emptyNodeValue);
    }
    
    public static boolean isEmpty(String nodeValue) {
        return nodeValue == null || EMPTY_STRING.equals(nodeValue);
    }
    
    public static boolean compareNumeric(String node1Value, String node2Value) {
        return isNumeric(node1Value) && isNumeric(node2Value);
    }
    
    /**
     * Is the value to compare a number.
     * 
     * @param value String to check
     * @return true if it's a number
     */
    public static boolean isNumeric(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static boolean isBlankTextNode(final Node node) {
        return node != null && node.getNodeType() == Node.TEXT_NODE
            && EMPTY_STRING.equals(node.getTextContent().trim());
    }

}
