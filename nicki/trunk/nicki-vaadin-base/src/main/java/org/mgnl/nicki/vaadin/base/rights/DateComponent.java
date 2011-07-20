package org.mgnl.nicki.vaadin.base.rights;

import org.mgnl.nicki.rights.core.RightAttribute;
import org.mgnl.nicki.vaadin.base.data.DateHelper;

import com.vaadin.ui.Component;
import com.vaadin.ui.PopupDateField;


public class DateComponent implements AttributeComponent {
	@Override
	public Component getInstance(RightAttribute rightAttribute) {
		PopupDateField field = new PopupDateField(rightAttribute.getLabel());
		DateHelper.init(field);
		return field;
	}

}
