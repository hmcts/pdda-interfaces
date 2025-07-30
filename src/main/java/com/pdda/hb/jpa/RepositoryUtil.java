package com.pdda.hb.jpa;

import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

public class RepositoryUtil {

    protected RepositoryUtil() {
        // Protected constructor
    }
    
    public static boolean isRepositoryActive(AbstractRepository<?> repository) {
        return repository != null
            && EntityManagerUtil.isEntityManagerActive(repository.getEntityManager());
    }
}