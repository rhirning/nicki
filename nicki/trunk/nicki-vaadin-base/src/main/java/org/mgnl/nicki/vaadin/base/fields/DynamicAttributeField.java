package org.mgnl.nicki.vaadin.base.fields;

import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectValueChangeListener;

import com.vaadin.ui.Component;

public interface DynamicAttributeField {

	Component getComponent(boolean readOnly);

	void init(String attributeName, DynamicObject dynamicObject,
			DynamicObjectValueChangeListener objectListener);
}
