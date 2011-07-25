package org.mgnl.nicki.vaadin.base.fields;

import com.vaadin.ui.Component;

public interface DynamicAttributeField {

	Component getComponent(boolean readOnly);
}
