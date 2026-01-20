package uk.gov.hmcts.pdda.business.services.pdda.cath;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

@SuppressWarnings({"PMD.LawOfDemeter", "PMD.CouplingBetweenObjects",
    "PMD.ExcessiveImports", "PMD.CognitiveComplexity", "PMD.GodClass", "PMD.TooManyMethods"})
public final class CathUtils {

    private static final Logger LOG = LoggerFactory.getLogger(CathUtils.class);

    private static final String APIM_ENABLED = "apim.enabled";
    private static final String APIM_URL = "cath.azure.oauth2.health-endpoint-url";
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer %s";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.nnn'Z'";
    private static final String FALSE = "false";
    private static final String POST_URL = "%s/publication";
    private static final String PROVENANCE = "PDDA";

    // Mapping of container names to their singular child element
    private static final Map<String, String> ARRAY_KEYS = Map.of(
            "CourtLists", "CourtList",
            "Sittings", "Sitting",
            "Hearings", "Hearing",
            "Charges", "Charge",
            "Defendants", "Defendant",
            "Cases", "Case"
    );
    // Keys that should always be represented as JSON arrays
    private static final Set<String> FORCE_ARRAY_KEYS = Set.of(
            "Counsel", "Solicitor", "CitizenNameForename", "ReserveList", "Hearing", "Fixture"
    );
    // Keys that should always be represented as JSON strings
    private static final Set<String> FORCE_STRING_KEYS = Set.of(
            "SittingSequenceNumber",
            "ListNote"
    );

    private CathUtils() {
        // Private constructor
    }

    public static String getDateTimeAsString(ZonedDateTime dateTime) {
        return DateTimeFormatter.ofPattern(DATETIME_FORMAT).format(dateTime);
    }

    public static HttpRequest getWebPageHttpPostRequest(String url, CourtelJson courtelJson) {
        LOG.debug("Building WebPage HTTP Request - getWebPageHttpPostRequest()");
        // Build the multipart/form-data boundary
        String boundary = "----JavaFormBoundary" + System.currentTimeMillis();
        String fileName = courtelJson.getDocumentName() + ".html";
        String fieldName = "file";
        // Build multipart/form-data body
        String body =
                "--" + boundary + "\r\n" + "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"\r\n" + "Content-Type: text/plain\r\n\r\n"
                        + courtelJson.getJson() + "\r\n" + "--" + boundary + "--\r\n";
        // Get the times
        String now = getDateTimeAsString(courtelJson.getContentDate());
        String endDate = getDateTimeAsString(courtelJson.getEndDate());
        // Get the bearer token
        String bearerToken = String.format(BEARER, courtelJson.getToken());
        // Return the HttpRequest for the post
        HttpRequest result = HttpRequest.newBuilder().uri(URI.create(url))
                .header(PublicationConfiguration.TYPE_HEADER, courtelJson.getArtefactType().toString())
                .header(PublicationConfiguration.SENSITIVITY_HEADER, courtelJson.getSensitivity())
                .header(PublicationConfiguration.PROVENANCE_HEADER, PROVENANCE)
                .header(PublicationConfiguration.DISPLAY_FROM_HEADER, now)
                .header(PublicationConfiguration.DISPLAY_TO_HEADER, endDate)
                .header(PublicationConfiguration.COURT_ID, courtelJson.getCrestCourtId())
                .header(PublicationConfiguration.LANGUAGE_HEADER, courtelJson.getLanguage().toString())
                .header(PublicationConfiguration.CONTENT_DATE, now)
                .header(PublicationConfiguration.SOURCE_ARTEFACT_ID_HEADER, courtelJson.getDocumentName())
                .header(AUTHORIZATION, bearerToken)
                .header(CONTENT_TYPE, courtelJson.getContentType() + "; boundary=" + boundary)
                .POST(BodyPublishers.ofString(body))
                .build();

        LOG.debug("getWebPageHttpPostRequest() - built POST");
        return result;
    }

