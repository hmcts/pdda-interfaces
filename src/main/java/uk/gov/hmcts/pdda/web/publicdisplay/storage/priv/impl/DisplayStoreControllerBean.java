package uk.gov.hmcts.pdda.web.publicdisplay.storage.priv.impl;

import jakarta.ejb.ApplicationException;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.pdda.business.AbstractControllerBean;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaystore.XhbDisplayStoreDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaystore.XhbDisplayStoreRepository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Stateless
@Service
@Transactional
@LocalBean
@ApplicationException(rollback = true)
@SuppressWarnings("PMD.NullAssignment")
public class DisplayStoreControllerBean extends AbstractControllerBean implements Serializable {

    private static final long serialVersionUID = -1482124759093214736L;

    private static final Logger LOG = LoggerFactory.getLogger(DisplayStoreControllerBean.class);

    private static final String ENTERED = " : entered";
    
    private static final String METHOD_END = ") - ";

    private transient XhbDisplayStoreRepository xhbDisplayStoreRepository;

    public DisplayStoreControllerBean(EntityManager entityManager) {
        super(entityManager);
    }

    public DisplayStoreControllerBean() {
        super();
    }

    // JUnit tests constructor
    public DisplayStoreControllerBean(EntityManager entityManager,
        XhbDisplayStoreRepository repository) {
        super(entityManager);
        this.xhbDisplayStoreRepository = repository;
    }


    @Override
    protected void clearRepositories() {
        super.clearRepositories();
        xhbDisplayStoreRepository = null;
    }

    /**
     * Indicates if a record exists in the database with the retrieval code supplied.
     * 
     * @param retrievalCode Retrieval Code
     * @return true if exists, else false
     */
    public boolean doesEntryExist(final String retrievalCode) {
        String methodName = "doesEntryExist(" + retrievalCode + METHOD_END;
        LOG.debug(methodName + ENTERED);

        boolean exists = false;
        Optional<XhbDisplayStoreDao> xds =
            getXhbDisplayStoreRepository().findByRetrievalCodeSafe(retrievalCode);
        if (xds.isPresent()) {
            exists = true;
        }
        return exists;
    }

    /**
     * Deletes a record using the retrieval code supplied.
     * 
     * @param retrievalCode Retrieval Code
     */
    public void deleteEntry(final String retrievalCode) {
        String methodName = "deleteEntry(" + retrievalCode + METHOD_END;
        LOG.debug(methodName + ENTERED);

        Optional<XhbDisplayStoreDao> xds =
            getXhbDisplayStoreRepository().findByRetrievalCodeSafe(retrievalCode);
        if (xds.isPresent()) {
            getXhbDisplayStoreRepository().delete(xds);
        }
    }

    /**
     * Retrieves the last modified value as milliseconds.
     * 
     * @param retrievalCode Retrieval Code of the value to retrieve
     * @return last modified in milliseconds
     */
    public long getLastModified(final String retrievalCode) {
        String methodName = "getLastModified(" + retrievalCode + METHOD_END;
        LOG.debug(methodName + ENTERED);

        long lastModified;
        Optional<XhbDisplayStoreDao> xds =
            getXhbDisplayStoreRepository().findByRetrievalCodeSafe(retrievalCode);
        if (xds.isPresent()) {
            lastModified = convertLocalDateTimeToDate(xds.get().getLastUpdateDate()).getTime();
        } else {
            lastModified = System.currentTimeMillis();
        }
        // Use integer division to force a rounding down.
        return lastModified / 1000 * 1000;
    }

    /**
     * Retrieve the contents of the XhbDisplayStore record.
     * 
     * @param retrievalCode Retrieval Code to retrieve the record
     * @return The record contents
     */
    public String readFromDatabase(final String retrievalCode) {
        String methodName = "readFromDatabase(" + retrievalCode + METHOD_END;
        LOG.debug(methodName + ENTERED);

        String contents = null;
        Optional<XhbDisplayStoreDao> xds =
            getXhbDisplayStoreRepository().findByRetrievalCodeSafe(retrievalCode);
        if (xds.isPresent()) {
            contents = xds.get().getContent();
        }
        return contents;
    }

    /**
     * If the XhbDisplayStore record exists, update the contents with the value supplied. If no
     * record exists with the Retrieval Code supplied, create it.
     * 
     * @param retrievalCode Retrieval Code to search for
     * @param contents Contents to set
     */
    public void writeToDatabaseOrig(final String retrievalCode, final String contents) {
        String methodName = "writeToDatabase(" + retrievalCode + "," + contents + METHOD_END;
        LOG.debug(methodName + ENTERED);

        Optional<XhbDisplayStoreDao> xds =
            getXhbDisplayStoreRepository().findByRetrievalCodeSafe(retrievalCode); // uses main EM
        if (xds.isPresent()) {
            XhbDisplayStoreDao entity = xds.get(); // managed and detached entity
            entity.setLastUpdateDate(LocalDateTime.now());
            entity.setLastUpdatedBy("PDDA");
            entity.setContent(contents);
            LOG.debug("Merging entity: displayStoreId={}, version={}", entity.getDisplayStoreId(),
                entity.getVersion());
            getXhbDisplayStoreRepository().save(entity); // Force update
        } else {
            XhbDisplayStoreDao newEntity = new XhbDisplayStoreDao();
            newEntity.setRetrievalCode(retrievalCode);
            newEntity.setContent(contents);
            getXhbDisplayStoreRepository().save(newEntity);
        }

    }


    public void writeToDatabase(final String retrievalCode, final String contents) {
        String methodName = "writeToDatabase(" + retrievalCode + "," + contents + METHOD_END;
        LOG.debug(methodName + ENTERED);

        Optional<XhbDisplayStoreDao> xds =
            getXhbDisplayStoreRepository().findByRetrievalCodeSafe(retrievalCode);

        if (xds.isPresent()) {
            XhbDisplayStoreDao entity = xds.get();
            if (Objects.equals(entity.getContent(), contents)) {
                LOG.info("Content already matches. Skipping update.");
                return;
            }
            entity.setLastUpdateDate(LocalDateTime.now());
            entity.setLastUpdatedBy("PDDA");
            entity.setContent(contents);

            LOG.debug("Retrying merge for entity: displayStoreId={}, version={}",
                entity.getDisplayStoreId(), entity.getVersion());

            getXhbDisplayStoreRepository().saveWithRetry(entity, 3); // try up to 3 times

        } else {
            XhbDisplayStoreDao newEntity = new XhbDisplayStoreDao();
            newEntity.setRetrievalCode(retrievalCode);
            newEntity.setContent(contents);
            getXhbDisplayStoreRepository().save(newEntity); // No retry needed for create
        }
    }


    /**
     * Converts a LocalDateTime object to a Date object.
     * 
     * @param ldt LocalDateTime object
     * @return Date object
     */
    private Date convertLocalDateTimeToDate(final LocalDateTime ldt) {
        String methodName = "convertLocalDateTimeToDate(" + ldt + METHOD_END;
        LOG.debug(methodName + ENTERED);

        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Returns the xhbDisplayStoreRepository object, initialising if currently null.
     * 
     * @return XhbDisplayStoreRepository
     */
    private XhbDisplayStoreRepository getXhbDisplayStoreRepository() {
        if (xhbDisplayStoreRepository == null || !isEntityManagerActive()) {
            xhbDisplayStoreRepository = new XhbDisplayStoreRepository(getEntityManager());
        }
        return xhbDisplayStoreRepository;
    }

}
