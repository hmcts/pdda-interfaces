package uk.gov.hmcts.pdda.web.publicdisplay.events;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.BranchEventXmlNode;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.EventXmlNode;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.LeafEventXmlNode;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Parses the CR Live Display event XML into a BranchEventXmlNode and extracts a LocalDateTime.
 */
@SuppressWarnings("PMD")
public final class CrLiveEventXmlParser {

    private CrLiveEventXmlParser() {
        // utility class
    }

    public static final class ParseResult {
        public final BranchEventXmlNode node;
        public final LocalDateTime eventTime;
        public final Integer hearingId;
        public final Integer scheduledHearingId;
        public final Integer defendantOnCaseId;
        public final String defendantName;

        public ParseResult(BranchEventXmlNode node, LocalDateTime eventTime,
                           Integer hearingId, Integer scheduledHearingId,
                           Integer defendantOnCaseId, String defendantName) {
            this.node = node;
            this.eventTime = eventTime;
            this.hearingId = hearingId;
            this.scheduledHearingId = scheduledHearingId;
            this.defendantOnCaseId = defendantOnCaseId;
            this.defendantName = defendantName;
        }
    }


    public static Optional<ParseResult> parse(String xml) {
        if (xml == null || xml.isBlank()) {
            return Optional.empty();
        }
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document doc = dbf.newDocumentBuilder()
                .parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

            Element root = doc.getDocumentElement();
            if (root == null || !"event".equals(root.getNodeName())) {
                return Optional.empty();
            }

            BranchEventXmlNode rootNode = new BranchEventXmlNode("event");
            // walk children
            NodeList children = root.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) child;
                    EventXmlNode node = buildNodeFromElement(el);
                    rootNode.add(node);
                }
            }

            // Extract date/time if available
            String dateStr = getSingleElementText(root, "date");
            String timeStr = getSingleElementText(root, "time");
            LocalDateTime evtTime = null;
            if (dateStr != null && !dateStr.isBlank()) {
                DateTimeFormatter dateFmt;
                if (dateStr.matches("\\d{2}/\\d{2}/\\d{2}")) {
                    // two-digit year, assume 20xx (adjust if you have other rule)
                    dateFmt = DateTimeFormatter.ofPattern("dd/MM/yy", Locale.UK);
                } else {
                    dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.UK);
                }
                LocalDate date = LocalDate.parse(dateStr, dateFmt);

                LocalTime time = (timeStr == null || timeStr.isBlank())
                        ? LocalTime.MIDNIGHT
                        : LocalTime.parse(timeStr); // expects "HH:mm" or "HH:mm:ss"

                evtTime = LocalDateTime.of(date, time);
            }

            String hearingIdStr = getSingleElementText(root, "hearing_id");
            Integer hearingId = (hearingIdStr == null || hearingIdStr.isBlank())
                    ? null
                    : Integer.valueOf(hearingIdStr);

            String schedIdStr = getSingleElementText(root, "scheduled_hearing_id");
            Integer scheduledHearingId = (schedIdStr == null || schedIdStr.isBlank())
                    ? null
                    : Integer.valueOf(schedIdStr);

            String docIdStr = getSingleElementText(root, "defendant_on_case_id");
            Integer defendantOnCaseId = (docIdStr == null || docIdStr.isBlank())
                    ? null
                    : Integer.valueOf(docIdStr);

            String defendantName = getSingleElementText(root, "defendant_name");

            return Optional.of(
                new ParseResult(
                    rootNode,
                    evtTime,
                    hearingId,
                    scheduledHearingId,
                    defendantOnCaseId,
                    defendantName
                )
            );

        } catch (Exception e) {
            // log and return empty to avoid crashing render
            // LOG.warn("Failed to parse CR live XML", e);
            return Optional.empty();
        }
    }

    private static String getSingleElementText(Element parent, String name) {
        NodeList nl = parent.getElementsByTagName(name);
        if (nl.getLength() == 0) {
            return null;
        }
        Node n = nl.item(0);
        return n.getTextContent() == null ? null : n.getTextContent().trim();
    }

    private static EventXmlNode buildNodeFromElement(Element el) {
        // If element has element children -> Branch
        NodeList children = el.getChildNodes();
        boolean hasElementChild = false;
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                hasElementChild = true;
                break;
            }
        }
        if (hasElementChild) {
            BranchEventXmlNode branch = new BranchEventXmlNode(el.getTagName());
            for (int i = 0; i < children.getLength(); i++) {
                Node n = children.item(i);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    branch.add(buildNodeFromElement((Element) n));
                }
            }
            return branch;
        } else {
            // Leaf: create a small concrete leaf node
            String text = el.getTextContent() == null ? "" : el.getTextContent().trim();
            return new LeafEventXmlNode(el.getTagName(), text);
        }
    }
}

