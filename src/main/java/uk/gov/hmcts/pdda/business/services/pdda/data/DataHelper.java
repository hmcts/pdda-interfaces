package uk.gov.hmcts.pdda.business.services.pdda.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;

import java.util.Optional;

/**
 * <p>
 * Title: DataHelper.
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
 * @author HarrisM
 * @version 1.0
 */
public class DataHelper extends FinderHelper {

    private static final Logger LOG = LoggerFactory.getLogger(DataHelper.class);

    public Optional<XhbCourtSiteDao> validateCourtSite(final Integer courtId,
        final String courtSiteName, final String courtSiteCode) {
        LOG.debug("validateCourtSite({})", courtSiteName);
        Optional<XhbCourtSiteDao> result = findCourtSite(courtId, courtSiteName);
        if (result.isEmpty()) {
            result = createCourtSite(courtId, courtSiteName, courtSiteCode);
        }
        return result;
    }
}
