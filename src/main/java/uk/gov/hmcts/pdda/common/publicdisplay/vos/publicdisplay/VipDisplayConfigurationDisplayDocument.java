package uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay;

import uk.gov.hmcts.framework.business.vos.CsAbstractValue;

/**

 * Title: VIP Display Configuration Display Document.

 * Description: A VIPDisplayConfigurationDisplayDocument defines a display document assigned to the
 * VIP screen.

 * Copyright: Copyright (c) 2003

 * Company: EDS

 * @author Bal Bhamra
 * @version $Id: VIPDisplayConfigurationDisplayDocument.java,v 1.2 2005/11/16 14:02:33 szfnvt Exp $
 */
public class VipDisplayConfigurationDisplayDocument extends CsAbstractValue {

    static final long serialVersionUID = -5909357967355650423L;

    private final boolean multipleCourt;

    private final String descriptionCode;

    private final String language;

    private final String country;

    /**
     * Constructor taked in descriptionCode, multipleCourt, language and country values.
     */
    public VipDisplayConfigurationDisplayDocument(String descriptionCode, boolean multipleCourt,
        String language, String country) {
        super();
        this.descriptionCode = descriptionCode;
        this.multipleCourt = multipleCourt;
        this.language = language;
        this.country = country;
    }

    /**
     * Get descriptionCode of the display document.
     */
    public String getDescriptionCode() {
        return descriptionCode;
    }

    /**
     * Get multipleCourt value of the display document.
     */
    public boolean isMultipleCourt() {
        return multipleCourt;
    }

    /**
     * Get language of the display document.
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Get country of the display document.
     */
    public String getCountry() {
        return country;
    }
}
