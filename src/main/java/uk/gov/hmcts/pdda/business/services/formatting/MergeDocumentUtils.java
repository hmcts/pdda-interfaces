package uk.gov.hmcts.pdda.business.services.formatting;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled.DocumentUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public final class MergeDocumentUtils {

    private static final String INVALID_AMOUNT_OF_DOCS = "Not enough documents to merge";
    private static final String INVALID_FIRST_DOC =
        "Primary document does not contain any root nodes";
    private static final String IWP = "IWP";
    private static final int MIN_DOCS_TO_MERGE = 2;

    private MergeDocumentUtils() {

    }

    public static XPathExpression[] getRootNodeExpressionArray(final String... rootNodes)
        throws XPathExpressionException {
        XPathExpression[] rootNodeExpression = new XPathExpression[rootNodes.length];
        for (int nodeNo = 0; nodeNo < rootNodes.length; nodeNo++) {
            rootNodeExpression[nodeNo] =
                XPathFactory.newInstance().newXPath().compile(rootNodes[nodeNo]);
        }
        return rootNodeExpression;
    }

    public static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory docBuilderFactory = DocumentUtils.getDocumentBuilderFactory();
        docBuilderFactory.setIgnoringElementContentWhitespace(true);
        return docBuilderFactory.newDocumentBuilder();
    }

    public static void validateDocuments(final Document... documents) throws IOException {
        if (documents.length < MIN_DOCS_TO_MERGE) {
            throw new IOException(INVALID_AMOUNT_OF_DOCS);
        }
    }

    public static List<Node> getBaseDocumentNodes(Document baseDocument,
        XPathExpression[] rootNodeExpression, String mergeType)
        throws XPathExpressionException, IOException {
        List<Node> nodes = getNodeList(rootNodeExpression, baseDocument);
        // means we have a blank xhibit document
        if (nodes.isEmpty() && IWP.equals(mergeType)) {
            // we should never get to this point as pdda will always have all courtlists
            // in there regardless of whether there's anything going on or not so throw exception
            // as something has gone wrong.
            throw new IOException(INVALID_FIRST_DOC);
        }
        return nodes;
    }

    public static List<Node> getNodeList(final XPathExpression[] rootNodeExpression,
        Document document) throws XPathExpressionException {
        List<Node> results = new ArrayList<>();
        for (XPathExpression rootNode : rootNodeExpression) {
            NodeList nodeList = (NodeList) rootNode.evaluate(document, XPathConstants.NODESET);
            for (int nodeNo = 0; nodeNo < nodeList.getLength(); nodeNo++) {
                results.add(nodeList.item(nodeNo));
            }
        }
        return results;
    }

    public static List<Node> getNodeListFromDocuments(final XPathExpression[] rootNodeExpression,
        final Document... documents) throws XPathExpressionException {
        List<Node> nodesToMerge = new ArrayList<>();
        for (int docNo = 1; docNo < documents.length; docNo++) {
            nodesToMerge.addAll(getNodeList(rootNodeExpression, documents[docNo]));
        }
        return nodesToMerge;
    }

    public static List<Node> getCppNodeList(Document document,
        XPathExpression... listRootNodeExpression) throws XPathExpressionException {
        List<Node> results = new ArrayList<>();
        for (XPathExpression rootNode : listRootNodeExpression) {
            NodeList nodeList = (NodeList) rootNode.evaluate(document, XPathConstants.NODESET);
            for (int nodeNo = 0; nodeNo < nodeList.getLength(); nodeNo++) {
                results.add(nodeList.item(nodeNo));
            }
        }
        return results;
    }

    public static List<Node> getNodesToMergeAtParentLevel(XPathExpression[] listRootNodeExpression,
        final Document... documents) throws XPathExpressionException, IOException {
        List<Node> nodesToMergeAtParentLevel = new ArrayList<>();
        for (Document document : documents) {
            nodesToMergeAtParentLevel.addAll(getCppNodeList(document, listRootNodeExpression));
        }
        if (nodesToMergeAtParentLevel.isEmpty()) {
            throw new IOException("Child document does not contain any root nodes");
        }
        return nodesToMergeAtParentLevel;
    }

    public static Node getFirmNode(Document baseDocument) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList nodes =
            (NodeList) xpath.evaluate("FirmList", baseDocument, XPathConstants.NODESET);
        if (nodes.getLength() > 0) {
            return nodes.item(0);
        }
        return null;
    }

    public static List<Node> getNodesToMerge2(XPathExpression[] listRootNodeExpression,
        final Document... documents) throws XPathExpressionException {
        List<Node> nodesToMerge2 = new ArrayList<>();
        for (int docNo = 1; docNo < documents.length; docNo++) {
            nodesToMerge2.addAll(getCppNodeList(documents[docNo], listRootNodeExpression));
        }
        return nodesToMerge2;
    }
}
