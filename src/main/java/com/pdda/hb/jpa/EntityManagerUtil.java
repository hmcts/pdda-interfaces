package com.pdda.hb.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.FlushModeType;
import uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet.InitializationService;

@SuppressWarnings("PMD.LawOfDemeter")
public class EntityManagerUtil {
    private static EntityManagerFactory entityManagerFactory;

    static {
        try {
            entityManagerFactory = InitializationService.getInstance().getEntityManagerFactory();

        } catch (RuntimeException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    protected EntityManagerUtil() {
        // Protected constructor
    }

    public static EntityManager getEntityManager() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.setFlushMode(FlushModeType.AUTO);
        return entityManager;
    }
    
    public static void setEntityManagerFactory(EntityManagerFactory factory) {
        entityManagerFactory = factory;
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
