package org.mgnl.nicki.vaadin.base.rights;

import org.mgnl.nicki.rights.core.RightAttribute;

import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;

public class TextComponent implements AttributeComponent {

	@Override
	public Component getInstance(RightAttribute rightAttribute) {
		TextField field = new TextField(rightAttribute.getLabel());
		field.setWidth("200px");
		return field;
	}

}
