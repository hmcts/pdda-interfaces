package uk.gov.hmcts;

import uk.gov.hmcts.pdda.business.entities.xhbrefjudge.XhbRefJudgeDao;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.JudgeName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class DummyJudgeUtil {
    
    private static final String NOTNULL = "Result is Null";
    private static final String TEST1 = "Test1";
    private static final String TEST2 = "Test2";

    private DummyJudgeUtil() {
        // Do nothing
    }
    
    public static XhbRefJudgeDao getXhbRefJudgeDao() {
        Integer refJudgeId = -1;
        String judgeType = "judgeType";
        Integer crestJudgeId = -1;
        String title = "title";
        String firstname = "firstname";
        String middleName = "middleName";
        String surname = "surname";
        String fullListTitle1 = "fullListTitle1";
        String fullListTitle2 = "fullListTitle2";
        String fullListTitle3 = "fullListTitle3";
        String statsCode = "statsCode";
        String initials = "initials";
        String honours = "honours";
        String judVers = "judVers";
        String obsInd = "N";
        String sourceTable = "sourceTable";
        Integer courtId = -1;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = 3;
        XhbRefJudgeDao result = new XhbRefJudgeDao();
        result.setRefJudgeId(refJudgeId);
        result.setJudgeType(judgeType);
        result.setCrestJudgeId(crestJudgeId);
        result.setTitle(title);
        result.setFirstname(firstname);
        result.setMiddleName(middleName);
        result.setSurname(surname);
        result.setFullListTitle1(fullListTitle1);
        result.setFullListTitle2(fullListTitle2);
        result.setFullListTitle3(fullListTitle3);
        result.setStatsCode(statsCode);
        result.setInitials(initials);
        result.setHonours(honours);
        result.setJudVers(judVers);
        result.setObsInd(obsInd);
        result.setSourceTable(sourceTable);
        result.setCourtId(courtId);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        return new XhbRefJudgeDao(result);
    }
    
    public static JudgeName getJudgeName() {
        JudgeName result = new JudgeName("Firstname", "Surname");
        assertNotNull(result.getName(), NOTNULL);
        assertNotNull(result.toString(), NOTNULL);
        return result;
    }
    
}
