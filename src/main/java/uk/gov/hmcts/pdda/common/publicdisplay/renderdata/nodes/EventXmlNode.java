package uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes;

import java.io.Serializable;

/**
 * Abstract class representing a value of a node of in processed xml document.
 * 
 * @author szfnvt
 */
public abstract class EventXmlNode implements Serializable {

    static final long serialVersionUID = -6652192030869054552L;

    protected static final String NL = System.getProperty("line.seperator", "\n");

    protected static final String TAB = "    ";

    private final String name;

    /**
     * Constructor taking in node name. Called by child nodes.
     * 
     * @param nameIn String
     */
    protected EventXmlNode(String nameIn) {
        name = nameIn;
    }

    /**
     * Returns name of node.
     * 
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Append contents of node for debug.
     * 
     * @return String
     */
    public String toDebug() {
        StringBuilder buffer = new StringBuilder();
        appendDebug(buffer, 0);
        return buffer.toString();
    }

    protected abstract void appendDebug(StringBuilder buffer, int depth);

    protected void appendIndent(StringBuilder buffer, int depth) {
        for (int i = 0; i < depth; i++) {
            buffer.append(TAB);
        }
    }
}
