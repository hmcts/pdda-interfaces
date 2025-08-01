package uk.gov.hmcts.pdda.business.services.pdda;

import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**

 * Title: PddaSftpUtil Test.


 * Description:


 * Copyright: Copyright (c) 2024


 * Company: CGI

 * @author Luke Gittins
 */
@ExtendWith(EasyMockExtension.class)
class PddaSftpUtilTest {

    @Test
    void testDefaultConstructor() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            new PddaSftpUtil();
        });
    }
    
}
