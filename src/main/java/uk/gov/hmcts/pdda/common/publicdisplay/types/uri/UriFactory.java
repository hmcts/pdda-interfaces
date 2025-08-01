package uk.gov.hmcts.pdda.common.publicdisplay.types.uri;

import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.exceptions.UnsupportedUriException;

/**
 * <p/>
 * Title: URIFactory.

 * <p/>
 * <p/>
 * Description:

 * <p/>
 * <p/>
 * Copyright: Copyright (c) 2003

 * <p/>
 * <p/>
 * Company: Electronic Data Systems

 * @author Neil Ellis
 * @version $Revision: 1.4 $
 */
public class UriFactory {

    protected UriFactory() {
        // Protected constructor
    }

    public static AbstractUri create(String uriString) {
        if (uriString.startsWith("pd://document")) {
            return new DisplayDocumentUri(uriString);
        } else if (uriString.startsWith("pd://display")) {
            return new DisplayUri(uriString);
        } else {
            throw new UnsupportedUriException(
                "The uri " + uriString + " is not a recognized URI type.");
        }
    }
}
