package com.pdda.hb.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.FlushModeType;
import uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet.InitializationService;

@SuppressWarnings("PMD.LawOfDemeter")
public class EntityManagerUtil {
    private static final EntityManagerFactory ENTITYMANAGERFACTORY;

    static {
        try {
            ENTITYMANAGERFACTORY = InitializationService.getInstance().getEntityManagerFactory();

        } catch (RuntimeException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    protected EntityManagerUtil() {
        // Protected constructor
    }

    public static EntityManager getEntityManager() {
        EntityManager entityManager = ENTITYMANAGERFACTORY.createEntityManager();
        entityManager.setFlushMode(FlushModeType.AUTO);
        return entityManager;
    }
    
    public static boolean isEntityManagerActive(EntityManager entityManager) {
        return entityManager != null && entityManager.isOpen();
    }

    public static boolean isEntityManagerClosed(EntityManager entityManager) {
        return entityManager == null || !entityManager.isOpen();
    }

    public static boolean isTransactionActive(EntityManager entityManager) {
        if (entityManager != null && entityManager.getTransaction() != null) {
            return entityManager.getTransaction().isActive();
        }
        return false;
    }
}
