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
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.CourtelJson;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.ListJson;
import uk.gov.hmcts.pdda.business.services.pdda.cath.CathUtils;

import java.net.http.HttpRequest;

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

    private static final String TRUE = "Result is False";

    @Mock
    private BlobHelper mockBlobHelper;

    @Mock
    private OAuth2Helper mockOAuth2Helper;

    @Mock
    private HttpRequest mockHttpRequest;

    @InjectMocks
    private CathHelper classUnderTest;

    @BeforeEach
    public void setUp() throws Exception {
        Mockito.mockStatic(CathUtils.class);

        classUnderTest = new CathHelper(mockOAuth2Helper);
    }

    @AfterEach
    public void tearDown() throws Exception {
        // Clear down statics
        Mockito.clearAllCaches();
    }

    @Test
    void testSend() {
        // Setup
        CourtelJson json = DummyCourtelUtil.getListJson();
        // Expects
        Mockito.when(mockOAuth2Helper.getAccessToken()).thenReturn("accessToken");
        Mockito
            .when(CathUtils.getHttpPostRequest(Mockito.isA(java.time.LocalDateTime.class),
                Mockito.isA(String.class), Mockito.isA(String.class), Mockito.isA(String.class),
                Mockito.isA(Integer.class), Mockito.isA(String.class), Mockito.isA(String.class)))
            .thenReturn(mockHttpRequest);
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
