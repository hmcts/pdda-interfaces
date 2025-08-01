package uk.gov.hmcts.pdda.common.publicdisplay.vos.publicdisplay;

import uk.gov.hmcts.framework.business.vos.CsAbstractValue;

/**

 * Title: VIP Display Configuration Display Document.

 * Description: A VIPDisplayConfiguration defines configuration for VIP Launcher screen.

 * Copyright: Copyright (c) 2003

 * Company: EDS

 * @author Bal Bhamra
 * @version $Id: VIPDisplayConfiguration.java,v 1.1 2005/08/02 13:37:11 szfnvt Exp $
 */
public class VipDisplayConfiguration extends CsAbstractValue {

    static final long serialVersionUID = 1154440904138095238L;

    private VipDisplayConfigurationDisplayDocument[] vipDisplayConfigurationDisplayDocuments;

    private VipDisplayConfigurationCourtRoom[] vipDisplayConfigurationCourtRooms;

    private final boolean unassignedCases;

    /**
     * VIPDisplayConfiguration.

     * Constructor takes in VIPDisplayConfigurationDisplayDocument[],
     * VIPDisplayConfigurationCourtRoom[]. unassignedCases
     */
    public VipDisplayConfiguration(
        VipDisplayConfigurationDisplayDocument[] vipDisplayConfigurationDisplayDocuments,
        VipDisplayConfigurationCourtRoom[] vipDisplayConfigurationCourtRooms,
        boolean unassignedCases) {
        super();
        setVipDisplayConfigurationDisplayDocuments(vipDisplayConfigurationDisplayDocuments);
        setVipDisplayConfigurationCourtRooms(vipDisplayConfigurationCourtRooms);
        this.unassignedCases = unassignedCases;
    }

    /**
     * Get VIPDisplayConfigurationDisplayDocument[] array.
     */
    public VipDisplayConfigurationDisplayDocument[] getVipDisplayConfigurationDisplayDocuments() {
        return vipDisplayConfigurationDisplayDocuments.clone();
    }
    
    private void setVipDisplayConfigurationDisplayDocuments(VipDisplayConfigurationDisplayDocument... 
        vipDisplayConfigurationDisplayDocuments) {
        this.vipDisplayConfigurationDisplayDocuments = vipDisplayConfigurationDisplayDocuments;
    }
    
    /**
     * Get VIPDisplayConfigurationCourtRoom[] array.
     */
    public VipDisplayConfigurationCourtRoom[] getVipDisplayConfigurationCourtRooms() {
        return vipDisplayConfigurationCourtRooms.clone();
    }
    
    private void setVipDisplayConfigurationCourtRooms(VipDisplayConfigurationCourtRoom... 
        vipDisplayConfigurationCourtRooms) {
        this.vipDisplayConfigurationCourtRooms = vipDisplayConfigurationCourtRooms;
    }

    /**
     * Get unassingedCases boolean.
     */
    public boolean isUnassignedCases() {
        return unassignedCases;
    }
}
