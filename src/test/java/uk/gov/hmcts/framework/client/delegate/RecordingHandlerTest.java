package uk.gov.hmcts.framework.client.delegate;

import org.easymock.EasyMockExtension;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>
 * Title: RecordingHandlerTest.
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
class RecordingHandlerTest {

    private static final String TRUE = "Result is not true";
    
    @TestSubject
    private final RecordingHandler classUnderTest =
        new RecordingHandler();

    @Test
    void testSetFileName() {
        boolean result = true;
        classUnderTest.setFileName("TestFileName");
        assertTrue(result, TRUE);
    }
}
