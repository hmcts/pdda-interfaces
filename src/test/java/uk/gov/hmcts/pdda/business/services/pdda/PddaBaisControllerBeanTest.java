package uk.gov.hmcts.pdda.business.services.pdda;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.pdda.business.services.pdda.sftp.SftpHelper;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>
 * Title: PDDA Bais Controller Bean Test.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2022
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Mark Harris
 */
@ExtendWith(EasyMockExtension.class)
class PddaBaisControllerBeanTest {

    private static final String TRUE = "Result is not True";

    @Mock
    private SftpHelper mockSftpHelper;

    @TestSubject
    private final PddaBaisControllerBean classUnderTest =
        new PddaBaisControllerBean(EasyMock.createMock(EntityManager.class));

    @TestSubject
    private final PddaBaisControllerBean classUnderTest2 = new PddaBaisControllerBean();

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testDoTask() {
        // Setup
        mockSftpHelper.processBaisMessages();
        EasyMock.replay(mockSftpHelper);
        // Run
        boolean result = false;
        try {
            classUnderTest.doTask();
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        // Checks
        EasyMock.verify(mockSftpHelper);
        assertTrue(result, TRUE);
    }

    @Test
    void testDoTask2() {
        // Setup
        mockSftpHelper.processBaisMessages();
        EasyMock.replay(mockSftpHelper);
        // Run
        boolean result = false;
        try {
            classUnderTest2.doTask();
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        // Checks
        EasyMock.verify(mockSftpHelper);
        assertTrue(result, TRUE);
    }

    @Test
    void testGetSftpHelper() {
        // Setup
        SftpHelper result = classUnderTest.getSftpHelper();
        SftpHelper result2 = null;
        // Checks
        assertNotNull(result, "Result is not null");
        assertNull(result2, "Result is null");
    }

}
