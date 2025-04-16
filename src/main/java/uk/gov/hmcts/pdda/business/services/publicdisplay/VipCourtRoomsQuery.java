package uk.gov.hmcts.pdda.business.services.publicdisplay;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.AbstractControllerBean;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title: Query to return all court rooms linked to the VIP Screen.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author unascribed
 * @version $Id: VipCourtRoomsQuery.java,v 1.4 2006/06/05 12:29:45 bzjrnl Exp $
 */
@SuppressWarnings("PMD.NullAssignment")
public class VipCourtRoomsQuery extends AbstractControllerBean {


    private static final Logger LOG = LoggerFactory.getLogger(VipCourtRoomsQuery.class);
    private XhbCourtRoomRepository xhbCourtRoomRepository;
    private boolean multiSite;

    public VipCourtRoomsQuery(EntityManager entityManager, boolean multiSite) {
        super(entityManager);
        this.multiSite = multiSite;
    }

    public VipCourtRoomsQuery(EntityManager entityManager, boolean multiSite,
        XhbCourtRoomRepository xhbCourtRoomRepository) {
        this(entityManager, multiSite);
        this.xhbCourtRoomRepository = xhbCourtRoomRepository;
    }

    @Override
    protected void clearRepositories() {
        super.clearRepositories();
        xhbCourtRoomRepository = null;
    }

    /**
     * Returns an array of CourtListValue.
     * 
     * @param courtId room ids for which the data is required
     * 
     * @return Summary by name data for the specified court rooms
     */
    public XhbCourtRoomDao[] getData(Integer courtId) {
        LOG.debug("getData({})", courtId);
        List<XhbCourtRoomDao> results = new ArrayList<>();
        List<XhbCourtRoomDao> crDaos;
        if (multiSite) {
            crDaos = getXhbCourtRoomRepository().findVipMultiSite(courtId);
        } else {
            crDaos = getXhbCourtRoomRepository().findVipMNoSite(courtId);
        }
        if (!crDaos.isEmpty()) {
            for (XhbCourtRoomDao crDao : crDaos) {
                results.add(crDao);
            }
        }
        return results.toArray(new XhbCourtRoomDao[0]);
    }

    private XhbCourtRoomRepository getXhbCourtRoomRepository() {
        if (xhbCourtRoomRepository == null || !isEntityManagerActive()) {
            xhbCourtRoomRepository = new XhbCourtRoomRepository(getEntityManager());
        }
        return xhbCourtRoomRepository;
    }
}
