package uk.gov.hmcts.framework.client.delegate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;


/**
 * The class provides decoration to log the invocations.
 * 
 * @author pznwc5
 */
public abstract class DecoratingHandler extends XhibitHandler implements Cloneable {
    protected static final Logger LOG = LoggerFactory.getLogger(DecoratingHandler.class);
    /**
     * Next handler in the chain.
     */
    protected DecoratingHandler next;

    protected CsBusinessDelegateHandler target;

    /**
     * Gets the delegate information.
     */
    protected CsBusinessDelegateInfo getDelegateInfo() {
        if (target != null) {
            return target.getDelegateInfo();
        } else {
            return next.getDelegateInfo();
        }
    }

    /**
     * Sets the next invocation handler.
     * 
     * @param next DecoratingHandler
     */
    public void setNextHandler(DecoratingHandler next) {
        this.next = next;
    }

    /**
     * Set the target.
     * 
     * @param target CsBusinessDelegateHandler
     */
    public void setTarget(CsBusinessDelegateHandler target) {
        if (next != null) {
            next.setTarget(target);
        } else {
            this.target = target;
        }
    }

    /**
     * Invokes the next handler.
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        LOG.debug("Invoking decorator: {}", getClass());
        if (target != null) {
            return target.invoke(proxy, method, args);
        }
        if (next == null) {
            throw new IllegalStateException("Next handler not set");
        }
        return next.invoke(proxy, method, args);
    }

    /**
     * Clones the object.
     * 
     * @return DecoratingHandler
     */
    public DecoratingHandler deepCopy() {
        try {
            DecoratingHandler clone = (DecoratingHandler) clone();
            if (clone.next != null) {
                clone.setNextHandler(next.deepCopy());
            }
            return clone;
        } catch (CloneNotSupportedException ex) {
            // This should never happen
            return null;
        }
    }

}
