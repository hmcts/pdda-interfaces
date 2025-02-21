package uk.gov.hmcts.pdda.business.services.pdda;

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
public class PddaMessageHelper {
    private static final Logger LOG = LoggerFactory.getLogger(PddaMessageHelper.class);
    private static final String LOG_CALLED = " called";

    private final EntityManager entityManager;
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
        return getPddaMessageRepository().findById(pddaMessageId);
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
            getRefPddaMessageTypeRepository().findByMessageType(messageType);
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
            getPddaMessageRepository().findByCpDocumentName(cpDocumentName);
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
        getPddaMessageRepository().save(dao);
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

    public void updatePddaMessage(XhbPddaMessageDao dao,
        String userDisplayName) {
        String methodName = "updatePddaMessage()";
        LOG.debug(methodName + LOG_CALLED);
        dao.setLastUpdatedBy(userDisplayName);
        getPddaMessageRepository().save(dao);
    }

    private XhbPddaMessageRepository getPddaMessageRepository() {
        if (pddaMessageRepository == null) {
            pddaMessageRepository = new XhbPddaMessageRepository(entityManager);
        }
        return pddaMessageRepository;
    }

    private XhbRefPddaMessageTypeRepository getRefPddaMessageTypeRepository() {
        if (refPddaMessageTypeRepository == null) {
            refPddaMessageTypeRepository = new XhbRefPddaMessageTypeRepository(entityManager);
        }
        return refPddaMessageTypeRepository;
    }

    private Optional<?> getFirstInList(List<?> daoList) {
        String methodName = "getFirstInList()";
        LOG.debug(methodName + LOG_CALLED);
        return daoList != null && !daoList.isEmpty() ? Optional.of(daoList.get(0))
            : Optional.empty();
    }
}
