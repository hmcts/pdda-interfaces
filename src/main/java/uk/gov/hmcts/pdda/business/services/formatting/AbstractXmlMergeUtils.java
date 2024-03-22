package uk.gov.hmcts.pdda.business.services.formatting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

@SuppressWarnings({"PMD.GodClass", "PMD.CyclomaticComplexity"})
public abstract class AbstractXmlMergeUtils extends AbstractXmlUtils {

    /**
     * Class constants.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(AbstractXmlMergeUtils.class);
    private static final String[] WARNEDLISTROOTNODES = {"WarnedList/CourtLists/CourtList"};
    private static final String IWP = "IWP";
    private static final String INVALID_FIRST_DOC = "Primary document does not contain any root nodes";
    
    private final String[] rootNodes;
    private final XPathExpression[] rootNodeExpression;

    private XPathExpression[] listRootNodeExpression;

    public abstract String[] getNodeMatchArray();

    public abstract String[] getNodePositionArray();

    public abstract String getMergeType();

    protected AbstractXmlMergeUtils(final String... rootNodes) throws XPathExpressionException {
        super();
        this.rootNodes = rootNodes.clone();
        this.rootNodeExpression = MergeDocumentUtils.getRootNodeExpressionArray(rootNodes);
    }

    public Document merge(final Document... documents) throws IOException, XPathExpressionException {
        LOG.debug("merge({})", (Object[]) documents);
        MergeDocumentUtils.validateDocuments(documents);

        // Use the first document as the base for the merge
        Document baseDocument = documents[0];

        // Get the list of Nodes on the documents to be merged
        List<Node> nodesToMerge = MergeDocumentUtils.getNodeListFromDocuments(rootNodeExpression, documents);

        // first check if there are any cpp to merge in, if not no point going any further
        // and just send back the original document
        if (nodesToMerge.isEmpty()) {
            LOG.debug("No nodes to merge");
        } else {
            // Get the list of Nodes on the original document
            List<Node> nodes =
                MergeDocumentUtils.getBaseDocumentNodes(baseDocument, rootNodeExpression, getMergeType());
            // means we have a blank pdda document
            if (nodes.isEmpty()) {
                mergeDocument(baseDocument, nodesToMerge, documents);
            } else if (isWarnedList()) {
                // if warned list we need to check if any of the courtList has no with/without
                mergeWarnedList(baseDocument, nodes, nodesToMerge, documents);
            }
        }
        return baseDocument;
    }

    private void mergeDocument(Document baseDocument, List<Node> nodesToMerge, Document... documents)
        throws IOException, XPathExpressionException {
        LOG.debug("mergeDocument({},{},{})", baseDocument, nodesToMerge, documents);
        String[] listRootNodes = MergeListUtils.getListRootNodes(this.getClass());

        // replace the blank courtlists with the new court list
        this.listRootNodeExpression = MergeListUtils.getlistRootNodeExpression(listRootNodes);

        List<Node> nodes = MergeDocumentUtils.getCppNodeList(baseDocument, listRootNodeExpression);
        if (nodes.isEmpty()) {
            throw new IOException(INVALID_FIRST_DOC);
        } else if (MergeListUtils.isWarnedListRootNodes(listRootNodes)) {
            WarnedListUtils.mergeBlankWarnedList(baseDocument, nodes, nodesToMerge, getNodeMatchArray(), getMergeType(),
                isFirmList(), isWarnedList());
        } else if (MergeListUtils.isFirmListRootNodes(listRootNodes)) {
            List<Node> nodesToMergeAtParentLevel =
                MergeDocumentUtils.getNodesToMergeAtParentLevel(listRootNodeExpression, documents);
            // merge any non blank courtlists
            for (Node nodeToMergeAtParentLevel : nodesToMergeAtParentLevel) {
                Node parentNode = MergeNodeUtils.getParentNodeByType(nodeToMergeAtParentLevel, nodes.get(0));
                mergeNode(baseDocument, parentNode, nodeToMergeAtParentLevel);
            }
        } else {
            List<Node> nodesToMergeAtParentLevel =
                MergeDocumentUtils.getNodesToMergeAtParentLevel(listRootNodeExpression, documents);
            mergeNode(baseDocument, nodes.get(0), nodesToMergeAtParentLevel.get(0));
        }
    }

    private void mergeWarnedList(Document baseDocument, List<Node> nodes, List<Node> nodesToMerge,
        Document... documents) throws XPathExpressionException {
        LOG.debug("mergeWarnedList({},{},{},{})", baseDocument, nodes, nodesToMerge, documents);
        List<Node> parentNodes = MergeNodeUtils.getParentNodes(nodes);
        this.listRootNodeExpression = new XPathExpression[WARNEDLISTROOTNODES.length];
        for (int nodeNo = 0; nodeNo < WARNEDLISTROOTNODES.length; nodeNo++) {
            this.listRootNodeExpression[nodeNo] =
                XPathFactory.newInstance().newXPath().compile(WARNEDLISTROOTNODES[nodeNo]);
            for (Node cppListNodesInXhibit : MergeDocumentUtils.getCppNodeList(baseDocument, listRootNodeExpression)) {
                WarnedListUtils.mergeWarnedNodes(baseDocument, parentNodes, nodesToMerge, cppListNodesInXhibit,
                    listRootNodeExpression, getNodeMatchArray(), getMergeType(), isFirmList(), isWarnedList(),
                    documents);
            }
        }
        // Do the merge
        for (Node node : nodes) {
            List<Node> nodesMerged = getNodesMerged(nodesToMerge, node, baseDocument);
            // Remove any successfully merged
            MergeNodeUtils.removeMergedNodes(nodesToMerge, nodesMerged);
        }

        // IF IWP then replace datetime
        if (IWP.equals(getMergeType())) {
            MergeDateUtils.replaceDateTime(baseDocument);
        } else {
            addRemainderNodes(baseDocument, nodes, nodesToMerge);
        }

        // If we have got here and the reserved list hasn't been added
        // from cpp we know xhibit can't have reserved so add it at the end
        if (isFirmList()) {
            for (Node nodeToMerge : nodesToMerge) {
                if (MergeNodeUtils.getParentNodeByType(nodeToMerge, FirmTag.RESERVE_LIST.value) != null) {
                    mergeNode(baseDocument, MergeDocumentUtils.getFirmNode(baseDocument), nodeToMerge.getParentNode());
                    break;
                }
            }
        }
    }

    private List<Node> getNodesMerged(List<Node> nodesToMerge, Node node, Document baseDocument) {
        List<Node> nodesMerged = new ArrayList<>();
        for (Node nodeToMerge : nodesToMerge) {
            if (isNodeMatchForMerge(node, nodeToMerge)) {
                mergeNode(baseDocument, node, nodeToMerge);
                nodesMerged.add(nodeToMerge);
            }
        }
        return nodesMerged;
    }

    private void addRemainderNodes(Document baseDocument, List<Node> nodes, List<Node> nodesToMerge) {
        List<Node> nodesImported = new ArrayList<>();
        // Add any remainders in the list to the document
        for (Node nodeToMerge : nodesToMerge) {
            Node node = nodes.get(nodes.size() - 1);
            // Check we aren't merging a child of an already merged parent
            if (!nodesImported.contains(nodeToMerge.getParentNode()) && addNode(baseDocument, node, nodeToMerge)) {
                nodesImported.add(nodeToMerge.getParentNode());
            }
        }
    }

    public String[] getRootNodes() {
        return this.rootNodes.clone();
    }

    protected boolean addNode(Document baseDocument, Node node, Node nodeToMerge) {
        Node nodeToUse = node;
        Node parentNode = nodeToUse.getParentNode();
        Node grandParent = parentNode.getParentNode();
        Node parentNodeToMerge = MergeNodeUtils.getParentNodeToMerge(baseDocument, nodeToMerge);
        if (allowEmptyParentNodes() || !isEmptyParentNode(parentNodeToMerge)) {
            if (grandParent.getNodeType() == Node.DOCUMENT_NODE) {
                parentNode.appendChild(parentNodeToMerge);
            } else {
                grandParent.appendChild(parentNodeToMerge);
            }
        }
        return true;
    }

    protected void mergeNode(Document baseDocument, Node node, Node nodeToMerge) {
        for (int i = 0; i < nodeToMerge.getChildNodes().getLength(); i++) {
            Node childNodeToMerge = baseDocument.importNode(nodeToMerge.getChildNodes().item(i), true);
            Node insertBeforeNode = getChildNodeBeforeInsert(node, childNodeToMerge);
            if (insertBeforeNode != null) {
                // if its iwp
                if (IWP.equals(getMergeType())) {
                    MergeIwpUtils.replaceIwp(childNodeToMerge, insertBeforeNode, node);
                } else {
                    node.insertBefore(childNodeToMerge, insertBeforeNode);
                }
            } else {
                MergeListUtils.appendNode(node, childNodeToMerge, getMergeType());
            }
        }
    }

    // ----- Matching -----

    protected boolean isNodeMatchForMerge(final Node node1, final Node node2) {
        return MergeComparisonUtils.compareNodes(getNodeMatchArray(), node1.getParentNode(), node2.getParentNode(),
            getMergeType(), isFirmList(), isWarnedList()) == 0;
    }

    // ----- Sorting / Positioning -----

    protected Node getChildNodeBeforeInsert(Node parentNode, Node childNodeToMerge) {
        Node node = null;
        boolean isWithFixedDateToMerge = WarnedListUtils.isWithFixedDateToMerge(childNodeToMerge, isWarnedList());
        boolean isWithoutFixedDateToMerge = WarnedListUtils.isWithoutFixedDateToMerge(childNodeToMerge, isWarnedList());

        for (int nodeNo = 0; nodeNo < parentNode.getChildNodes().getLength(); nodeNo++) {
            Node childNode = parentNode.getChildNodes().item(nodeNo);
            if (isChildNodeValid(childNode, childNodeToMerge, isWithFixedDateToMerge, isWithoutFixedDateToMerge)) {
                node = childNode;
                break;
            }
        }
        return node;
    }

    private boolean isChildNodeValid(Node childNode, Node childNodeToMerge, boolean isWithFixedDateToMerge,
        boolean isWithoutFixedDateToMerge) {
        if (childNode.getNodeType() == Node.ELEMENT_NODE) {
            if (IWP.equals(getMergeType())) {
                if (MergeComparisonUtils.getNodePositionForSorting(childNode, childNodeToMerge, getNodePositionArray(),
                    getMergeType(), isFirmList(), isWarnedList()) <= 0) {
                    return true;
                }
            } else if (isWarnedList() && WarnedListUtils.isWarnedListChildNodeValid(childNode, childNodeToMerge,
                isWithFixedDateToMerge, isWithoutFixedDateToMerge, getMergeType(), isFirmList(), isWarnedList())) {
                return true;
            } else if (MergeComparisonUtils.getNodePositionForSorting(childNode, childNodeToMerge,
                getNodePositionArray(), getMergeType(), isFirmList(), isWarnedList()) < 0) {
                return true;
            }
        }
        return false;

    }

}
