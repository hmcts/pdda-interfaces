package uk.gov.hmcts.pdda.business.services.publicdisplay;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsetdd.XhbRotationSetDdDao;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsetdd.XhbRotationSetDdRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsets.XhbRotationSetsDao;
import uk.gov.hmcts.pdda.business.entities.xhbrotationsets.XhbRotationSetsRepository;
import uk.gov.hmcts.pdda.business.services.publicdisplay.exceptions.PublicDisplayCheckedException;
import uk.gov.hmcts.pdda.business.services.publicdisplay.exceptions.RotationSetNotFoundCheckedException;
import uk.gov.hmcts.pdda.common.publicdisplay.events.ConfigurationChangeEvent;
import uk.gov.hmcts.pdda.common.publicdisplay.jms.PublicDisplayNotifier;
import uk.gov.hmcts.pdda.common.publicdisplay.types.configuration.CourtConfigurationChange;
import uk.gov.hmcts.pdda.common.publicdisplay.types.configuration.CourtRotationSetConfigurationChange;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.RotationSetComplexValue;
import uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay.RotationSetDdComplexValue;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * Title: Rotation Set Maintain Helper.
 * </p>
 * <p>
 * Description: Helper methods for creating and editting a rotation set.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: EDS
 * </p>
 *
 * @author unascribed
 * @version $Id: RotationSetMaintainHelper.java,v 1.8 2006/04/12 13:18:29 bzjrnl Exp $
 */

public class RotationSetMaintainHelper {
    private static final Logger LOG = LoggerFactory.getLogger(RotationSetMaintainHelper.class);

    protected RotationSetMaintainHelper() {
        // Protected constructor
    }

    /**
     * Creates a new rotation set with associated documents.
     *
     * @param newRotationSet This object must contain a RotationSetBasic value with court Id populated
     *        and a list of RotationSetDDComplex values with RotationSetDDBasicValues and valid
     *        DisplayDocumentBasicValues
     */
    public static void createRotationSets(RotationSetComplexValue newRotationSet, final EntityManager entityManager) {
        createRotationSets(newRotationSet, new XhbCourtRepository(entityManager),
            new XhbRotationSetsRepository(entityManager), new XhbRotationSetDdRepository(entityManager));

    }

    public static void createRotationSets(RotationSetComplexValue newRotationSet,
        final XhbCourtRepository xhbCourtRepository, final XhbRotationSetsRepository xhbRotationSetsRepository,
        final XhbRotationSetDdRepository xhbRotationSetDdRepository) {
        LOG.debug("createRotationSets({},{},{},{})", newRotationSet, xhbCourtRepository, xhbRotationSetsRepository,
            xhbRotationSetDdRepository);
        // Get the court id and lookup the court local reference
        final Integer courtId = newRotationSet.getCourtId();

        Optional<XhbCourtDao> courtLocal = xhbCourtRepository.findById(courtId);
        if (!courtLocal.isPresent()) {
            LOG.debug("Court is Not Present {}", courtLocal);
            throw new uk.gov.hmcts.framework.business.exceptions.CourtNotFoundException(courtId);
        }

        // Create the rotation set local reference
        Optional<XhbRotationSetsDao> rotationSetLocal =
            xhbRotationSetsRepository.update(newRotationSet.getRotationSetsDao());
        if (rotationSetLocal.isPresent()) {
            addRotationSetDds(newRotationSet, xhbRotationSetDdRepository);
        }
    }

    /**
     * Updates the rotation set with display documents that have been selected.
     * 
     * <p>Note: sends a RotationSet changed JMS configuration message
     *
     * @param rotationSet The rotation set being updated and an array of display documents with ordering
     *        and delay information
     * @throws PublicDisplayCheckedException Thrown if rotation set does not exist in the DB
     */
    public static void setDisplayDocumentsForRotationSet(RotationSetComplexValue rotationSet,
        PublicDisplayNotifier notifier, final EntityManager entityManager) {
        LOG.debug("setDisplayDocumentsForRotationSet({},{},{})", rotationSet, notifier, entityManager);
        setDisplayDocumentsForRotationSet(rotationSet, notifier, new XhbRotationSetsRepository(entityManager),
            new XhbRotationSetDdRepository(entityManager));
    }

