package uk.gov.hmcts.pdda.business.entities.xhbcourtellist;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.io.Serializable;



@Repository
public class XhbCourtelListRepository extends AbstractRepository<XhbCourtelListDao> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public XhbCourtelListRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbCourtelListDao> getDaoClass() {
        return XhbCourtelListDao.class;
    }
}
