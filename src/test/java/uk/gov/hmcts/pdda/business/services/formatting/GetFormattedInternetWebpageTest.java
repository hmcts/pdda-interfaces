package uk.gov.hmcts.pdda.business.services.formatting;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import uk.gov.hmcts.DummyFormattingUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingDao;
import uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3.CourtUtils;
import uk.gov.hmcts.pdda.business.services.pdda.BlobHelper;
import uk.gov.hmcts.pdda.business.vos.formatting.FormattingValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GetFormattedInternetWebpageTest {

    private FormattingServicesProcessing classUnderTest;

    @Mock private EntityManager mockEntityManager;
    @Mock private BlobHelper mockBlobHelper;
    @Mock private Document mockCppDocument;

    private final String translationXml = "<translations/>";
    private final String courtSite = "SomeCourtSite";

    @BeforeEach
    void setUp() throws IOException, SAXException {
        classUnderTest = new FormattingServicesProcessing(mockEntityManager, mockBlobHelper) {
            @Override
            protected Long getLatestXhibitClobId(Integer courtId, String docType, String lang, String courtSite) {
                return 42L;
            }

            @Override
            protected StringBuilder processIwpCppFormatting(Long clobId, FormattingValue formattingValue,
                                                            Document cppDocument, String translationXml,
                                                            XhbCppFormattingDao val) {
                return new StringBuilder("<processed-xml/>");
            }

            @Override
            protected StringBuilder getFormattedInternetWebpageCourtSite(
                XhbCppFormattingDao val,
                FormattingValue formattingValue,
                Document cppDocument,
                String translationXml,
                Long pddaClobId) {
                return new StringBuilder("<court-site-retry/>");
            }
        };
    }

    @Test
    void testPathWhenClobListIsNull() throws IOException, SAXException {
        // Arrange
        List<StringBuilder> clobList = null;
        XhbCppFormattingDao dao = DummyFormattingUtil.getXhbCppFormattingDao();
        FormattingValue formattingValue = DummyFormattingUtil.getFormattingValue();

        // Act
        StringBuilder result = classUnderTest.getFormattedInternetWebpage(
            clobList, dao, formattingValue, courtSite, mockCppDocument, translationXml
        );

        // Assert
        assertNotNull(result);
        assertEquals("<processed-xml/>", result.toString());
    }

    @Test
    void testPathWhenCourtSiteNotInClobAndClobIdIsNull() throws IOException {
        // Arrange
        List<StringBuilder> clobList = new ArrayList<>();
        clobList.add(new StringBuilder("existing content")); // prevent isEmpty()


        try (MockedStatic<CourtUtils> courtUtils = mockStatic(CourtUtils.class)) {
            courtUtils.when(() -> CourtUtils.isCourtSiteInClob(anyList(), eq(courtSite))).thenReturn(false);

            // Redefine classUnderTest inside the mock block to ensure static mocking is effective
            classUnderTest = new FormattingServicesProcessing(mockEntityManager, mockBlobHelper) {
                @Override
                protected Long getLatestXhibitClobId(Integer courtId, String docType, String lang, String courtSite) {
                    return null; // <- triggers getFormattedInternetWebpageCourtSite
                }

                @Override
                protected StringBuilder getFormattedInternetWebpageCourtSite(
                    XhbCppFormattingDao val,
                    FormattingValue formattingValue,
                    Document cppDocument,
                    String translationXml,
                    Long pddaClobId) {
                    return new StringBuilder("<retried/>");
                }

                @Override
                protected StringBuilder processIwpCppFormatting(Long clobId, FormattingValue formattingValue,
                                                                Document cppDocument, String translationXml,
                                                                XhbCppFormattingDao val) {
                    return new StringBuilder("<processed-but-unexpected/>");
                }
            };

            XhbCppFormattingDao dao = DummyFormattingUtil.getXhbCppFormattingDao();
            FormattingValue formattingValue = DummyFormattingUtil.getFormattingValue();

            // Act
            StringBuilder result = classUnderTest.getFormattedInternetWebpage(
                clobList, dao, formattingValue, courtSite, mockCppDocument, translationXml
            );

            // Assert
            assertNotNull(result);
            assertEquals("<retried/>", result.toString());
        }
    }


    @Test
    void testPathWhenCourtSiteAlreadyInClob() throws IOException, SAXException {
        // Arrange
        List<StringBuilder> clobList = new ArrayList<>();
        clobList.add(new StringBuilder("existing"));

        XhbCppFormattingDao dao = DummyFormattingUtil.getXhbCppFormattingDao();
        FormattingValue formattingValue = DummyFormattingUtil.getFormattingValue();

        try (MockedStatic<CourtUtils> courtUtils = mockStatic(CourtUtils.class)) {
            courtUtils.when(() -> CourtUtils.isCourtSiteInClob(anyList(), eq(courtSite))).thenReturn(true);

            // Act
            StringBuilder result = classUnderTest.getFormattedInternetWebpage(
                clobList, dao, formattingValue, courtSite, mockCppDocument, translationXml
            );

            // Assert
            assertNull(result);
        }
    }
}

