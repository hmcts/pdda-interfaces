package uk.gov.hmcts.pdda.business;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcpplist.XhbCppListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbformatting.XhbFormattingRepository;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * <p>
 * Title: AbstractControllerBean Test.
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
class AbstractControllerBeanTest {

    private static final String NOT_INSTANCE = "Result is Not An Instance of";

    @TestSubject
    private final AbstractControllerBean classUnderTest =
        new AbstractControllerBean(EasyMock.createMock(EntityManager.class));

    @Test
    void testGetXhbClobRepository() {
        assertInstanceOf(XhbClobRepository.class, classUnderTest.getXhbClobRepository(),
            NOT_INSTANCE);
    }

    @Test
    void testGetXhbConfigPropRepository() {
        assertInstanceOf(XhbConfigPropRepository.class, classUnderTest.getXhbConfigPropRepository(),
            NOT_INSTANCE);
    }

    @Test
    void testGetXhbCppListRepository() {
        assertInstanceOf(XhbCppListRepository.class, classUnderTest.getXhbCppListRepository(),
            NOT_INSTANCE);
    }

    @Test
    void testGetXhbFormattingRepository() {
        assertInstanceOf(XhbFormattingRepository.class, classUnderTest.getXhbFormattingRepository(),
            NOT_INSTANCE);
    }
}
