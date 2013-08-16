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
package org.mgnl.nicki.shop.attributes;

import org.mgnl.nicki.core.util.Classes;


public class AttributeComponentFactory {
	public enum Component {
		DATE("org.mgnl.nicki.shop.attributes.DateComponent"),
		TEXT("org.mgnl.nicki.shop.attributes.TextComponent"),
		CHECKBOX("org.mgnl.nicki.shop.attributes.CheckboxComponent"),
		SELECT("org.mgnl.nicki.shop.attributes.SelectComponent"),
		FREESELECT("org.mgnl.nicki.shop.attributes.FreeSelectComponent"),
		STATIC("org.mgnl.nicki.shop.attributes.LabelComponent"),
		DEFAULT("org.mgnl.nicki.shop.attributes.LabelComponent")
		;
		
		private String className;

		Component(String className) {
			this.className = className;
		}

		@SuppressWarnings("unchecked")
		public <T extends Object> AttributeComponent<T> getInstance() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
			return (AttributeComponent<T>) Classes.newInstance(className);
		}
	}


	static public <T extends Object> AttributeComponent<T> getAttributeComponent(String type) {
		try {
			Component component = Component.valueOf(type);
			if (component == null) {
				component = Component.DEFAULT;
			}
			return component.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
