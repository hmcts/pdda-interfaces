package uk.gov.hmcts.pdda.common.publicdisplay.util;

import org.slf4j.Logger;


/**

 * Title: Debuggable.

 * Description:

 * Copyright: Copyright (c) 2003

 * Company: Electronic Data Systems

 * @author Neil Ellis
 * @version $Revision: 1.2 $
 */
@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface Debuggable {
    void debug(Logger logger);
}
