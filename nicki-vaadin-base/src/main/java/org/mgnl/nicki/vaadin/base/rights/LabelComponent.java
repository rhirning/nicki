package org.mgnl.nicki.vaadin.base.rights;

import org.mgnl.nicki.rights.core.RightAttribute;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

public class LabelComponent implements AttributeComponent {

	@Override
	public Component getInstance(RightAttribute rightAttribute) {
		Label label = new Label(rightAttribute.getLabel());
		return label;
	}

}
