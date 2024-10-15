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
import uk.gov.hmcts.pdda.business.services.pdda.sftp.SftpService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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

    private static final String NULL = "Result is Null";
    private static final String NOT_NULL = "Result is Not Null";

    @Mock
    private SftpService mockSftpService;

    @TestSubject
    private final PddaBaisControllerBean classUnderTest =
        new PddaBaisControllerBean(EasyMock.createMock(EntityManager.class));

    @TestSubject
    private final PddaBaisControllerBean classUnderTest2 = new PddaBaisControllerBean();

    /*
     * @SuppressWarnings("PMD.UseUnderscoresInNumericLiterals") private static final int TEST_PORT =
     * 65182;
     */

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    /*
     * @Test void testDoTask() { // Setup mockSftpService.processBaisMessages(TEST_PORT); //
     * EasyMock.replay(mockSftpHelper); // Run boolean result = false; try {
     * classUnderTest.doTask(); result = true; } catch (Exception exception) { fail(exception); } //
     * Checks // EasyMock.verify(mockSftpHelper); assertTrue(result, TRUE); }
     * 
     * @Test
     * 
     * void testDoTask2() { // Setup
     * EasyMock.expect(mockSftpService.processBaisMessages(TEST_PORT)).andReturn(false);
     * EasyMock.replay(mockSftpService); // Run boolean result = false; try {
     * classUnderTest2.doTask(); result = true; } catch (Exception exception) { fail(exception); }
     * // Checks EasyMock.verify(mockSftpService); assertTrue(result, TRUE); }
     */

    @Test
    void testGetSftpService1() {
        // Setup
        SftpService result = classUnderTest.getSftpService();
        SftpService result2 = null;
        // Checks
        assertNotNull(result, NOT_NULL);
        assertNull(result2, NULL);
    }

    @Test
    void testGetSftpService2() {
        // Setup
        SftpService result = classUnderTest2.getSftpService();
        SftpService result2 = null;
        // Checks
        assertNotNull(result, NOT_NULL);
        assertNull(result2, NULL);
    }

}
