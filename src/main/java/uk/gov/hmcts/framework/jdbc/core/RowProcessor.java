package uk.gov.hmcts.framework.jdbc.core;

import java.util.List;

/**
 * <p>
 * Title: An interface for processing rows.
 * </p>
 * <p>
 * Description: This can be used by JDBC queries as a callback handler
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: Electronic Data Systems
 * </p>
 * 
 * @author Meeraj Kunnumpurath
 * @version 1.0
 */

public interface RowProcessor {

    /**
     * This method is called to process the row.
     * 
     * @param row row
     */
    void processRow(PddaRow row);

    /**
     * Use this for any pre-processing. Implementing classes should make sure that this is called in
     * processRow before processing and overridden properly to add pre-processing logic.
     * 
     * @see ReflectionRowProcessor
     * @param row row
     */
    void preProcessRow(PddaRow row);

    /**
     * Use this for any post processing. Implementing classes should make sure that this is called
     * in processRow after processing and overridden properly to add post-processing logic.
     * 
     * @see ReflectionRowProcessor
     * @param row row
     */
    void postProcessRow(PddaRow row);

    /**
     * Get all the child processors for this processor.
     */
    List<?> getChildProcessors();

    /**
     * This method is called after a column in the row is processed.
     * 
     * @param childProcessor RowProcessor
     */
    void addChildProcessor(RowProcessor childProcessor);

}
