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
package org.mgnl.nicki.ldap.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DataModel;
import org.mgnl.nicki.core.objects.DynamicAttribute;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.ldap.helper.LdapHelper;
import org.mgnl.nicki.ldap.helper.LdapHelper.LOGIC;

@SuppressWarnings("serial")
public class LdapDataModel implements DataModel, Serializable {

	private Map<String, DynamicLdapAttribute> attributes = new HashMap<String, DynamicLdapAttribute>();
	private List<String> objectClasses = new ArrayList<String>();
	private List<String> additionalObjectClasses = new ArrayList<String>();
	private String namingAttribute = null;

	private List<DynamicLdapAttribute> mandatoryAttributes = null;
	private List<DynamicAttribute> optionalAttributes = null;
	private List<DynamicAttribute> listOptionalAttributes = null;
	private List<DynamicAttribute> foreignKeys = null;
	private List<DynamicAttribute> listForeignKeys = null;

	private Map<String, String> children = new HashMap<String, String>();
	private Map<String, DynamicReference> references = new HashMap<String, DynamicReference>();
	

	public enum ATTRIBUTE_TYPE {MANDATORY, OPTIONAL, OPTIONAL_LIST, FOREIGN_KEY, FOREIGN_KEY_LIST, STRUCTURED, UNDEFINED};
	
