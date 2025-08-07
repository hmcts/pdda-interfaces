package uk.gov.hmcts.framework.xml.sax;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public abstract class ForkContentDocumentHandler implements ContentHandler {
 
    /**
     * The first handler to recieve sax events.
     */
    private final ContentHandler handler1;

    /**
     * The second handler to recieve sax events.
     */
    private final ContentHandler handler2;
    
    protected ForkContentDocumentHandler(ContentHandler handler1, ContentHandler handler2) {
        if (handler1 == null) {
            throw new IllegalArgumentException("handler1: null");
        }
        if (handler2 == null) {
            throw new IllegalArgumentException("handler2: null");
        }
        this.handler1 = handler1;
        this.handler2 = handler2;
    }
    
    protected ContentHandler getContentHandler1() {
        return this.handler1;
    }
    
    protected ContentHandler getContentHandler2() {
        return this.handler2;
    }
    
    /**
     * ContentHandler implementation: Begin the scope of a prefix-URI Namespace mapping.
     */
    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        getContentHandler1().startPrefixMapping(prefix, uri);
        getContentHandler2().startPrefixMapping(prefix, uri);
    }

    /**
     * ContentHandler implementation: End the scope of a prefix-URI mapping.
     */
    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        getContentHandler1().endPrefixMapping(prefix);
        getContentHandler2().endPrefixMapping(prefix);
    }

}
