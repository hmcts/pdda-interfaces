package uk.gov.hmcts.pdda.business.services.cpp;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.pdda.business.services.cpplist.CppListControllerBean;
import uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3.CppStagingInboundControllerBean;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * AbstractCppInitialProcessingControllerBeanHelpersTest.
 **/
@ExtendWith(EasyMockExtension.class)
class AbstractCppInitialProcessingControllerBeanHelpersTest {

    private static final String NOT_INSTANCE = "Result is Not An Instance of";

    @Mock
    protected EntityManager mockEntityManager;

    @Mock
    private EntityTransaction mockTransaction;

    @Mock
    private Query mockQuery;

    @TestSubject
    protected final CppInitialProcessingControllerBean classUnderTest =
        new CppInitialProcessingControllerBean(mockEntityManager);

    @BeforeEach
    void setUp() {
        EasyMock.expect(mockEntityManager.getTransaction()).andReturn(mockTransaction).anyTimes();
        EasyMock.expect(mockTransaction.isActive()).andReturn(false).anyTimes();

        EasyMock.expect(mockEntityManager.createNamedQuery("XHB_CONFIG_PROP.findByPropertyName"))
            .andReturn(mockQuery).anyTimes();
        EasyMock.expect(mockQuery.getResultList()).andReturn(Collections.emptyList()).anyTimes();

        EasyMock.replay(mockEntityManager, mockTransaction);
    }

    @Test
    void testGetCppStagingInboundControllerBean() {
        assertInstanceOf(CppStagingInboundControllerBean.class,
            classUnderTest.getCppStagingInboundControllerBean(), NOT_INSTANCE);
    }

    @Test
    void testGetCppListControllerBean() {
        assertInstanceOf(CppListControllerBean.class, classUnderTest.getCppListControllerBean(),
            NOT_INSTANCE);
    }

}
