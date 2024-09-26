package uk.gov.hmcts.pdda.business.services.pdda.cath;

import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.CourtelJson;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.PublicationConfiguration;
import uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet.InitializationService;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("PMD.LawOfDemeter")
public final class CathUtils {

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

    public static HttpRequest getHttpPostRequest(String url,
        CourtelJson courtelJson) {
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
}
