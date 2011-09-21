package org.mgnl.nicki.vaadin.base.editor;

import java.util.List;

import org.mgnl.nicki.ldap.objects.DynamicObject;

import com.vaadin.ui.Component;

public interface DynamicObjectValueChangeListener {

	void valueChange(DynamicObject dynamicObject, String name, List<Object> values);

	void valueChange(DynamicObject dynamicObject, String attributeName, String value);

	boolean acceptAttribute(String name);

	void close(Component component);

	void refresh(DynamicObject dynamicObject);

}
