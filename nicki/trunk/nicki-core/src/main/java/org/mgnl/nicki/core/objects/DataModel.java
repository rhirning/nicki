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

import javax.naming.directory.Attributes;

import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.context.NickiContext;

public interface DataModel  {

	public enum ATTRIBUTE_TYPE {MANDATORY, OPTIONAL, OPTIONAL_LIST, FOREIGN_KEY, FOREIGN_KEY_LIST, STRUCTURED, UNDEFINED};
	
	List<String> getObjectClasses();
	String getNamingAttribute();
	List<DynamicAttribute> getMandatoryAttributes();
	List<DynamicAttribute> getOptionalAttributes();
	List<DynamicAttribute> getListOptionalAttributes();
	List<DynamicAttribute> getForeignKeys();
	List<DynamicAttribute> getListForeignKeys();
	Map<String, String> getChildren();
// TODO	Map<String, DynamicReference> getReferences();
	void addChild(String attribute, String filter);
	void addObjectClasses(String objectClass);
	void addAdditionalObjectClasses(String objectClass);
	Attributes getLdapAttributesForCreate(DynamicObject dynamicObject);
	// objectClass + naming
	void addBasicLdapAttributes(Attributes myAttrs, DynamicObject dynamicObject);
	Attributes getLdapAttributes(DynamicObject dynamicObject);
	void addLdapAttributes(Attributes myAttrs, DynamicObject dynamicObject, boolean nullable);
	Map<DynamicAttribute, Object> getNonMandatoryAttributes(DynamicObject dynamicObject);	
	String getNamingLdapAttribute();
	DynamicAttribute getDynamicAttribute(String name);
	boolean childrenAllowed();
	void addAttribute(DynamicAttribute dynAttribute);
	void init(NickiContext context, DynamicObject dynamicObject, ContextSearchResult rs);
	boolean isComplete(DynamicObject dynamicObject);
	Map<String, DynamicAttribute> getAttributes();
	List<String> getAdditionalObjectClasses();
	String getObjectClassFilter();
}
