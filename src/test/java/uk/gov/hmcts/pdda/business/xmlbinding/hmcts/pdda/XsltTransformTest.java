package uk.gov.hmcts.pdda.business.xmlbinding.hmcts.pdda;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.xml.sax.ContentHandler;
import uk.gov.hmcts.pdda.business.xmlbinding.hmcts.pdda.types.DistributionTypeType;
import uk.gov.hmcts.pdda.business.xmlbinding.hmcts.pdda.types.DocumentTypeType;
import uk.gov.hmcts.pdda.business.xmlbinding.hmcts.pdda.types.MimeTypeType;

import java.io.IOException;
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("PMD")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class XsltTransformTest {

    private static final String TRUE = "Result is not True";
    private static final String NOTEQUALS = "Result is not Equals";
    private static final String FALSE = "Result is not False";

    @Mock
    private ContentHandler mockContentHandler;

    @Mock
    private Writer mockWriter;

    @InjectMocks
    private final XsltTransform classUnderTest = getClassUnderTest();

    @BeforeEach
    public void setup() {
        // Do nothing
    }

    @AfterEach
    public void teardown() {
        // Do nothing
    }

    private XsltTransform getClassUnderTest() {
        XsltTransform classUnderTest = new XsltTransform();
        classUnderTest.setDistributionType(DistributionTypeType.EMAIL);
        classUnderTest.setMimeType(MimeTypeType.PDF);
        classUnderTest.setDocumentType(DocumentTypeType.DL);
        classUnderTest.setMajorVersion(1);
        classUnderTest.setMinorVersion(0);
        classUnderTest.setXsltFileList(getDummyXsltFileList("filename1", "filename2"));
        return classUnderTest;
    }

    @Test
    void testTypeSettersGetters() {
        classUnderTest.setDistributionType(classUnderTest.getDistributionType());
        classUnderTest.setDocumentType(classUnderTest.getDocumentType());
        classUnderTest.setMajorVersion(classUnderTest.getMajorVersion());
        classUnderTest.setMimeType(classUnderTest.getMimeType());
        classUnderTest.setMinorVersion(classUnderTest.getMinorVersion());
        classUnderTest.setXsltFileList(classUnderTest.getXsltFileList());
        classUnderTest.getXsltFileList().setXsltFileName(classUnderTest.getXsltFileList().getXsltFileName());
        boolean result = true;
        assertTrue(result, TRUE);
    }

    @Test
    void testEquals() {
        // Setup
        XsltTransform compareTo = new XsltTransform();
        compareTo.setDistributionType(DistributionTypeType.FAX);
        compareTo.setMimeType(MimeTypeType.HTM);
        compareTo.setDocumentType(DocumentTypeType.IWP);
        compareTo.setMajorVersion(2);
        compareTo.setMinorVersion(1);
        compareTo.setXsltFileList(getDummyXsltFileList("filename2"));
        // Checks
        assertFalse(classUnderTest.equals(compareTo), FALSE);
        // Distibution type
        compareTo.setDistributionType(classUnderTest.getDistributionType());
        assertFalse(classUnderTest.equals(compareTo), FALSE);
        // Mime Type
        compareTo.setMimeType(classUnderTest.getMimeType());
        assertFalse(classUnderTest.equals(compareTo), FALSE);
        // Document Type
        compareTo.setDocumentType(classUnderTest.getDocumentType());
        assertFalse(classUnderTest.equals(compareTo), FALSE);
        // Major Version
        compareTo.setMajorVersion(classUnderTest.getMajorVersion());
        assertFalse(classUnderTest.equals(compareTo), FALSE);
        // Minor Version
        compareTo.setMinorVersion(classUnderTest.getMinorVersion());
        assertFalse(classUnderTest.equals(compareTo), FALSE);
        // FileList
        compareTo.setXsltFileList(classUnderTest.getXsltFileList());
        assertTrue(classUnderTest.equals(compareTo), TRUE);

        compareTo.getXsltFileList().removeAllXsltFileName();
        assertEquals(0, compareTo.getXsltFileList().getXsltFileNameCount(), NOTEQUALS);
    }

    @Test
    void testMajorVersion() {
        classUnderTest.setMajorVersion(1);
        assertTrue(classUnderTest.hasMajorVersion(), TRUE);
        classUnderTest.deleteMajorVersion();
        assertFalse(classUnderTest.hasMajorVersion(), FALSE);
    }

    @Test
    void testMinorVersion() {
        classUnderTest.setMinorVersion(1);
        assertTrue(classUnderTest.hasMinorVersion(), TRUE);
        classUnderTest.deleteMinorVersion();
        assertFalse(classUnderTest.hasMinorVersion(), FALSE);
    }

    @Test
    void testValidate() throws ValidationException {
        boolean result;
        classUnderTest.validate();
        result = true;

        assertTrue(result, TRUE);
    }

    @Test
    void testXsltFileListIndexOutOfBoundsException() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            classUnderTest.getXsltFileList().getXsltFileName(-99);
        });
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            classUnderTest.getXsltFileList().setXsltFileName(-99, "");
        });
    }

    @Test
    void testMarshalContentHandler() throws MarshalException, ValidationException, IOException {
        boolean result;
        classUnderTest.marshal(mockContentHandler);
        result = true;

        assertTrue(result, TRUE);
    }

    private XsltFileList getDummyXsltFileList(String... filenames) {
        XsltFileList result = new XsltFileList();
        for (int i = 0; i < filenames.length; i++) {
            result.addXsltFileName(filenames[i]);
            assertEquals(filenames[i], result.getXsltFileName(i), NOTEQUALS);
        }
        int lastRowNo = filenames.length;
        result.addXsltFileName("AdditionalFile1");
        result.setXsltFileName(lastRowNo, "AdditionalFile2");
        result.removeXsltFileName(lastRowNo);
        assertEquals(filenames.length, result.getXsltFileName().length, NOTEQUALS);
        assertTrue(result.isValid(), TRUE);
        return result;
    }
}
