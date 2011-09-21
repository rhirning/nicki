package org.mgnl.nicki.vaadin.base.data;

import com.vaadin.ui.DateField;

public class DateHelper {

	public static void init(DateField field) {
		field.setResolution(DateField.RESOLUTION_DAY);
		field.setDateFormat("dd.MM.yyyy");
	}

}
