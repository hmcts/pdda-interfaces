package uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.gov.hmcts.framework.services.CsServices;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.BranchEventXmlNode;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.EventXmlNode;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.LeafEventXmlNode;

public final class EventXmlNodeHelper {
    /** Logger object. */
    private static final Logger LOG = LoggerFactory.getLogger(EventXmlNodeHelper.class);

    private static final String SEPARATOR = "---------------------------------------------------------------";
    
    private EventXmlNodeHelper() {
        // Reduce Visibility
    }

    /**
     * Reads String passed in which contains path to xml file. A Document object is created from the
     * xml which is processed to create a VelocityXMLNode object.
     */
    public static EventXmlNode buildEventNode(String xml) {
        LOG.debug("START:");
        if (xml != null) {
            Document doc = CsServices.getXmlServices().createDocFromString(xml);
            LOG.debug(SEPARATOR);

            LOG.debug("Get document element");

            // <event>
            Element element = doc.getDocumentElement();

            LOG.debug(SEPARATOR);

            // Process Element Nodes
            return process(element);
        }
        return null;
    }

    /**
     * Takes a Node object and converts it into a Document object from the xml which is processed to
     * create a VelocityXMLNode object.
     */
    public static EventXmlNode buildEventNode(Node xmlNode) {
        LOG.debug("START:");
        if (xmlNode != null) {

            // Process Element Nodes
            return process(xmlNode);
        }
        return null;
    }

    private static EventXmlNode process(Node node) {
        if (node.hasChildNodes() && noTextNodeChildren(node)) {
            return processBranchNode(node);
        } else {
            return processLeafNode(node);
        }
    }

    private static boolean noTextNodeChildren(Node node) {
        LOG.debug("******** noTextNodeChildren ENTRY********");
        if (node.hasChildNodes()) {
            NodeList nodes = node.getChildNodes();

            for (int i = 0; i < nodes.getLength(); i++) {
                LOG.debug("Node name: " + node.getNodeName());
                LOG.debug("Node value: " + node.getNodeValue());
                LOG.debug("Child Node name: " + nodes.item(i).getNodeName());
                LOG.debug("Child Node value: " + nodes.item(i).getNodeValue());
                LOG.debug("Child Node type: " + nodes.item(i).getNodeType());

                if (nodes.item(i).getNodeType() != Node.TEXT_NODE) {
                    LOG.debug("******** nonTextNodeChildren EXIT: true********");
                    return true;
                }
            }
        }
        LOG.debug("******** nonTextNodeChildren EXIT: false********");
        return false;
    }

    private static LeafEventXmlNode processLeafNode(Node node) {
        LOG.debug(SEPARATOR);
        LOG.debug("******** processLeafNode ENTRY********");
        LOG.debug(SEPARATOR);

        String key;
        String value = null;

        // LEAFNODE
        // If childnodes are text nodes then concatenate and trim into single
        // node
        // If child node does not contain any text then value null
        key = node.getNodeName();

        if (node.hasChildNodes()) {
            LOG.debug("Has Child Nodes");
            NodeList nodes = node.getChildNodes();

            StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < nodes.getLength(); i++) {
                if (nodes.item(i).getNodeType() == Node.TEXT_NODE) {
                    LOG.debug("nodes.item(i).getNodeType(): " + nodes.item(i).getNodeType());
                    LOG.debug("Appending Leaf Value: " + nodes.item(i).getNodeValue());
                    buffer.append(nodes.item(i).getNodeValue());
                }
            }
            value = buffer.toString().trim();
            LOG.debug("Leaf Value: " + value);
        } else {
            // THIS SHOULD NEVER BE THE CASE
            if (node.getNodeType() == Node.TEXT_NODE) {
                value = node.getNodeValue();
            }
        }

        return new LeafEventXmlNode(key, value);
    }

    private static BranchEventXmlNode processBranchNode(Node nodeIn) {
        LOG.debug(SEPARATOR);
        LOG.debug("******** processBranchNode ENTRY********");
        LOG.debug(SEPARATOR);

        BranchEventXmlNode branchNode = new BranchEventXmlNode(nodeIn.getNodeName());
        NodeList list = nodeIn.getChildNodes();
        Node node;

        LOG.debug("NodeList length: " + list.getLength());

        for (int i = 0; i < list.getLength(); i++) {
            // for each node, check iof child nodes
            node = list.item(i);
            if (!checkForWhiteSpace(node)) {
                LOG.debug(SEPARATOR);

                branchNode.add(process(node));

                LOG.debug(SEPARATOR);
            }
        }

        LOG.debug(SEPARATOR);
        LOG.debug("******** processBranchNode EXIT********");
        LOG.debug(SEPARATOR);

        return branchNode;
    }

    private static boolean checkForWhiteSpace(Node node) {
        return node.getNodeType() == Node.TEXT_NODE && "".equals(node.getNodeValue().trim());
    }
}
