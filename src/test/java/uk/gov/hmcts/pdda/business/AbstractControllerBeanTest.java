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

    private EntityManager mockEntityManager;

    @TestSubject
    private final AbstractControllerBean classUnderTest = getClassUnderTest();

    private AbstractControllerBean getClassUnderTest() {
        mockEntityManager = EasyMock.createMock(EntityManager.class);
        return new AbstractControllerBean(mockEntityManager);
    }

    @Test
    void testGetXhbClobRepository() {
        expectEntityManagerIsOpen();
        assertInstanceOf(XhbClobRepository.class, classUnderTest.getXhbClobRepository(),
            NOT_INSTANCE);
    }

    @Test
    void testGetXhbConfigPropRepository() {
        expectEntityManagerIsOpen();
        assertInstanceOf(XhbConfigPropRepository.class, classUnderTest.getXhbConfigPropRepository(),
            NOT_INSTANCE);
    }

    @Test
    void testGetXhbCppListRepository() {
        expectEntityManagerIsOpen();
        assertInstanceOf(XhbCppListRepository.class, classUnderTest.getXhbCppListRepository(),
            NOT_INSTANCE);
    }

    @Test
    void testGetXhbFormattingRepository() {
        expectEntityManagerIsOpen();
        assertInstanceOf(XhbFormattingRepository.class, classUnderTest.getXhbFormattingRepository(),
            NOT_INSTANCE);
    }
    
    private void expectEntityManagerIsOpen() {
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        EasyMock.replay(mockEntityManager);
    }
}
