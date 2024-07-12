package uk.gov.hmcts;

import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.ArtefactType;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.Language;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.ListJson;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.ListType;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListDao;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.net.ssl.SSLSession;

public final class DummyCourtelUtil {

    private static final String TEST1 = "Test1";
    private static final String TEST2 = "Test2";


    private DummyCourtelUtil() {
        // Do nothing
    }

    public static XhbCourtelListDao getXhbCourtelListDao() {
        Integer courtelListId = Integer.valueOf(-1);
        Integer xmlDocumentId = Integer.valueOf(-2);
        Long clobId = Long.valueOf(-3);
        Long blobId = Long.valueOf(-3);
        String messageText = "messageText";
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = Integer.valueOf(3);
        XhbCourtelListDao result = new XhbCourtelListDao();
        result.setCourtelListId(courtelListId);
        result.setXmlDocumentId(xmlDocumentId);
        result.setXmlDocumentClobId(clobId);
        result.setBlobId(blobId);
        result.setMessageText(messageText);
        result.setSentToCourtel("N");
        result.setNumSendAttempts(0);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        return new XhbCourtelListDao(result);
    }
    
    public static ListJson getListJson() {
        ListJson result = new ListJson();
        result.setListType(ListType.CROWN_DAILY_LIST);
        result.setLanguage(Language.ENGLISH);
        result.setJson("");
        return result;
    }

    public static HttpResponse<String> getHttpResponse(int statusCode, String body) {
        return new HttpResponse<>() {
            @Override
            public int statusCode() {
                return statusCode;
            }

            @Override
            public HttpRequest request() {
                return null;
            }

            @Override
            public Optional<HttpResponse<String>> previousResponse() {
                return Optional.empty();
            }

            @Override
            public HttpHeaders headers() {
                return null;
            }

            @Override
            public String body() {
                return body;
            }

            @Override
            public Optional<SSLSession> sslSession() {
                return Optional.empty();
            }

            @Override
            public URI uri() {
                return null;
            }

            @Override
            public HttpClient.Version version() {
                return null;
            }
        };
    }

}
