package uk.gov.hmcts.pdda.business.services.pdda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListDao;

/**
 * <p>
 * Title: CathHelper.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2024
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Luke Gittins
 * @version 1.0
 */
public class CathHelper {

    private static final Logger LOG = LoggerFactory.getLogger(CathHelper.class);
    private static final String ENTERED = " : entered";
    
    public void send(XhbCourtelListDao xhbCourtelListDao) {
        LOG.debug("send()" + ENTERED);
        // TODO PDDA-364
    }
}
