package uk.gov.hmcts.pdda.business.services.pdda.sftp;

import jakarta.persistence.EntityManager;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.services.pdda.PddaMessageHelper;


public class SftpHelperUtil extends SftpService {

    private static final Logger LOG = LoggerFactory.getLogger(SftpHelperUtil.class);

    private static final String TWO_PARAMS = "{}{}";

    private static final String USE_KEY_VAULT_PROPERTIES = "USE_KEY_VAULT_PROPERTIES";

    public SftpHelperUtil(EntityManager entityManager,
        XhbConfigPropRepository xhbConfigPropRepository, Environment environment,
        PddaMessageHelper pddaMessageHelper, XhbClobRepository clobRepository,
        XhbCourtRepository courtRepository) {
        super(entityManager, xhbConfigPropRepository, environment, pddaMessageHelper,
            clobRepository, courtRepository);
    }

    public SftpHelperUtil(EntityManager entityManager) {
        super(entityManager);
    }


    /**
     * Populate the SFTP configuration.
     * 
     * @return The SFTP configuration
     */
    protected SftpConfig populateSftpConfig(int sftpPort) {

        methodName = "populateSftpConfig()";
        LOG.debug(methodName, LOG_CALLED);

        SftpConfig sftpConfig = new SftpConfig();

        if (sftpPort > 0) { // Testing
            sftpConfig.setPort(sftpPort);
            sftpConfig = getTestSftpConfig(sftpConfig);
        } else {
            // Firstly, are we using Database or Key Vault to lookup credentials?
            // This will be a database lookup. If it is indeterminate, we will use the Database.
            sftpConfig.setUseKeyVault(checkWhetherToUseKeyVault());

            // Set the rest of the params
            sftpConfig = getSftpConfigHelper().getConfigParams(sftpConfig);
        }

        return sftpConfig;
    }


    /**
     * Get and set the data for testing.
     * 
     * @param sftpConfig The SFTP Config
     * @return Updated SFTP Config
     */
    private SftpConfig getTestSftpConfig(SftpConfig sftpConfig) {
        sftpConfig.setHost("localhost");
        sftpConfig.setCpUsername("cpUsername");
        sftpConfig.setCpPassword("cpPassword");
        sftpConfig.setCpRemoteFolder("/directory/");
        sftpConfig.setXhibitUsername("xhibitUsername");
        sftpConfig.setXhibitPassword("xhibitPassword");
        sftpConfig.setXhibitRemoteFolder("/directory/");
        sftpConfig.setActiveRemoteFolder("/directory/");

        return sftpConfig;
    }

    /**
     * Check whether to use Key Vault.
     * 
     * @return True if using Key Vault, false otherwise
     */
    private boolean checkWhetherToUseKeyVault() {

        methodName = "retrieveFromBaisCp()";
        LOG.debug(methodName, LOG_CALLED);

        String methodName = "checkWhetherToUseKeyVault()";
        LOG.debug(TWO_PARAMS, methodName, LOG_CALLED);

        // Fetch and validate the properties
        try {
            String useKeyVault = getMandatoryConfigValue(USE_KEY_VAULT_PROPERTIES);
            LOG.debug("Use key Vault?: {}", useKeyVault);
            return Boolean.parseBoolean(useKeyVault);
        } catch (InvalidConfigException ex) {
            LOG.error("Error fetching properties: {}", ExceptionUtils.getStackTrace(ex));
            return false;
        }
    }
}
