/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.shop.attributes;

import java.util.HashMap;
import java.util.Map;



public class AttributeComponentFactory {
	public static final String DEFAULT = "default";
	protected static Map<String, String> attributeComponents 
		= new HashMap<String, String>();
	static {
		attributeComponents.put("date", "org.mgnl.nicki.vaadin.base.shop.attributes.DateComponent");
		attributeComponents.put("text", "org.mgnl.nicki.vaadin.base.shop.attributes.TextComponent");
		attributeComponents.put("checkbox", "org.mgnl.nicki.vaadin.base.shop.attributes.CheckboxComponent");
		attributeComponents.put("select", "org.mgnl.nicki.vaadin.base.shop.attributes.SelectComponent");
		attributeComponents.put("freeselect", "org.mgnl.nicki.vaadin.base.shop.attributes.FreeSelectComponent");
		attributeComponents.put("static", "org.mgnl.nicki.vaadin.base.shop.attributes.LabelComponent");
		attributeComponents.put(DEFAULT, "org.mgnl.nicki.vaadin.base.shop.attributes.LabelComponent");
	}
	
	static public AttributeComponent getAttributeComponent(String type) {
		try {
			if (attributeComponents.containsKey(type)) {
				return (AttributeComponent) Class.forName(attributeComponents.get(type)).newInstance();
			} else {
				return (AttributeComponent) Class.forName(attributeComponents.get(DEFAULT)).newInstance();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
