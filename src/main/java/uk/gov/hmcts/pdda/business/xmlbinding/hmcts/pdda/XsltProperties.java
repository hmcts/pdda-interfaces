package uk.gov.hmcts.pdda.business.xmlbinding.hmcts.pdda;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Class XsltProperties.
 * 
 * @version $Revision$ $Date$
 */
public class XsltProperties extends AbstractTypeMarshal<XsltTransform> implements java.io.Serializable {

    // --------------------------/
    // - Class/Member Variables -/
    // --------------------------/

    private static final long serialVersionUID = -1589292923881364110L;
    
    private static final Logger LOG = LoggerFactory.getLogger(XsltProperties.class);

    // -----------/
    // - Methods -/
    // -----------/

    /**
     * Method addXsltTransform.
     * 
     * 
     * 
     * @param xsltTransform XsltTransform
     */
    public void addXsltTransform(XsltTransform xsltTransform) {
        getList().add(xsltTransform);
    }

    /**
     * Note: hashCode() has not been overriden.
     * 
     * @param obj Object
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof XsltProperties) {

            XsltProperties temp = (XsltProperties) obj;
            if (this.getList() != null) {
                if (temp.getList() == null || !this.getList().equals(temp.getList())) {
                    return false;
                }
            } else if (temp.getList() != null) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        LOG.debug("hashCode()");
        return super.hashCode();
    }

    /**
     * Method getXsltTransform.
     * 
     * 
     * 
     * @param index int
     * @return XsltTransform
     */
    public XsltTransform getXsltTransform(int index) {
        // -- check bounds for index
        if (index < 0 || index >= getList().size()) {
            throw new IndexOutOfBoundsException("getXsltTransform: Index value '" + index + "' not in range [0.."
                + (getList().size() - 1) + "]");
        }

        return getList().get(index);
    }

    /**
     * Method getXsltTransform.
     * 
     * 
     * 
     * @return XsltTransform
     */
    public XsltTransform[] getXsltTransform() {
        int size = getList().size();
        XsltTransform[] xsltTransformArray = new XsltTransform[size];
        for (int index = 0; index < size; index++) {
            xsltTransformArray[index] = getList().get(index);
        }
        return xsltTransformArray;
    }

    /**
     * Method getXsltTransformCount.
     * 
     * 
     * 
     * @return int
     */
    public int getXsltTransformCount() {
        return getList().size();
    }

    /**
     * Method removeAllXsltTransform.
     * 
     */
    public void removeAllXsltTransform() {
        getList().clear();
    }

    /**
     * Method removeXsltTransform.
     * 
     * 
     * 
     * @param index int
     * @return XsltTransform
     */
    public XsltTransform removeXsltTransform(int index) {
        XsltTransform obj = getList().get(index);
        getList().remove(index);
        return obj;
    }

    /**
     * Method setXsltTransform.
     * 
     * 
     * 
     * @param index int
     * @param xsltTransform XsltTransform
     */
    public void setXsltTransform(int index, XsltTransform xsltTransform) {
        // -- check bounds for index
        if (index < 0 || index >= getList().size()) {
            throw new IndexOutOfBoundsException("setXsltTransform: Index value '" + index + "' not in range [0.."
                + (getList().size() - 1) + "]");
        }
        getList().set(index, xsltTransform);
    }

    /**
     * Method setXsltTransform.
     * 
     * 
     * 
     * @param xsltTransformArray XsltTransformArray
     */
    public void setXsltTransform(XsltTransform... xsltTransformArray) {
        // -- copy array
        getList().clear();
        getList().addAll(Arrays.asList(xsltTransformArray));
    }

    /**
     * Method unmarshal.
     * 
     * 
     * 
     * @param reader Reader
     * @return XsltProperties
     */
    public static XsltProperties unmarshal(java.io.Reader reader) throws MarshalException, ValidationException {
        return (XsltProperties) Unmarshaller.unmarshal(XsltProperties.class, reader);
    }
}
