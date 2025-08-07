package uk.gov.hmcts.pdda.common.publicdisplay.data;

import java.util.Date;

/**
 * <p>
 * Title: DataContext.
 * </p>
 * 
 * <p>
 * Description:
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
public class DataContext {
    public Date date;

    /**
     * Creates a new DataContext object.
     * 
     * @param date the date.
     */
    public DataContext(Date date) {
        this.date = date;
    }

    /**
     * Sets the date.
     * 
     * @param date the date.
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Returns the date.
     * 
     * @return the date.
     */
    public Date getDate() {
        return date;
    }
}
