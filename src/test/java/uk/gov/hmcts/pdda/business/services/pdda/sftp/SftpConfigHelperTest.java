package uk.gov.hmcts.pdda.business.services.pdda.sftp;

import com.jcraft.jsch.Session;
import de.ppi.fakesftpserver.extension.FakeSftpServerExtension;
import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.services.pdda.PddaMessageHelper;
import uk.gov.hmcts.pdda.business.services.pdda.PddaSftpHelper;

/**

 * Title: PddaHelperBaisTest.


 * Description:


 * Copyright: Copyright (c) 2023


 * Company: CGI

 * @author Mark Harris
 */
@ExtendWith(EasyMockExtension.class)
@SuppressWarnings({"PMD"})
class SftpConfigHelperTest {

    private static final Logger LOG = LoggerFactory.getLogger(SftpConfigHelperTest.class);

    @RegisterExtension
    public final FakeSftpServerExtension sftpServer = new FakeSftpServerExtension();

    private static final String NOT_NULL = "Result is Null";

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
    private final SftpConfigHelper classUnderTest =
        new SftpConfigHelper(EasyMock.createMock(EntityManager.class), mockXhbConfigPropRepository,
            mockEnvironment, mockPddaMessageHelper, mockXhbClobRepository, mockXhbCourtRepository);

}
