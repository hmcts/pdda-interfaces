package uk.gov.hmcts.framework.services.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * Default error handler.
 * @author pznwc5
 */
public class ErrorChecker implements ErrorHandler {
    @Override
    public void error(SAXParseException exception) throws SAXParseException {
        throw exception;
    }

    @Override
    public void warning(SAXParseException exception) throws SAXParseException {
        throw exception;
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXParseException {
        throw exception;
    }
}
