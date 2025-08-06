package uk.gov.hmcts.datagenerator;

import org.w3c.dom.Document;
import uk.gov.hmcts.datagenerator.util.XmlUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * DailyListXmlUpdater is a utility class that updates an XML file with the current date and time,
 * and saves it to a specified output directory.

 * Usage: java DailyListXmlUpdater [inputFile] [outputFolder] [overrideDate] [mode] e.g. java
 * DailyListXmlUpdater /tmp/DailyList.xml /tmp/sftpfolder/output 2025-05-08 CPP

 */
@SuppressWarnings("PMD")
public class DailyListXmlUpdater {

    private DailyListXmlUpdater() {
        // Prevent instantiation
    }

    public static void main(String[] args) throws Exception {
        final String inputFile = args.length > 0 ? args[0] : "DailyList.xml";
        String outputFolder = args.length > 1 ? args[1] : "output";

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

        Files.createDirectories(Paths.get(outputFolder));

        LocalDateTime now = LocalDateTime.now();
        LocalDate effectiveDate = overrideDate != null ? overrideDate : now.toLocalDate();

        DateTimeFormatter fileTimeFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timestampFormat =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        DateTimeFormatter pubTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        DateTimeFormatter docNameFormat = DateTimeFormatter.ofPattern("dd-MMM-yy");

        final String timestamp = now.format(timestampFormat);
        final String pubTime = now.format(pubTimeFormat);
        final String fileTimestamp = now.format(fileTimeFormat);
        final String docName = "Daily List FINAL v1 " + now.format(docNameFormat).toUpperCase();
        final String todayDate = effectiveDate.format(dateFormat);

        Document doc = XmlUtils.loadSecureXmlDocument(new File(inputFile));

        XmlUtils.updateTagText(doc, "DocumentName", docName);
        XmlUtils.updateTagText(doc, "TimeStamp", timestamp);
        XmlUtils.updateTagText(doc, "StartDate", todayDate);
        XmlUtils.updateTagText(doc, "EndDate", todayDate);
        XmlUtils.updateTagText(doc, "PublishedTime", pubTime);
        XmlUtils.updateTagText(doc, "HearingDate", todayDate);

        String courtCode =
            doc.getElementsByTagNameNS("*", "CourtHouseCode").item(0).getTextContent().trim();

        String outputFileName;
        if (mode.equals("XHIBIT")) {
            int fourDigitRand = new SecureRandom().nextInt(9000) + 1000;
            int twoDigitRand = new SecureRandom().nextInt(90) + 10;
            outputFileName = "PDDA_XDL_" + fourDigitRand + "_" + twoDigitRand + "_" + courtCode
                + "_" + fileTimestamp;
        } else {
            outputFileName = "DailyList_" + courtCode + "_" + fileTimestamp + ".xml";
        }

        File outputFile = Paths.get(outputFolder, outputFileName).toFile();
        XmlUtils.writeXmlToFile(doc, outputFile);

        System.out.println("Updated file saved as: " + outputFile.getAbsolutePath());
    }
}
