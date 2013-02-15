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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicAttribute;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicReference;
import org.mgnl.nicki.core.context.NickiContext;

@SuppressWarnings("serial")
public class DataModel implements Serializable {

	private Map<String, DynamicAttribute> attributes = new HashMap<String, DynamicAttribute>();
	private List<String> objectClasses = new ArrayList<String>();
	private List<String> additionalObjectClasses = new ArrayList<String>();
	private String namingAttribute = null;

	private List<DynamicAttribute> mandatoryAttributes = null;
	private List<DynamicAttribute> optionalAttributes = null;
	private List<DynamicAttribute> listOptionalAttributes = null;
	private List<DynamicAttribute> foreignKeys = null;
	private List<DynamicAttribute> listForeignKeys = null;

	private Map<String, Class<? extends DynamicObject>> children = new HashMap<String, Class<? extends DynamicObject>>();
	private Map<String, DynamicReference> references = new HashMap<String, DynamicReference>();
	

	public enum ATTRIBUTE_TYPE {MANDATORY, OPTIONAL, OPTIONAL_LIST, FOREIGN_KEY, FOREIGN_KEY_LIST, STRUCTURED, UNDEFINED};
	
	public List<String> getObjectClasses() {
		return objectClasses;
	}
	public String getNamingAttribute() {
		return namingAttribute;
	}
	
	public  List<DynamicAttribute> getMandatoryAttributes() {
		if (this.mandatoryAttributes == null) {
			this.mandatoryAttributes = new ArrayList<DynamicAttribute>();
			for (DynamicAttribute dynAttribute : this.attributes.values()) {
				if (dynAttribute.isMandatory()) {
					this.mandatoryAttributes.add(dynAttribute);
				}
			}
		}
		return mandatoryAttributes;
	}
	public List<DynamicAttribute> getOptionalAttributes() {
		if (this.optionalAttributes == null) {
			this.optionalAttributes = new ArrayList<DynamicAttribute>();
			for (DynamicAttribute dynAttribute : this.attributes.values()) {
				if (!dynAttribute.isMandatory()) {
					this.optionalAttributes.add(dynAttribute);
				}
			}
		}
		return optionalAttributes;
	}
	public List<DynamicAttribute> getListOptionalAttributes() {
		if (this.listOptionalAttributes == null) {
			this.listOptionalAttributes = new ArrayList<DynamicAttribute>();
			for (DynamicAttribute dynAttribute : this.attributes.values()) {
				if (dynAttribute.isMultiple()) {
					this.listOptionalAttributes.add(dynAttribute);
				}
			}
		}
		return listOptionalAttributes;
	}
	public List<DynamicAttribute> getForeignKeys() {
		if (this.foreignKeys == null) {
			this.foreignKeys = new ArrayList<DynamicAttribute>();
			for (DynamicAttribute dynAttribute : this.attributes.values()) {
				if (dynAttribute.isForeignKey() && !dynAttribute.isMultiple()) {
					this.foreignKeys.add(dynAttribute);
				}
			}
		}
		return foreignKeys;
	}
	public List<DynamicAttribute> getListForeignKeys() {
		if (this.listForeignKeys == null) {
			this.listForeignKeys = new ArrayList<DynamicAttribute>();
			for (DynamicAttribute dynAttribute : this.attributes.values()) {
				if (dynAttribute.isForeignKey() && dynAttribute.isMultiple()) {
					this.listForeignKeys.add(dynAttribute);
				}
			}
		}
		return listForeignKeys;
	}
	public Map<String, Class<? extends DynamicObject>> getChildren() {
		return children;
	}
	public Map<String, DynamicReference> getReferences() {
		return references;
	}

	public void addChild(String attribute, Class<? extends DynamicObject> filter) {
		children.put(attribute, filter);		
	}
	public void addObjectClasses(String objectClass) {
		objectClasses.add(objectClass);
	}
	public void addAdditionalObjectClasses(String objectClass) {
		additionalObjectClasses.add(objectClass);
	}

	public Attributes getLdapAttributes(DynamicObject dynamicObject) {
		Attributes myAttrs = new BasicAttributes(true);
		addLdapAttributes(myAttrs, dynamicObject, true);
		return myAttrs;
	}
	
