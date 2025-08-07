/*
 * This class was automatically generated with <a href="http://www.castor.org">Castor 1.0</a>, using
 * an XML Schema on Xhibit and copied into PDDA V2 as a permananent class. $Id$
 */

package uk.gov.hmcts.pdda.business.xmlbinding.hmcts.pdda.types;

import java.util.Enumeration;
import java.util.Properties;

/**
 * Class DocumentTypeType.
 * 
 * @version $Revision$ $Date$
 */
public final class DocumentTypeType implements java.io.Serializable {

    // --------------------------/
    // - Class/Member Variables -/
    // --------------------------/

    private static final long serialVersionUID = -3028900297802518348L;

    /**
     * The DL type.
     */
    public static final int DL_TYPE = 0;

    /**
     * The instance of the DL type.
     */
    public static final DocumentTypeType DL = new DocumentTypeType(DL_TYPE, "DL");

    /**
     * The DLL type.
     */
    public static final int DLL_TYPE = 1;

    /**
     * The instance of the DLL type.
     */
    public static final DocumentTypeType DLL = new DocumentTypeType(DLL_TYPE, "DLL");

    /**
     * The DLP type.
     */
    public static final int DLP_TYPE = 2;

    /**
     * The instance of the DLP type.
     */
    public static final DocumentTypeType DLP = new DocumentTypeType(DLP_TYPE, "DLP");

    /**
     * The FL type.
     */
    public static final int FL_TYPE = 3;

    /**
     * The instance of the FL type.
     */
    public static final DocumentTypeType FL = new DocumentTypeType(FL_TYPE, "FL");

    /**
     * The FLL type.
     */
    public static final int FLL_TYPE = 4;

    /**
     * The instance of the FLL type.
     */
    public static final DocumentTypeType FLL = new DocumentTypeType(FLL_TYPE, "FLL");

    /**
     * The IWP type.
     */
    public static final int IWP_TYPE = 5;

    /**
     * The instance of the IWP type.
     */
    public static final DocumentTypeType IWP = new DocumentTypeType(IWP_TYPE, "IWP");

    /**
     * The RL type.
     */
    public static final int RL_TYPE = 6;

    /**
     * The instance of the RL type.
     */
    public static final DocumentTypeType RL = new DocumentTypeType(RL_TYPE, "RL");

    /**
     * The WL type.
     */
    public static final int WL_TYPE = 7;

    /**
     * The instance of the WL type.
     */
    public static final DocumentTypeType WL = new DocumentTypeType(WL_TYPE, "WL");

    /**
     * The WLL type.
     */
    public static final int WLL_TYPE = 8;

    /**
     * The instance of the WLL type.
     */
    public static final DocumentTypeType WLL = new DocumentTypeType(WLL_TYPE, "WLL");

    /**
     * The WLS type.
     */
    public static final int WLS_TYPE = 9;

    /**
     * The instance of the WLS type.
     */
    public static final DocumentTypeType WLS = new DocumentTypeType(WLS_TYPE, "WLS");

    /**
     * The FLS type.
     */
    public static final int FLS_TYPE = 10;

    /**
     * The instance of the FLS type.
     */
    public static final DocumentTypeType FLS = new DocumentTypeType(FLS_TYPE, "FLS");

    /**
     * The DLS type.
     */
    public static final int DLS_TYPE = 11;

    /**
     * The instance of the DLS type.
     */
    public static final DocumentTypeType DLS = new DocumentTypeType(DLS_TYPE, "DLS");

    /**
     * The ROS type.
     */
    public static final int ROS_TYPE = 12;

    /**
     * The instance of the ROS type.
     */
    public static final DocumentTypeType ROS = new DocumentTypeType(ROS_TYPE, "ROS");

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

    private DocumentTypeType(int type, String value) {
        super();
        this.type = type;
        this.stringValue = value;
    } 

    // -----------/
    // - Methods -/
    // -----------/

    /**
     * Method enumerate.
     * 
     * <p>Returns an enumeration of all possible instances of DocumentTypeType
     * 
     * @return Enumeration
     */
    public static Enumeration enumerate() {
        return memberTable.elements();
    } // -- Enumeration enumerate()

    /**
     * Method getType.
     * 
     * <p>Returns the type of this DocumentTypeType
     * 
     * @return int
     */
    public int getType() {
        return this.type;
    } // -- int getType()

    /**
     * Method init.
     * 
     * 
     * 
     * @return Properties
     */
    private static Properties init() {
        Properties members = new Properties();
        members.put("DL", DL);
        members.put("DLL", DLL);
        members.put("DLP", DLP);
        members.put("FL", FL);
        members.put("FLL", FLL);
        members.put("IWP", IWP);
        members.put("RL", RL);
        members.put("WL", WL);
        members.put("WLL", WLL);
        members.put("WLS", WLS);
        members.put("FLS", FLS);
        members.put("DLS", DLS);
        members.put("ROS", ROS);
        return members;
    } // -- Properties init()

    /**
     * Method readResolve.
     * 
     * <p>will be called during deserialization to replace the deserialized object with the correct
     * constant instance. <br/>
     * 
     * @return Object
     */
    private Object readResolve() {
        return valueOf(this.stringValue);
    } // -- Object readResolve()

    /**
     * Method toString.
     * 
     * <p>Returns the String representation of this DocumentTypeType
     * 
     * @return String
     */
    @Override
    public String toString() {
        return this.stringValue;
    } // -- String toString()

    /**
     * Method valueOf.
     * 
     * <p>Returns a new DocumentTypeType based on the given String value.
     * 
     * @param string String
     * @return DocumentTypeType
     */
    public static DocumentTypeType valueOf(
        String string) {
        Object obj = null;
        if (string != null) {
            obj = memberTable.get(string);
        }
        if (obj == null) {
            String err = "'" + string + "' is not a valid DocumentTypeType";
            throw new IllegalArgumentException(err);
        }
        return (DocumentTypeType) obj;
    }

}
