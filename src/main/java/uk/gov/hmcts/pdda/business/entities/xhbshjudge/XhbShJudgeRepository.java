package uk.gov.hmcts.pdda.business.entities.xhbshjudge;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.io.Serializable;

@Repository
public class XhbShJudgeRepository extends AbstractRepository<XhbShJudgeDao> implements Serializable {

    private static final long serialVersionUID = 1L;

    public XhbShJudgeRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbShJudgeDao> getDaoClass() {
        return XhbShJudgeDao.class;
    }
}
