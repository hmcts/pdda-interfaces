package uk.gov.hmcts.pdda.business.services.formatting;

import jakarta.ejb.EJBException;
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
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import uk.gov.hmcts.DummyFormattingUtil;
import uk.gov.hmcts.framework.services.TranslationServices;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingDao;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcpplist.XhbCppListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbformatting.XhbFormattingDao;
import uk.gov.hmcts.pdda.business.exception.formatting.FormattingException;
import uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3.CourtUtils;
import uk.gov.hmcts.pdda.business.services.pdda.BlobHelper;
import uk.gov.hmcts.pdda.business.vos.formatting.FormattingValue;
import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundles;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.time.LocalDateTime;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

/**
 * Title: FormattingServicesProcessing Test.
 * Description:
 * Copyright: Copyright (c) 2024
 * Company: CGI

 * @author Luke Gittins
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("PMD")
class FormattingServicesProcessingTest {

    private static final String NULL = "Result is Null";

    @Mock
    private Reader mockReader;
    
    @Mock
    private XhbCppFormattingRepository mockCppFormattingRepo;

    @Mock
    private OutputStream mockOutputStream;
    
    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private BlobHelper mockBlobHelper;
    
    @Mock
    private XhbCppListRepository mockCppListRepo;
    

    @InjectMocks
    private FormattingServices classUnderTest = new FormattingServices(
        EasyMock.createMock(EntityManager.class), EasyMock.createMock(BlobHelper.class));

    @BeforeEach
    void setup() {
        classUnderTest = new FormattingServices(mockEntityManager, mockBlobHelper) {
            @Override
            protected XhbCppFormattingRepository getXhbCppFormattingRepository() {
                return mockCppFormattingRepo;
            }

            @Override
            protected String getClobData(Long clobId) {
                return "<cppDocument></cppDocument>"; // prevent null
            }
            
            @Override
            protected XhbCppListRepository getXhbCppListRepository() {
                return mockCppListRepo;
            }
        };
    }


    
    @Test
    void testGetFormattingValue() {
        XhbFormattingDao xhbFormattingDao = DummyFormattingUtil.getXhbFormattingDao();
        FormattingValue result =
            classUnderTest.getFormattingValue(xhbFormattingDao, mockReader, mockOutputStream);
        assertNotNull(result, NULL);
    }
    
    /**
     * processIwpDocument throws on missing merge result.
     */
    @Test
    void testProcessIwpDocumentThrowsOnEmptyResult() {
        FormattingValue formattingValue = DummyFormattingUtil.getFormattingValue(
            "<xml/>", "IWP", "application/pdf", null // uses courtId = 81
        );

        when(mockCppFormattingRepo.findLatestByCourtDateInDocSafe(eq(81), eq("IWP"), any(LocalDateTime.class)))
            .thenReturn(DummyFormattingUtil.getXhbCppFormattingDao());

        assertThrows(FormattingException.class, () ->
            classUnderTest.processIwpDocument(formattingValue, "<translation/>"));
    }
    
    
    @Test
    void testProcessIwpDocumentThrowsXPathException() throws SAXException, ParserConfigurationException, IOException {
        // Arrange
        FormattingValue formattingValue = DummyFormattingUtil
            .getFormattingValue("<xml/>", "IWP", "application/pdf", null);

        XhbCppFormattingDao dao = DummyFormattingUtil.getXhbCppFormattingDao();
        when(mockCppFormattingRepo.findLatestByCourtDateInDocSafe(eq(81), eq("IWP"), any(LocalDateTime.class)))
            .thenReturn(dao);

        classUnderTest = new FormattingServices(mockEntityManager, mockBlobHelper) {
            @Override
            protected XhbCppFormattingRepository getXhbCppFormattingRepository() {
                return mockCppFormattingRepo;
            }

            @Override
            protected String getClobData(Long clobId) {
                return "<cppDocument></cppDocument>";
            }
        };

        // Mock static method CourtUtils.getCourtSites to throw XPathExpressionException
        try (MockedStatic<CourtUtils> mocked = org.mockito.Mockito.mockStatic(CourtUtils.class)) {
            mocked.when(() -> CourtUtils.getCourtSites(any()))
                .thenThrow(new XPathExpressionException("Mock XPath failure"));

            // Act & Assert
            assertThrows(FormattingException.class, () ->
                classUnderTest.processIwpDocument(formattingValue, "<translation/>"));
        }
    }
    
    @Test
    void testProcessIwpCppFormattingThrowsFormattingException() {
        FormattingValue formattingValue = DummyFormattingUtil.getFormattingValue();
        XhbCppFormattingDao cppDao = DummyFormattingUtil.getXhbCppFormattingDao();

        // Use invalid XML that causes a parsing exception
        classUnderTest = new FormattingServices(mockEntityManager, mockBlobHelper) {
            @Override
            protected String getClobData(Long clobId) {
                return null; // causes StringReader(null) → NullPointerException inside createInputDocument
            }
        };

        assertThrows(FormattingException.class, () ->
            classUnderTest
            .processIwpCppFormatting(123L, formattingValue, mock(Document.class), "<translation/>", cppDao));
    }
    
    @Test
    void testProcessListDocumentThrowsFormattingException() {
        FormattingValue formattingValue = DummyFormattingUtil.getFormattingValue();
        formattingValue.setXmlDocumentClobId(123L);

        when(mockCppListRepo.findByClobIdSafe(123L)).thenThrow(new EJBException("Simulated failure"));

        assertThrows(FormattingException.class, () ->
            classUnderTest.processListDocument(formattingValue, "<translation/>"));
    }
    
    @Test
    void testGetTranslationBundlesReturnsNonNull() {
        // Arrange
        TranslationBundles mockBundles = mock(TranslationBundles.class);
        TranslationServices mockTranslationServices = mock(TranslationServices.class);

        try (MockedStatic<TranslationServices> mockedStatic = mockStatic(TranslationServices.class)) {
            mockedStatic.when(TranslationServices::getInstance).thenReturn(mockTranslationServices);
            when(mockTranslationServices.getTranslationBundles(null)).thenReturn(mockBundles);

            // Act
            TranslationBundles result = classUnderTest.getTranslationBundles();

            // Assert
            assertNotNull(result);
            assertEquals(mockBundles, result);
        }
    }

}
