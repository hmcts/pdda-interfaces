package uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub;

import uk.gov.hmcts.pdda.common.publicdisplay.data.DataContext;
import uk.gov.hmcts.pdda.web.publicdisplay.configuration.DisplayConfigurationReader;

/**
 * <p/>
 * Title: WorkFlowContext.
 * </p>
 * <p/>
 * <p/>
 * Description:
 * </p>
 * <p/>
 * <p/>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p/>
 * <p/>
 * Company: Electronic Data Systems
 * </p>
 * 
 * @author Neil Ellis
 * @version $Revision: 1.5 $
 */
public final class WorkFlowContext {
    private DataContext dataContext;

    private final DisplayConfigurationReader displayConfigurationReader;

    private WorkFlowContext(final DisplayConfigurationReader displayConfigurationReader) {
        this.displayConfigurationReader = displayConfigurationReader;
    }

    private WorkFlowContext() {
        this.displayConfigurationReader = DisplayConfigurationReader.getInstance();
    }

    /**
     * newInstance.
     * 
     * @return WorkFlowContext
     */
    public static WorkFlowContext newInstance() {
        return new WorkFlowContext();
    }

    /**
     * newInstance.
     * 
     * @param displayConfigurationReader TODO:
     * 
     * @return WorkFlowContext
     */
    public static WorkFlowContext newInstance(
        final DisplayConfigurationReader displayConfigurationReader) {
        return new WorkFlowContext(displayConfigurationReader);
    }

    /**
     * setDataContext.
     * 
     * @param dataContext DataContext
     */
    public void setDataContext(DataContext dataContext) {
        this.dataContext = dataContext;
    }

    /**
     * getDataContext.
     * 
     * @return DataContext
     */
    public DataContext getDataContext() {
        return dataContext;
    }

    /**
     * getDisplayConfigurationReader.
     * 
     * @return DisplayConfigurationReader
     */
    public DisplayConfigurationReader getDisplayConfigurationReader() {
        return displayConfigurationReader;
    }
}
