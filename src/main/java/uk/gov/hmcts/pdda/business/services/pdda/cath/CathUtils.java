package uk.gov.hmcts.pdda.business.services.pdda.cath;

import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.CourtelJson;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.PublicationConfiguration;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListRepository;
import uk.gov.hmcts.pdda.business.services.formatting.TransformerUtils;
import uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet.InitializationService;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

@SuppressWarnings({"PMD.LawOfDemeter", "PMD.AssignmentInOperand"})
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
    private static final String PROVENANCE = "PDDA";

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
        return HttpRequest.newBuilder().uri(URI.create(url))
            .header(PublicationConfiguration.TYPE_HEADER, courtelJson.getArtefactType().toString())
            .header(PublicationConfiguration.PROVENANCE_HEADER, PROVENANCE)
            .header(PublicationConfiguration.DISPLAY_FROM_HEADER, now)
            .header(PublicationConfiguration.DISPLAY_TO_HEADER, nextMonth)
            .header(PublicationConfiguration.COURT_ID, courtelJson.getCourtId().toString())
            .header(PublicationConfiguration.LIST_TYPE, courtelJson.getListType().toString())
            .header(PublicationConfiguration.LANGUAGE_HEADER, courtelJson.getLanguage().toString())
            .header(PublicationConfiguration.CONTENT_DATE, now).header(AUTHENTICATION, bearerToken)
            .header(CONTENT_TYPE, CONTENT_TYPE_JSON)
            .POST(BodyPublishers.ofString(courtelJson.getJson())).build();
    }

    public static boolean isApimEnabled() {
        return !FALSE.equalsIgnoreCase(
            InitializationService.getInstance().getEnvironment().getProperty(APIM_ENABLED));
    }

    public static String getApimUri() {
        String apimUri = InitializationService.getInstance().getEnvironment().getProperty(APIM_URL);
        return String.format(POST_URL, apimUri);
    }

    public static Long transformXmlUsingSchema(Long clobId,
        XhbCourtelListRepository xhbCourtelListRepository, XhbClobRepository xhbClobRepository,
        String xsltSchemaPath) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        // Get the predefined xslt schema
        Source xsltSource =
            new StreamSource(new File(classLoader.getResource(xsltSchemaPath).getFile()));
        Templates templates = transformerFactory.newTemplates(xsltSource);
        Transformer transformer = templates.newTransformer();

        // Check if there is an entry in the xhb_courtel_list table with the documentClobId
        Optional<XhbCourtelListDao> xhbCourtelListDao =
            xhbCourtelListRepository.findByXmlDocumentClobId(clobId);

        if (xhbCourtelListDao.isPresent()) {
            // Get the original xml clob
            Optional<XhbClobDao> xhbClobDaoOriginalXml = xhbClobRepository.findById(clobId);
            if (xhbClobDaoOriginalXml.isPresent()) {
                // Transform the xml
                Source xmlSource =
                    new StreamSource(new StringReader(xhbClobDaoOriginalXml.get().getClobData()));
                StringWriter outWriter = TransformerUtils.transformList(transformer, xmlSource);

                // Save the transformed Xml to the xhb_clob table
                XhbClobDao xhbClobDaoTransformedXml = new XhbClobDao();
                xhbClobDaoTransformedXml.setClobData(outWriter.toString());
                xhbClobRepository.save(xhbClobDaoTransformedXml);

                // Return the Translated Xml Clob Id
                return xhbClobDaoTransformedXml.getClobId();
            }
        }
        return null;
    }

    public static Long fetchXmlAndGenerateJson(Long clobId, XhbClobRepository xhbClobRepository) {
        // Fetch the Xml
        Optional<XhbClobDao> xhbClobDaoTransformedXml = xhbClobRepository.findById(clobId);

        if (xhbClobDaoTransformedXml.isPresent()) {
            // Save the Json to the xhb_clob_table
            XhbClobDao xhbClobDaoJson = new XhbClobDao();
            xhbClobDaoJson.setClobData(generateJsonFromString(xhbClobDaoTransformedXml.get().getClobData()).toString());
            xhbClobRepository.save(xhbClobDaoJson);
            
            // Return the Json Clob Id
            return xhbClobDaoJson.getClobId();
        }
        return null;
    }

    private static JSONObject generateJsonFromString(String stringToConvert) {
        return XML.toJSONObject(stringToConvert);
    }

}
