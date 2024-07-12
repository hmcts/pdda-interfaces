package uk.gov.hmcts.pdda.business.services.pdda.cath;

import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.PublicationConfiguration;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class CathUtils {

    private static final String DATETIME_FORMAT = "yyyy-MM-ddTHH:mm";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String PROVENANCE = "PDDA";

    private CathUtils() {
    }
    
    public static String getDateTimeAsString(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern(DATETIME_FORMAT));
    }

    public static HttpRequest getHttpPostRequest(LocalDateTime dateTime, String url,
        String typeHeader, String listType, Integer courtId, String language, String json) {
        // Get the times
        String now = CathUtils.getDateTimeAsString(dateTime);
        String nextMonth = CathUtils.getDateTimeAsString(dateTime.plusMonths(1));
        // Return the HttpRequest for the post
        return HttpRequest.newBuilder().uri(URI.create(url))
            .header(PublicationConfiguration.TYPE_HEADER, typeHeader)
            .header(PublicationConfiguration.PROVENANCE_HEADER, PROVENANCE)
            .header(PublicationConfiguration.DISPLAY_FROM_HEADER, now)
            .header(PublicationConfiguration.DISPLAY_TO_HEADER, nextMonth)
            .header(PublicationConfiguration.COURT_ID, courtId.toString())
            .header(PublicationConfiguration.LIST_TYPE, listType)
            .header(PublicationConfiguration.LANGUAGE_HEADER, language)
            .header(PublicationConfiguration.CONTENT_DATE, now)
            .header(CONTENT_TYPE, CONTENT_TYPE_JSON).POST(BodyPublishers.ofString((json))).build();
    }
}