	public void addLdapAttributes(Attributes myAttrs, DynamicObject dynamicObject, boolean nullable) {

		// single attributes (except namingAttribute)
		for (DynamicAttribute dynAttribute : getAttributes().values()) {
			if (!dynAttribute.isNaming()&& !dynAttribute.isMultiple() && !dynAttribute.isReadonly()) {
				String value = StringUtils.trimToNull(dynamicObject.getAttribute(dynAttribute.getName()));
				if (nullable || value != null) {
					Attribute attribute = new BasicAttribute(dynAttribute.getExternalName(), value);
					myAttrs.put(attribute);
				}
			}
		}
		
		// multi attributes
		for (DynamicAttribute dynAttribute : getAttributes().values()) {
			if (dynAttribute.isMultiple() && !dynAttribute.isReadonly()) {
				Attribute attribute = new BasicAttribute(dynAttribute.getExternalName());
				@SuppressWarnings("unchecked")
				List<String> list = (List<String>) dynamicObject.get(dynAttribute.getName());
				if (list != null) {
					for (String value : list) {
						if (StringUtils.isNotEmpty(value)) {
							attribute.add(value);
						}
					}
				}
				if (nullable || attribute.size() > 0) {
					myAttrs.put(attribute);
				}
			}
		}
		
	}

	public Map<DynamicAttribute, Object> getNonMandatoryAttributes(DynamicObject dynamicObject) {
		Map<DynamicAttribute, Object> map = new HashMap<DynamicAttribute, Object>();
		// single attributes (except namingAttribute)
		for (DynamicAttribute dynAttribute : getAttributes().values()) {
			if (!dynAttribute.isNaming()&& !dynAttribute.isMultiple()
					&& !dynAttribute.isReadonly() && !dynAttribute.isMandatory()) {
				String value = StringUtils.trimToNull(dynamicObject.getAttribute(dynAttribute.getName()));
				if (value != null) {
					map.put(dynAttribute, value);
				}
			}
		}
		
		// multi attributes
		for (DynamicAttribute dynAttribute : getAttributes().values()) {
			if (dynAttribute.isMultiple() && !dynAttribute.isReadonly()
					&& !dynAttribute.isMandatory()) {
				@SuppressWarnings("unchecked")
				List<String> list = (List<String>) dynamicObject.get(dynAttribute.getName());
				if (list != null && list.size() > 0) {
					map.put(dynAttribute, list);
				}
			}
		}
		
		return map;
	}

	
	public String getNamingLdapAttribute() {
		return getAttributes().get(getNamingAttribute()).getExternalName();
	}
	
	public DynamicAttribute getDynamicAttribute(String name) {
		return getAttributes().get(name);
	}
	
	public boolean childrenAllowed() {
		try {
			return (getChildren().keySet().size() > 0);
		} catch (Exception e) {
			return false;
		}
	}
	
	public void addAttribute(DynamicAttribute dynAttribute) {
		String attributeName =  dynAttribute.getName();
		this.attributes.put(attributeName, dynAttribute);
		if (dynAttribute.isNaming()) {
			this.namingAttribute = attributeName;
		}
	}
	public void init(NickiContext context, DynamicObject dynamicObject, ContextSearchResult rs) {
		for (DynamicAttribute dynamicAttribute : this.attributes.values()) {
			dynamicAttribute.init(context, dynamicObject, rs);
		}
	}
	public boolean isComplete(DynamicObject dynamicObject) {
		boolean complete = true;
		// check naming attribute
		complete &= dynamicObject.attributeIsNotEmpty(getNamingAttribute());
		
		// check mandatory attributes
		for (DynamicAttribute dynAttribute : getMandatoryAttributes()) {
			complete &= dynamicObject.attributeIsNotEmpty(dynAttribute.getName());
		}
		
		/**
		 *  check path
		 *  - parent must exist
		 *  - new: unique
		**/
		// path must exist
		complete &= (StringUtils.isNotEmpty(dynamicObject.getPath()));

		// parent must exist
		complete &= dynamicObject.getContext().isExist(dynamicObject.getParentPath());
		
		// new: must be unique
		if (dynamicObject.isNew()) {
			complete &= !dynamicObject.getContext().isExist(dynamicObject.getPath());			
		}
		return complete;
	}
	
	public Map<String, DynamicAttribute> getAttributes() {
		return attributes;
	}
	public List<String> getAdditionalObjectClasses() {
		return additionalObjectClasses;
	}

}
