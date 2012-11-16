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
package org.mgnl.nicki.core.objects;

import java.util.List;
import java.util.Map;

public interface DataModel  {

	public enum ATTRIBUTE_TYPE {MANDATORY, OPTIONAL, OPTIONAL_LIST, FOREIGN_KEY, FOREIGN_KEY_LIST, STRUCTURED, UNDEFINED};
	
	<T extends DynamicAttribute> List<T> getMandatoryAttributes();
	<T extends DynamicAttribute> List<T> getOptionalAttributes();
	<T extends DynamicAttribute> List<T> getListOptionalAttributes();
	<T extends DynamicAttribute> List<T> getForeignKeys();
	<T extends DynamicAttribute> List<T> getListForeignKeys();
	Map<String, String> getChildren();
	void addChild(String attribute, String filter);
	void addObjectClasses(String objectClass);
	void addAdditionalObjectClasses(String objectClass);
	<T extends DynamicAttribute> Map<T, Object> getNonMandatoryAttributes(DynamicObject dynamicObject);	
	String getNamingLdapAttribute();
	DynamicAttribute getDynamicAttribute(String name);
	boolean childrenAllowed();
	boolean isComplete(DynamicObject dynamicObject);
	<T extends DynamicAttribute> Map<String, T> getAttributes();
	List<String> getAdditionalObjectClasses();
	String getObjectClassFilter();
	void addAttribute(DynamicAttribute dynAttribute);
}
