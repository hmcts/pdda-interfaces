package uk.gov.hmcts.pdda.business.services.pdda.sftp;

import com.jcraft.jsch.Session;
import de.ppi.fakesftpserver.extension.FakeSftpServerExtension;
import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.core.env.Environment;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.services.pdda.PddaMessageHelper;
import uk.gov.hmcts.pdda.business.services.pdda.PddaSftpHelper;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * <p>
 * Title: PddaHelperBaisTest.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2023
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Mark Harris
 */
@ExtendWith(EasyMockExtension.class)
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.SingularField"})
class SftpHelperUtilTest {

    @RegisterExtension
    public final FakeSftpServerExtension sftpServer = new FakeSftpServerExtension();

    private static final String NOT_NULL = "Result is Not Null";

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private PddaMessageHelper mockPddaMessageHelper;

    @Mock
    private PddaSftpHelper mockPddaSftpHelper;

    @Mock
    private SftpConfigHelper mockSftpConfigHelper;

    @Mock
    private SftpConfig mockSftpConfig;

    @Mock
    private XhbConfigPropRepository mockXhbConfigPropRepository;

    @Mock
    private XhbCourtRepository mockXhbCourtRepository;

    @Mock
    private XhbClobRepository mockXhbClobRepository;

    @Mock
    private Session mockSession;

    @Mock
    private SftpService mockSftpService;

    @Mock
    private Environment mockEnvironment;

    private final SftpConfig sftpConfig = new SftpConfig();

    @TestSubject
    private final SftpHelperUtil classUnderTest =
        new SftpHelperUtil(EasyMock.createMock(EntityManager.class), mockXhbConfigPropRepository,
            mockEnvironment, mockPddaMessageHelper, mockXhbClobRepository, mockXhbCourtRepository);

    @TestSubject
    private final SftpHelperUtil classUnderTest2 =
        new SftpHelperUtil(EasyMock.createMock(EntityManager.class));


    @Test
    void testPopulateSftpConfig() {
        // Setup
        sftpConfig.setHost("localhost");
        sftpConfig.setPort(22);
        // Run
        classUnderTest.populateSftpConfig(sftpConfig.getPort());

        sftpConfig.setPort(0);
        classUnderTest.populateSftpConfig(sftpConfig.getPort());
        // Checks
        assertNotNull(sftpConfig.getHost(), NOT_NULL);
        assertNotNull(sftpConfig.getPort(), NOT_NULL);
    }


}
