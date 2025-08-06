package uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub;

/**

 * Title: Workflow Interface.

 * Description: The interface that must be implemented when creating a workflow for processing
 * public display events

 * Copyright: Copyright (c) 2003

 * Company: Electronic Data Systems

 * @author Neil Ellis
 * @version $Revision: 1.2 $
 */
@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface WorkFlow {
    void process();
}
