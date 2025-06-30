package uk.gov.hmcts.pdda.common.publicdisplay.util;

import org.slf4j.Logger;


/**
 * <p>
 * Title: Debuggable.
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * 
 * <p>
 * Company: Electronic Data Systems
 * </p>
 * 
 * @author Neil Ellis
 * @version $Revision: 1.2 $
 */
@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface Debuggable {
    void debug(Logger logger);
}
