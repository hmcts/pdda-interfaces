package uk.gov.hmcts.pdda.courtlog.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.gov.hmcts.framework.services.xml.XmlServicesImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"PMD.ForLoopVariableCount"})
class CourtLogMarshaller {
    private static final Logger LOG = LoggerFactory.getLogger(CourtLogMarshaller.class);

    public CourtLogMarshaller() {
        LOG.debug("XMLHashMapGenerator() - entry");
    }

    // Inner class to handle to logic needed to determine if the node that
    // is
    // currently being processed should be held in a Map or Collection
    private final class CourtLogUnmarshalledNode {

        private final Logger courtLogUnmarshalledLogger =
            LoggerFactory.getLogger(CourtLogUnmarshalledNode.class.getName());

        private final Map nodeAsMap = new ConcurrentHashMap<>();

        @SuppressWarnings("unchecked")
        private void put(String nodeName, Object nodeValue) {

            String methodName = "put - ";
            courtLogUnmarshalledLogger
                .debug(methodName + "nodeName: " + nodeName + ", nodeValue: " + nodeValue);

            Object currNodeValue = nodeAsMap.get(nodeName);
            courtLogUnmarshalledLogger.debug(methodName + "currNodeValue: " + currNodeValue);

            if (currNodeValue == null) {
                // No entry with the specified key currently exists so simple
                // add it to the map being used.
                courtLogUnmarshalledLogger.debug(methodName + "Adding node to existing map");
                nodeAsMap.put(nodeName, nodeValue);
            } else {
                // An entry in the map with this key already exists so we need
                // to determine if the value is a Collection (more than one
                // entry with this key already added) or a String (only one
                // entry with this key has so far been added
                if (currNodeValue instanceof Collection) {
                    // Simply add value to the collection
                    courtLogUnmarshalledLogger
                        .debug(methodName + "Adding node to existing collection");
                    ((Collection) currNodeValue).add(nodeValue);
                } else {
                    // Replace the current entry with a collection but add
                    // the
                    // original value to the collection so it isn't lost
                    courtLogUnmarshalledLogger.debug(methodName + "Adding node to new collection");
                    List<Object> nodeValueAsCollection = new ArrayList<>();
                    nodeValueAsCollection.add(currNodeValue);
                    nodeValueAsCollection.add(nodeValue);
                    nodeAsMap.put(nodeName, nodeValueAsCollection);
                }
            }
        }

        private Map getUnmarshalledNode() {

            return nodeAsMap;
        }
    }

    public String marshall(Map props, String rootNodeName) {
        LOG.debug("marshall() - entry");
        return XmlServicesImpl.getInstance().generateXmlFromPropSet(props, rootNodeName);
    }

    public Map unmarshall(String xml) {
        LOG.debug("unmarshall() - entry - xml : " + xml);

        Document doc = CourtLogXmlHelper.createDocument(xml);
        Map props = unmarshallNode(doc.getDocumentElement()).getUnmarshalledNode();

        return (props != null) ? props : new HashMap<>();
    }

    /*
     * JonP : Investigating why, when editing Directions for Case / Defs to Attend, only the last
     * defendant in the list appears ticked in edit dialog. The list of Defs to Attend in built here
     * by parsing XML into a Map of Maps, but because the node name is used as Map key, each new
     * defendant gets mapped over the last one .. hence only the last one in list ends up in the
     * map. PDF : Fixed 24/09 - see new inner class CourtLogUnmarshalledNode
     */
    private CourtLogUnmarshalledNode unmarshallNode(Node rootNode) {
        LOG.debug("unmarshallNode() - entry - processing Node : " + rootNode.getNodeName());

        CourtLogUnmarshalledNode courtLogUnmarshalledNode = new CourtLogUnmarshalledNode();

        NodeList nodes = rootNode.getChildNodes();
        Node node;

        if (nodes.getLength() > 0) {
            for (int i = 0, n = nodes.getLength(); i < n; i++) {
                node = nodes.item(i);

                if (node.hasChildNodes()) {
                    if (node.getFirstChild().getNodeType() == Node.TEXT_NODE) {
                        courtLogUnmarshalledNode.put(node.getNodeName(),
                            node.getFirstChild().getNodeValue());
                    } else {
                        courtLogUnmarshalledNode.put(node.getNodeName(),
                            unmarshallNode(node).getUnmarshalledNode());
                    }
                }
            }
        }

        return courtLogUnmarshalledNode;
    }
}
