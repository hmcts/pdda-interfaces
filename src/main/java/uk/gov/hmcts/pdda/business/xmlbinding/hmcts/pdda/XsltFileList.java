package uk.gov.hmcts.pdda.business.xmlbinding.hmcts.pdda;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.io.Serializable;

/**
 * Class XsltFileList.
 * 
 * @version $Revision$ $Date$
 */
public class XsltFileList extends AbstractTypeMarshal<String> implements Serializable {

    // --------------------------/
    // - Class/Member Variables -/
    // --------------------------/

    private static final long serialVersionUID = -7758882152009771806L;
    
    private static final Logger LOG = LoggerFactory.getLogger(XsltFileList.class);

    // -----------/
    // - Methods -/
    // -----------/

    /**
     * Method addXsltFileName.
     * 
     * 
     * 
     * @param xsltFileName String
     */
    public void addXsltFileName(String xsltFileName) {
        getList().add(xsltFileName);
    } // -- void addXsltFileName(String)

    /**
     * Note: hashCode() has not been overridden.
     * 
     * @param obj Object
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof XsltFileList) {

            XsltFileList temp = (XsltFileList) obj;
            if (this.getList() != null) {
                if (temp.getList() == null
                    || !this.getList().equals(temp.getList())) {
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
     * Method getXsltFileName.
     * 
     * 
     * 
     * @param index int
     * @return String
     */
    public String getXsltFileName(int index) {
        // -- check bounds for index
        if (index < 0 || index >= getList().size()) {
            throw new IndexOutOfBoundsException("getXsltFileName: Index value '" + index
                + "' not in range [0.." + (getList().size() - 1) + "]");
        }

        return getList().get(index);
    }

    /**
     * Method getXsltFileName.
     * 
     * 
     * 
     * @return String
     */
    public String[] getXsltFileName() {
        int size = getList().size();
        String[] array = new String[size];
        for (int index = 0; index < size; index++) {
            array[index] = getList().get(index);
        }
        return array;
    }

    /**
     * Method getXsltFileNameCount.
     * 
     * 
     * 
     * @return int
     */
    public int getXsltFileNameCount() {
        return getList().size();
    }

    /**
     * Method removeAllXsltFileName.
     * 
     */
    public void removeAllXsltFileName() {
        getList().clear();
    }

    /**
     * Method removeXsltFileName.
     * 
     * 
     * 
     * @param index int
     * @return String
     */
    public String removeXsltFileName(int index) {
        String obj = getList().get(index);
        getList().remove(index);
        return obj;
    }

    /**
     * Method setXsltFileName.
     * 
     * @param index int
     * @param xsltFileName String
     */
    public void setXsltFileName(int index, String xsltFileName) {
        // -- check bounds for index
        if (index < 0 || index >= getList().size()) {
            throw new IndexOutOfBoundsException("setXsltFileName: Index value '" + index
                + "' not in range [0.." + (getList().size() - 1) + "]");
        }
        getList().set(index, xsltFileName);
    }

    /**
     * Method setXsltFileName.
     * 
     * @param xsltFileNameArray StringArray
     */
    public void setXsltFileName(String... xsltFileNameArray) {
        // -- copy array
        getList().clear();
        for (String xsltObj : xsltFileNameArray) {
            getList().add(xsltObj);
        }
    }

    /**
     * Method unmarshal.
     * 
     * @param reader Reader
     * @return XsltFileList XsltFileList
     */
    public static XsltFileList unmarshal(
        Reader reader)
        throws MarshalException, ValidationException {
        return (XsltFileList) Unmarshaller.unmarshal(XsltFileList.class, reader);
    }
}
