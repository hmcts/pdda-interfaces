package uk.gov.hmcts.framework.util;

import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseDao;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**

 * Title: ReflectionEqualsHelperTest.


 * Description:


 * Copyright: Copyright (c) 2024


 * Company: CGI

 * @author Luke Gittins
 */
@ExtendWith(EasyMockExtension.class)
class ReflectionEqualsHelperTest {

    private static final String TRUE = "Result is not True";
    
    @Test
    void testEqualFieldsDifferentObjects() throws ParseException {
        XhbCaseDao xhbCaseDao = new XhbCaseDao();
        assertTrue(ReflectionEqualsHelper.equalFields(xhbCaseDao, xhbCaseDao), TRUE);
    }
}
