package uk.gov.hmcts.pdda.web.publicdisplay.imaging.exceptions;

import uk.gov.hmcts.pdda.common.publicdisplay.exceptions.Fatal;
import uk.gov.hmcts.pdda.common.publicdisplay.exceptions.PublicDisplayRuntimeException;

import java.net.URL;

/**
 * <p/>
 * Title: ImageLoadingException.

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
 * @version $Revision: 1.3 $
 */
public class ImageLoadingException extends PublicDisplayRuntimeException implements Fatal {

    private static final long serialVersionUID = 1L;

    public ImageLoadingException(URL url) {
        super("Could not load the image at the URL '" + url + "'.");
    }
}
