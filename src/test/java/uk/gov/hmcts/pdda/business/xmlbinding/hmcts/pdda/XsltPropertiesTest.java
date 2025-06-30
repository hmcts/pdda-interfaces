
package uk.gov.hmcts.pdda.business.xmlbinding.hmcts.pdda;

import org.exolab.castor.xml.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.pdda.business.xmlbinding.hmcts.pdda.types.DocumentTypeType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("PMD")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class XsltPropertiesTest {

    private static final String EQUALS = "Results are not Equal";
    private static final String FALSE = "Result is not False";
    private static final String TRUE = "Result is not True";
    private static final String EMPTY_STRING = "";

    @InjectMocks
    private final XsltProperties classUnderTest = getClassUnderTest();

    @BeforeEach
    public void setup() {
        // Do nothing
    }

    @AfterEach
    public void teardown() {
        // Do nothing
    }

    private XsltProperties getClassUnderTest() {
        XsltProperties classUnderTest = new XsltProperties();
        classUnderTest.removeAllXsltTransform();
        XsltTransform[] xsltTransforms = {getXsltTransform(DocumentTypeType.DL)};
        classUnderTest.setXsltTransform(xsltTransforms);
        classUnderTest.addXsltTransform(getXsltTransform(DocumentTypeType.IWP));
        return classUnderTest;
    }

    @Test
    void testTypeSettersGetters() {
        int arrayNo = classUnderTest.getXsltTransformCount();
        classUnderTest.setXsltTransform(classUnderTest.getXsltTransform());
        assertEquals(arrayNo, classUnderTest.getXsltTransform().length, EQUALS);
        classUnderTest.addXsltTransform(getXsltTransform(DocumentTypeType.FL));
        classUnderTest.setXsltTransform(arrayNo - 1, getXsltTransform(DocumentTypeType.WL));
        classUnderTest.removeXsltTransform(arrayNo - 1);
    }

    @Test
    void testEquals() {
        XsltProperties compareTo;
        compareTo = classUnderTest;
        Boolean isEquals = classUnderTest.equals(compareTo);
        assertTrue(isEquals, TRUE);
        compareTo = new XsltProperties();
        isEquals = classUnderTest.equals(compareTo);
        assertFalse(isEquals, FALSE);
        XsltTransform[] xsltTransforms = {getXsltTransform(DocumentTypeType.DL)};
        compareTo.setXsltTransform(xsltTransforms);
        isEquals = classUnderTest.equals(compareTo);
        assertFalse(isEquals, FALSE);
        isEquals = EMPTY_STRING.equals(classUnderTest);
        assertFalse(isEquals, FALSE);

    }

    @Test
    void testValidate() throws ValidationException {
        boolean result;
        classUnderTest.validate();
        classUnderTest.isValid();
        result = true;

        assertTrue(result, TRUE);
    }

    @Test
    void testSetXsltTransformIndexOutOfBoundsException() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            classUnderTest.getXsltTransform(-99);
        });
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            classUnderTest.setXsltTransform(-99, new XsltTransform());
        });
    }

    private XsltTransform getXsltTransform(DocumentTypeType documentType) {
        XsltTransform result = new XsltTransform();
        result.setDocumentType(documentType);
        return result;
    }
}
