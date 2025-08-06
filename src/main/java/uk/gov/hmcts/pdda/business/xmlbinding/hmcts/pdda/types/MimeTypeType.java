/*
 * This class was automatically generated with <a href="http://www.castor.org">Castor 1.0</a>, using
 * an XML Schema on Xhibit and copied into PDDA V2 as a permananent class. $Id$
 */

package uk.gov.hmcts.pdda.business.xmlbinding.hmcts.pdda.types;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;

/**
 * Class MimeTypeType.

 * @version $Revision$ $Date$
 */
public final class MimeTypeType implements Serializable {

    // --------------------------/
    // - Class/Member Variables -/
    // --------------------------/

    private static final long serialVersionUID = -7706464055525413935L;

    /**
     * The HTM type.
     */
    public static final int HTM_TYPE = 0;

    /**
     * The instance of the HTM type.
     */
    public static final MimeTypeType HTM = new MimeTypeType(HTM_TYPE, "HTM");

    /**
     * The PDF type.
     */
    public static final int PDF_TYPE = 1;

    /**
     * The instance of the PDF type.
     */
    public static final MimeTypeType PDF = new MimeTypeType(PDF_TYPE, "PDF");

    /**
     * The FOP type.
     */
    public static final int FOP_TYPE = 2;

    /**
     * The instance of the FOP type.
     */
    public static final MimeTypeType FOP = new MimeTypeType(FOP_TYPE, "FOP");

    /**
     * Field _memberTable.
     */
    private static Properties memberTable = init();

    /**
     * Field type.
     */
    private final int type;

    /**
     * Field stringValue.
     */
    private final String stringValue;

    // ----------------/
    // - Constructors -/
    // ----------------/

    private MimeTypeType(int type, String value) {
        super();
        this.type = type;
        this.stringValue = value;
    }

    // -----------/
    // - Methods -/
    // -----------/

    /**
     * Returns an enumeration of all possible instances of MimeTypeType.

     * @return Enumeration
     */
    public static Enumeration enumerate() {
        return memberTable.elements();
    }

    /**
     * Returns the type of this MimeTypeType.

     * @return int
     */
    public int getType() {
        return this.type;
    }

    /**
     * Method init.

     * @return Properties
     */
    private static Properties init() {
        Properties members = new Properties();
        members.put("HTM", HTM);
        members.put("PDF", PDF);
        members.put("FOP", FOP);
        return members;
    }

    /**
     * Called during deserialization to replace the deserialized object with the correct
     * constant instance.

     * @return Object
     */
    private Object readResolve() {
        return valueOf(this.stringValue);
    }

    /**
     * Returns the String representation of this MimeTypeType.

     * @return String
     */
    @Override
    public String toString() {
        return this.stringValue;
    }

    /**
     * Returns a new MimeTypeType based on the given String value.

     * @param string String
     * @return MimeTypeType
     */
    public static MimeTypeType valueOf(
        String string) {
        Object obj = fromString(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid MimeTypeType";
            throw new IllegalArgumentException(err);
        }
        return (MimeTypeType) obj;
    }
    
    public static MimeTypeType fromString(String value) {
        if (value != null) {
            return (MimeTypeType) memberTable.get(value.toUpperCase(Locale.getDefault()));
        }
        return null;
    }
}
