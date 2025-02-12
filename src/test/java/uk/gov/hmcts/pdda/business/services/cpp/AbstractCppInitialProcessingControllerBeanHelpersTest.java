
package uk.gov.hmcts.pdda.business.services.cpp;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;
import uk.gov.hmcts.pdda.business.entities.xhbblob.XhbBlobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.services.cpplist.CppListControllerBean;
import uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3.CppStagingInboundControllerBean;
import uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3.CppStagingInboundHelper;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * AbstractCppInitialProcessingControllerBeanHelpersTest.
 **/
@ExtendWith(EasyMockExtension.class)
class AbstractCppInitialProcessingControllerBeanHelpersTest {

    private static final String NOT_INSTANCE = "Result is Not An Instance of";

    private EntityManager mockEntityManager;
    private XhbConfigPropRepository mockXhbConfigPropRepository;
    private XhbCourtRepository mockXhbCourtRepository;
    private XhbClobRepository mockXhbClobRepository;
    private XhbBlobRepository mockXhbBlobRepository;

    @TestSubject
    protected final CppInitialProcessingControllerBean classUnderTest = getClassUnderTest();

    private CppInitialProcessingControllerBean getClassUnderTest() {
        mockEntityManager = EasyMock.createMock(EntityManager.class);
        mockXhbConfigPropRepository = EasyMock.createMock(XhbConfigPropRepository.class);
        mockXhbCourtRepository = EasyMock.createMock(XhbCourtRepository.class);
        mockXhbClobRepository = EasyMock.createMock(XhbClobRepository.class);
        mockXhbBlobRepository = EasyMock.createMock(XhbBlobRepository.class);
        
        CppInitialProcessingControllerBean classUnderTest =
            new CppInitialProcessingControllerBean(mockEntityManager);

        ReflectionTestUtils.setField(classUnderTest, "cppStagingInboundHelper",
            EasyMock.createMock(CppStagingInboundHelper.class));
        ReflectionTestUtils.setField(classUnderTest, "xhbClobRepository",
            mockXhbClobRepository);
        ReflectionTestUtils.setField(classUnderTest, "xhbBlobRepository",
            mockXhbBlobRepository);
        ReflectionTestUtils.setField(classUnderTest, "xhbCourtRepository",
            mockXhbCourtRepository);
        ReflectionTestUtils.setField(classUnderTest, "xhbConfigPropRepository",
            mockXhbConfigPropRepository);
        return classUnderTest;
    }

    @Test
    void testGetCppStagingInboundControllerBean() {
        expectEntityManagerIsOpen();
        expectRepositoryIsOpen(mockXhbConfigPropRepository);
        expectRepositoryIsOpen(mockXhbCourtRepository);
        expectRepositoryIsOpen(mockXhbClobRepository);
        expectRepositoryIsOpen(mockXhbBlobRepository);
        assertInstanceOf(CppStagingInboundControllerBean.class,
            classUnderTest.getCppStagingInboundControllerBean(), NOT_INSTANCE);
    }

    @Test
    void testGetCppListControllerBean() {
        expectEntityManagerIsOpen();
        assertInstanceOf(CppListControllerBean.class, classUnderTest.getCppListControllerBean(),
            NOT_INSTANCE);
    }

    private void expectEntityManagerIsOpen() {
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        EasyMock.replay(mockEntityManager);
    }
    
    @SuppressWarnings("rawtypes")
    private void expectRepositoryIsOpen(AbstractRepository mockRepository) {
        EasyMock.expect(mockRepository.getEntityManager()).andReturn(mockEntityManager);
        EasyMock.replay(mockRepository);
    }
}
