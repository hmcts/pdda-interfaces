package uk.gov.hmcts.pdda.business.xmlbinding.hmcts.pdda.types;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DocumentTypeTypeTest {


    private static final Logger LOG = LoggerFactory.getLogger(DocumentTypeTypeTest.class);
    private static final String TRUE = "Result is not True";

    @BeforeEach
    public void setup() {
        // Do nothing
    }

    @AfterEach
    public void teardown() {
        // Do nothing
    }

    @Test
    void testInvalidType() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            DocumentTypeType.valueOf("Invalid");
        });
    }

    @Test
    void testDailyList() {
        Map<DocumentTypeType, Integer> map = new ConcurrentHashMap<>();
        map.put(DocumentTypeType.DL, DocumentTypeType.DL_TYPE);
        map.put(DocumentTypeType.DLL, DocumentTypeType.DLL_TYPE);
        map.put(DocumentTypeType.DLP, DocumentTypeType.DLP_TYPE);
        map.put(DocumentTypeType.DLS, DocumentTypeType.DLS_TYPE);
        boolean result = false;
        try {
            testTypeMap(map);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testFirmList() {
        Map<DocumentTypeType, Integer> map = new ConcurrentHashMap<>();
        map.put(DocumentTypeType.FL, DocumentTypeType.FL_TYPE);
        map.put(DocumentTypeType.FLL, DocumentTypeType.FLL_TYPE);
        map.put(DocumentTypeType.FLS, DocumentTypeType.FLS_TYPE);
        boolean result = false;
        try {
            testTypeMap(map);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testInternetWebPages() {
        Map<DocumentTypeType, Integer> map = new ConcurrentHashMap<>();
        map.put(DocumentTypeType.IWP, DocumentTypeType.IWP_TYPE);
        boolean result = false;
        try {
            testTypeMap(map);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testRTypes() {
        Map<DocumentTypeType, Integer> map = new ConcurrentHashMap<>();
        map.put(DocumentTypeType.RL, DocumentTypeType.RL_TYPE);
        map.put(DocumentTypeType.ROS, DocumentTypeType.ROS_TYPE);
        boolean result = false;
        try {
            testTypeMap(map);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testWarnedList() {
        Map<DocumentTypeType, Integer> map = new ConcurrentHashMap<>();
        map.put(DocumentTypeType.WL, DocumentTypeType.WL_TYPE);
        map.put(DocumentTypeType.WLL, DocumentTypeType.WLL_TYPE);
        map.put(DocumentTypeType.WLS, DocumentTypeType.WLS_TYPE);
        boolean result = false;
        try {
            testTypeMap(map);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    private void testTypeMap(Map<DocumentTypeType, Integer> map) {
        for (Map.Entry<DocumentTypeType, Integer> entry : map.entrySet()) {
            DocumentTypeType documentTypeType = entry.getKey();
            Integer type = entry.getValue();
            boolean result = testType(documentTypeType, type);
            assertTrue(result, TRUE);
        }
    }

    @SuppressWarnings("static-access")
    private boolean testType(DocumentTypeType classUnderTest, Integer type) {
        LOG.debug("testType({},{})", classUnderTest, type);
        assertEquals(type, classUnderTest.getType(), "Results are not Equal");
        assertNotNull(classUnderTest.enumerate(), "Result is Null");
        assertNotNull(classUnderTest.toString(), "Result is Null");
        return true;
    }
}
