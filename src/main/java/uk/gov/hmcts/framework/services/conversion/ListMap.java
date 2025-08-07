package uk.gov.hmcts.framework.services.conversion;

import uk.gov.hmcts.framework.business.vos.CsValueObject;
import uk.gov.hmcts.framework.exception.CsUnrecoverableException;
import uk.gov.hmcts.framework.services.CsServices;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("PMD.LooseCoupling")
public class ListMap extends BasicNumericMap {

    public ListMap(ValueConverter converter) {
        super(converter);
    }
   
    /**
     * Processes arraylists of vos and creates a map for each. Stores the created maps in the
     * innerMap HashMap. These are accessed via getInnerMap()
     */
    public void putList(String name, List<?> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        // get the first item and check its a value
        Object obj = list.get(0);
        if (!(obj instanceof CsValueObject)) {
            return;
        }
        CsValueObject value;
        StringMap innerMap;
        ArrayList<Object> innerMaps = new ArrayList<>();
        Iterator<?> itr = list.iterator();
        while (itr != null && itr.hasNext()) {
            value = (CsValueObject) itr.next();
            innerMap = getStringMap();
            innerMap.copyValueToMap(value);
            innerMaps.add(innerMap);
        }
        getMap().put(name, innerMaps);
    }
    
    private StringMap getStringMap() {
        return new StringMap(getConverter());
    }
    
    private void copyListToMap(String propertyName, Method method, Object bean)
        throws IllegalAccessException, InvocationTargetException {
        Object[] args = {};
        List<?> list = (ArrayList<?>) method.invoke(bean, args);
        putList(propertyName, list);
    }

    private boolean isTypeList(Class<?> type) {
        return type == List.class;
    }
    
    @Override
    protected void copyTypeToMap(Class<?> type, String propertyName, Method method, Object bean) {
        try {
            if (isTypeList(type)) {
                copyListToMap(propertyName, method, bean);
            } else {
                super.copyTypeToMap(type, propertyName, method, bean);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            CsUnrecoverableException ex = new CsUnrecoverableException(e);
            CsServices.getDefaultErrorHandler().handleError(ex, ListMap.class);
            throw ex;
        }
    }
}
