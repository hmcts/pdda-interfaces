package uk.gov.hmcts.pdda.business.services.pdda.data;

import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;

import java.util.Optional;

/**
 * <p>
 * Title: FinderHelper.
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
@SuppressWarnings("PMD.LawOfDemeter")
public class FinderHelper extends CreationHelper {

    public FinderHelper() {
        super();
    }

    // JUnit constructor
    public FinderHelper(RepositoryHelper repositoryHelper) {
        super(repositoryHelper);
    }

    public Optional<XhbCourtSiteDao> findCourtSite(final Integer courtId,
        final String courtSiteName) {
        return getRepositoryHelper().getXhbCourtSiteRepository().findByCourtSiteName(courtId,
            courtSiteName);
    }

    public Optional<XhbCourtRoomDao> findCourtRoom(final Integer courtId,
        final Integer crestCourtRoomNo) {
        return getRepositoryHelper().getXhbCourtRoomRepository().findByCourtRoomNo(courtId,
            crestCourtRoomNo);
    }
}