	public List<String> getObjectClasses() {
		return objectClasses;
	}
	public String getNamingAttribute() {
		return namingAttribute;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public  List<DynamicLdapAttribute> getMandatoryAttributes() {
		if (this.mandatoryAttributes == null) {
			this.mandatoryAttributes = new ArrayList<DynamicLdapAttribute>();
			for (Iterator<DynamicLdapAttribute> iterator = this.attributes.values().iterator(); iterator.hasNext();) {
				DynamicLdapAttribute dynAttribute  = iterator.next();
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
			for (Iterator<DynamicLdapAttribute> iterator = this.attributes.values().iterator(); iterator.hasNext();) {
				DynamicAttribute dynAttribute  = iterator.next();
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
			for (Iterator<DynamicLdapAttribute> iterator = this.attributes.values().iterator(); iterator.hasNext();) {
				DynamicAttribute dynAttribute  = iterator.next();
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
			for (Iterator<DynamicLdapAttribute> iterator = this.attributes.values().iterator(); iterator.hasNext();) {
				DynamicAttribute dynAttribute  = iterator.next();
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
			for (Iterator<DynamicLdapAttribute> iterator = this.attributes.values().iterator(); iterator.hasNext();) {
				DynamicAttribute dynAttribute  = iterator.next();
				if (dynAttribute.isForeignKey() && dynAttribute.isMultiple()) {
					this.listForeignKeys.add(dynAttribute);
				}
			}
		}
		return listForeignKeys;
	}
	public Map<String, String> getChildren() {
		return children;
	}
	public Map<String, DynamicReference> getReferences() {
		return references;
	}

	public void addChild(String attribute, String filter) {
		children.put(attribute, filter);		
	}
	public void addObjectClasses(String objectClass) {
		objectClasses.add(objectClass);
	}
	public void addAdditionalObjectClasses(String objectClass) {
		additionalObjectClasses.add(objectClass);
	}
	
	public Attributes getLdapAttributesForCreate(BaseLdapDynamicObject dynamicObject) {
		Attributes myAttrs = new BasicAttributes(true);
		addBasicLdapAttributes(myAttrs, dynamicObject);
		addLdapAttributes(myAttrs, dynamicObject, false);
		return myAttrs;
	}
	
	// objectClass + naming
	public void addBasicLdapAttributes(Attributes myAttrs, BaseLdapDynamicObject dynamicObject) {
		Attribute oc = new BasicAttribute("objectClass");
		for (Iterator<String> iterator = dynamicObject.getModel().getObjectClasses().iterator(); iterator.hasNext();) {
			String objectClass = iterator.next();
			oc.add(objectClass);
		}
		for (Iterator<String> iterator = dynamicObject.getModel().getAdditionalObjectClasses().iterator(); iterator.hasNext();) {
			String objectClass = iterator.next();
			oc.add(objectClass);
		}
		myAttrs.put(oc);
		for (Iterator<DynamicLdapAttribute> iterator = dynamicObject.getModel().getMandatoryAttributes().iterator(); iterator.hasNext();) {
			DynamicLdapAttribute dynAttribute =  iterator.next();
			if (dynAttribute.isNaming()) {
				myAttrs.put(dynAttribute.getLdapName(), dynamicObject.getAttribute(dynAttribute.getName()));
			}			
		}
	}

	public Attributes getLdapAttributes(DynamicObject dynamicObject) {
		Attributes myAttrs = new BasicAttributes(true);
		addLdapAttributes(myAttrs, dynamicObject, true);
		return myAttrs;
	}
	
	public void addLdapAttributes(Attributes myAttrs, DynamicObject dynamicObject, boolean nullable) {

		// single attributes (except namingAttribute)
		for (Iterator<DynamicLdapAttribute> iterator = getAttributes().values().iterator(); iterator.hasNext();) {
			DynamicLdapAttribute dynAttribute = iterator.next();
			if (!dynAttribute.isNaming()&& !dynAttribute.isMultiple() && !dynAttribute.isReadonly()) {
				String value = StringUtils.trimToNull(dynamicObject.getAttribute(dynAttribute.getName()));
				if (nullable || value != null) {
					Attribute attribute = new BasicAttribute(dynAttribute.getLdapName(), value);
					myAttrs.put(attribute);
				}
			}
		}
		
		// multi attributes
		for (Iterator<DynamicLdapAttribute> iterator = getAttributes().values().iterator(); iterator.hasNext();) {
			DynamicLdapAttribute dynAttribute = iterator.next();
			if (dynAttribute.isMultiple() && !dynAttribute.isReadonly()) {
				Attribute attribute = new BasicAttribute(dynAttribute.getLdapName());
				@SuppressWarnings("unchecked")
				List<String> list = (List<String>) dynamicObject.get(dynAttribute.getName());
				if (list != null) {
					for (Iterator<String> iterator2 = list.iterator(); iterator2.hasNext();) {
						String value = iterator2.next();
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
		for (Iterator<DynamicLdapAttribute> iterator = getAttributes().values().iterator(); iterator.hasNext();) {
			DynamicLdapAttribute dynAttribute = iterator.next();
			if (!dynAttribute.isNaming()&& !dynAttribute.isMultiple()
					&& !dynAttribute.isReadonly() && !dynAttribute.isMandatory()) {
				String value = StringUtils.trimToNull(dynamicObject.getAttribute(dynAttribute.getName()));
				if (value != null) {
					map.put(dynAttribute, value);
				}
			}
		}
		
		// multi attributes
		for (Iterator<DynamicLdapAttribute> iterator = getAttributes().values().iterator(); iterator.hasNext();) {
			DynamicLdapAttribute dynAttribute = iterator.next();
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
		return getAttributes().get(getNamingAttribute()).getLdapName();
	}
	
	public DynamicLdapAttribute getDynamicAttribute(String name) {
		return getAttributes().get(name);
	}
	
	public boolean childrenAllowed() {
		try {
			return (getChildren().keySet().size() > 0);
		} catch (Exception e) {
			return false;
		}
	}
	
	public void addAttribute(DynamicLdapAttribute dynAttribute) {
		String attributeName =  dynAttribute.getName();
		this.attributes.put(attributeName, dynAttribute);
		if (dynAttribute.isNaming()) {
			this.namingAttribute = attributeName;
		}
	}
	public void init(NickiContext context, DynamicObject dynamicObject, ContextSearchResult rs) {
		for (Iterator<DynamicLdapAttribute> iterator = this.attributes.values().iterator(); iterator.hasNext();) {
			iterator.next().init(context, dynamicObject, rs);
		}
	}
	public boolean isComplete(DynamicObject dynamicObject) {
		boolean complete = true;
		// check naming attribute
		complete &= dynamicObject.attributeIsNotEmpty(getNamingAttribute());
		
		// check mandatory attributes
		for (Iterator<DynamicLdapAttribute> iterator = getMandatoryAttributes().iterator(); iterator.hasNext();) {
			DynamicAttribute dynAttribute = iterator.next();
			if (dynAttribute.isStatic()) {
				StaticAttribute staticAttribute = (StaticAttribute) dynAttribute;
				complete &= StringUtils.isNotEmpty(staticAttribute.getValue());
			} else {
				complete &= dynamicObject.attributeIsNotEmpty(dynAttribute.getName());
			}
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
	@SuppressWarnings("unchecked")
	public Map<String, DynamicLdapAttribute> getAttributes() {
		return attributes;
	}
	public List<String> getAdditionalObjectClasses() {
		return additionalObjectClasses;
	}
	public String getObjectClassFilter() {
		StringBuffer sb = new StringBuffer();
		for (Iterator<String> iterator = getObjectClasses().iterator(); iterator.hasNext();) {
			String objectClass = iterator.next();
			LdapHelper.addQuery(sb, "objectClass=" + objectClass, LOGIC.AND);
		}

		return sb.toString();
	}

}
