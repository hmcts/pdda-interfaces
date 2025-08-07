package uk.gov.hmcts.framework.jdbc.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title: AbstractRowProcessor.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: Electronic Data Systems
 * </p>
 * 
 * @author Meeraj Kunnumpurath
 * @version $Id: AbstractRowProcessor.java,v 1.5 2006/06/05 12:30:15 bzjrnl Exp $
 */
public abstract class AbstractRowProcessor implements RowProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractRowProcessor.class);

    // Child processors
    private final List<RowProcessor> childProcessors = new ArrayList<>();

    /**
     * getChildProcessors.
     * 
     * @return RowProcessorList
     */
    @Override
    public List<RowProcessor> getChildProcessors() {
        return this.childProcessors;
    }

    /**
     * addChildProcessor.
     * 
     * @param childProcessor RowProcessor
     * @see <code>RowProcessor</code>
     */
    @Override
    public void addChildProcessor(final RowProcessor childProcessor) {
        if (childProcessor == null) {
            throw new IllegalArgumentException("childProcessor");
        }

        this.childProcessors.add(childProcessor);
    }

    /**
     * Use this for any pre-processing. Implementing classes should make sure that this is called in
     * processRow before processing and overridden properly to add pre-processing logic.
     * 
     * @see RowProcessor
     * @param row Row
     */
    @Override
    public void preProcessRow(final PddaRow row) {
        // no implementation required by default...
        LOG.debug("preProcessRow()");
    }

    /**
     * Use this for any post processing. Implementing classes should make sure that this is called
     * in processRow after processing and overridden properly to add post-processing logic.
     * 
     * @see RowProcessor
     * @param row Row
     */
    @Override
    public void postProcessRow(final PddaRow row) {
        // no implementation required by default...
        LOG.debug("postProcessRow()");
    }
}
