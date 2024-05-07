package uk.gov.hmcts.pdda.business.services.pdda;

import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.DummyFormattingUtil;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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

    private XhbClobRepository mockXhbClobRepository;

    private CourtelHelper classUnderTest;

    @BeforeEach
    public void setUp() throws Exception {
        mockXhbClobRepository = EasyMock.mock(XhbClobRepository.class);
        classUnderTest = new CourtelHelper(mockXhbClobRepository);
    }

    @AfterEach
    public void tearDown() throws Exception {
        // No teardown required
    }
    
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

    @Test
    void testWriteToCourtel() {
        // Setup
        XhbClobDao xhbClobDao = DummyFormattingUtil.getXhbClobDao(0L, "");
        // Expects
        EasyMock.expect(mockXhbClobRepository.findById(xhbClobDao.getClobId())).andReturn(Optional.of(xhbClobDao));
        EasyMock.replay(mockXhbClobRepository);
        // Run
        boolean result = false;
        try {
            classUnderTest.writeToCourtel(xhbClobDao.getClobId());
            result = true;
        } catch (Exception exception) {
            fail(exception.getMessage());
        }
        assertTrue(result, NOT_TRUE);
    }

}
