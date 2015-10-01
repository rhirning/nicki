package org.mgnl.nicki.vaadin.base.search;

import org.mgnl.nicki.core.objects.DynamicObject;

public interface DynamicObjectSearcher<T extends DynamicObject> {

	void setDynamicObject(Class<T> clazz, T dynamicObject);
}
