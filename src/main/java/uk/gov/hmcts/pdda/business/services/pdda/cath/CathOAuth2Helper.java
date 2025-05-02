package uk.gov.hmcts.pdda.business.services.pdda.cath;

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
public class CathOAuth2Helper extends OAuth2Helper {

    private static final String CATH_AZURE_TOKEN_URL = "cath.azure.oauth2.token-url";
    private static final String CATH_AZURE_CLIENT_ID =
        "cath.azure.active-directory.credential.client-id";
    private static final String CATH_AZURE_CLIENT_SECRET =
        "cath.azure.active-directory.credential.client-secret";

    @Override
    protected String getTokenUrl() {
        return env.getProperty(CATH_AZURE_TOKEN_URL);
    }

    @Override
    protected String getClientId() {
        return env.getProperty(CATH_AZURE_CLIENT_ID);
    }

    @Override
    protected String getClientSecret() {
        return env.getProperty(CATH_AZURE_CLIENT_SECRET);
    }

}
