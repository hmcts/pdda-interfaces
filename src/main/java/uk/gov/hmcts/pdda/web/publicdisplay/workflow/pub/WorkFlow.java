package uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub;

/**
 * <p>
 * Title: Workflow Interface.
 * </p>
 * 
 * <p>
 * Description: The interface that must be implemented when creating a workflow for processing
 * public display events
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
 * @version $Revision: 1.2 $
 */
@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface WorkFlow {
    void process();
}
