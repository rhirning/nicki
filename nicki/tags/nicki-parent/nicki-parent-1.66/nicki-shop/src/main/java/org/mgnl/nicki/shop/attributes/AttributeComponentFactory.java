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

import java.util.HashMap;
import java.util.Map;

import org.mgnl.nicki.core.util.Classes;



public class AttributeComponentFactory {
	public static final String DEFAULT = "default";
	protected static Map<String, String> attributeComponents 
		= new HashMap<String, String>();
	static {
		attributeComponents.put("date", "org.mgnl.nicki.shop.attributes.DateComponent");
		attributeComponents.put("text", "org.mgnl.nicki.shop.attributes.TextComponent");
		attributeComponents.put("checkbox", "org.mgnl.nicki.shop.attributes.CheckboxComponent");
		attributeComponents.put("select", "org.mgnl.nicki.shop.attributes.SelectComponent");
		attributeComponents.put("freeselect", "org.mgnl.nicki.shop.attributes.FreeSelectComponent");
		attributeComponents.put("static", "org.mgnl.nicki.shop.attributes.LabelComponent");
		attributeComponents.put(DEFAULT, "org.mgnl.nicki.shop.attributes.LabelComponent");
	}
	
	static public AttributeComponent getAttributeComponent(String type) {
		try {
			if (attributeComponents.containsKey(type)) {
				return (AttributeComponent) Classes.newInstance(attributeComponents.get(type));
			} else {
				return (AttributeComponent) Classes.newInstance(attributeComponents.get(DEFAULT));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}