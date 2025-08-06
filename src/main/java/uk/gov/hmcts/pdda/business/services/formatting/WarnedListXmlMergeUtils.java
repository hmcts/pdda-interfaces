package uk.gov.hmcts.pdda.business.services.formatting;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.xml.xpath.XPathExpressionException;

public class WarnedListXmlMergeUtils extends AbstractListXmlMergeUtils {

    public WarnedListXmlMergeUtils() throws XPathExpressionException {
        super("WarnedList/CourtLists/CourtList/WithFixedDate",
            "WarnedList/CourtLists/CourtList/WithoutFixedDate");
    }

    @Override
    public String[] getNodePositionArray() {
        return WarnedListUtils.getNodePositionArray();
    }

    @Override
    protected void mergeNode(Document baseDocument, Node node, Node nodeToMerge) {
        Node childNodeToMerge = baseDocument.importNode(nodeToMerge, true);
        Node insertBeforeNode = getChildNodeBeforeInsert(node.getParentNode(), childNodeToMerge);
        if (insertBeforeNode != null) {
            String[] args =
                {WarnedTag.WITH_FIXED_DATE.value, WarnedTag.WITHOUT_FIXED_DATE.value};
            List<String> validNodes = Arrays.asList(args);
            Map<String, String> validMap = MergeNodeUtils.getNodeMapValues(validNodes, childNodeToMerge);
            if (!validMap.isEmpty()) {
                node.getParentNode().insertBefore(childNodeToMerge, insertBeforeNode);
            }
        } else {
            MergeListUtils.appendNode(node.getParentNode(), childNodeToMerge, getMergeType());
        }
    }
}
