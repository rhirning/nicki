/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.data;

import com.vaadin.ui.DateField;

public class DateHelper {

	public static void init(DateField field) {
		field.setResolution(DateField.RESOLUTION_DAY);
		field.setDateFormat("dd.MM.yyyy");
	}

}
