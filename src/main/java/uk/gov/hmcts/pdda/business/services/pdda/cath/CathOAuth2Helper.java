package uk.gov.hmcts.pdda.business.services.pdda.cath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.pdda.business.services.pdda.OAuth2Helper;

/**
 * <p>
 * Title: CathOAuth2Helper.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Mark Harris
 * @version 1.0
 */
@Component
public class CathOAuth2Helper extends OAuth2Helper {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(CathOAuth2Helper.class);
    
    private static final String CATH_AZURE_TOKEN_URL = "cath.azure.oauth2.token-url";
    private static final String CATH_AZURE_CLIENT_ID =
        "cath.azure.active-directory.credential.client-id";
    private static final String CATH_AZURE_CLIENT_SECRET =
        "cath.azure.active-directory.credential.client-secret";

    @Override
    protected String getTokenUrl() {
        String tokenUrl = env.getProperty(CATH_AZURE_TOKEN_URL);
        if (tokenUrl == null) {
            LOG.error("Token URL property '{}' not found in environment.", CATH_AZURE_TOKEN_URL);
            return "";
        }
        return env.getProperty(CATH_AZURE_TOKEN_URL);
    }

    @Override
    protected String getClientId() {
        String clientId = env.getProperty(CATH_AZURE_CLIENT_ID);
        if (clientId == null) {
            LOG.error("Client ID property '{}' not found in environment.", CATH_AZURE_CLIENT_ID);
            return "";
        }
        return env.getProperty(CATH_AZURE_CLIENT_ID);
    }

    @Override
    protected String getClientSecret() {
        String clientSecret = env.getProperty(CATH_AZURE_CLIENT_SECRET);
        if (clientSecret == null) {
            LOG.error("Client Secret property '{}' not found in environment.", CATH_AZURE_CLIENT_SECRET);
            return "";
        }
        return env.getProperty(CATH_AZURE_CLIENT_SECRET);
    }

}