    public static HttpRequest getListHttpPostRequest(String url, CourtelJson courtelJson) {
        LOG.debug("Building List HTTP Request - getListHttpPostRequest()");
        // Get the times
        String now = getDateTimeAsString(courtelJson.getContentDate());
        String endDate = getDateTimeAsString(courtelJson.getEndDate());
        // Get the bearer token
        String bearerToken = String.format(BEARER, courtelJson.getToken());
        // Return the HttpRequest for the post
        HttpRequest result = HttpRequest.newBuilder().uri(URI.create(url))
                .header(PublicationConfiguration.TYPE_HEADER, courtelJson.getArtefactType().toString())
                .header(PublicationConfiguration.SENSITIVITY_HEADER, courtelJson.getSensitivity())
                .header(PublicationConfiguration.PROVENANCE_HEADER, PROVENANCE)
                .header(PublicationConfiguration.DISPLAY_FROM_HEADER, now)
                .header(PublicationConfiguration.DISPLAY_TO_HEADER, endDate)
                .header(PublicationConfiguration.COURT_ID, courtelJson.getCrestCourtId())
                .header(PublicationConfiguration.LANGUAGE_HEADER, courtelJson.getLanguage().toString())
                .header(PublicationConfiguration.CONTENT_DATE, now)
                .header(PublicationConfiguration.SOURCE_ARTEFACT_ID_HEADER, courtelJson.getDocumentName())
                .header(AUTHORIZATION, bearerToken)
                .header(CONTENT_TYPE, courtelJson.getContentType())
                .header(PublicationConfiguration.LIST_TYPE, courtelJson.getListType().toString())
                .POST(BodyPublishers.ofString(courtelJson.getJson()))
                .build();

        LOG.debug("getListHttpPostRequest() - built POST");
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
            throws TransformerException, IOException {
        String xsltNamespaceSchemaPath = "config/xsl/listTransformation/Namespace_Schema.xslt";
        
        // Check if there is an entry in the courtel_list table with the documentClobId
        Optional<XhbCourtelListDao> xhbCourtelListDao =
                xhbCourtelListRepository.findByXmlDocumentClobIdSafe(clobId);

        if (xhbCourtelListDao.isPresent()) {
            // Fetch the original xml clob and xml document records
            Optional<XhbClobDao> xhbClobDaoOriginalXml = xhbClobRepository.findByIdSafe(clobId);
            Optional<XhbXmlDocumentDao> xhbXmlDocumentDaoOriginalXml =
                    xhbXmlDocumentRepository.findByXmlDocumentClobId(clobId);
            if (xhbClobDaoOriginalXml.isPresent() && xhbXmlDocumentDaoOriginalXml.isPresent()) {
                
                // Transform the xml, removing the unneeded elements and attributes
                String transformedXml = 
                    transformList(xsltSchemaPath, xhbClobDaoOriginalXml.get().getClobData());
                
                // Transform the xml, removing the namespaces
                String transformedWithoutNamespacesXml = 
                    transformList(xsltNamespaceSchemaPath, transformedXml);

                // Save the transformed Xml to the clob table
                XhbClobDao xhbClobDaoTransformedXml = new XhbClobDao();
                xhbClobDaoTransformedXml.setClobData(transformedWithoutNamespacesXml);
                xhbClobRepository.savePersist(xhbClobDaoTransformedXml);

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
                xhbXmlDocumentRepository.savePersist(xhbXmlDocumentDaoTransformedXml);

                // Save to the cath_document_link table with the original and transformed xml
                // id's
                XhbCathDocumentLinkDao xhbCathDocumentLinkDao = new XhbCathDocumentLinkDao();
                xhbCathDocumentLinkDao
                        .setOrigCourtelListDocId(xhbCourtelListDao.get().getCourtelListId());
                xhbCathDocumentLinkDao
                        .setCathXmlId(xhbXmlDocumentDaoTransformedXml.getXmlDocumentId());
                xhbCathDocumentLinkRepository.savePersist(xhbCathDocumentLinkDao);

                // Return the cath_document_link record
                return xhbCathDocumentLinkDao;
            }
        }
        return null;
    }
    
    private static String transformList(String xslSchemaPath, String xmlToTransform)
        throws TransformerException, IOException {
        // Get the XSLT schema from the classpath
        Resource resource = new ClassPathResource(xslSchemaPath);
        
        try (InputStream inputStream = resource.getInputStream()) {
            StreamSource xslSource = new StreamSource(inputStream);
            xslSource.setSystemId(resource.getURL().toExternalForm());
            Templates templates = TransformerFactory.newInstance().newTemplates(xslSource);
            Transformer transformer = templates.newTransformer();
            
            // Transform the XML
            Source xmlSource = new StreamSource(new StringReader(xmlToTransform));
            StringWriter outWriter = TransformerUtils.transformList(transformer, xmlSource);
            
            return outWriter.toString();
        }
    }

    public static void fetchXmlAndGenerateJson(XhbCathDocumentLinkDao xhbCathDocumentLinkDao,
        XhbCathDocumentLinkRepository xhbCathDcoumentLlinkRepository,
        XhbXmlDocumentRepository xhbXmlDocumentRepository, XhbClobRepository xhbClobRepository,
        XhbCourtelListRepository xhbCourtelListRepository,
        XhbCppStagingInboundRepository xhbCppStagingInboundRepository)
        throws ParserConfigurationException, SAXException, IOException {

        // Fetch the xml_document record
        Optional<XhbXmlDocumentDao> xhbXmlDocumentDaoTransformedXml =
                xhbXmlDocumentRepository.findByIdSafe(xhbCathDocumentLinkDao.getCathXmlId());

        if (xhbXmlDocumentDaoTransformedXml.isPresent()) {
            // Fetch the clob record
            Optional<XhbClobDao> xhbClobDaoTransformedXml = xhbClobRepository
                    .findByIdSafe(xhbXmlDocumentDaoTransformedXml.get().getXmlDocumentClobId());

            if (xhbClobDaoTransformedXml.isPresent()) {
                // Generate the Json and save it to the clob_table
                XhbClobDao xhbClobDaoJson = new XhbClobDao();
                xhbClobDaoJson.setClobData(
                        generateJsonFromString(xhbClobDaoTransformedXml.get().getClobData())
                                .toString());
                xhbClobRepository.savePersist(xhbClobDaoJson);

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
                xhbXmlDocumentRepository.savePersist(xhbXmlDocumentDaoJson);

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
                xhbCathDcoumentLlinkRepository
                        .findByIdSafe(xhbCathDocumentLinkDao.getCathDocumentLinkId());

        if (xhbCathDocumentLinkDaoNoJsonId.isPresent()) {
            // Update the existing record with the cathJsonId
            xhbCathDocumentLinkDaoNoJsonId.get()
                    .setCathJsonId(xhbXmlDocumentDaoJson.getXmlDocumentId());
            xhbCathDcoumentLlinkRepository.update(xhbCathDocumentLinkDaoNoJsonId.get());
        }
    }

    private static JSONObject generateJsonFromString(String stringToConvert) {
        JSONObject json = XML.toJSONObject(stringToConvert);
        // Apply JSON transformation steps to flatten list arrays
        flattenListArrays(json);
        enforceArrayValues(json);
        coerceValuesToStrings(json);
        removeEmptyStringFields(json);
        return json;
    }

    private static void flattenListArrays(Object node) {
        if (node instanceof JSONObject json) {
            // Flatten each configured array-like structure
            ARRAY_KEYS.forEach((parent, child) -> normalizeArrayField(json, parent, child));
            // Recursively flatten nested arrays
            json.keySet().forEach(key -> flattenListArrays(json.get(key)));
        } else if (node instanceof JSONArray array) {
            for (int i = 0; i < array.length(); i++) {
                flattenListArrays(array.get(i));
            }
        }
    }

    private static void normalizeArrayField(JSONObject json, String parentKey, String childKey) {
        // Skip if the parent key is not present
        if (!json.has(parentKey)) {
            return;
        }
        // Get the value of the parent key
        Object value = json.get(parentKey);
        if (value instanceof JSONObject parentObj && parentObj.has(childKey)) {
            json.put(parentKey, toNormalizedArray(parentObj.get(childKey), childKey));
            // Skip if the child key is not present
        } else if (value instanceof JSONArray arrayValue) {
            json.put(parentKey, normalizeArray(arrayValue, childKey));
        }
    }

    private static JSONArray toNormalizedArray(Object value, String childKey) {
        // Convert the value to a JSONArray if it is not already one
        JSONArray array = value instanceof JSONArray ? (JSONArray) value : new JSONArray().put(value);
        // Normalize the array
        return normalizeArray(array, childKey);
    }

    private static JSONArray normalizeArray(JSONArray originalArray, String childKey) {
        JSONArray normalized = new JSONArray();
        for (int i = 0; i < originalArray.length(); i++) {
            // Remove wrapper objects that only contain the singular key
            Object element = unwrapElement(originalArray.get(i), childKey);
            if (element instanceof JSONArray nestedArray) {
                for (int j = 0; j < nestedArray.length(); j++) {
                    normalized.put(nestedArray.get(j));
                }
            } else {
                normalized.put(element);
            }
        }
        return normalized;
    }

    private static Object unwrapElement(Object element, String childKey) {
        if (element instanceof JSONObject obj && obj.length() == 1 && obj.has(childKey)) {
            return obj.get(childKey);
        }
        return element;
    }

    private static void enforceArrayValues(Object node) {
        if (node instanceof JSONObject json) {
            for (String key : new HashSet<>(json.keySet())) {
                Object value = json.get(key);
                if (FORCE_ARRAY_KEYS.contains(key)) {
                    // Force the value to be an array, even if it's a single object or string
                    JSONArray arrayValue = toJsonArray(value);
                    json.put(key, arrayValue);
                    // Recursively process the array contents
                    enforceArrayValues(arrayValue);
                } else {
                    // Recursively process nested structures
                    enforceArrayValues(value);
                }
            }
        } else if (node instanceof JSONArray array) {
            for (int i = 0; i < array.length(); i++) {
                Object element = array.get(i);
                // Check if any element in the array is a JSONObject that might contain keys we need to enforce
                if (element instanceof JSONObject nestedJson) {
                    enforceArrayValues(nestedJson);
                } else {
                    enforceArrayValues(element);
                }
            }
        }
    }

    private static JSONArray toJsonArray(Object value) {
        // If already an array, return it
        if (value instanceof JSONArray array) {
            return array;
        }
        // If null, return empty array
        if (value == null) {
            return new JSONArray();
        }
        // Otherwise, wrap the value (object, string, number, etc.) in an array
        JSONArray array = new JSONArray();
        array.put(value);
        return array;
    }

    private static void coerceValuesToStrings(Object node) {
        if (node instanceof JSONObject json) {
            for (String key : new HashSet<>(json.keySet())) {
                Object value = json.get(key);
                if (FORCE_STRING_KEYS.contains(key) && value != null) {
                    // Force the value to be a string, even if it's a number
                    json.put(key, value.toString());
                } else {
                    // Recursively process nested structures
                    coerceValuesToStrings(value);
                }
            }
        } else if (node instanceof JSONArray array) {
            for (int i = 0; i < array.length(); i++) {
                Object element = array.get(i);
                // Recursively process each element in the array
                coerceValuesToStrings(element);
            }
        }
    }

    private static void removeEmptyStringFields(Object node) {
        if (node instanceof JSONObject json) {
            cleanObject(json);
        } else if (node instanceof JSONArray array) {
            cleanTopLevelArray(array);
        }
    }

    private static void cleanObject(JSONObject json) {
        // iterate a copy of the keys so we can remove while iterating
        for (String key : new HashSet<>(json.keySet())) {
            Object value = json.get(key);

            if (isBlankString(value)) {
                json.remove(key);
                continue;
            }

            if (value instanceof JSONArray arrayValue) {
                if (arrayValue.isEmpty()) {
                    json.remove(key);
                } else {
                    JSONArray cleaned = cleanArrayPreserveThenClean(arrayValue);
                    if (cleaned.isEmpty()) {
                        json.remove(key);
                    } else {
                        json.put(key, cleaned);
                    }
                }
                continue;
            }

            if (isEmptyJsonObject(value)) {
                json.remove(key);
                continue;
            }

            // For any other object types (including non-empty JSONObject or nested JSONArray),
            // recurse to clean their children.
            removeEmptyStringFields(value);
        }
    }

    private static JSONArray cleanArrayPreserveThenClean(JSONArray arrayValue) {
        final JSONArray cleaned = new JSONArray();

        for (int i = 0; i < arrayValue.length(); i++) {
            Object element = arrayValue.get(i);

            // skip blank strings
            if (element instanceof String str && str.isBlank()) {
                continue;
            }

            // skip empty JSON objects (as-is, before recursive cleaning)
            if (element instanceof JSONObject obj && obj.isEmpty()) {
                continue;
            }

            // preserve the element (same order as original), then clean it in-place
            cleaned.put(element);
            removeEmptyStringFields(element);
        }

        return cleaned;
    }

    private static void cleanTopLevelArray(JSONArray array) {
        // Original behaviour only recursed into nested objects/arrays for top-level arrays,
        // without removing top-level blank strings/empty objects.
        for (int i = 0; i < array.length(); i++) {
            Object element = array.get(i);
            if (element instanceof JSONObject || element instanceof JSONArray) {
                removeEmptyStringFields(element);
            }
        }
    }

    private static boolean isBlankString(Object obj) {
        return obj instanceof String str && str.isBlank();
    }

    private static boolean isEmptyJsonObject(Object obj) {
        return obj instanceof JSONObject object && object.isEmpty();
    }
}
