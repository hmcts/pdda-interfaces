package uk.gov.hmcts.pdda.business.services.pdda;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageDao;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrefpddamessagetype.XhbRefPddaMessageTypeDao;
import uk.gov.hmcts.pdda.business.entities.xhbrefpddamessagetype.XhbRefPddaMessageTypeRepository;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * Title: PDDAMessageHelper.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2022
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Mark Harris
 * @version 1.0
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.NullAssignment"})
public class PddaMessageHelper {
    private static final Logger LOG = LoggerFactory.getLogger(PddaMessageHelper.class);
    private static final String LOG_CALLED = " called";

    private EntityManager entityManager;
    private XhbPddaMessageRepository pddaMessageRepository;
    private XhbRefPddaMessageTypeRepository refPddaMessageTypeRepository;

    public PddaMessageHelper(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * findByPddaMessageId.
     * @param pddaMessageId Integer
     * @return XhbPddaMessageDao
     */
    public Optional<XhbPddaMessageDao> findByPddaMessageId(final Integer pddaMessageId) {
        String methodName = "findByPddaMessageId()";
        LOG.debug(methodName + LOG_CALLED);
        return getPddaMessageRepository().findByIdSafe(pddaMessageId);
    }

    /**
     * findByMessageType.
     * @param messageType String
     * @return XhbPddaMessageDao
     */
    @SuppressWarnings("unchecked")
    public Optional<XhbRefPddaMessageTypeDao> findByMessageType(String messageType) {
        String methodName = "findByMessageType()";
        LOG.debug(methodName + LOG_CALLED);
        List<XhbRefPddaMessageTypeDao> daoList =
            getRefPddaMessageTypeRepository().findByMessageTypeSafe(messageType);
        return (Optional<XhbRefPddaMessageTypeDao>) getFirstInList(daoList);
    }

    /**
     * findByCpDocumentName.
     * @param cpDocumentName String
     * @return XhbPddaMessageDao
     */
    @SuppressWarnings("unchecked")
    public Optional<XhbPddaMessageDao> findByCpDocumentName(String cpDocumentName) {
        String methodName = "findByCpDocumentName()";
        LOG.debug(methodName + LOG_CALLED);
        List<XhbPddaMessageDao> daoList =
            getPddaMessageRepository().findByCpDocumentNameSafe(cpDocumentName);
        return (Optional<XhbPddaMessageDao>) getFirstInList(daoList);
    }

    /**
     * findUnrespondedCPMessages.
     * @return List
     */
    public List<XhbPddaMessageDao> findUnrespondedCpMessages() {
        String methodName = "findUnrespondedCPMessages()";
        LOG.debug(methodName + LOG_CALLED);
        return getPddaMessageRepository().findUnrespondedCpMessages();
    }

    /**
     * savePddaMessage.
     * @param dao XhbPddaMessageDao
     */
    public void savePddaMessage(XhbPddaMessageDao dao) {
        String methodName = "savePddaMessage()";
        LOG.debug(methodName + LOG_CALLED);
        
        // Check a final time to make sure the document doesn't already exist before saving
        if (getPddaMessageRepository().findByCpDocumentNameSafe(dao.getCpDocumentName())
            .isEmpty()) {
            getPddaMessageRepository().save(dao);
        } else {
            LOG.debug("There is already an entry for the document: {}", dao.getCpDocumentName());
        }
    }

    /**
     * savePddaMessageType.
     * @param dao XhbRefPddaMessageTypeDao
     * @return XhbPddaMessageDao
     */
    public Optional<XhbRefPddaMessageTypeDao> savePddaMessageType(XhbRefPddaMessageTypeDao dao) {
        String methodName = "savePddaMessageType()";
        LOG.debug(methodName + LOG_CALLED);
        return getRefPddaMessageTypeRepository().update(dao);
    }

    public Optional<XhbPddaMessageDao> updatePddaMessage(XhbPddaMessageDao dao,
        String userDisplayName) {
        String methodName = "updatePddaMessage()";
        LOG.debug(methodName + LOG_CALLED);
        dao.setLastUpdatedBy(userDisplayName);
        return getPddaMessageRepository().update(dao);
    }

    protected XhbPddaMessageRepository getPddaMessageRepository() {
        if (pddaMessageRepository == null || !isEntityManagerActive()) {
            pddaMessageRepository = new XhbPddaMessageRepository(getEntityManager());
        }
        return pddaMessageRepository;
    }

    protected XhbRefPddaMessageTypeRepository getRefPddaMessageTypeRepository() {
        if (refPddaMessageTypeRepository == null || !isEntityManagerActive()) {
            refPddaMessageTypeRepository = new XhbRefPddaMessageTypeRepository(getEntityManager());
        }
        return refPddaMessageTypeRepository;
    }
    
    protected void clearRepositories() {
        LOG.info("clearRepositories()");
        refPddaMessageTypeRepository = null;
        pddaMessageRepository = null;
    }

    private Optional<?> getFirstInList(List<?> daoList) {
        String methodName = "getFirstInList()";
        LOG.debug(methodName + LOG_CALLED);
        return daoList != null && !daoList.isEmpty() ? Optional.of(daoList.get(0))
            : Optional.empty();
    }
    
    protected EntityManager getEntityManager() {
        if (!isEntityManagerActive()) {
            LOG.debug("getEntityManager() - Creating new entityManager");
            clearRepositories();
            entityManager = EntityManagerUtil.getEntityManager();
        }
        return entityManager;
    }
    
    private boolean isEntityManagerActive() {
        return EntityManagerUtil.isEntityManagerActive(entityManager);
    }

    /**
     * Checks if the given document is excluded based on the provided court codes.
     *
     * @param inDocument the document identifier in the format "PublicDisplay_XXX_&lt;more chars&gt;".
     * @param courtsExcluded a comma-separated list of court codes to check against.
     * @return true if the document's court code is in the excluded list, false otherwise.
     */
    public static boolean isCourtExcluded(String inDocument, String courtsExcluded) {
        if (inDocument == null) {
            return false;
        }
        
        // Match and extract the 3-digit number after "PublicDisplay_"
        String regex = "PublicDisplay_(\\d{3})_.*";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(inDocument);

        if (!matcher.matches()) {
            return false; // Doesn't match the expected format
        }

        String courtCode = matcher.group(1);

        if (courtsExcluded == null || courtsExcluded.isEmpty()) {
            return false; // empty list can't contain anything
        }
        
        // Split and check for match
        String[] codes = courtsExcluded.split(",");
        for (String code : codes) {
            if (courtCode.equals(code.trim())) {
                return true;
            }
        }

        return false;
    }
}
