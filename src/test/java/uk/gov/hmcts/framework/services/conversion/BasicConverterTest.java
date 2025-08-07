package uk.gov.hmcts.framework.services.conversion;

import org.easymock.EasyMockExtension;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>
 * Title: BasicConverterTest.
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
class BasicConverterTest {

    private static final String TRUE = "Result is not true";
    private static final String FALSE = "Result is not false";

    @TestSubject
    private final BasicConverter classUnderTest = new BasicConverter();

    @Test
    void testParseBooleanTrue() {
        assertTrue(classUnderTest.parseBoolean("true"), TRUE);
    }
    
    @Test
    void testParseBooleanFalse() {
        assertFalse(classUnderTest.parseBoolean("false"), FALSE);
    }
    
    @Test
    void testformatBooleanTrue() {
        assertEquals("true", classUnderTest.formatBoolean(true), TRUE);
    }
    
    @Test
    void testformatBooleanFalse() {
        assertEquals("false", classUnderTest.formatBoolean(false), FALSE);
    }
}
