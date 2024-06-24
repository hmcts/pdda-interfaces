package uk.gov.hmcts.pdda.business.services.pdda;

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
import uk.gov.hmcts.DummyCourtelUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListJson;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>
 * Title: CathHelperTest Test.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2024
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Mark Harris
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CathHelperTest {

    private static final String NOTNULL = "Result is null";
    private static final String TRUE = "Result is False";

    @Mock
    private BlobHelper mockBlobHelper;

    @InjectMocks
    private CathHelper classUnderTest;

    @BeforeEach
    public void setUp() throws Exception {
        classUnderTest = new CathHelper(mockBlobHelper);
    }

    @AfterEach
    public void tearDown() throws Exception {
        // Do nothing
    }

    @Test
    void testConvertDaoToJsonObject() {
        // Setup
        byte[] dummyByteArray = {'1', '2', '3'};
        XhbCourtelListDao dummyDao = DummyCourtelUtil.getXhbCourtelListDao();
        // Expects
        Mockito.when(mockBlobHelper.getBlobData(dummyDao.getBlobId())).thenReturn(dummyByteArray);
        // Run
        XhbCourtelListJson result = classUnderTest.convertDaoToJsonObject(dummyDao);
        // Checks
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testGenerateJsonString() {
        // Setup
        XhbCourtelListJson dummyJsonDao = DummyCourtelUtil.getXhbCourtelListJson();
        // Run
        String result = classUnderTest.generateJsonString(dummyJsonDao);
        // Checks
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testSend() {
        // Setup
        String json = "";
        // Run
        boolean result = false;
        try {
            classUnderTest.send(json);
            result = true;
        } catch (Exception exception) {
            fail(exception.getMessage());
        }
        // Checks
        assertTrue(result, TRUE);
    }
}
