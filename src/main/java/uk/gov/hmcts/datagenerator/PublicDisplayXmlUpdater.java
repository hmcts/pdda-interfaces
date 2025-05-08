package uk.gov.hmcts.datagenerator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


/**
 * DailyListXmlUpdater is a utility class that updates an XML file with the current date and time,
 * and saves it to a specified output directory.
 * <p>
 * Usage: java DailyListXmlUpdater [inputFile] [outputFolder] [overrideDate] e.g. java
 * DailyListXmlUpdater /tmp/PublicDisplay.xml /tmp/sftpfolder/output 2025-05-08
 * </p>
 */
@SuppressWarnings("PMD")
public class PublicDisplayXmlUpdater {

    private PublicDisplayXmlUpdater() {
        // Prevent instantiation
    }

    public static void main(String[] args) throws Exception {
        final String inputFile = args.length > 0 ? args[0] : "PublicDisplay.xml";
        String outputFolder = args.length > 1 ? args[1] : "output";

        // Ensure output directory exists
        Files.createDirectories(Paths.get(outputFolder));
        
        // Use current timestamp for time-specific fields
        LocalDateTime now = LocalDateTime.now();

        // Subtract 15 minutes
        LocalDateTime adjustedTime = now.minusMinutes(15);
        final String dayOfWeek =
            adjustedTime.getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, Locale.UK);
        final String day = String.format("%02d", adjustedTime.getDayOfMonth());
        final String month =
            adjustedTime.getMonth().getDisplayName(java.time.format.TextStyle.FULL, Locale.UK);
        final String year = String.valueOf(adjustedTime.getYear());
        final String hour = String.format("%02d", adjustedTime.getHour());
        final String min = String.format("%02d", adjustedTime.getMinute());
        final String shortDate = adjustedTime.format(DateTimeFormatter.ofPattern("dd/MM/yy"));
        final String shortTime = adjustedTime.format(DateTimeFormatter.ofPattern("HH:mm"));

        // Get current time
        DateTimeFormatter fileTimeFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        final String fileTimestamp = now.format(fileTimeFormat);

        // Load and parse the XML and secure against XXE attacks
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        factory.setNamespaceAware(true);

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(inputFile));

        // Update relevant tags
        updateTagText(doc, "dayofweek", dayOfWeek);
        updateTagText(doc, "date", day);
        updateTagText(doc, "month", month);
        updateTagText(doc, "year", year);
        updateTagText(doc, "hour", hour);
        updateTagText(doc, "min", min);

        // Update all <event> <date> and <time>
        NodeList eventNodes = doc.getElementsByTagNameNS("*", "event");
        for (int i = 0; i < eventNodes.getLength(); i++) {
            Node event = eventNodes.item(i);
            if (event.getNodeType() == Node.ELEMENT_NODE) {
                Element eventElem = (Element) event;
                NodeList children = eventElem.getChildNodes();
                for (int j = 0; j < children.getLength(); j++) {
                    Node child = children.item(j);
                    if ("date".equals(child.getNodeName())) {
                        child.setTextContent(shortDate);
                    } else if ("time".equals(child.getNodeName())) {
                        child.setTextContent(shortTime);
                    }
                }
            }
        }

        // Extract courthouse code from filename if it matches pattern
        // PublicDisplay_<courtCode>_*.xml
        String courtCode = "457"; // default
        String inputFilename = new File(inputFile).getName();
        if (inputFilename.matches("PublicDisplay_4\\d{2}_.*\\.xml")) {
            // 14 = length of "PublicDisplay_", 17 = end of 3-digit courtCode
            courtCode = inputFilename.substring(14, 17);
        }

        // Compose output file path
        String outputFileName;
        outputFileName = "PublicDisplay_" + courtCode + "_" + fileTimestamp + ".xml";

        // Write the updated XML and secure against XXE attacks
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        Transformer transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        File outputFile = Paths.get(outputFolder, outputFileName).toFile();

        // Optional: normalise and remove whitespace nodes to clean DOM
        doc.getDocumentElement().normalize();
        removeWhitespaceNodes(doc.getDocumentElement());

        transformer.transform(new DOMSource(doc), new StreamResult(outputFile));

        System.out.println("Updated file saved as: " + outputFile.getAbsolutePath());
    }

    private static void updateTagText(Document doc, String tagName, String newValue) {
        NodeList list = doc.getElementsByTagNameNS("*", tagName);
        for (int i = 0; i < list.getLength(); i++) {
            list.item(i).setTextContent(newValue);
        }
    }

    private static void removeWhitespaceNodes(Element element) {
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

}