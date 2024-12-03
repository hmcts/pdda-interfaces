package uk.gov.hmcts.pdda.business.services.pdda.data;

import com.pdda.hb.jpa.EntityManagerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.services.formatting.MergeDocumentUtils;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled.DocumentUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.parsers.ParserConfigurationException;
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
@SuppressWarnings({"PMD.NullAssignment", "PMD.TooManyMethods", "PMD.ExcessiveParameterList"})
public class ListNodesHelper {

    private static final Logger LOG = LoggerFactory.getLogger(ListNodesHelper.class);
    private static final String DAILY_LIST = "DailyList/CourtLists/CourtList";
    private ListObjectHelper listObjectHelper;

    public void processNodes() {
        XhbClobRepository xhbClobRepository =
            new XhbClobRepository(EntityManagerUtil.getEntityManager());
        Optional<XhbClobDao> clob = xhbClobRepository.findById(1);
        if (clob.isPresent()) {
            try {
                String clobData = clob.get().getClobData();
                // Build the clob data as a document
                Document document = DocumentUtils.createInputDocument(clobData);
                // Get the list of nodes below the passed in root node
                String[] rootNodes = {DAILY_LIST};
                List<Node> nodes = MergeDocumentUtils.getNodeList(
                    MergeDocumentUtils.getRootNodeExpressionArray(rootNodes), document);
                // Process the nodes
                processNodes(nodes);
            } catch (SAXException | IOException | ParserConfigurationException
                | XPathExpressionException e) {
                LOG.error("Failed to get nodes");
            }
        }
    }

    /**
     * Process the nodes.
     * @param nodes Nodes
     */
    public void processNodes(List<Node> nodes) {
        LOG.debug("processNodes()");
        listObjectHelper = new ListObjectHelper();
        Map<String, String> nodesMap = new ConcurrentHashMap<>();
        for (Node node : nodes) {
            processChildNodes(node, nodesMap);
        }
    }

    /**
     * Recursive called method to process the child nodes.
     * @param node Node
     * @param nodesMap Map
     */
    protected void processChildNodes(Node node, Map<String, String> nodesMap) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            String name = node.getNodeName();
            String text = node.getTextContent();
            nodesMap.put(name, text);
            listObjectHelper.validateNodeMap(nodesMap, name);
            // Loop through the child nodes
            Map<String, String> childNodesMap = new ConcurrentHashMap<>();
            for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                childNodesMap.putAll(nodesMap);
                Node childNode = node.getChildNodes().item(i);
                // Call the next level down
                processChildNodes(childNode, childNodesMap);
            } 
        }
    }
}
