/*
 * This class was automatically generated with <a href="http://www.castor.org">Castor 1.0</a>, using
 * an XML Schema on Xhibit and copied into PDDA V2 as a permananent class. $Id$
 */

package uk.gov.hmcts.pdda.business.xmlbinding.hmcts.pdda.types;

import java.util.Enumeration;
import java.util.Properties;

/**
 * Class DistributionTypeType.

 * @version $Revision$ $Date$
 */
public final class DistributionTypeType implements java.io.Serializable {

    // --------------------------/
    // - Class/Member Variables -/
    // --------------------------/

    private static final long serialVersionUID = -3230026591131847812L;

    /**
     * The EMAIL type.
     */
    public static final int EMAIL_TYPE = 0;

    /**
     * The instance of the EMAIL type.
     */
    public static final DistributionTypeType EMAIL = new DistributionTypeType(EMAIL_TYPE, "EMAIL");

    /**
     * The FAX type.
     */
    public static final int FAX_TYPE = 1;

    /**
     * The instance of the FAX type.
     */
    public static final DistributionTypeType FAX = new DistributionTypeType(FAX_TYPE, "FAX");

    /**
     * The FTP type.
     */
    public static final int FTP_TYPE = 2;

    /**
     * The instance of the FTP type.
     */
    public static final DistributionTypeType FTP = new DistributionTypeType(FTP_TYPE, "FTP");

    /**
     * The POST type.
     */
    public static final int POST_TYPE = 3;

    /**
     * The instance of the POST type.
     */
    public static final DistributionTypeType POST = new DistributionTypeType(POST_TYPE, "POST");

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

    private DistributionTypeType(int type, String value) {
        super();
        this.type = type;
        this.stringValue = value;
    }

    // -----------/
    // - Methods -/
    // -----------/

    /**
     * Returns an enumeration of all possible instances of DistributionTypeType.

     * @return Enumeration
     */
    public static Enumeration enumerate() {
        return memberTable.elements();
    }

    /**
     * Returns the type of this DistributionTypeType.

     * @return int
     */
    public int getType() {
        return this.type;
    } // -- int getType()

    /**
     * Method init.

     * @return Properties
     */
    private static Properties init() {
        Properties members = new Properties();
        members.put("EMAIL", EMAIL);
        members.put("FAX", FAX);
        members.put("FTP", FTP);
        members.put("POST", POST);
        return members;
    } // -- Properties init()

    /**
     * Called during deserialization to replace the deserialized object with the correct
     * constant instance.

     * @return Object
     */
    private Object readResolve() {
        return valueOf(this.stringValue);
    } // -- Object readResolve()

    /**
     * Returns the String representation of this DistributionTypeType.

     * @return String
     */
    @Override
    public String toString() {
        return this.stringValue;
    } // -- String toString()

    /**
     * Returns a new DistributionTypeType based on the given String value.

     * @param string String
     * @return DistributionTypeType
     */
    public static DistributionTypeType valueOf(String string) {
        Object obj = null;
        if (string != null) {
            obj = memberTable.get(string);
        }
        if (obj == null) {
            String err = "'" + string + "' is not a valid DistributionTypeType";
            throw new IllegalArgumentException(err);
        }
        return (DistributionTypeType) obj;
    }

}