    public static void setDisplayDocumentsForRotationSet(RotationSetComplexValue rotationSet,
        PublicDisplayNotifier notifier, final XhbRotationSetsRepository xhbRotationSetsRepository,
        final XhbRotationSetDdRepository xhbRotationSetDdRepository) {
        LOG.debug("setDisplayDocumentsForRotationSet({},{},{},{})", rotationSet, notifier, xhbRotationSetsRepository,
            xhbRotationSetDdRepository);

        final Integer rotationSetId = rotationSet.getRotationSetId();
        Optional<XhbRotationSetsDao> rotationSetLocal = xhbRotationSetsRepository.findById(Long.valueOf(rotationSetId));
        if (!rotationSetLocal.isPresent()) {
            throw new RotationSetNotFoundCheckedException(rotationSetId);
        }

        // Check that we are not editing a system RS.
        if ("Y".equals(rotationSetLocal.get().getDefaultYn())) {
            throw new PublicDisplayCheckedException("pubdisp.rotationset.editsystem",
                new String[] {rotationSetLocal.get().getDescription()}, "Can not edit a system rotation set");
        }

        // A flag to identify if any changes have been made that require
        // a JMS message to be sent
        boolean hasNotifyChanges = rotationSetLocal.get().setData(rotationSet.getRotationSetsDao());

        // Updates or deletes rotation set dds
        boolean rotationSetDDsUpdated =
            updateOrDeleteRotationSetDds(rotationSetLocal.get(), rotationSet, xhbRotationSetDdRepository);

        // Add new rotation set dds
        boolean rotationSetDDsAdded = addRotationSetDds(rotationSet, xhbRotationSetDdRepository);

        hasNotifyChanges = hasNotifyChanges || rotationSetDDsAdded || rotationSetDDsUpdated;
        // If a change has occurred that should require re-rendering
        // then send a message.
        if (hasNotifyChanges) {
            LOG.debug("Sending Notification");
            sendNotification(rotationSet, notifier);
        }
    }

    /**
     * This deletes a rotation set only if it is not a system rotation set and is not assigned to any
     * displays.
     *
     * @param rotationSet RotationSetComplexValue
     * @throws PublicDisplayCheckedException Exception
     */
    public static void deleteRotationSet(RotationSetComplexValue rotationSet, final EntityManager entityManager) {
        LOG.debug("deleteRotationSet({},{})", rotationSet, entityManager);
        deleteRotationSet(rotationSet, new XhbRotationSetsRepository(entityManager),
            new XhbRotationSetDdRepository(entityManager), new XhbDisplayRepository(entityManager));
    }

    public static void deleteRotationSet(RotationSetComplexValue rotationSet, final XhbRotationSetsRepository xrsRepo,
        XhbRotationSetDdRepository xrsddRepo, final XhbDisplayRepository xhbDisplayRepository) {
        LOG.debug("deleteRotationSet({},{},{})", rotationSet, xrsRepo, xrsddRepo);
        // Check optimistic locking.
        // Get the rotation set from the DB
        Optional<XhbRotationSetsDao> rotationSetLocal = xrsRepo.findById(Long.valueOf(rotationSet.getRotationSetId()));
        if (!rotationSetLocal.isPresent()) {
            // if the object could not be found, then it is already deleted.
            // Even though this is an unexpected condition, it results in
            // what
            // we wanted so no need to report an error.
            return;
        }

        // Check that we are not deleting a system RS.
        if ("Y".equals(rotationSetLocal.get().getDefaultYn())) {
            throw new PublicDisplayCheckedException("pubdisp.rotationset.deletesystem",
                new String[] {rotationSetLocal.get().getDescription()}, "Can not delete a system rotation set");
        }

        // Check the rotation set is not assigned to any displays.
        List<XhbDisplayDao> xhbDisplays =
            xhbDisplayRepository.findByRotationSetId(rotationSetLocal.get().getRotationSetId());
        if (!xhbDisplays.isEmpty()) {
            throw new PublicDisplayCheckedException("pubdisp.rotationset.assigned",
                new String[] {rotationSetLocal.get().getDescription()}, "Can not delete a that is currently in use");
        }

        // Delete the rotation set and the rotation set dd's
        RotationSetDdComplexValue[] rsDdComplex = rotationSet.getRotationSetDdComplexValues();
        for (RotationSetDdComplexValue rsDd : rsDdComplex) {
            xrsddRepo.delete(Optional.of(rsDd.getRotationSetDdDao()));
        }

        xrsRepo.delete(Optional.of(rotationSet.getRotationSetsDao()));
    }

