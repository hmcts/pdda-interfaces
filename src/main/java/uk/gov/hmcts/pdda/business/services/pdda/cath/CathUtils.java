package uk.gov.hmcts.pdda.business.services.pdda.cath;

import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import uk.gov.hmcts.pdda.business.entities.xhbcathdocumentlink.XhbCathDocumentLinkDao;
import uk.gov.hmcts.pdda.business.entities.xhbcathdocumentlink.XhbCathDocumentLinkRepository;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.CourtelJson;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.PublicationConfiguration;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundRepository;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentDao;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentRepository;
import uk.gov.hmcts.pdda.business.services.formatting.TransformerUtils;
import uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet.InitializationService;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

@SuppressWarnings({"PMD.LawOfDemeter", "PMD.AssignmentInOperand", "PMD.CouplingBetweenObjects",
    "PMD.ExcessiveImports"})
public final class CathUtils {

    private static final Logger LOG = LoggerFactory.getLogger(CathUtils.class);

    private static final String APIM_ENABLED = "apim.enabled";
    private static final String APIM_URL = "apim.uri";
    private static final String AUTHENTICATION = "Authorization";
    private static final String BEARER = "Bearer %s";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String DATETIME_FORMAT = "yyyy-MM-ddHH:mm";
    private static final String FALSE = "false";
    private static final String POST_URL = "%s/publication";
    private static final String GOVERNANCE = "PDDA";

    private CathUtils() {
        // Private constructor
    }

    public static String getDateTimeAsString(LocalDateTime dateTime) {
        return DateTimeFormatter.ofPattern(DATETIME_FORMAT).format(dateTime);
    }

    public static HttpRequest getHttpPostRequest(String url, CourtelJson courtelJson) {
        // Get the times
        String now = getDateTimeAsString(courtelJson.getContentDate());
        String nextMonth = getDateTimeAsString(courtelJson.getContentDate().plusMonths(1));
        // Get the bearer token
        String bearerToken = String.format(BEARER, courtelJson.getToken());
        // Return the HttpRequest for the post
        HttpRequest result = HttpRequest.newBuilder().uri(URI.create(url))
            .header(PublicationConfiguration.TYPE_HEADER, courtelJson.getArtefactType().toString())
            .header(PublicationConfiguration.GOVERNANCE_HEADER, GOVERNANCE)
            .header(PublicationConfiguration.DISPLAY_FROM_HEADER, now)
            .header(PublicationConfiguration.DISPLAY_TO_HEADER, nextMonth)
            .header(PublicationConfiguration.COURT_ID, courtelJson.getCrestCourtId())
            .header(PublicationConfiguration.LIST_TYPE, courtelJson.getListType().toString())
            .header(PublicationConfiguration.LANGUAGE_HEADER, courtelJson.getLanguage().toString())
            .header(PublicationConfiguration.CONTENT_DATE, now).header(AUTHENTICATION, bearerToken)
            .header(CONTENT_TYPE, CONTENT_TYPE_JSON)
            .POST(BodyPublishers.ofString(courtelJson.getJson())).build();
        LOG.debug("getHttpPostRequest() - built POST");
        return result;
    }

    public static boolean isApimEnabled() {
        return !FALSE.equalsIgnoreCase(
            InitializationService.getInstance().getEnvironment().getProperty(APIM_ENABLED));
    }

    public static String getApimUri() {
        String apimUri = InitializationService.getInstance().getEnvironment().getProperty(APIM_URL);
        return String.format(POST_URL, apimUri);
    }

