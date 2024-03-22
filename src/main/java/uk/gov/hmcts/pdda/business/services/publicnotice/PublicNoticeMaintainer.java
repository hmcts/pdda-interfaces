package uk.gov.hmcts.pdda.business.services.publicnotice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.PddaEntityHelper;
import uk.gov.hmcts.pdda.business.entities.xhbconfiguredpublicnotice.XhbConfiguredPublicNoticeDao;
import uk.gov.hmcts.pdda.business.vos.services.publicnotice.DefinitivePublicNoticeStatusValue;
import uk.gov.hmcts.pdda.business.vos.services.publicnotice.DisplayablePublicNoticeValue;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * Title: PublicNoticeMaintainer.
 * </p>
 * <p>
 * Description: A helper class to manipulate DisplayablePublicNoticeValue's so that they can be
 * written into the database with necessary optimistic lock checking.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * <p>
 * Company: Electronic Data Systems
 * </p>
 * 
 * @author Rakesh Lakhani
 * @version $Id: PublicNoticeMaintainer.java,v 1.1 2005/02/15 14:58:36 sz0t7n Exp $
 */
public final class PublicNoticeMaintainer {

    private static final Logger LOG = LoggerFactory.getLogger(PublicNoticeMaintainer.class);
    private static final String PN_ACTIVE = "1";
    private static final String PN_INACTIVE = "0";
    private static final String INVALID_PN_FOR_COURTROOM = "publicnotice.invalid_pn_for_courtroom";

    /**
     * Empty private constructor so that class can not be instantiated.
     */
    private PublicNoticeMaintainer() {
        // private constructor
    }

    public static void updateIsActive(DisplayablePublicNoticeValue value) {
        LOG.debug("updateIsActive({})", value);
        Optional<XhbConfiguredPublicNoticeDao> basicValue = PddaEntityHelper.xcpnFindByPrimaryKey(value.getId());
        if (basicValue.isPresent()) {
            XhbConfiguredPublicNoticeDao updatedBasicValue = updateIsActive(value, basicValue.get());
            PddaEntityHelper.cpnUpdate(updatedBasicValue);
        }
    }

    public static XhbConfiguredPublicNoticeDao updateIsActive(DisplayablePublicNoticeValue value,
        XhbConfiguredPublicNoticeDao basicValue) {
        basicValue.setIsActive(value.isActive() ? PN_ACTIVE : PN_INACTIVE);

        // Set the version for optimistic lock checking.
        basicValue.setVersion(value.getVersion());
        return basicValue;
    }

    public static void updateActiveStatus(Integer courtRoomId, DefinitivePublicNoticeStatusValue value,
        boolean checkOptimisticLock) {
        LOG.debug("updateActiveStatus({},{},{})", courtRoomId, value, checkOptimisticLock);
        // DisplayablePublicNoticeValue value)
        List<XhbConfiguredPublicNoticeDao> basicValues =
            PddaEntityHelper.xcpnFindByDefinitivePnCourtRoomValue(courtRoomId, value.getDefinitivePublicNoticeId());
        XhbConfiguredPublicNoticeDao updatedBasicValue =
            updateActiveStatus(courtRoomId, value, checkOptimisticLock, basicValues);
        PddaEntityHelper.cpnUpdate(updatedBasicValue);
    }

    public static XhbConfiguredPublicNoticeDao updateActiveStatus(Integer courtRoomId,
        DefinitivePublicNoticeStatusValue value, boolean checkOptimisticLock,
        List<XhbConfiguredPublicNoticeDao> basicValues) {
        if (basicValues.isEmpty()) {
            throw new PublicNoticeException(INVALID_PN_FOR_COURTROOM, "Cannot find configuredPN with XhbCourtRoomId "
                + courtRoomId + " & definitivePNId" + value.getDefinitivePublicNoticeId());
        }
        XhbConfiguredPublicNoticeDao basicValue = basicValues.get(0);
        basicValue.setIsActive(value.isActive() ? PN_ACTIVE : PN_INACTIVE);

        if (checkOptimisticLock) {
            // Set the version for optimistic lock checking.
            basicValue.setVersion(value.getVersion());
        }
        return basicValue;
    }

}
