package uk.gov.hmcts.pdda.business.services.pdda;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3.CppStagingInboundHelper;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * <p>
 * Title: PddaHelper Test for the Repository Helpers.
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
class PddaHelperRepositoryHelpersTest {

    private static final String NOT_INSTANCE = "Result is Not An Instance of";

    @TestSubject
    private final PddaHelper classUnderTest =
        new PddaHelper(EasyMock.createMock(EntityManager.class));

    @Test
    void testGetClobRepository() {
        assertInstanceOf(XhbClobRepository.class, classUnderTest.getClobRepository(), NOT_INSTANCE);
    }

    @Test
    void testGetCourtRepository() {
        assertInstanceOf(XhbCourtRepository.class, classUnderTest.getCourtRepository(),
            NOT_INSTANCE);
    }

    @Test
    void testGetPddaMessageHelper() {
        assertInstanceOf(PddaMessageHelper.class, classUnderTest.getPddaMessageHelper(),
            NOT_INSTANCE);
    }

    @Test
    void testGetCppStagingInboundHelper() {
        assertInstanceOf(CppStagingInboundHelper.class, classUnderTest.getCppStagingInboundHelper(),
            NOT_INSTANCE);
    }

    @Test
    void testGetSftpHelper() {
        assertInstanceOf(PddaSftpHelper.class, classUnderTest.getPddaSftpHelper(), NOT_INSTANCE);
    }

}