    public static XhbCathDocumentLinkDao transformXmlUsingSchema(Long clobId,
        XhbCourtelListRepository xhbCourtelListRepository, XhbClobRepository xhbClobRepository,
        XhbXmlDocumentRepository xhbXmlDocumentRepository,
        XhbCathDocumentLinkRepository xhbCathDocumentLinkRepository, String xsltSchemaPath)
        throws TransformerException {
        String xsltNamespaceSchemaPath = "config/xsl/listTransformation/Namespace_Schema.xslt";
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        // Get the predefined xslt schema
        Source xsltSource =
            new StreamSource(new File(classLoader.getResource(xsltSchemaPath).getFile()));
        Templates templates = transformerFactory.newTemplates(xsltSource);
        Transformer cutDowntransformer = templates.newTransformer();

        // Get the predefined namespace xslt schema
        Source xsltNameSpaceSource =
            new StreamSource(new File(classLoader.getResource(xsltNamespaceSchemaPath).getFile()));
        Templates nameSpaceTemplates = transformerFactory.newTemplates(xsltNameSpaceSource);
        Transformer nameSpaceTransformer = nameSpaceTemplates.newTransformer();

        // Check if there is an entry in the courtel_list table with the documentClobId
        Optional<XhbCourtelListDao> xhbCourtelListDao =
            xhbCourtelListRepository.findByXmlDocumentClobId(clobId);

        if (xhbCourtelListDao.isPresent()) {
            // Fetch the original xml clob and xml document records
            Optional<XhbClobDao> xhbClobDaoOriginalXml = xhbClobRepository.findById(clobId);
            Optional<XhbXmlDocumentDao> xhbXmlDocumentDaoOriginalXml =
                xhbXmlDocumentRepository.findByXmlDocumentClobId(clobId);
            if (xhbClobDaoOriginalXml.isPresent() && xhbXmlDocumentDaoOriginalXml.isPresent()) {
                // Transform the xml, removing the unneeded elements and attributes
                Source xmlSource =
                    new StreamSource(new StringReader(xhbClobDaoOriginalXml.get().getClobData()));
                StringWriter outWriter =
                    TransformerUtils.transformList(cutDowntransformer, xmlSource);

                // Transform the xml, removing the namespaces
                Source xmlCutDownSource = new StreamSource(new StringReader(outWriter.toString()));
                StringWriter outWriterWithRemovedNameSpaces =
                    TransformerUtils.transformList(nameSpaceTransformer, xmlCutDownSource);

                // Save the transformed Xml to the clob table
                XhbClobDao xhbClobDaoTransformedXml = new XhbClobDao();
                xhbClobDaoTransformedXml.setClobData(outWriterWithRemovedNameSpaces.toString());
                xhbClobRepository.save(xhbClobDaoTransformedXml);

                // Save the transformed xml to xml_document table
                XhbXmlDocumentDao xhbXmlDocumentDaoTransformedXml = new XhbXmlDocumentDao();
                xhbXmlDocumentDaoTransformedXml
                    .setDateCreated(xhbXmlDocumentDaoOriginalXml.get().getDateCreated());
                xhbXmlDocumentDaoTransformedXml
                    .setDocumentTitle(xhbXmlDocumentDaoOriginalXml.get().getDocumentTitle());
                xhbXmlDocumentDaoTransformedXml
                    .setXmlDocumentClobId(xhbClobDaoTransformedXml.getClobId());
                xhbXmlDocumentDaoTransformedXml.setStatus("IO");
                xhbXmlDocumentDaoTransformedXml
                    .setDocumentType(xhbXmlDocumentDaoOriginalXml.get().getDocumentType());
                xhbXmlDocumentDaoTransformedXml
                    .setExpiryDate(xhbXmlDocumentDaoOriginalXml.get().getExpiryDate());
                xhbXmlDocumentDaoTransformedXml
                    .setCourtId(xhbXmlDocumentDaoOriginalXml.get().getCourtId());
                xhbXmlDocumentRepository.save(xhbXmlDocumentDaoTransformedXml);

                // Save to the cath_document_link table with the original and transformed xml
                // id's
                XhbCathDocumentLinkDao xhbCathDocumentLinkDao = new XhbCathDocumentLinkDao();
                xhbCathDocumentLinkDao
                    .setOrigCourtelListDocId(xhbCourtelListDao.get().getCourtelListId());
                xhbCathDocumentLinkDao
                    .setCathXmlId(xhbXmlDocumentDaoTransformedXml.getXmlDocumentId());
                xhbCathDocumentLinkRepository.save(xhbCathDocumentLinkDao);

                // Return the cath_document_link record
                return xhbCathDocumentLinkDao;
            }
        }
        return null;
    }

