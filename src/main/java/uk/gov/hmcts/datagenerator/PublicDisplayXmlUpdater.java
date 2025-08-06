package uk.gov.hmcts.datagenerator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.gov.hmcts.datagenerator.util.XmlUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * PublicDisplayXmlUpdater is a utility class that updates an XML file with the current date and
 * time, and saves it to a specified output directory.

 * Usage: java PublicDisplayXmlUpdater [inputFile] [outputFolder]

 */
@SuppressWarnings("PMD")
public class PublicDisplayXmlUpdater {

    private PublicDisplayXmlUpdater() {
        // Prevent instantiation
    }

    public static void main(String[] args) throws Exception {
        final String inputFile = args.length > 0 ? args[0] : "PublicDisplay.xml";
        String outputFolder = args.length > 1 ? args[1] : "output";

        Files.createDirectories(Paths.get(outputFolder));

        LocalDateTime now = LocalDateTime.now();
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
        final String fileTimestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        Document doc = XmlUtils.loadSecureXmlDocument(new File(inputFile));

        XmlUtils.updateTagText(doc, "dayofweek", dayOfWeek);
        XmlUtils.updateTagText(doc, "date", day);
        XmlUtils.updateTagText(doc, "month", month);
        XmlUtils.updateTagText(doc, "year", year);
        XmlUtils.updateTagText(doc, "hour", hour);
        XmlUtils.updateTagText(doc, "min", min);

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

        String courtCode = "457";
        String inputFilename = new File(inputFile).getName();
        if (inputFilename.matches("PublicDisplay_4\\d{2}_.*\\.xml")) {
            courtCode = inputFilename.substring(14, 17);
        }

        String outputFileName = "PublicDisplay_" + courtCode + "_" + fileTimestamp + ".xml";
        File outputFile = Paths.get(outputFolder, outputFileName).toFile();

        XmlUtils.writeXmlToFile(doc, outputFile);

        System.out.println("Updated file saved as: " + outputFile.getAbsolutePath());
    }
}
