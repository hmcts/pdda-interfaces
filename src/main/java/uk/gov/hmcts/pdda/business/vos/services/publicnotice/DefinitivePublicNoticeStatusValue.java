package uk.gov.hmcts.pdda.business.vos.services.publicnotice;

import uk.gov.hmcts.framework.business.vos.CsAbstractValue;

/**
 * Description of the Class.

 * @author tzxbys
 * @created 24 February 2003
 */
public class DefinitivePublicNoticeStatusValue extends CsAbstractValue {
    private static final long serialVersionUID = 2715038451448616559L;
    Integer definitivePublicNoticeId;

    boolean active;

    /**
     * Constructor for the DefinitivePublicNoticeStatusValue object.

     * @param definitivePublicNoticeId Integer
     * @param active boolean
     */
    public DefinitivePublicNoticeStatusValue(Integer definitivePublicNoticeId, boolean active) {
        super();
        this.definitivePublicNoticeId = definitivePublicNoticeId;
        this.active = active;
    }

    /**
     * Gets the definitivePublicNoticeId attribute of the DefinitivePublicNoticeStatusValue object.

     * @return The definitivePublicNoticeId value
     */
    public Integer getDefinitivePublicNoticeId() {
        return this.definitivePublicNoticeId;
    }

    /**
     * Gets the isActive attribute of the DefinitivePublicNoticeStatusValue object.

     * @return The isActive value
     */
    public boolean isActive() {
        return this.active;
    }

}
