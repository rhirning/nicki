/**
 * Copyright (c) 2003-2015 Dr. Ralf Hirning
 * All rights reserved.
 *  
 * This program is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 * 
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU Public License v3.0
 * which is available at
 * http://www.gnu.org/licenses/gpl.html
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 * 
 * Any modifications to this file must keep this entire header
 * intact.
*/
package org.mgnl.nicki.shop.base.attributes;

import org.mgnl.nicki.core.util.Classes;

public enum Component {
	DATE("org.mgnl.nicki.shop.attributes.DateComponent"),
	TEXT("org.mgnl.nicki.shop.attributes.TextComponent"),
	CHECKBOX("org.mgnl.nicki.shop.attributes.CheckboxComponent"),
	SELECT("org.mgnl.nicki.shop.attributes.SelectComponent"),
	FREESELECT("org.mgnl.nicki.shop.attributes.FreeSelectComponent"),
	STATIC("org.mgnl.nicki.shop.attributes.LabelComponent"),
	GENERIC("org.mgnl.nicki.shop.attributes.GenericComponent"),
	DEFAULT("org.mgnl.nicki.shop.attributes.LabelComponent");

	private String className;

	Component(String className) {
		this.className = className;
	}

	public Object getInstance() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		return Classes.newInstance(className);
	}
}
