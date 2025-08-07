package uk.gov.hmcts.pdda.business.vos.translation.impl;

/**
 * <p>
 * Title: XmlBuffer.
 * </p>
 * <p>
 * Description: Buffer for creating the XML
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: Electronic Data Systems
 * </p>
 * 
 * @author William Fardell, Xdevelopment (2004)
 * @version $Id: XmlBuffer.java,v 1.4 2006/06/05 12:28:29 bzjrnl Exp $
 */
@SuppressWarnings("PMD.AvoidStringBufferField")
public class XmlBuffer {
    private static final boolean APPEND_WHITESPACE = isAppendWhitespace();

    private StringBuilder buffer;

    /**
     * Append a line no escape.
     * 
     * @param text String
     */
    public void append(String text) {
        if (text != null) {
            getBuffer().append(text);
        }
    }

    /**
     * Append a line.
     * 
     * @param text String
     */
    public void appendLine(String text) {
        append(text);
        appendLine();
    }

    /**
     * Append a line.
     */
    public void appendLine() {
        if (APPEND_WHITESPACE) {
            getBuffer().append('\n');
        }
    }

    /**
     * Append the specified indent.
     * 
     * @param indent int
     */
    public void appendIndent(int indent) {
        if (APPEND_WHITESPACE) {
            for (int i = 0; i < indent; i++) {
                getBuffer().append("    ");
            }
        }
    }

    /**
     * Append the specified attribute if not null.
     * 
     * @param name String
     * @param value String
     */
    public void appendAttribute(String name, String value) {
        if (name != null && value != null) {
            getBuffer().append(' ');
            appendXml(name);
            getBuffer().append("=\"");
            appendXml(value);
            getBuffer().append('"');
        }
    }

    /**
     * Append the text escaping any characters.
     * 
     * @param text String
     */
    public void appendXml(String text) {
        if (text != null) {
            char[] source = text.toCharArray();
            for (char character : source) {
                switch (character) {
                    case '"':
                        getBuffer().append("&quot;");
                        break;
                    case '\'':
                        getBuffer().append("&apos;");
                        break;
                    case '&':
                        getBuffer().append("&amp;");
                        break;
                    case '<':
                        getBuffer().append("&lt;");
                        break;
                    case '>':
                        getBuffer().append("&gt;");
                        break;
                    default:
                        getBuffer().append(character);
                        break;
                }
            }
        }
    }

    /**
     * Convert the buffer into a string.
     */
    @Override
    public String toString() {
        return getBuffer().toString();
    }
    
    private StringBuilder getBuffer() {
        if (this.buffer == null) {
            this.buffer = new StringBuilder();
        }
        return this.buffer;
    }

    //
    // Utilities
    //

    private static boolean isAppendWhitespace() {
        String appendWhitespace = System.getProperty(
            "uk.gov.hmcts.pdda.business.vos.translation.impl.XmlgetBuffer().appendWhitespace");
        return appendWhitespace != null && ("YES".equalsIgnoreCase(appendWhitespace)
            || "TRUE".equalsIgnoreCase(appendWhitespace));
    }
}
