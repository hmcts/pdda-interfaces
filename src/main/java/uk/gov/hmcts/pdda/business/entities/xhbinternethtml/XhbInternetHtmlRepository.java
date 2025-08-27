package uk.gov.hmcts.pdda.business.entities.xhbinternethtml;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;


@Repository
@SuppressWarnings("unchecked")
public class XhbInternetHtmlRepository extends AbstractRepository<XhbInternetHtmlDao> {

    public XhbInternetHtmlRepository(EntityManager em) {
        super(em);
    }

    @Override
    public Class<XhbInternetHtmlDao> getDaoClass() {
        return XhbInternetHtmlDao.class;
    }

}
