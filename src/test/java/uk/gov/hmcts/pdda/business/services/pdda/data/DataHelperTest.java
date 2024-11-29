package uk.gov.hmcts.pdda.business.services.pdda.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DataHelperTest {

    private static final String TRUE = "Result is False";

    @InjectMocks
    private final LocalDataHelper classUnderTest = new LocalDataHelper();

    /**
     * validateCourtSite.
     */
    @Test
    void testValidateCourtSite() {
        XhbCourtSiteDao dao = DummyCourtUtil.getXhbCourtSiteDao();
        boolean result = testValidateCourtSite(dao, false);
        assertTrue(result, TRUE);
        result = testValidateCourtSite(dao, true);
        assertTrue(result, TRUE);
    }

    private boolean testValidateCourtSite(XhbCourtSiteDao dao, boolean isPresent) {
        classUnderTest.isPresent = isPresent;
        Optional<XhbCourtSiteDao> result = classUnderTest.validateCourtSite(dao.getCourtSiteId(),
            dao.getCourtSiteName(), dao.getCourtSiteCode());
        return result.isPresent();
    }

    /**
     * Local test version of the DataHelper.
     */
    public class  LocalDataHelper extends DataHelper {
        
        public boolean isPresent;
        
        /**
         * validateCourtSite overrides.
         */
        @Override
        public Optional<XhbCourtSiteDao> findCourtSite(final Integer courtId,
            final String courtSiteName) {
            return this.isPresent ? Optional.of(new XhbCourtSiteDao()) : Optional.empty();
        }

        @Override
        public Optional<XhbCourtSiteDao> createCourtSite(final Integer courtId,
            final String courtSiteName, final String courtSiteCode) {
            return Optional.of(new XhbCourtSiteDao());
        }
    }
}
