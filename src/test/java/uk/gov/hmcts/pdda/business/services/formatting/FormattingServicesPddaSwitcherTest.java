package uk.gov.hmcts.pdda.business.services.formatting;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import org.xml.sax.ContentHandler;
import uk.gov.hmcts.DummyFormattingUtil;
import uk.gov.hmcts.DummyServicesUtil;
import uk.gov.hmcts.framework.services.XmlServices;
import uk.gov.hmcts.framework.services.XslServices;
import uk.gov.hmcts.pdda.business.entities.xhbblob.XhbBlobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppformattingmerge.XhbCppFormattingMergeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcpplist.XhbCppListDao;
import uk.gov.hmcts.pdda.business.entities.xhbcpplist.XhbCppListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbformatting.XhbFormattingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentRepository;
import uk.gov.hmcts.pdda.business.services.pdda.BlobHelper;
import uk.gov.hmcts.pdda.business.services.pdda.CourtelHelper;
import uk.gov.hmcts.pdda.business.vos.formatting.FormattingValue;
import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundles;
import uk.gov.hmcts.pdda.business.xmlbinding.formatting.FormattingConfig;

import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * FormattingServicesPddaSwitcherTest.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.TooManyFields", "PMD.CouplingBetweenObjects"})
class FormattingServicesPddaSwitcherTest {

    private static final String NULL = "Result is not Null";
    private static final String TRUE = "Result is not True";
    private static final String DOCTYPE_DAILY_LIST = "DL";
    private static final String TRANSLATION_BUNDLE_XML = "";
    private static final String XML = "XML";

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private TranslationBundles mockTranslationBundles;

    @Mock
    private FormattingConfig mockFormattingConfig;

    @Mock
    private XslServices mockXslServices;

    @Mock
    private XmlServices mockXmlServices;

    @Mock
    private TransformerFactory mockTransformerFactory;

    @Mock
    private Transformer mockTransformer;

    @Mock
    private ContentHandler mockContentHandler;

    @Mock
    private XhbBlobRepository mockXhbBlobRepository;

    @Mock
    private XhbClobRepository mockXhbClobRepository;

    @Mock
    private XhbCppListRepository mockXhbCppListRepository;

    @Mock
    private XhbCppFormattingRepository mockXhbCppFormattingRepository;

    @Mock
    private XhbFormattingRepository mockXhbFormattingRepository;

    @Mock
    private XhbConfigPropRepository mockXhbConfigPropRepository;

    @Mock
    private XhbCppFormattingMergeRepository mockXhbCppFormattingMergeRepository;

    @Mock
    private XhbXmlDocumentRepository mockXhbXmlDocumentRepository;

    @Mock
    private BlobHelper mockBlobHelper;
    
    @Mock
    private CourtelHelper mockCourtelHelper;

    @InjectMocks
    private FormattingServices classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new FormattingServices(mockEntityManager, mockCourtelHelper, mockBlobHelper);
        ReflectionTestUtils.setField(classUnderTest, "xhbConfigPropRepository",
            mockXhbConfigPropRepository);
        ReflectionTestUtils.setField(classUnderTest, "xhbCppListRepository",
            mockXhbCppListRepository);
        ReflectionTestUtils.setField(classUnderTest, "translationBundles", mockTranslationBundles);
        ReflectionTestUtils.setField(classUnderTest, "xslServices", mockXslServices);
        ReflectionTestUtils.setField(classUnderTest, "formattingConfig", mockFormattingConfig);
        ReflectionTestUtils.setField(classUnderTest, "xhbBlobRepository", mockXhbBlobRepository);
        ReflectionTestUtils.setField(classUnderTest, "xhbClobRepository", mockXhbClobRepository);
        ReflectionTestUtils.setField(classUnderTest, "xhbCppListRepository",
            mockXhbCppListRepository);
        ReflectionTestUtils.setField(classUnderTest, "xhbCppFormattingRepository",
            mockXhbCppFormattingRepository);
        ReflectionTestUtils.setField(classUnderTest, "xhbFormattingRepository",
            mockXhbFormattingRepository);
        ReflectionTestUtils.setField(classUnderTest, "xhbCppFormattingMergeRepository",
            mockXhbCppFormattingMergeRepository);
        ReflectionTestUtils.setField(classUnderTest, "xhbXmlDocumentRepository",
            mockXhbXmlDocumentRepository);
    }

    @AfterEach
    public void tearDown() {
        // Do nothing
    }

    @Test
    void testPddaSwitcherBothXhibitAndPdda() {
        boolean result =
            testPddaSwitcherBlocked(FormattingServicesTest.PddaSwitcher.PDDA_AND_XHIBIT);
        assertTrue(result, TRUE);
    }

    @Test
    void testPddaSwitcherBothXhibitOnly() {
        boolean result = testPddaSwitcherBlocked(FormattingServicesTest.PddaSwitcher.XHIBIT_ONLY);
        assertTrue(result, TRUE);
    }

    private boolean testPddaSwitcherBlocked(String pddaSwitcherValue) {
        // Setup
        expectPddaSwitcher(pddaSwitcherValue);
        XhbClobDao xhbClobDao =
            DummyFormattingUtil.getXhbClobDao(Long.valueOf(1), FormattingServicesTest.CPP_LIST);
        XhbCppListDao xhbCppListDao = DummyFormattingUtil.getXhbCppListDao();
        xhbCppListDao.setListClobId(xhbClobDao.getClobId());
        xhbCppListDao.setListType(DOCTYPE_DAILY_LIST);
        FormattingValue formattingValue = DummyFormattingUtil
            .getFormattingValue(xhbClobDao.getClobData(), DOCTYPE_DAILY_LIST, XML, xhbCppListDao);
        Mockito.when(mockTranslationBundles.toXml()).thenReturn(TRANSLATION_BUNDLE_XML);
        // Run
        classUnderTest.processDocument(formattingValue, mockEntityManager);
        // Checks
        // ...Make sure the XmlUtils is null (ie it did not run)
        assertNull(classUnderTest.getXmlUtils(), NULL);
        return true;
    }

    private void expectPddaSwitcher(String propertyValue) {
        expectConfigProp(FormattingServicesTest.PddaSwitcher.PDDA_SWITCH, propertyValue);
    }

    private void expectConfigProp(String propertyName, String propertyValue) {
        List<XhbConfigPropDao> daoList = new ArrayList<>();
        daoList.add(DummyServicesUtil.getXhbConfigPropDao(propertyName, propertyValue));
        Mockito.when(mockXhbConfigPropRepository.findByPropertyName(propertyName))
            .thenReturn(daoList);
    }
}
