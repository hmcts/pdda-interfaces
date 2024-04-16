package uk.gov.hmcts.pdda.business.services.pdda.lighthouse;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundRepository;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageRepository;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * <p>
 * Title: LighthousePddaRepos Test.
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
class LighthousePddaReposTest {

    private static final String NOT_INSTANCE = "Result is Not An Instance of";

    @Mock
    private EntityManager mockEntityManager;

    @TestSubject
    private final LighthousePdda classUnderTest = new LighthousePdda(mockEntityManager);

    @Test
    void testGetXhbPddaMessageRepository() {
        assertInstanceOf(XhbPddaMessageRepository.class,
            classUnderTest.getXhbPddaMessageRepository(), NOT_INSTANCE);
    }

    @Test
    void testGetXhbCppStagingInboundRepository() {
        assertInstanceOf(XhbCppStagingInboundRepository.class,
            classUnderTest.getXhbCppStagingInboundRepository(), NOT_INSTANCE);
    }
}
