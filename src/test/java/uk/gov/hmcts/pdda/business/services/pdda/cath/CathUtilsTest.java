package uk.gov.hmcts.pdda.business.services.pdda.cath;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyCourtelUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.CourtelJson;

import java.net.http.HttpRequest;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * <p>
 * Title: CathUtils Test.
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
class CathUtilsTest {

    private static final String NOTNULL = "Result is null";

    @Test
    void testGetDateTimeAsString() {
        String result = CathUtils.getDateTimeAsString(LocalDateTime.now());
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testGetHttpPostRequest() {
        // Setup
        CourtelJson courtelJson = DummyCourtelUtil.getListJson();
        String url = "https://dummy.com/url";
        // Run
        HttpRequest result = CathUtils.getHttpPostRequest(LocalDateTime.now(), url, courtelJson);
        assertNotNull(result, NOTNULL);
    }
}
