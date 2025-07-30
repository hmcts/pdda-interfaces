package uk.gov.hmcts.datagenerator.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


@SuppressWarnings("PMD")
public final class XmlUtils {

    private XmlUtils() {
        // Prevent instantiation
    }

    /**
     * Loads and parses an XML document from a file with XXE protection enabled.
     */
    public static Document loadSecureXmlDocument(File file) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        factory.setNamespaceAware(true);

        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(file);
    }

    /**
     * Updates the text content of all tags matching a given name.
     */
    public static void updateTagText(Document doc, String tagName, String newValue) {
        NodeList list = doc.getElementsByTagNameNS("*", tagName);
        for (int i = 0; i < list.getLength(); i++) {
            list.item(i).setTextContent(newValue);
        }
    }

    /**
     * Recursively removes whitespace-only text nodes.
     */
    public static void removeWhitespaceNodes(Element element) {
        NodeList children = element.getChildNodes();
        for (int i = children.getLength() - 1; i >= 0; i--) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.TEXT_NODE && child.getTextContent().trim().isEmpty()) {
                element.removeChild(child);
            } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                removeWhitespaceNodes((Element) child);
            }
        }
    }

    /**
     * Writes an XML Document to a file with indentation and XXE protection.
     */
    public static void writeXmlToFile(Document doc, File outputFile) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        doc.getDocumentElement().normalize();
        removeWhitespaceNodes(doc.getDocumentElement());

        transformer.transform(new DOMSource(doc), new StreamResult(outputFile));
    }
}
