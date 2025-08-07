package uk.gov.hmcts.pdda.business.services.pdda;

import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * <p>
 * Title: PddaSftpUtil Test.
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
