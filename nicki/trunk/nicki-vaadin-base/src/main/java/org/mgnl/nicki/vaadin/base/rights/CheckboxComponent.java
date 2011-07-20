package org.mgnl.nicki.vaadin.base.rights;

import org.mgnl.nicki.rights.core.RightAttribute;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;

public class CheckboxComponent implements AttributeComponent {

	@Override
	public Component getInstance(RightAttribute rightAttribute) {
		CheckBox field = new CheckBox(rightAttribute.getLabel());
		field.setWidth("200px");
		return field;
	}

}
