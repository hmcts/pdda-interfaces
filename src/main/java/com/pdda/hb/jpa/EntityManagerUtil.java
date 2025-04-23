package com.pdda.hb.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.FlushModeType;
import uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet.InitializationService;

@SuppressWarnings({"PMD.LawOfDemeter", "PMD.AvoidSynchronizedAtMethodLevel"})
public class EntityManagerUtil {
    private static EntityManagerFactory entityManagerFactory;

    protected EntityManagerUtil() {
        // Protected constructor to prevent instantiation
    }

    public static synchronized void setEntityManagerFactory(EntityManagerFactory factory) {
        entityManagerFactory = factory;
    }

    private static synchronized void initializeIfNecessary() {
        if (entityManagerFactory == null) {
            entityManagerFactory = InitializationService.getInstance().getEntityManagerFactory();
        }
    }

    public static EntityManager getEntityManager() {
        initializeIfNecessary();
        if (entityManagerFactory == null) {
            throw new IllegalStateException("EntityManagerFactory is not initialized.");
        }
        EntityManager entityManager = entityManagerFactory.createEntityManager();
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
