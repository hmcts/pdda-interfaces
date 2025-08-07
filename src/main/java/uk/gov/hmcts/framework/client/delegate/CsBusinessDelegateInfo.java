package uk.gov.hmcts.framework.client.delegate;

/**
 * <p>
 * Title: CsBusinessDelegateInfo.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Framework Team
 * @version 1.0
 */
public class CsBusinessDelegateInfo {
    private final Class<?> delegateClass;

    private final Class<?> homeClass;

    public CsBusinessDelegateInfo(Class<?> delegateClass, Class<?> homeClass) {
        this.delegateClass = delegateClass;
        this.homeClass = homeClass;
    }

    public Class<?> getDelegateClass() {
        return delegateClass;
    }

    public Class<?> getHomeClass() {
        return homeClass;
    }
}
