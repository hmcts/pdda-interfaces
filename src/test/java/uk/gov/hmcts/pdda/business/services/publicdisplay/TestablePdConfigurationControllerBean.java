package uk.gov.hmcts.pdda.business.services.publicdisplay;

import jakarta.persistence.EntityManager;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaydocument.XhbDisplayDocumentRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaylocation.XhbDisplayLocationRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaytype.XhbDisplayTypeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsetdd.XhbRotationSetDdRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsets.XhbRotationSetsRepository;
import uk.gov.hmcts.pdda.business.services.publicdisplay.database.query.VipDisplayCourtRoomQuery;
import uk.gov.hmcts.pdda.business.services.publicdisplay.database.query.VipDisplayDocumentQuery;
import uk.gov.hmcts.pdda.common.publicdisplay.jms.PublicDisplayNotifier;

/**
 * <p>
 * Title: TestablePdConfigurationControllerBean.
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
 * @author Scott Atwell
 */
@SuppressWarnings({"PMD"})
class TestablePdConfigurationControllerBean extends PdConfigurationControllerBean {

    private static final long serialVersionUID = 6593690695126813388L;
    private final EntityManager em;
    private final XhbRotationSetsRepository rotationSetsRepo;

    private final XhbDisplayDocumentRepository displayDocumentRepository;


    public TestablePdConfigurationControllerBean(EntityManager em,
        XhbCourtRepository xhbCourtRepository, XhbRotationSetsRepository rotationSetsRepo,
        XhbRotationSetDdRepository rotationSetDdRepo, XhbDisplayTypeRepository displayTypeRepo,
        XhbDisplayRepository displayRepo, XhbDisplayLocationRepository locationRepo,
        XhbCourtSiteRepository siteRepo, XhbCourtRoomRepository roomRepo,
        PublicDisplayNotifier notifier, VipDisplayDocumentQuery docQuery,
        VipDisplayCourtRoomQuery roomQuery,
        DisplayRotationSetDataHelper displayRotationSetDataHelper,
        XhbDisplayDocumentRepository displayDocumentRepository) {
        super(em, xhbCourtRepository, rotationSetsRepo, rotationSetDdRepo, displayTypeRepo,
            displayRepo, locationRepo, siteRepo, roomRepo, notifier, docQuery, roomQuery, displayRotationSetDataHelper);
        this.em = em;
        this.rotationSetsRepo = rotationSetsRepo;
        this.displayDocumentRepository = displayDocumentRepository;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected XhbRotationSetsRepository getXhbRotationSetsRepository() {
        return rotationSetsRepo;
    }

    @Override
    protected XhbDisplayDocumentRepository getXhbDisplayDocumentRepository() {
        return displayDocumentRepository;
    }

}