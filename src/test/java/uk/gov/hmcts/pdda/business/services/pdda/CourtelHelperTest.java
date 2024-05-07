package uk.gov.hmcts.pdda.business.services.pdda;

import org.easymock.EasyMockExtension;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>
 * Title: CourtelHelperTest.
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
class CourtelHelperTest {

    private static final String NOT_TRUE = "Result is not True";
    private static final String NOT_FALSE = "Result is not False";
    
    @TestSubject
    private final CourtelHelper classUnderTest = new CourtelHelper();
    
    @Test
    void testIsCourtelSendableDocumentValid() {
        for (String type : CourtelHelper.VALID_LISTS) {
            assertTrue(classUnderTest.isCourtelSendableDocument(type), NOT_TRUE);
        }
    }
    
    @Test
    void testIsCourtelSendableDocumentInvalid() {
        assertFalse(classUnderTest.isCourtelSendableDocument("INVALID"), NOT_FALSE);
    }
}
