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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AttributeComponentFactory {
	private static final Logger LOG = LoggerFactory.getLogger(AttributeComponentFactory.class);

	@SuppressWarnings("unchecked")
	static public <T extends Object> BaseAttributeComponent<T> getAttributeComponent(String type) {
		try {
			Component component = Component.valueOf(type);
			if (component == null) {
				component = Component.DEFAULT;
			}
			return (BaseAttributeComponent<T>) component.getInstance();
		} catch (Exception e) {
			LOG.error("Error", e);
		}
		return null;
	}

}
