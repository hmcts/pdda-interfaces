package uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.impl;

import uk.gov.hmcts.pdda.web.publicdisplay.types.RenderChanges;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.Createable;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.Removeable;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.WorkFlow;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.WorkFlowContext;

/**
 * <p>
 * Title: Basic Work Flow.
 * </p>
 * 
 * <p>
 * Description: This class declares some helper methods that can be re-used for processing
 * RenderChanges
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
public abstract class AbstractBasicWorkFlow implements WorkFlow {
    private final WorkFlowContext context;

    private RenderChanges renderChanges;

    /**
     * Creates a new AbstractBasicWorkFlow object.
     * 
     * @param context WorkFlowContext
     * @param renderChanges RenderChanges
     */
    protected AbstractBasicWorkFlow(WorkFlowContext context, RenderChanges renderChanges) {
        this.context = context;
        this.renderChanges = renderChanges;
    }

    /**
     * Creates a new AbstractBasicWorkFlow object.
     * 
     * @param context WorkFlowContext
     */
    protected AbstractBasicWorkFlow(WorkFlowContext context) {
        this.context = context;
    }

    /**
     * Get context.
     * 
     * @return context:
     */
    public WorkFlowContext getContext() {
        return context;
    }

    /**
     * Set renderChanges.
     * 
     * @param renderChanges RenderChanges
     */
    public void setRenderChanges(RenderChanges renderChanges) {
        this.renderChanges = renderChanges;
    }

    /**
     * Get render changes.
     * 
     * @return renderChanges RenderChanges
     */
    public RenderChanges getRenderChanges() {
        return renderChanges;
    }

    /**
     * createAll.
     * 
     * @param createables CreateableArray
     */
    protected void createAll(Createable... createables) {
        for (Createable creatable : createables) {
            creatable.create();
        }
    }

    /**
     * This method processes the render changes class. It extracts the documents to start rendering.
     * If the array is not empty, it requests the data for document, requests that it is rendered
     * and stored.
     * 
     * 
     * @pre renderChanges != null
     * @pre renderChanges.getDocumentsToStartRendering() != null
     * @pre forall Object dd1 inarray renderChanges.getDocumentsToStartRendering() | dd1 != null
     * @pre forall Object dd2 inarray renderChanges.getDocumentsToStopRendering() | dd2 != null
     * 
     */
    protected void processDisplayDocumentChanges() {
        createAll(renderChanges.getDocumentsToStartRendering());
        removeAll(renderChanges.getDocumentsToStopRendering());
    }

    /**
     * This method processes the render changes class. It extracts the rotation sets to be rendered.
     * If the array is not empty, it requests that the rotation set is rendered and stored.
     * 
     * 
     * @pre renderChanges != null
     * @pre renderChanges.getDisplayRotationSetsToStartRendering() != null
     * @pre forall Object rs1 inarray renderChanges.getDisplayRotationSetsToStartRendering() | rs1
     *      != null
     * @pre forall Object rs2 inarray renderChanges.getDisplayRotationSetsToStopRendering() | rs2 !=
     *      null
     */
    protected void processRotationSetChanges() {
        createAll(renderChanges.getDisplayRotationSetsToStartRendering());
        removeAll(renderChanges.getDisplayRotationSetsToStopRendering());
    }

    /**
     * RemoveAll.
     * 
     * @param removeables RemoveableArray
     */
    protected void removeAll(Removeable... removeables) {
        for (Removeable removeable : removeables) {
            removeable.remove();
        }
    }
}
