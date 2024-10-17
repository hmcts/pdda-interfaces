package uk.gov.hmcts.pdda.business.services.pdda.cath;

import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.CourtelJson;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.PublicationConfiguration;
import uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet.InitializationService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
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

    public static void transformXmlUsingSchema(String inputXmlPath, String xsltSchemaPath,
        String outputXmlPath) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        // Get the predefined xslt schema
        Source xsltSource =
            new StreamSource(new File(classLoader.getResource(xsltSchemaPath).getFile()));
        Templates templates = transformerFactory.newTemplates(xsltSource);
        Transformer transformer = templates.newTransformer();

        // Get the xml
        Source xmlSource =
            new StreamSource(new File(classLoader.getResource(inputXmlPath).getFile()));

        // Transform the xml
        StringWriter outWriter = new StringWriter();
        StreamResult result = new StreamResult(outWriter);
        transformer.transform(xmlSource, result);

        // Write out the transformed xml in a file
        try (BufferedWriter wr = Files.newBufferedWriter(Paths.get(outputXmlPath))) {
            wr.write(outWriter.toString());
        } catch (IOException e) {
            LOG.debug("Failed to write file, with exception: ", e);
        }
    }

    public static void fetchXmlAndGenerateJson(String inputXmlPath, String outputJsonPath) {
        // Fetch and Read Transformed Xml File
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br =
            Files.newBufferedReader(Paths.get(inputXmlPath), StandardCharsets.UTF_8)) {
            // Loop through all the lines in the transformed Xml
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append('\n');
            }
            // Generate the Json File
            createJsonFile(resultStringBuilder, outputJsonPath);
        } catch (IOException e) {
            LOG.debug("Failed to read file: ", e);
        }
    }

    private static void createJsonFile(StringBuilder resultStringBuilder, String outputJsonPath) {
        // Create the file if it doesn't already exist
        try (BufferedWriter wr = Files.newBufferedWriter(Paths.get(outputJsonPath))) {
            // Write out the Json into the file
            wr.write(generateJsonFromString(resultStringBuilder.toString()).toString(2));
        } catch (IOException e) {
            LOG.debug("Failed to write file, with exception: ", e);
        }
    }

    private static JSONObject generateJsonFromString(String stringToConvert) {
        return XML.toJSONObject(stringToConvert);
    }

}
