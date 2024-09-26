package uk.gov.hmcts.pdda.business.services.formatting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


public class FirmListXmlMergeUtils extends AbstractListXmlMergeUtils {

    private static final Logger LOG = LoggerFactory.getLogger(FirmListXmlMergeUtils.class);

    public enum Attribute {
        SITTING_DATE("SittingDate");

        String value;

        Attribute(String value) {
            this.value = value;
        }
    }

    public FirmListXmlMergeUtils() throws XPathExpressionException {
        super("FirmList/CourtLists/CourtList/Sittings", "FirmList/ReserveList/Hearing");
    }

    @Override
    public String[] getNodePositionArray() {
        return FirmListUtils.getNodePositionArray();
    }

    @Override
    protected void mergeNode(Document baseDocument, Node node, Node nodeToMerge) {
        // i.e. is there a reserved node in cpp but nothing in xhibit
        if (FirmListUtils.isReservedNode(nodeToMerge) && FirmTag.TOP_NODE == FirmTag.fromString(node.getNodeName())) {
            Node childNodeToMerge = baseDocument.importNode(nodeToMerge, true);
            // append this reserved node to the cpp list
            node.appendChild(childNodeToMerge);

        } else if (FirmListUtils.isReservedNode(node)) {
            Node reservedListNode = MergeNodeUtils.getParentNodeByType(node, FirmTag.RESERVE_LIST.value);
            Node childNodeToMerge = baseDocument.importNode(nodeToMerge, true);
            Node insertBeforeNode = getChildNodeBeforeInsert(node, childNodeToMerge);
            if (insertBeforeNode != null) {
                insertBeforeNode = MergeNodeUtils.getParentNodeByType(insertBeforeNode, FirmTag.HEARING.value);
                reservedListNode.insertBefore(childNodeToMerge, insertBeforeNode);
            } else {
                reservedListNode.appendChild(childNodeToMerge);
            }
        } else {
            mergeNonReservedNode(baseDocument, node, nodeToMerge);
        }
    }

    private void mergeNonReservedNode(Document baseDocument, Node node, Node nodeToMerge) {
        String nodeUniqueKey = FirmListUtils.getUniqueKey(node);
        Node courtListsNode = node.getParentNode().getParentNode();
        Node courtNodeToMerge = FirmListUtils.cloneSiblingNode(nodeToMerge, Tag.COURTHOUSE.value);
        Node sittingsNodeToMerge = nodeToMerge.cloneNode(false);
        for (int i = 0; i < nodeToMerge.getChildNodes().getLength(); i++) {
            Node childNodeToMerge = baseDocument.importNode(nodeToMerge.getChildNodes().item(i), true);
            if (!allowEmptyParentNodes() && isEmptyParentNode(childNodeToMerge)) {
                continue;
            }
            String nodeToMergeUniqueKey = FirmListUtils.getUniqueKey(childNodeToMerge);
            boolean newNodeRequired =
                nodeUniqueKey != null && nodeToMergeUniqueKey != null && !nodeToMergeUniqueKey.equals(nodeUniqueKey);
            nodeUniqueKey = FirmListUtils.getNodeUniqueKey(nodeUniqueKey, nodeToMergeUniqueKey);
            if (newNodeRequired) {
                appendNewCourtList(baseDocument, courtListsNode, courtNodeToMerge, sittingsNodeToMerge, nodeUniqueKey,
                    childNodeToMerge);
            } else {
                Node insertBeforeNode = getChildNodeBeforeInsert(node, childNodeToMerge);
                if (insertBeforeNode != null) {
                    node.insertBefore(childNodeToMerge, insertBeforeNode);
                } else {
                    MergeListUtils.appendNode(node, childNodeToMerge, getMergeType());
                }
            }
        }
    }

    private void appendNewCourtList(Document baseDocument, Node courtListsNode, Node courtNodeToMerge,
        Node sittingsNodeToMerge, String sittingDate, Node childNodeToMerge) {
        Node newCourtListNode = baseDocument.importNode(courtNodeToMerge.cloneNode(true), true);
        MergeNodeUtils.setNodeAttribute(newCourtListNode, Attribute.SITTING_DATE.value, sittingDate);
        // Create new <Sittings> Tag
        Node newSittingsListNode = baseDocument.importNode(sittingsNodeToMerge.cloneNode(true), true);
        // Attach the tags to the new structure
        Node insertBeforeNode = FirmListUtils.getCourtNodeNodeBeforeInsert(courtListsNode, newCourtListNode);
        if (insertBeforeNode != null) {
            courtListsNode.insertBefore(newCourtListNode, insertBeforeNode);
        } else {
            MergeListUtils.appendNode(courtListsNode, newCourtListNode, getMergeType());
        }
        MergeListUtils.appendNode(newCourtListNode, newSittingsListNode, getMergeType());
        MergeListUtils.appendNode(newSittingsListNode, childNodeToMerge, getMergeType());
    }

