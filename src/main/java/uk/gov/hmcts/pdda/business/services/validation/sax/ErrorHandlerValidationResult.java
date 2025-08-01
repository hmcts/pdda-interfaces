package uk.gov.hmcts.pdda.business.services.validation.sax;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import uk.gov.hmcts.pdda.business.services.validation.ValidationResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * SAX Error handler for collating and printing errors.

 * @author Fardellwi
 */
@SuppressWarnings("PMD.LawOfDemeter")
public class ErrorHandlerValidationResult implements ErrorHandler, ValidationResult {

    private final List<Error> errorList = new ArrayList<>();
    private static final String EOL = "\n";

    @Override
    public void error(final SAXParseException exception) throws SAXException {
        errorList.add(new Error(Error.Type.ERROR, exception));
    }

    @Override
    public void fatalError(final SAXParseException exception) throws SAXException {
        errorList.add(new Error(Error.Type.FATAL, exception));
    }

    @Override
    public void warning(final SAXParseException exception) throws SAXException {
        errorList.add(new Error(Error.Type.WARN, exception));
    }

    @Override
    public boolean isValid() {
        for (Error error : errorList) {
            if (error.type != Error.Type.WARN) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        Iterator<Error> errors = errorList.iterator();
        if (errors.hasNext()) {
            StringBuilder builder = new StringBuilder();
            builder.append(errors.next());
            while (errors.hasNext()) {
                builder.append(EOL).append(errors.next());
            }
            return builder.toString();
        }
        return "";
    }

    private static class Error {
        private enum Type {
            FATAL, ERROR, WARN
        }

        private final Type type;
        private final SAXParseException exception;

        public Error(final Type typ, final SAXParseException ex) {
            if (ex == null) {
                throw new IllegalArgumentException("pException: null");
            }
            type = typ;
            exception = ex;
        }

        @Override
        public String toString() {
            return type + " (" + exception.getLineNumber() + ":" + exception.getColumnNumber() + "): "
                + exception.getMessage();
        }
    }
}
