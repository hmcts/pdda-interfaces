package uk.gov.hmcts.pdda.business.services.pdda.cath;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import uk.gov.hmcts.pdda.business.entities.xhbcathdocumentlink.XhbCathDocumentLinkDao;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundDao;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundRepository;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentDao;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentRepository;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.CouplingBetweenObjects"})
public final class CathDocumentTitleUtils {

    private static final Map<String, String> DOC_TYPES =
        Map.of("DL", "Daily List", "FL", "Firm List", "WL", "Warned List");

    private CathDocumentTitleUtils() {
        // Private constructor
    }

    public static String generateDocumentTitle(XhbCathDocumentLinkDao xhbCathDocumentLinkDao,
        XhbCourtelListRepository xhbCourtelListRepository,
        XhbCppStagingInboundRepository xhbCppStagingInboundRepository,
        XhbXmlDocumentRepository xhbXmlDocumentRepository, XhbClobRepository xhbClobRepository)
        throws ParserConfigurationException, SAXException, IOException {

        // Fetch courtel_list record from cath_document_Link original Xml Id
        Optional<XhbCourtelListDao> xhbCourtelListDao =
            xhbCourtelListRepository.findById(xhbCathDocumentLinkDao.getOrigCourtelListDocId());

        if (xhbCourtelListDao.isPresent()) {
            // Fetch cppStagingInboundDao from courtel_list xmlDocumentClobId
            Optional<XhbCppStagingInboundDao> xhbCppStagingInboundDao =
                xhbCppStagingInboundRepository
                    .findByClobId(xhbCourtelListDao.get().getXmlDocumentClobId());
            // Fetch the xmlDocumentDao from the courtel_list xmlDocumentClobId
            Optional<XhbXmlDocumentDao> xhbXmlDocumentDao = xhbXmlDocumentRepository
                .findByXmlDocumentClobId(xhbCourtelListDao.get().getXmlDocumentClobId());
            // Fetch the clob from the xmlDocumentClobId
            Optional<XhbClobDao> xhbClobDao =
                xhbClobRepository.findById(xhbCourtelListDao.get().getXmlDocumentClobId());

            if (xhbCppStagingInboundDao.isPresent() && xhbXmlDocumentDao.isPresent()
                && xhbClobDao.isPresent()) {
                return appendDocumentTitleElements(xhbClobDao.get(), xhbXmlDocumentDao.get(),
                    xhbCppStagingInboundDao.get());
            }
            return null;
        }
        return null;
    }

    public static CathDocumentTitleBuilder generateCathDocumentTitleBuilderFromClob(
        XhbClobDao xhbClobDao) throws ParserConfigurationException, SAXException, IOException {

        // Create a Document Builder to get the clob data
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newDefaultInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        InputSource inputSource = new InputSource(new StringReader(xhbClobDao.getClobData()));
        Document document = documentBuilder.parse(inputSource);

        // Get the cs:ListHeader nodes
        Node listHeaderNode = document.getElementsByTagName("cs:ListHeader").item(0);
        NodeList listHeaderChildNodes = listHeaderNode.getChildNodes();

        CathDocumentTitleBuilder cathDocumentTitleBuilder = new CathDocumentTitleBuilder();

        for (int i = 0; i < listHeaderChildNodes.getLength(); i++) {
            Node node = listHeaderChildNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE
                && Objects.equals("cs:StartDate", node.getNodeName())) {
                cathDocumentTitleBuilder.setStartDate(node.getTextContent());
            } else if (node.getNodeType() == Node.ELEMENT_NODE
                && Objects.equals("cs:EndDate", node.getNodeName())) {
                cathDocumentTitleBuilder.setEndDate(node.getTextContent());
            } else if (node.getNodeType() == Node.ELEMENT_NODE
                && Objects.equals("cs:Version", node.getNodeName())) {
                cathDocumentTitleBuilder.setVersion(node.getTextContent());
            } else if (node.getNodeType() == Node.ELEMENT_NODE
                && Objects.equals("cs:PublishedTime", node.getNodeName())) {
                cathDocumentTitleBuilder.setPublishedDateTime(node.getTextContent());
            }
        }
        return cathDocumentTitleBuilder;
    }


    private static String appendDocumentTitleElements(XhbClobDao xhbClobDao,
        XhbXmlDocumentDao xhbXmlDocumentDao, XhbCppStagingInboundDao xhbCppStagingInboundDao)
        throws ParserConfigurationException, SAXException, IOException {

        // Generate the CathDocumentTitleBuilder object from the clobData
        CathDocumentTitleBuilder cathDocumentTitleBuilder =
            generateCathDocumentTitleBuilderFromClob(xhbClobDao);

        StringBuilder documentTitle = new StringBuilder(150);

        // Part 1 - List type
        String listType = "";
        for (Map.Entry<String, String> entry : DOC_TYPES.entrySet()) {
            if (xhbXmlDocumentDao.getDocumentType().equals(entry.getKey())) {
                listType = entry.getValue() + " ";
            }
        }
        documentTitle.append(listType);

        // Part 1.5 - From & To Date (Firm Lists and Warned Lists Only)
        if ("Firm List ".equals(listType) || "Warned List ".equals(listType)) {
            String fromAndToDate = "from " + cathDocumentTitleBuilder.getStartDate() + " to "
                + cathDocumentTitleBuilder.getEndDate() + " ";
            documentTitle.append(fromAndToDate);
        }

        // Part 2 - DRAFT/FINAL + Version
        String version = reformatVersion(cathDocumentTitleBuilder.getVersion());
        
        // Part 3 - Publish Date Time
        String publishedDateTime = cathDocumentTitleBuilder.getPublishedDateTime() + " ";

        // Part 4 - CPP_STAGING_INBOUND ID
        String cppStagingInboundId =
            "CPP_STAGING_INBOUND_ID=" + xhbCppStagingInboundDao.getCppStagingInboundId();

        // Append all final elements
        documentTitle.append(version).append(publishedDateTime).append(cppStagingInboundId);

        return documentTitle.toString();
    }
    
    private static String reformatVersion(String version) {
        // This will take the version i.e DRAFT 1 and turn it into DRAFT v1
        if (!"NOT VERSIONED".equals(version)) {
            String[] splitVersion = version.split(" "); // i.e = 1
            String versionNumber = "v" + splitVersion[1] + " "; // i.e = v1
            return splitVersion[0] + " " + versionNumber;
        }
        // NOT VERSIONED will be just returned
        return version;
    }
}
