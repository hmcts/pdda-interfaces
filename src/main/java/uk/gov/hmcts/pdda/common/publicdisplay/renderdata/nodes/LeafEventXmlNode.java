package uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Object representing a value of a leaf node of a processed xml document.

 * @author szfnvt
 */
public class LeafEventXmlNode extends EventXmlNode {

    static final long serialVersionUID = 499986711326206014L;

    private static final Logger LOG = LoggerFactory.getLogger(LeafEventXmlNode.class);
    private static final String EMPTY_STRING = "";

    private final String value;

    /**
     * Constructor taking in Name and Value of node.

     * @param nameIn String
     * @param valueIn String
     */
    public LeafEventXmlNode(String nameIn, String valueIn) {
        super(nameIn);
        value = valueIn;
    }

    /**
     * Retrieve leaf node value.

     * @return String
     */
    public String getValue() {
        return value;
    }

    @Override
    protected void appendDebug(StringBuilder buffer, int depth) {
        appendIndent(buffer, depth);
        buffer.append(getName()).append(": ").append(value);
    }

    /**
     * Get Day component of date.

     * @return String
     */
    public String getDay() {
        String day = value.substring(0, indexOf("-"));
        if (day == null || EMPTY_STRING.equals(day.trim())) {
            LOG.debug("Unable to retrieve day from date value: {}", value);
            day = EMPTY_STRING;
        }
        return day;
    }

    /**
     * Get Month component of date.

     * @return String
     */
    public String getMonth() {
        String month = value.substring(indexOf("-") + 1, lastIndexOf("-"));
        if (month == null || EMPTY_STRING.equals(month.trim())) {
            LOG.debug("Unable to retrieve month from date value: {}", value);
            month = EMPTY_STRING;
        }
        return month;
    }

    /**
     * Get Year component of date.

     * @return String
     */
    public String getYear() {
        String year = value.substring(lastIndexOf("-") + 1, value.length());
        if (year == null || EMPTY_STRING.equals(year.trim())) {
            LOG.debug("Unable to retrieve year from date value: {}", value);
            year = EMPTY_STRING;
        }
        return year;
    }

    private int indexOf(String string) {
        return value.indexOf(string);
    }

    private int lastIndexOf(String string) {
        return value.lastIndexOf(string);
    }

}