    public static void fetchXmlAndGenerateJson(XhbCathDocumentLinkDao xhbCathDocumentLinkDao,
        XhbCathDocumentLinkRepository xhbCathDcoumentLlinkRepository,
        XhbXmlDocumentRepository xhbXmlDocumentRepository, XhbClobRepository xhbClobRepository,
        XhbCourtelListRepository xhbCourtelListRepository,
        XhbCppStagingInboundRepository xhbCppStagingInboundRepository)
        throws ParserConfigurationException, SAXException, IOException {

        // Fetch the xml_document record
        Optional<XhbXmlDocumentDao> xhbXmlDocumentDaoTransformedXml =
            xhbXmlDocumentRepository.findById(xhbCathDocumentLinkDao.getCathXmlId());

        if (xhbXmlDocumentDaoTransformedXml.isPresent()) {
            // Fetch the clob record
            Optional<XhbClobDao> xhbClobDaoTransformedXml = xhbClobRepository
                .findById(xhbXmlDocumentDaoTransformedXml.get().getXmlDocumentClobId());

            if (xhbClobDaoTransformedXml.isPresent()) {
                // Generate the Json and save it to the clob_table
                XhbClobDao xhbClobDaoJson = new XhbClobDao();
                xhbClobDaoJson.setClobData(
                    generateJsonFromString(xhbClobDaoTransformedXml.get().getClobData())
                        .toString());
                xhbClobRepository.save(xhbClobDaoJson);

                // Save the json record to the xml_document table
                XhbXmlDocumentDao xhbXmlDocumentDaoJson = new XhbXmlDocumentDao();
                xhbXmlDocumentDaoJson.setDateCreated(LocalDate.parse(CathDocumentTitleUtils
                    .generateCathDocumentTitleBuilderFromClob(xhbClobDaoTransformedXml.get())
                    .getStartDate()).atStartOfDay());
                xhbXmlDocumentDaoJson.setDocumentTitle(
                    CathDocumentTitleUtils.generateDocumentTitle(xhbCathDocumentLinkDao,
                        xhbClobDaoTransformedXml.get(), xhbCourtelListRepository,
                        xhbCppStagingInboundRepository, xhbXmlDocumentRepository));
                xhbXmlDocumentDaoJson.setXmlDocumentClobId(xhbClobDaoJson.getClobId());
                xhbXmlDocumentDaoJson.setStatus("ND");
                xhbXmlDocumentDaoJson.setDocumentType("JSN");
                xhbXmlDocumentDaoJson
                    .setExpiryDate(xhbXmlDocumentDaoTransformedXml.get().getExpiryDate());
                xhbXmlDocumentDaoJson
                    .setCourtId(xhbXmlDocumentDaoTransformedXml.get().getCourtId());
                xhbXmlDocumentRepository.save(xhbXmlDocumentDaoJson);

                // Update the cath_document_link record with the cathJsonId
                updateCathDocumentlinkWithJsonId(xhbCathDcoumentLlinkRepository,
                    xhbCathDocumentLinkDao, xhbXmlDocumentDaoJson);
            }
        }
    }

    private static void updateCathDocumentlinkWithJsonId(
        XhbCathDocumentLinkRepository xhbCathDcoumentLlinkRepository,
        XhbCathDocumentLinkDao xhbCathDocumentLinkDao, XhbXmlDocumentDao xhbXmlDocumentDaoJson) {
        // Fetch the existing cath_document_link record
        Optional<XhbCathDocumentLinkDao> xhbCathDocumentLinkDaoNoJsonId =
            xhbCathDcoumentLlinkRepository.findById(xhbCathDocumentLinkDao.getCathDocumentLinkId());

        if (xhbCathDocumentLinkDaoNoJsonId.isPresent()) {
            // Update the existing record with the cathJsonId
            xhbCathDocumentLinkDaoNoJsonId.get()
                .setCathJsonId(xhbXmlDocumentDaoJson.getXmlDocumentId());
            xhbCathDcoumentLlinkRepository.update(xhbCathDocumentLinkDaoNoJsonId.get());
        }
    }

    private static JSONObject generateJsonFromString(String stringToConvert) {
        return XML.toJSONObject(stringToConvert);
    }

}
