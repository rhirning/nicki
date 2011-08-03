package org.mgnl.nicki.vaadin.base.validation;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.editor.ValidationException;

import com.vaadin.ui.Field;

public class Validation {
	public static void notNull(DynamicObject dynamicObject, String error) throws ValidationException {
		if (dynamicObject == null) {
			throw new ValidationException(error);
		}
	}

	public static void notNull(Date date, String error) throws ValidationException {
		if (date == null) {
			throw new ValidationException(error);
		}
	}

	public static void dateInFuture(Date date, String error) throws ValidationException {
		if (date == null || date.compareTo(new Date()) < 0) {
			throw new ValidationException(error);
		}
	}

	public static void dateInPast(Date date, String error) throws ValidationException {
		if (date == null || date.compareTo(new Date()) > 0) {
			throw new ValidationException(error);
		}
	}

	public static void notEmpty(Field component, String error) throws ValidationException {
		if (StringUtils.isEmpty((String) component.getValue())) {
			throw new ValidationException(error);
		}
	}


}
