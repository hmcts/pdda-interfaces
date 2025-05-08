package uk.gov.hmcts.datagenerator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Random;
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
 * Usage: java DailyListXmlUpdater [inputFile] [outputFolder] [overrideDate] [mode] e.g. java
 * DailyListXmlUpdater /tmp/DailyList.xml /tmp/sftpfolder/output 2025-05-08 CPP
 * </p>
 */
@SuppressWarnings("PMD")
public class DailyListXmlUpdater {

    private DailyListXmlUpdater() {
        // Prevent instantiation
    }

    public static void main(String[] args) throws Exception {
        final String inputFile = args.length > 0 ? args[0] : "DailyList.xml";
        String outputFolder = args.length > 1 ? args[1] : "output";

        // Parse override date if provided
        LocalDate overrideDate = null;
        if (args.length > 2) {
            try {
                overrideDate = LocalDate.parse(args[2], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException e) {
                System.err.println(
                    "Invalid date format for override date (expected yyyy-MM-dd), using today.");
            }
        }

        String mode = args.length > 3 ? args[3].toUpperCase() : "CPP";
        if (!mode.equals("CPP") && !mode.equals("XHIBIT")) {
            System.err.println("Error: Mode must be either 'CPP' or 'XHIBIT'");
            System.exit(1);
        }

        // Ensure output directory exists
        Files.createDirectories(Paths.get(outputFolder));

        // Use current timestamp for time-specific fields
        LocalDateTime now = LocalDateTime.now();
        LocalDate effectiveDate = overrideDate != null ? overrideDate : now.toLocalDate();

        // Get current time
        DateTimeFormatter fileTimeFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timestampFormat =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        DateTimeFormatter pubTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        DateTimeFormatter docNameFormat = DateTimeFormatter.ofPattern("dd-MMM-yy");

        String timestamp = now.format(timestampFormat);
        final String pubTime = now.format(pubTimeFormat);
        String fileTimestamp = now.format(fileTimeFormat);
        String docName = "Daily List FINAL v1 " + now.format(docNameFormat).toUpperCase();
        final String todayDate = effectiveDate.format(dateFormat);

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
        updateTagText(doc, "DocumentName", docName);
        updateTagText(doc, "TimeStamp", timestamp);
        updateTagText(doc, "StartDate", todayDate);
        updateTagText(doc, "EndDate", todayDate);
        updateTagText(doc, "PublishedTime", pubTime);
        updateTagText(doc, "HearingDate", todayDate);

        // Extract courthouse code
        String courtCode =
            doc.getElementsByTagNameNS("*", "CourtHouseCode").item(0).getTextContent().trim();

        // Compose output file path
        String outputFileName;
        if (mode.equals("XHIBIT")) {
            int fourDigitRand = new Random().nextInt(9000) + 1000;
            int twoDigitRand = new Random().nextInt(90) + 10;
            outputFileName = "PDDA_XDL_" + fourDigitRand + "_" + twoDigitRand + "_" + courtCode
                + "_" + fileTimestamp;
        } else {
            outputFileName = "DailyList_" + courtCode + "_" + fileTimestamp + ".xml";
        }

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