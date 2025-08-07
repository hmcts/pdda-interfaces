package uk.gov.hmcts.pdda.web.publicdisplay.error;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * <p/>
 * To change the template for this generated type comment go to Window - Preferences - Java - Code
 * Generation - Code and Comments.
 * 
 * @author pznwc5
 */
@SuppressWarnings("PMD.AvoidSynchronizedStatement")
public final class ErrorGatherer implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_SIZE = 100;

    private static ErrorGatherer instance = new ErrorGatherer();

    private final List<Object> errors = new LinkedList<>();

    private int size = DEFAULT_SIZE;

    private ErrorGatherer() {
        // Private constructor
    }

    public static ErrorGatherer getInstance() {
        return instance;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void addError(ProcessingError error) {
        synchronized (this) {
            if (errors.size() == size) {
                ((LinkedList<Object>) errors).removeLast();
            }
            ((LinkedList<Object>) errors).addFirst(error);
        }
    }

    public ProcessingError[] getErrors() {
        synchronized (this) {
            return errors.toArray(new ProcessingError[0]);
        }
    }

    public void flush() {
        synchronized (this) {
            errors.clear();
        }
    }

}
