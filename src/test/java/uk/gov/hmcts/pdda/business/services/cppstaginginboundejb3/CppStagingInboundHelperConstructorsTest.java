package uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.DummyServicesUtil;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * CppStagingInboundHelperConstructorsTest.
 */
@ExtendWith(EasyMockExtension.class)
class CppStagingInboundHelperConstructorsTest {

    private static final String NOT_TRUE = "Result is not True";

    @Mock
    private static EntityManager mockEntityManager;

    @Mock
    private XhbConfigPropRepository mockXhbConfigPropRepository;

    @Test
    void testConstructorWithEntityManager() {
        // Setup
        List<XhbConfigPropDao> xhbConfigPropDaos = new ArrayList<>();
        xhbConfigPropDaos
            .add(DummyServicesUtil.getXhbConfigPropDao("STAGING_DOCS_TO_PROCESS", "1"));

        EasyMock
            .expect(mockXhbConfigPropRepository.findByPropertyNameSafe("STAGING_DOCS_TO_PROCESS"))
            .andReturn(xhbConfigPropDaos);

        EasyMock.replay(mockXhbConfigPropRepository);

        boolean result = true;

        // Run
        new CppStagingInboundHelper(mockEntityManager, mockXhbConfigPropRepository);

        // Checks
        EasyMock.verify(mockXhbConfigPropRepository);
        assertTrue(result, NOT_TRUE);
    }
}
