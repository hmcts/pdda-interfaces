package uk.gov.hmcts.pdda.business.services.pdda.sftp;

import jakarta.persistence.EntityManager;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.services.pdda.PddaMessageHelper;


/**
 * SFTP Configuration Helper class to handle getting the SFTP configuration.
 */
public class SftpConfigHelper extends SftpService {

    private static final Logger LOG = LoggerFactory.getLogger(SftpConfigHelper.class);

    protected static final String LOG_CALLED = " called";
    private static final String SFTP_ERROR = "SFTP Error:";


    /**
     * JUnit constructor.

     * @param entityManager The EntityManager
     * @param xhbConfigPropRepository The XhbConfigPropRepository
     * @param environment The Environment
     */
    public SftpConfigHelper(EntityManager entityManager,
        XhbConfigPropRepository xhbConfigPropRepository, Environment environment,
        PddaMessageHelper pddaMessageHelper, XhbClobRepository clobRepository,
        XhbCourtRepository courtRepository) {
        super(entityManager, xhbConfigPropRepository, environment,
            pddaMessageHelper, clobRepository, courtRepository);
    }

    /**
     * JUnit constructor.

     * @param entityManager The EntityManager
     */
    public SftpConfigHelper(EntityManager entityManager) {
        super(entityManager);
        this.entityManager = entityManager;
    }


    /**
     * Get a new SSHClient.

     * @return The SSHClient
     */
    public SSHClient getNewSshClient() {
        SSHClient ssh = new SSHClient();
        ssh.addHostKeyVerifier(new PromiscuousVerifier());
        return ssh;
    }


    public SftpConfig getJschSession(SftpConfig sftpConfig) {
        // Create a session
        try {
            LOG.debug("Connection validated successfully");
            sftpConfig.setSession(getPddaSftpHelper().createSession(sftpConfig.getXhibitUsername(),
                sftpConfig.getXhibitPassword(), sftpConfig.getHost(), sftpConfig.getPort()));
            LOG.debug("A session has been established");
        } catch (Exception ex) {
            sftpConfig.setErrorMsg(SFTP_ERROR + ex.getMessage());
            LOG.error("Stacktrace2:: {}", ExceptionUtils.getStackTrace(ex));
        }

        return sftpConfig;
    }

}
