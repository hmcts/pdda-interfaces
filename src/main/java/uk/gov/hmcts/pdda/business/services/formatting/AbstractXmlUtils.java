package uk.gov.hmcts.pdda.business.services.formatting;

import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;


public class AbstractXmlUtils {

    protected static final int LESS_THAN = -1;
    protected static final int EQUAL_TO = 0;
    protected static final int GREATER_THAN = 1;

    protected AbstractXmlUtils() {
        // Protected constructor
    }
    
    /**
     * Create a serializer to write to the output stream.
     *
     * @param out the stream to create the serializer from
     * @return the new serializer
     */
    public ContentHandler createXmlSerializer(OutputStream out) throws IOException {
        Serializer serializer = createSerializer(Method.XML);
        serializer.setOutputStream(out);
        return serializer.asContentHandler();
    }

    /**
     * Create a serializer to write to the character stream.
     *
     * @param writer the character stream to create the serializer from
     * @return the new serializer
     */
    public ContentHandler createXmlSerializer(Writer writer) throws IOException {
        Serializer serializer = createSerializer(Method.XML);
        serializer.setWriter(writer);
        return serializer.asContentHandler();
    }

    /**
     * Create a serializer to write to the output stream.
     *
     * @param out the stream to create the serializer from
     * @return the new serializer
     */
    public ContentHandler createHtmlSerializer(OutputStream out) throws IOException {
        Serializer serializer = createSerializer(Method.HTML);
        serializer.setOutputStream(out);
        return serializer.asContentHandler();
    }

    /**
     * Create a serializer to write to the character stream.
     *
     * @param writer the character stream to create the serializer from
     * @return the new serializer
     */
    public ContentHandler createHtmlSerializer(Writer writer) throws IOException {
        Serializer serializer = createSerializer(Method.HTML);
        serializer.setWriter(writer);
        return serializer.asContentHandler();
    }

    /**
     * Creates a Serializer for the give mime type.
     *
     * @param mimeType The mime type e.g. HTML, XML etc.
     * @return the Serializer for the given mimeType
     */
    private Serializer createSerializer(String mimeType) {
        return SerializerFactory.getSerializer(OutputPropertiesFactory.getDefaultMethodProperties(mimeType));
    }
    
    // are we processing a warned list
    protected boolean isWarnedList() {
        return MergeListUtils.isWarnedList(this.getClass());
    }

    // are we processing a daily list
    protected boolean isDailyList() {
        return MergeListUtils.isDailyList(this.getClass());
    }

    // are we processing a firmlist
    protected boolean isFirmList() {
        return MergeListUtils.isFirmList(this.getClass());
    }
    
    protected boolean isEmptyParentNode(Node node) {
        String childTag = isWarnedList() ? WarnedTag.FIXTURE.value : Tag.SITTING.value;
        return !MergeNodeUtils.hasChildNode(node, childTag);
    }

    protected boolean allowEmptyParentNodes() {
        return false;
    }
}
