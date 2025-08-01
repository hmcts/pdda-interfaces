package uk.gov.hmcts.framework.xml.sax;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * Allow multiple content handlers to process the same stream of events.

 * @author Will Fardell
 */
public class ForkContentHandler extends ForkContentDocumentHandler implements ContentHandler {

    /**
     * Construct a new ForkContentHandler for the handlers.
     */
    public ForkContentHandler(ContentHandler handler1, ContentHandler handler2) {
        super(handler1, handler2);
    }

    /**
     * ContentHandler implementation: Set the document loacator for the content handlers.
     */
    @Override
    public void setDocumentLocator(Locator locator) {
        getContentHandler1().setDocumentLocator(locator);
        getContentHandler2().setDocumentLocator(locator);
    }

    /**
     * ContentHandler implementation: Receive notification of the beginning of a document.
     */
    @Override
    public void startDocument() throws SAXException {
        getContentHandler1().startDocument();
        getContentHandler2().startDocument();
    }

    /**
     * ContentHandler implementation: Receive notification of the end of a document.
     */
    @Override
    public void endDocument() throws SAXException {
        getContentHandler1().endDocument();
        getContentHandler2().endDocument();
    }

    /**
     * ContentHandler implementation: Receive notification of the beginning of an element.
     */
    @Override
    public void startElement(String namespaceUri, String localName, String name, Attributes atts)
        throws SAXException {
        getContentHandler1().startElement(namespaceUri, localName, name, atts);
        getContentHandler2().startElement(namespaceUri, localName, name, atts);
    }

    /**
     * ContentHandler implementation: Receive notification of the end of an element.
     */
    @Override
    public void endElement(String namespaceUri, String localName, String name)
        throws SAXException {
        getContentHandler1().endElement(namespaceUri, localName, name);
        getContentHandler2().endElement(namespaceUri, localName, name);
    }

    /**
     * ContentHandler implementation: Receive notification of character data.
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        getContentHandler1().characters(ch, start, length);
        getContentHandler2().characters(ch, start, length);
    }

    /**
     * ContentHandler implementation: Receive notification of ignorable whitespace in element
     * content.
     */
    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        getContentHandler1().ignorableWhitespace(ch, start, length);
        getContentHandler2().ignorableWhitespace(ch, start, length);
    }

    /**
     * ContentHandler implementation: Receive notification of a processing instruction.
     */
    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        getContentHandler1().processingInstruction(target, data);
        getContentHandler2().processingInstruction(target, data);
    }

    /**
     * ContentHandler implementation: Receive notification of a skipped entity.
     */
    @Override
    public void skippedEntity(String name) throws SAXException {
        getContentHandler1().skippedEntity(name);
        getContentHandler2().skippedEntity(name);
    }
}
