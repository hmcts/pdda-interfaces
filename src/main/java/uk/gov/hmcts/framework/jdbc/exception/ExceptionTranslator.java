package uk.gov.hmcts.framework.jdbc.exception;

/**

 * Title: Exception translator.


 * Description: This should be implemented by classes that translate SQLException to JDBC framework
 * Exceptions


 * Copyright: Copyright (c) 2003


 * Company: Electronic Data Systems

 * @author Meeraj Kunnumpurath
 * @version $Id: ExceptionTranslator.java,v 1.8 2006/06/05 12:30:17 bzjrnl Exp $
 */
public abstract class ExceptionTranslator {

    /** Translator used by the factory. */
    private static final ExceptionTranslator TRANSLATOR;

    // static initializer for creating the translator
    static {
        TRANSLATOR = new ExceptionTranslator.DefaultExceptionTranslatorImpl();
    }

    /**
     * Translates the SQLException to the JDBC framework exception.

     * @param ex SQL exception that needs to be translated
     * @return Data access exception specific to the JDBC framework
     */
    public abstract DataAccessException translate(Exception ex);

    /**
     * Translates the SQLException to the JDBC framework exception.

     * @param ex SQL exception that needs to be translated
     * @param col String
     * @return Data access exception specific to the JDBC framework
     */
    public abstract DataAccessException translate(Exception ex, String col);

    /**
     * Translates the SQLException to the JDBC framework exception.

     * @param ex SQL exception that needs to be translated
     * @param col int
     * @return Data access exception specific to the JDBC framework
     */
    public abstract DataAccessException translate(Exception ex, int col);

    /**
     * Returns the default implementation.

     * @return ExceptionTranslator
     */
    public static ExceptionTranslator getInstance() {
        return TRANSLATOR;
    }

    // Default implementation with nesting
    private static final class DefaultExceptionTranslatorImpl extends ExceptionTranslator {
        @Override
        public DataAccessException translate(Exception ex) {
            return new DataAccessException(ex.getMessage(), ex);
        }

        @Override
        public DataAccessException translate(Exception ex, String col) {
            return new DataAccessException(ex.getMessage(), ex, col);
        }

        @Override
        public DataAccessException translate(Exception ex, int col) {
            return new DataAccessException(ex.getMessage(), ex, String.valueOf(col));
        }
    }
}
