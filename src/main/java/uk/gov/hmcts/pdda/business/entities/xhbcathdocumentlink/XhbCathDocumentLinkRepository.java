package uk.gov.hmcts.pdda.business.entities.xhbcathdocumentlink;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.io.Serializable;

@Repository
public class XhbCathDocumentLinkRepository extends AbstractRepository<XhbCathDocumentLinkDao> implements Serializable {

    private static final long serialVersionUID = 1L;

    public XhbCathDocumentLinkRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbCathDocumentLinkDao> getDaoClass() {
        return XhbCathDocumentLinkDao.class;
    }
}
