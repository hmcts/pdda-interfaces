package uk.gov.hmcts.pdda.common.publicdisplay.types.document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * <p>
 * Title: An enumeration of possible CasesRequired values.
 * </p>
 * 
 * <p>
 * Description: Possible values are simply <b>all</b> or <b>active</b>.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * 
 * <p>
 * Company: Electronic Data Systems
 * </p>
 * 
 * @author Neil Ellis
 * @version $Revision: 1.3 $
 */
public final class CasesRequired implements Serializable {

    static final long serialVersionUID = -8030925848816402590L;
    
    private static final Logger LOG = LoggerFactory.getLogger(CasesRequired.class);

    public static final CasesRequired ALL = new CasesRequired("all");

    public static final CasesRequired ACTIVE = new CasesRequired("active");

    private final String value;

    private CasesRequired(String val) {
        this.value = val;
    }

    /**
     * Object comparison.
     * 
     * @param obj Object to compare.
     * 
     * @return true if they are semantically identical.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return obj instanceof CasesRequired && value.equals(((CasesRequired) obj).value);
    }

    /**
     * Returns the String representation of the instance.
     * 
     * @return the String representation of the instance.
     * 
     * @post return != null
     */
    @Override
    public String toString() {
        return value;
    }

    @Override
    public int hashCode() {
        LOG.debug("hashCode()");
        return super.hashCode();
    }
}
