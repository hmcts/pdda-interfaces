package uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query;

import org.easymock.EasyMock;
import uk.gov.hmcts.pdda.business.entities.AbstractRepository;

import java.util.List;

@SuppressWarnings("PMD.TestClassWithoutTestCases")
class AbstractQueryTest {
    
    protected AbstractQueryTest() {
        super();
    }
    
    protected void addReplayArray(List<AbstractRepository<?>> replayArray, AbstractRepository<?> repository) {
        if (!replayArray.contains(repository)) {
            replayArray.add(repository);
        }
    }
    
    protected void doReplayArray(List<AbstractRepository<?>> replayArray) {
        for (AbstractRepository<?> repository : replayArray) {
            EasyMock.replay(repository);
        }
    }
    
    protected void verifyReplayArray(List<AbstractRepository<?>> replayArray) {
        for (AbstractRepository<?> repository : replayArray) {
            EasyMock.verify(repository);
        }
    }
}
