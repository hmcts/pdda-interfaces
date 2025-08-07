package uk.gov.hmcts.pdda.business.xmlbinding.hmcts.pdda;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.Validator;

import java.util.ArrayList;
import java.util.List;

public class AbstractTypeMarshal<T> {

    /**
     * Field.
     */
    private final List<T> list;
    
    public AbstractTypeMarshal() {
        super();
        this.list = new ArrayList<>();
    }
    
    protected List<T> getList() {
        return this.list;
    }
    
    /**
     * Method marshal.
     * 
     * 
     * 
     * @param out Writer
     */
    public void marshal(java.io.Writer out)
        throws MarshalException, ValidationException {

        Marshaller.marshal(this, out);
    }

    /**
     * Method marshal.
     * 
     * @param handler ContentHandler
     */
    public void marshal(org.xml.sax.ContentHandler handler) throws java.io.IOException,
        MarshalException, ValidationException {

        Marshaller.marshal(this, handler);
    }
    
    /**
     * Method isValid.
     * 
     * @return boolean
     */
    public boolean isValid() {
        try {
            validate();
        } catch (ValidationException vex) {
            return false;
        }
        return true;
    } // -- boolean isValid()

    /**
     * Method validate.
     * 
     */
    public void validate() throws ValidationException {
        Validator validator = new Validator();
        validator.validate(this);
    } // -- void validate()
}
