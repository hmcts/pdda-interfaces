package uk.gov.hmcts.framework.jdbc.core;

import uk.gov.hmcts.framework.jdbc.exception.DataAccessException;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Processes result sets to generate Java bean instances for when the value object that is required
 * to be constructed is aware of how to populate itself.
 * 
 * @author tz0d5m
 * @version $Revision: 1.3 $
 */
public class RowPopulatorRowProcessor extends AbstractRowProcessor {
    private final Class<?> rowPopulatorClass;

    // default the size to 100, if required, overload the constructor to
    // allow custom sizes for each required implementation...
    private final Collection<RowPopulatorInterface> results = new ArrayList<>(100);

    private RowPopulatorInterface lastVO;

    /**
     * Only constructor that takes the <code>Class</code> of the value object that is aware of how
     * to populate itself from a <code>Row</code>.
     * 
     * @param rowPopulatorClass The value object class that is able to populate its own values. This
     *        must be an instance of <code>RowPopulatorRowProcessor</code>.
     * @throws IllegalArgumentException if the passed in rowPopulatorClass is
     *        null, or it is not an instance of RowPopulatorRowProcessor.
     * @see uk.gov.hmcts.framework.jdbc.core.RowPopulatorRowProcessor
     */
    public RowPopulatorRowProcessor(Class<?> rowPopulatorClass) {
        super();
        if (rowPopulatorClass == null) {
            throw new IllegalArgumentException("rowPopulatorClass cannot be null");
        }

        if (!RowPopulatorInterface.class.isAssignableFrom(rowPopulatorClass)) {
            throw new IllegalArgumentException(
                rowPopulatorClass + " is not assignable to RowPopulatorInterface");
        }

        this.rowPopulatorClass = rowPopulatorClass;
    }

    /**
     * Process the row passed in to construct a new (or add to previous) value object of the type
     * passed into the constructor.
     * 
     * <p>This implementation does not currently call the preProcessRow(Row) or postProcessRow(Row).
     * 
     * @see uk.gov.hmcts.framework.jdbc.core.RowProcessor
     *      #processRow(uk.gov.hmcts.framework.jdbc.core.Row)
     * @see prePro
     */
    @Override
    public void processRow(PddaRow row) {
        // do not add a new entry if deemed as duplicate...
        if (lastVO == null || !lastVO.isDuplicateRow(row)) {
            try {
                lastVO = (RowPopulatorInterface) rowPopulatorClass.newInstance();
                lastVO.populate(row);
                results.add(lastVO);
            } catch (InstantiationException e) {
                throw new DataAccessException("Unable to instantiate " + rowPopulatorClass, e);
            } catch (IllegalAccessException e) {
                throw new DataAccessException("Illegal access to " + rowPopulatorClass, e);
            }
        }

        // ... add the non-duplicated parts...
        lastVO.addRow(row);
    }

    /**
     * Acquire the <code>Collection</code> containing all of the results created by this row
     * processor.
     * 
     * @return The results <code>Collection</code>.
     */
    public Collection<RowPopulatorInterface> getResults() {
        return this.results;
    }
}
