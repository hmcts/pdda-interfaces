package uk.gov.hmcts.pdda.business.services.pdda.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uk.gov.hmcts.pdda.business.services.formatting.MergeDocumentUtils;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled.DocumentUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

/**
 * <p>
 * Title: ListNodesHelper.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2024
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author HarrisM
 * @version 1.0
 */
@SuppressWarnings({"PMD.NullAssignment", "PMD.TooManyMethods", "PMD.ExcessiveParameterList",
    "PMD.CognitiveComplexity"})
public class ListNodesHelper implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ListNodesHelper.class);
    private ListObjectHelper listObjectHelper;
    private final Map<String, Integer> numberedNodes = new ConcurrentHashMap<>();

    public ListNodesHelper() {
        // Default constructor
    }

    // Junit constructor
    public ListNodesHelper(ListObjectHelper listObjectHelper) {
        this.listObjectHelper = listObjectHelper;
    }

    public void processClobData(String clobData) {
        LOG.info("processClobData()");
        try {
            // Build the clob data as a document
            Document document = DocumentUtils.createInputDocument(clobData);
            // Get the list of nodes below the passed in root node
            String[] rootNodes = {ListObjectHelper.ROOTNODE};
            List<Node> nodes = MergeDocumentUtils
                .getNodeList(MergeDocumentUtils.getRootNodeExpressionArray(rootNodes), document);

            // Process the nodes
            processNodes(nodes);
        } catch (SAXException | IOException | ParserConfigurationException
            | XPathExpressionException ex) {
            LOG.error("processClobData() - Failed to get nodes: {}", ex.getMessage());
        }
        LOG.info("processClobData() - Finished");
    }

    /**
     * Process the nodes.
     * 
     * @param topNodes Nodes
     */
    protected void processNodes(List<Node> topNodes) {
        LOG.debug("processNodes()");
        Map<String, String> courtListNodesMap = new ConcurrentHashMap<>();
        Map<String, String> sittingNodesMap = new ConcurrentHashMap<>();
        Map<String, String> judgeNodesMap = new ConcurrentHashMap<>();
        Map<String, String> hearingNodesMap = new ConcurrentHashMap<>();
        Map<String, String> defendantNodesMap = new ConcurrentHashMap<>();
        for (Node topNode : topNodes) {
            // Get reference data used by the main nodes
            Map<String, String> referenceNodesMap =
                getReferenceNodeMap(topNode, ListObjectHelper.LISTHEADER_NODE);
            // Loop the courtlists
            for (Node courtListNode : getChildNodesArray(ListObjectHelper.COURTLIST_NODE,
                topNode)) {
                courtListNodesMap.clear();
                courtListNodesMap.putAll(referenceNodesMap);
                courtListNodesMap.putAll(getNodesMap(courtListNode));
                courtListNodesMap
                    .putAll(getReferenceNodeMap(courtListNode, ListObjectHelper.COURTHOUSE_NODE));
                getListObjectHelper().validateNodeMap(courtListNodesMap,
                    ListObjectHelper.COURTLIST_NODE);
                // Sittings in the courtlist
                for (Node sittingNode : getChildNodesArray(ListObjectHelper.SITTING_NODE,
                    courtListNode)) {
                    sittingNodesMap.clear();
                    sittingNodesMap.putAll(courtListNodesMap);
                    sittingNodesMap.putAll(getNodesMap(sittingNode));
                    getListObjectHelper().validateNodeMap(sittingNodesMap,
                        ListObjectHelper.SITTING_NODE);
                    
                    // Hearings in the sitting
                    for (Node hearingNode : getChildNodesArray(ListObjectHelper.HEARING_NODE,
                        sittingNode)) {
                        hearingNodesMap.clear();
                        hearingNodesMap.putAll(sittingNodesMap);
                        hearingNodesMap.putAll(getNodesMap(hearingNode));
                        hearingNodesMap.putAll(
                            getReferenceNodeMap(hearingNode, ListObjectHelper.HEARINGDETAILS_NODE));
                        getListObjectHelper().validateNodeMap(hearingNodesMap,
                            ListObjectHelper.HEARING_NODE);
                        // Defendants in the hearing
                        for (Node defendantNode : getChildNodesArray(
                            ListObjectHelper.DEFENDANT_NODE, hearingNode)) {
                            defendantNodesMap.clear();
                            defendantNodesMap.putAll(hearingNodesMap);
                            defendantNodesMap.putAll(getNodesMap(defendantNode));
                            defendantNodesMap.putAll(getReferenceNodeMap(defendantNode,
                                ListObjectHelper.DEFENDANTNAME_NODE));
                            getListObjectHelper().validateNodeMap(defendantNodesMap,
                                ListObjectHelper.DEFENDANT_NODE);
                        }
                    }
                    
                    // Judge in the sitting
                    for (Node judgeNode : getChildNodesArray(ListObjectHelper.JUDGE_NODE,
                        sittingNode)) {
                        judgeNodesMap.clear();
                        judgeNodesMap.putAll(getNodesMap(judgeNode));
                        getListObjectHelper().validateNodeMap(judgeNodesMap,
                            ListObjectHelper.JUDGE_NODE);
                    }
                }
            }
        }
        LOG.debug("processNodes() - Finished");
    }

    protected Map<String, String> getReferenceNodeMap(Node parentNode, String rootNode) {
        Map<String, String> results = new ConcurrentHashMap<>();
        for (Node childNode : getChildNodesArray(rootNode, parentNode)) {
            results.putAll(getNodesMap(childNode));
        }
        return results;
    }

    /**
     * Loop through the child nodes.
     * 
     * @param rootNode String
     * @param node Node
     * @return list
     */
    public List<Node> getChildNodesArray(String rootNode, Node node) {
        List<Node> result = new ArrayList<>();
        String[] rootNodes = {rootNode};
        try {
            for (XPathExpression rootNodeExp : Arrays
                .asList(MergeDocumentUtils.getRootNodeExpressionArray(rootNodes))) {
                NodeList nodeList = (NodeList) rootNodeExp.evaluate(node, XPathConstants.NODESET);
                for (int nodeNo = 0; nodeNo < nodeList.getLength(); nodeNo++) {
                    result.add(nodeList.item(nodeNo));
                }
            }
        } catch (XPathExpressionException e) {
            LOG.error("Failed to get nodes for {}", rootNode);
        }
        return result;
    }

    /**
     * Return the node map of name and value.
     * 
     * @param node Node
     * @return nodesMap
     */
    private Map<String, String> getNodesMap(Node node) {
        Map<String, String> nodesMap = new ConcurrentHashMap<>();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            // Add any node attribute values
            nodesMap.putAll(getNodeAttributes(node));
            // Add any node child values
            for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                Node childNode = node.getChildNodes().item(i);
                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    String name = getName(childNode);
                    String text = childNode.getTextContent();
                    nodesMap.put(name, text);
                }
            }
        }
        return nodesMap;
    }

    private String getName(Node node) {
        String name = node.getNodeName();
        if (getListObjectHelper().isNumberedNode(node.getNodeName())) {
            Integer nodeNumber = 1;
            if (numberedNodes.containsKey(node.getNodeName())) {
                nodeNumber = numberedNodes.get(node.getNodeName());
            }
            name += "." + nodeNumber;
            numberedNodes.put(node.getNodeName(), nodeNumber + 1);
        } else {
            numberedNodes.clear();
        }
        return name;
    }

    /**
     * Return the node attributes as a map prefix with the nodeName.
     * 
     * @param node Node return nodesMap
     */
    protected Map<String, String> getNodeAttributes(Node node) {
        Map<String, String> results = new ConcurrentHashMap<>();
        NamedNodeMap attributesList = node.getAttributes();
        for (int i = 0; i < attributesList.getLength(); i++) {
            results.put(node.getNodeName() + "." + attributesList.item(i).getNodeName(),
                attributesList.item(i).getNodeValue());
        }
        return results;
    }

    private ListObjectHelper getListObjectHelper() {
        if (listObjectHelper == null) {
            listObjectHelper = new ListObjectHelper();
        }
        return listObjectHelper;
    }
}
