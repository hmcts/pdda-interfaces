package com.pdda.hb.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet.InitializationService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.FlushModeType;

public class EntityManagerUtil {
    private static final Logger LOG = LoggerFactory.getLogger(EntityManagerUtil.class);
    private static final EntityManagerFactory ENTITYMANAGERFACTORY;

    static {
        try {
            ENTITYMANAGERFACTORY = InitializationService.getInstance().getEntityManagerFactory();

        } catch (RuntimeException ex) {
            LOG.error("Initial SessionFactory creation failed.{}", ex.getMessage());
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
}
