
package uk.gov.hmcts.pdda.business.services.publicdisplay.data.impl;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query.PublicDisplayQuery;
import uk.gov.hmcts.pdda.business.services.publicdisplay.exceptions.CourtRoomNotFoundException;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayDocumentUri;

import java.util.Optional;

/**
 * <p/>
 * Title: SingleCourtRoomDataSource.
 * </p>
 * <p/>
 * <p/>
 * Description:
 * </p>
 * <p/>
 * <p/>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p/>
 * <p/>
 * Company: Electronic Data Systems
 * </p>
 * 
 * @author Neil Ellis
 * @version $Revision: 1.8 $
 */
public class SingleCourtRoomDataSource extends GenericPublicDisplayDataSource {
    private static final Logger LOG = LoggerFactory.getLogger(SingleCourtRoomDataSource.class);

    private XhbCourtSiteRepository xhbCourtSiteRepository;
    private XhbCourtRoomRepository xhbCourtRoomRepository;

    public SingleCourtRoomDataSource(DisplayDocumentUri uri, PublicDisplayQuery query) {
        super(uri, query);
    }

    // Junit constructor
    public SingleCourtRoomDataSource(DisplayDocumentUri uri, PublicDisplayQuery query,
        XhbCourtRepository xhbCourtRepository,
        XhbCourtSiteRepository xhbCourtSiteRepository,
        XhbCourtRoomRepository xhbCourtRoomRepository) {
        super(uri, query, xhbCourtRepository);
        this.xhbCourtSiteRepository = xhbCourtSiteRepository;
        this.xhbCourtRoomRepository = xhbCourtRoomRepository;
    }

    /**
     * If there is any data for the query place it into the data object otherwise leave it empty.
     * 
     * @pre getUri() != null
     * @pre getData() != null
     */
    @Override
    public void retrieve(final EntityManager entityManager) {
        LOG.debug("retrieve({})", entityManager);
        super.retrieve(entityManager);
        setCourtRoomNumber(getUri().getCourtRoomIds()[0], entityManager);
    }

    /**
     * Sets the court name in the data object for the court that we're retrieving data for.
     * 
     * @param courtRoomId int
     */
    private void setCourtRoomNumber(final int courtRoomId, final EntityManager entityManager) {
        LOG.debug("setCourtRoomNumber({},{})", courtRoomId, entityManager);
        Optional<XhbCourtRoomDao> courtRoom =
            getXhbCourtRoomRepository(entityManager).findById(courtRoomId);
        if (!courtRoom.isPresent()) {
            throw new CourtRoomNotFoundException(courtRoomId);
        }
        Optional<XhbCourtSiteDao> courtSite = getCourtSite(courtRoom, entityManager);
        if (!courtSite.isPresent()) {
            LOG.error("CourtroomId {} has invalid courtSiteId {}", courtRoomId,
                courtRoom.get().getCourtSiteId());
            throw new CourtRoomNotFoundException(courtRoomId);
        }

        getData().setCourtSiteShortName(courtSite.get().getShortName());
        getData().setCourtRoomName(courtRoom.get().getCourtRoomName());
    }

    private Optional<XhbCourtSiteDao> getCourtSite(Optional<XhbCourtRoomDao> courtRoom,
        final EntityManager entityManager) {
        LOG.debug("getCourtSite({},{})", courtRoom, entityManager);
        Optional<XhbCourtSiteDao> courtSite = Optional.empty();
        if (courtRoom.isPresent()) {
            if (courtRoom.get().getXhbCourtSite() != null) {
                courtSite = Optional.of(courtRoom.get().getXhbCourtSite());
            } else {
                courtSite = getXhbCourtSiteRepository(entityManager)
                    .findById(courtRoom.get().getCourtSiteId());
            }
        }
        return courtSite;
    }

    private XhbCourtSiteRepository getXhbCourtSiteRepository(EntityManager entityManager) {
        if (xhbCourtSiteRepository == null) {
            return new XhbCourtSiteRepository(entityManager);
        }
        return xhbCourtSiteRepository;
    }

    private XhbCourtRoomRepository getXhbCourtRoomRepository(EntityManager entityManager) {
        if (xhbCourtRoomRepository == null) {
            return new XhbCourtRoomRepository(entityManager);
        }
        return xhbCourtRoomRepository;
    }
}