    /**
     * Adds new rotation set dds to the rotation sets.
     *
     * <p>src/main/java/uk.gov.hmcts.pdda/business/services/publicdisplay/RotationSetMaintainHelper.java
     * 
     * @param rotationSet Rotation set DDs to be added
     * @return boolean
     */
    private static boolean addRotationSetDds(RotationSetComplexValue rotationSet,
        final XhbRotationSetDdRepository repo) {
        LOG.debug("addRotationSetDds({},{})", rotationSet, repo);
        boolean hasAdded = false;

        RotationSetDdComplexValue[] rotationSetDdComplexValues = rotationSet.getRotationSetDdComplexValues();
        for (RotationSetDdComplexValue rotationSetDdComplexValue : rotationSetDdComplexValues) {
            XhbRotationSetDdDao rotationSetDdBasicValue = rotationSetDdComplexValue.getRotationSetDdDao();

            if (rotationSetDdBasicValue.getPrimaryKey() != null) {
                continue;
            }
            LOG.debug("Saving BasicValue {}", rotationSetDdBasicValue);
            // Create the record
            repo.save(rotationSetDdBasicValue);

            if (!hasAdded) {
                hasAdded = true;
            }
        }

        return hasAdded;
    }

    private static boolean updateOrDeleteRotationSetDds(XhbRotationSetsDao rotationSetLocal,
        RotationSetComplexValue rotationSet, final XhbRotationSetDdRepository repo) {
        boolean hasUpdatedOrDeleted = false;

        // get the local references of existing rotation set DD's
        List<XhbRotationSetDdDao> tmp = rotationSetLocal.getXhbRotationSetDds();
        XhbRotationSetDdDao[] rotationSetDdLocals = tmp.toArray(new XhbRotationSetDdDao[0]);

        // Now iterate through the locals.
        // If for each local a corresponding value basic value can be found
        // in the data passed it, attempt to update it,
        // otherwise it has been removed, so remove it from the DB.
        for (XhbRotationSetDdDao rotationSetDdLocal : rotationSetDdLocals) {
            Integer rotationSetDdId = rotationSetDdLocal.getRotationSetDdId();
            if (rotationSet.hasRotationSetDd(rotationSetDdId)) {
                XhbRotationSetDdDao rotationSetDd = rotationSet.getRotationSetDd(rotationSetDdId);
                LOG.debug("Rotation Set DD for id " + rotationSetDd.getPrimaryKey() + " found.");
                LOG.debug("Page order = " + rotationSetDd.getOrdering() + ", delay = " + rotationSetDd.getPageDelay());
                repo.update(rotationSetDd);
                hasUpdatedOrDeleted = true;
            } else {
                // Delete record
                repo.delete(Optional.of(rotationSetDdLocal));
                hasUpdatedOrDeleted = true;
            }
        }
        return hasUpdatedOrDeleted;
    }

    /**
     * Sends a JMS notification.
     *
     * @param rotationSet The rotation set that has been changed
     */
    private static void sendNotification(RotationSetComplexValue rotationSet, PublicDisplayNotifier notifier) {
        LOG.debug("sendNotification({},{})", rotationSet, notifier);
        CourtConfigurationChange ccc =
            new CourtRotationSetConfigurationChange(rotationSet.getRotationSetsDao().getCourtId().intValue(),
                rotationSet.getRotationSetsDao().getPrimaryKey().intValue());
        ConfigurationChangeEvent ccEvent = new ConfigurationChangeEvent(ccc);
        notifier.sendMessage(ccEvent);
    }

}
