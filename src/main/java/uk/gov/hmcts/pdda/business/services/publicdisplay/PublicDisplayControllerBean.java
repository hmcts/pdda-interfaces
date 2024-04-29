package uk.gov.hmcts.pdda.business.services.publicdisplay;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsetdd.XhbRotationSetDdRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsets.XhbRotationSetsRepository;
import uk.gov.hmcts.pdda.business.services.publicdisplay.database.query.VipDisplayCourtRoomQuery;
import uk.gov.hmcts.pdda.business.services.publicdisplay.database.query.VipDisplayDocumentQuery;
import uk.gov.hmcts.pdda.common.publicdisplay.jms.PublicDisplayNotifier;

import java.util.Date;

@SuppressWarnings("PMD.ExcessiveParameterList")
public class PublicDisplayControllerBean extends AbstractPdConfigDisplaysControllerBean {

    private static final Logger LOG = LoggerFactory.getLogger(PdConfigurationControllerBean.class);

    protected PublicDisplayControllerBean() {
        super();
    }

    protected PublicDisplayControllerBean(EntityManager entityManager) {
        super(entityManager);
    }

    protected PublicDisplayControllerBean(EntityManager entityManager, XhbClobRepository xhbClobRepository,
        XhbCourtRepository xhbCourtRepository, XhbConfigPropRepository xhbConfigPropRepository,
        XhbCppFormattingRepository xhbCppFormattingRepository, XhbRotationSetsRepository xhbRotationSetsRepository,
        XhbRotationSetDdRepository xhbRotationSetDdRepository, XhbDisplayRepository xhbDisplayRepository,
        PublicDisplayNotifier publicDisplayNotifier, VipDisplayDocumentQuery vipDisplayDocumentQuery,
        VipDisplayCourtRoomQuery vipDisplayCourtRoomQuery) {
        super(entityManager, xhbClobRepository, xhbCourtRepository, xhbConfigPropRepository, xhbCppFormattingRepository,
            xhbRotationSetsRepository, xhbRotationSetDdRepository, xhbDisplayRepository, publicDisplayNotifier,
            vipDisplayDocumentQuery, vipDisplayCourtRoomQuery);
    }

    /**
     * Check the Public display activation status for this particular scheduled hearing.
     * 
     * @param schedHearingId Integer
     * @return boolean true if scheduled hearing is active
     */
    public boolean isPublicDisplayActive(final Integer schedHearingId) {
        LOG.debug("isPublicDisplayActive({})", schedHearingId);
        return PublicDisplayActivationHelper.isPublicDisplayActive(schedHearingId, getEntityManager());
    }

    /**
     * Sets the public display for this scheduling hearing to deActivate.
     * 
     * @param schedHearingId Integer
     */
    public void deActivatePublicDisplay(final Integer schedHearingId, final Date deactivationDate) {
        LOG.debug("deActivatePublicDisplay({},{})", schedHearingId, deactivationDate);
        PublicDisplayActivationHelper.activatePublicDisplay(getPublicDisplayNotifier(), schedHearingId,
            deactivationDate, false, getEntityManager());
    }

    /**
     * Sets the public display for this scheduling hearing to Activate.
     * 
     * @param schedHearingId Integer
     */
    public void activatePublicDisplay(final Integer schedHearingId, final Date activationDate) {
        LOG.debug("activatePublicDisplay({},{})", schedHearingId, activationDate);
        PublicDisplayActivationHelper.activatePublicDisplay(getPublicDisplayNotifier(), schedHearingId, activationDate,
            true, getEntityManager());
    }
}
