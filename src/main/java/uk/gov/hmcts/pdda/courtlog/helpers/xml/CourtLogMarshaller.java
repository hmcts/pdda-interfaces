package uk.gov.hmcts.pdda.courtlog.helpers.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.gov.hmcts.framework.services.xml.XmlServicesImpl;

class CourtLogMarshaller {
    private static final Logger LOG = LoggerFactory.getLogger(CourtLogMarshaller.class);

    public CourtLogMarshaller() {
        LOG.debug("XMLHashMapGenerator() - entry");
    }

    // Inner class to handle to logic needed to determine if the node that
    // is
    // currently being processed should be held in a Map or Collection
    /** Holds unmarshalled nodes in a map where duplicate keys become lists. */
    private class CourtLogUnmarshalledNode {

        private static final Logger log =
                LoggerFactory.getLogger(CourtLogUnmarshalledNode.class);

        private final Map<String, Object> nodeAsMap = new HashMap<>();

        protected void put(String nodeName, Object nodeValue) {
            Objects.requireNonNull(nodeName, "nodeName");

            log.debug("put: nodeName={}, nodeValue={}", nodeName, nodeValue);

            Object current = nodeAsMap.get(nodeName);
            log.debug("put: currentValue={}", current);

            if (current == null) {
                // First value for this key
                nodeAsMap.put(nodeName, nodeValue);
                return;
            }

            if (current instanceof List<?>) {
                // Subsequent values for this key
                @SuppressWarnings("unchecked")
                List<Object> list = (List<Object>) current;
                list.add(nodeValue);
            } else {
                // Second value: promote to a list and keep the original
                List<Object> list = new ArrayList<>(2);
                list.add(current);
                list.add(nodeValue);
                nodeAsMap.put(nodeName, list);
            }
        }

        /** Returns an unmodifiable snapshot of the underlying map. */
        protected Map<String, Object> getUnmarshalledNode() {
            return Collections.unmodifiableMap(new HashMap<>(nodeAsMap));
            // or: return Map.copyOf(nodeAsMap);  // if you don't need a deep copy
        }
    }

    public String marshall(Map props, String rootNodeName) {
        LOG.debug("marshall() - entry");
        return XmlServicesImpl.getInstance().generateXMLFromPropSet(props, rootNodeName);
    }

    public Map unmarshall(String xml) {
        LOG.debug("unmarshall() - entry - xml : " + xml);

        Document doc = CourtLogXmlHelper.createDocument(xml);
        Map props = unmarshallNode(doc.getDocumentElement()).getUnmarshalledNode();

        return ((props != null) ? props : new HashMap());
    }

    /*
     * JonP : Investigating why, when editing Directions for Case / Defs to
     * Attend, only the last defendant in the list appears ticked in edit
     * dialog. The list of Defs to Attend in built here by parsing XML into a
     * Map of Maps, but because the node name is used as Map key, each new
     * defendant gets mapped over the last one .. hence only the last one in
     * list ends up in the map. PDF : Fixed 24/09 - see new inner class
     * CourtLogUnmarshalledNode
     */
    private CourtLogUnmarshalledNode unmarshallNode(Node rootNode) {
        LOG.debug("unmarshallNode() - entry - processing Node : " + rootNode.getNodeName());

        CourtLogUnmarshalledNode cLUNode = new CourtLogUnmarshalledNode();

        NodeList nodes = rootNode.getChildNodes();
        Node node;

        if (nodes.getLength() > 0) {
            for (int i = 0, n = nodes.getLength(); i < n; i++) {
                node = nodes.item(i);

                if (node.hasChildNodes()) {
                    if (node.getFirstChild().getNodeType() == Node.TEXT_NODE) {
                        cLUNode.put(node.getNodeName(), node.getFirstChild().getNodeValue());
                    } else {
                        cLUNode.put(node.getNodeName(), unmarshallNode(node).getUnmarshalledNode());
                    }
                }
            }
        }

        return cLUNode;
    }
}
