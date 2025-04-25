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

    private static final String CATH_CLIENT_ID =
        "spring.cloud.azure.active-directory.credential.client-id";
    private static final String CATH_CLIENT_SECRET =
        "spring.cloud.azure.active-directory.credential.client-secret";

    @Override
    protected String getClientId() {
        return env.getProperty(CATH_CLIENT_ID);
    }
    
    @Override
    protected String getClientSecret() {
        return env.getProperty(CATH_CLIENT_SECRET);
    }
}
