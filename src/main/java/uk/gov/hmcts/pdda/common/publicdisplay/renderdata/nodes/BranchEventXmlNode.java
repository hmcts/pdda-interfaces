package uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



/**
 * Object representing a Branch node of an xml document. This node will contain other Branch and
 * leaf nodes.

 * @author szfnvt

 */
public class BranchEventXmlNode extends EventXmlNode {

    static final long serialVersionUID = 7660114920405929035L;

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(BranchEventXmlNode.class);

    private final Map<String, Object> map = new ConcurrentHashMap<>();

    /**
     * Constructor taking in name of node.

     * @param nameIn String
     */
    public BranchEventXmlNode(String nameIn) {
        super(nameIn);
    }

    /**
     * Method returning a single value. This can be the first item in a list.

     * @param key String
     * @return Object
     */
    public Object get(String key) {
        Object value = map.get(key);
        if (value instanceof List) {
            if (((List<?>) value).isEmpty()) {
                return null;
            } else {
                return ((List<?>) value).get(0);
            }
        } else {
            return value;
        }
    }

    /**
     * method return a list containing one or more values.

     * @param key String
     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<Object> getList(String key) {
        Object value = map.get(key);
        if (LOG.isDebugEnabled()) {
            LOG.debug(key + ": " + value);
        }
        if (value instanceof List) {
            return (List) value;
        } else if (value != null) {
            List<Object> list = new ArrayList<>(1);
            list.add(value);
            return list;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Add EventXMLNode to internal Map either as a single node or as part of a list.

     * @param node EventXMLNode
     */
    @SuppressWarnings("unchecked")
    public void add(EventXmlNode node) {
        // Check if node already exists in
        Object oldNode;
        oldNode = map.get(node.getName());
        if (oldNode == null) {
            LOG.debug("Another Value does not exist for key: " + node.getName());
            map.put(node.getName(), node);
        } else if (oldNode instanceof List) {
            LOG.debug("Value of type List does exists for key: " + node.getName());
            ((List) oldNode).add(node);
        } else {
            map.remove(node.getName());
            LOG.debug("Value of " + oldNode + " exists for key: " + node.getName());
            LOG.debug("Old Node Removed!");    
            LOG.debug("Value of exists for key: " + node.getName());
            List<Object> values = new ArrayList<>();
            values.add(oldNode);
            values.add(node);
            map.put(node.getName(), values);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Added old and new values to List and stored against key.");
            }
        }
    }

    /**
     * Returns map for branch containing branch and leaf nodes.

     * @return Map
     */
    public Map<String, Object> getMap() {
        return map;
    }

    @Override
    protected void appendDebug(StringBuilder buffer, int depth) {
        appendIndent(buffer, depth);
        buffer.append(getName()).append(':');

        List<String> keyList = new ArrayList<>();
        keyList.addAll(map.keySet());
        Collections.sort(keyList);
        Iterator<String> keys = keyList.iterator();
        while (keys.hasNext()) {
            Object key = keys.next();
            Object value = map.get(key);
            if (value instanceof List) {
                Iterator<?> values = ((List<?>) value).iterator();
                while (values.hasNext()) {
                    buffer.append(NL);
                    ((EventXmlNode) values.next()).appendDebug(buffer, depth + 1);
                }
            } else {
                buffer.append(NL);
                ((EventXmlNode) value).appendDebug(buffer, depth + 1);
            }
        }
    }

}