    @Override
    protected Node getChildNodeBeforeInsert(Node parentNode, Node childNodeToMerge) {
        if (FirmListUtils.isReservedNode(parentNode) && FirmListUtils.isReservedNode(childNodeToMerge)) {
            if (MergeComparisonUtils.getNodePositionForSorting(parentNode, childNodeToMerge, getNodePositionArray(),
                getMergeType(), isFirmList(), isWarnedList()) < 0) {
                return parentNode;
            }
            return null;
        }
        return super.getChildNodeBeforeInsert(parentNode, childNodeToMerge);
    }

    @Override
    protected boolean isNodeMatchForMerge(final Node node1, final Node node2) {
        // Check if this is a reserved list
        boolean isMatch = FirmListUtils.isReservedNode(node1) && FirmListUtils.isReservedNode(node2);
        if (!isMatch) {
            // Check if the court matches
            isMatch = super.isNodeMatchForMerge(node1, node2);
            // Check the sitting dates of the two courts
            if (isMatch) {
                String nodeSittingDate = FirmListUtils.getSittingDateFromCourtNode(node1.getParentNode());
                String nodeToMergeSittingDate = FirmListUtils.getSittingDateFromCourtNode(node2.getParentNode());
                if (nodeSittingDate != null && nodeToMergeSittingDate != null) {
                    isMatch = nodeToMergeSittingDate.equals(nodeSittingDate);
                }
            }
        }
        return isMatch;
    }

    @Override
    protected boolean addNode(Document baseDocument, Node node, Node nodeToMerge) {
        // if to merge in is reserved return as that is done at the end
        if (FirmListUtils.isReservedNode(nodeToMerge)) {
            return false;
        }
        // if the last xhibit is a reserved but we are trying to merge in an xhibit then we want to
        // set node to a courtlist
        Node nodeToUse = node;
        if (FirmListUtils.isReservedNode(nodeToUse)) {
            nodeToUse = nodeToUse.getParentNode().getPreviousSibling();
        }

        Node courtListNode = MergeNodeUtils.getParentNodeByType(nodeToUse, Tag.COURTLIST.value);
        Node courtListsNode = courtListNode != null ? courtListNode.getParentNode() : null;
        if (courtListsNode == null && nodeToUse.getNodeType() == Node.ELEMENT_NODE
            && Tag.COURTLISTS == Tag.fromString(nodeToUse.getNodeName())) {
            courtListsNode = nodeToUse;
        }
        Node courtListToMerge = null;
        Node insertBeforeNode = null;
        if (courtListsNode != null) {
            courtListToMerge = MergeNodeUtils.getParentNodeToMerge(baseDocument, nodeToMerge);
            insertBeforeNode = FirmListUtils.getCourtNodeNodeBeforeInsert(courtListsNode, courtListToMerge);
        }
        // meaning either insertBeforeNode is not null or it is null i.e. it needs to be at the end
        // eg. would come back a null if xhibit had 14th for haverfordwest and cpp was 15th
        // for swansea
        if (courtListsNode != null) {
            courtListsNode.insertBefore(courtListToMerge, insertBeforeNode);
            return true;

        } else {
            return super.addNode(baseDocument, nodeToUse, nodeToMerge);
        }
    }

    @Override
    protected boolean isEmptyParentNode(Node node) {
        boolean isEmpty = super.isEmptyParentNode(node);
        if (isEmpty) {
            // Check if this is a reservelist
            isEmpty = !FirmListUtils.isReservedNode(node);
        }
        return isEmpty;
    }

    public Document sortFirmCourtLists(Document doc) {
        try {
            String pathToMatchOn = "FirmList/CourtLists/CourtList";
            XPathExpression rootNodeExpression = XPathFactory.newInstance().newXPath().compile(pathToMatchOn);


            List<Node> results = new ArrayList<>();
            NodeList nodeList = (NodeList) rootNodeExpression.evaluate(doc, XPathConstants.NODESET);
            for (int nodeNo = 0; nodeNo < nodeList.getLength(); nodeNo++) {
                results.add(nodeList.item(nodeNo));
            }
            Node courtListsNode = results.get(0).getParentNode();

            // remove the children
            while (courtListsNode.hasChildNodes()) {
                courtListsNode.removeChild(courtListsNode.getFirstChild());
            }

            // sort the court list items
            results = FirmListUtils.sortCourtLists(results);

            // add all nodes back in order
            for (Node node : results) {
                // only add back in if not blank
                Node childNodeToMerge = doc.importNode(node, true);
                if (!isEmptyParentNode(childNodeToMerge)) {
                    MergeListUtils.appendNode(courtListsNode, childNodeToMerge, getMergeType());
                }
            }
            // no need to error now as it'll get thrown out during processing
        } catch (XPathExpressionException e) {
            LOG.error("unable to sort cpp document as no courtlist items found ");
        }
        return doc;
    }
}
