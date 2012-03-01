/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
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
