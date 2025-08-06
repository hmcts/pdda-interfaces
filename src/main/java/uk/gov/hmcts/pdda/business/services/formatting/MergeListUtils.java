package uk.gov.hmcts.pdda.business.services.formatting;

import org.w3c.dom.Node;

import java.io.IOException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public final class MergeListUtils {

    private static final String IWP = "IWP";
    private static final String[] DAILYLISTROOTNODES = {"DailyList/CourtLists"};
    private static final String[] FIRMLISTROOTNODES = {"FirmList/CourtLists", "FirmList/ReserveList"};
    private static final String[] WARNEDLISTROOTNODES = {"WarnedList/CourtLists/CourtList"};
    private static final String INVALID_FIRST_DOC = "Primary document does not contain any root nodes";
    
    private MergeListUtils() {
        
    }
    
    public static String[] getListRootNodes(Class<?> clazz) throws IOException {
        if (isDailyList(clazz)) {
            return DAILYLISTROOTNODES.clone();
        } else if (isFirmList(clazz)) {
            return FIRMLISTROOTNODES.clone();
        } else if (isWarnedList(clazz)) {
            return WARNEDLISTROOTNODES.clone();
        } else {
            throw new IOException(INVALID_FIRST_DOC);
        }
    }
    
    public static boolean isFirmListRootNodes(String... nodes) {
        return FIRMLISTROOTNODES == nodes;
    }
    
    public static boolean isWarnedListRootNodes(String... nodes) {
        return WARNEDLISTROOTNODES == nodes;
    }
    
    public static boolean isWarnedList(Class<?> clazz) {
        return clazz.isAssignableFrom(WarnedListXmlMergeUtils.class);
    }

    public static boolean isDailyList(Class<?> clazz) {
        return clazz.isAssignableFrom(DailyListXmlMergeUtils.class);
    }

    public static boolean isFirmList(Class<?> clazz) {
        return clazz.isAssignableFrom(FirmListXmlMergeUtils.class);
    }
    
    public static XPathExpression[] getlistRootNodeExpression(String... listRootNodes)
        throws XPathExpressionException {
        XPathExpression[] listRootNodeExpression = new XPathExpression[listRootNodes.length];
        for (int nodeNo = 0; nodeNo < listRootNodes.length; nodeNo++) {
            listRootNodeExpression[nodeNo] =
                XPathFactory.newInstance().newXPath().compile(listRootNodes[nodeNo]);
        }
        return listRootNodeExpression;
    }
    
    public static void appendNode(Node node, Node childNodeToMerge, String mergeType) {
        // If the last child is a text spacer and so is the next one,
        // clear it to avoid formatting issues
        if (CompareXmlNodes.isBlankTextNode(node.getLastChild()) && CompareXmlNodes.isBlankTextNode(childNodeToMerge)) {
            node.getLastChild().setTextContent(node.getLastChild().getTextContent().trim());
        }
        if (!IWP.equals(mergeType)) {
            node.appendChild(childNodeToMerge);
        }
    }
}
