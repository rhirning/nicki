
package org.mgnl.nicki.core.objects;

/*-
 * #%L
 * nicki-core
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


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
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.objects.DynamicAttribute.CREATEONLY;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings("serial")
public class DataModel implements Serializable {

	private Map<String, DynamicAttribute> attributes = new HashMap<String, DynamicAttribute>();
	private List<String> objectClasses = new ArrayList<String>();
	private List<String> additionalObjectClasses = new ArrayList<String>();
	private String namingAttribute;

	private List<DynamicAttribute> mandatoryAttributes;
	private List<DynamicAttribute> optionalAttributes;
	private List<DynamicAttribute> listOptionalAttributes;
	private List<DynamicAttribute> foreignKeys;
	private List<DynamicAttribute> listForeignKeys;

	private Map<String, ChildFilter> children = new HashMap<String, ChildFilter>();
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
	public Map<String, ChildFilter> getChildren() {
		return children;
	}
	public Map<String, DynamicReference> getReferences() {
		return references;
	}

	public void addChild(String attribute, ChildFilter filter) {
		children.put(attribute, filter);		
	}
	public void addObjectClasses(String objectClass) {
		objectClasses.add(objectClass);
	}
	public void addAdditionalObjectClasses(String objectClass) {
		additionalObjectClasses.add(objectClass);
	}

	public Attributes getLdapAttributes(DynamicObject dynamicObject, CREATEONLY createOnly) {
		Attributes myAttrs = new BasicAttributes(true);
		addLdapAttributes(myAttrs, dynamicObject, true, createOnly);
		return myAttrs;
	}

	public Attributes getLdapAttributes(DynamicObject dynamicObject, String[] attributeNames, CREATEONLY createOnly) {
		Attributes myAttrs = new BasicAttributes(true);
		addLdapAttributes(myAttrs, dynamicObject, true, attributeNames, createOnly);
		return myAttrs;
	}
	
	public void addLdapAttributes(Attributes myAttrs, DynamicObject dynamicObject, boolean nullable, CREATEONLY createOnly) {
		addLdapAttributes(myAttrs, dynamicObject, nullable, null, createOnly);
	}
	
	public void addLdapAttributes(Attributes myAttrs, DynamicObject dynamicObject,
			boolean nullable, String[] attributeNames, CREATEONLY createOnly) {

		// single attributes (except namingAttribute)
		for (DynamicAttribute dynAttribute : getAttributes().values()) {
			if (attributeNames == null || attributeNames.length == 0 || DataHelper.contains(attributeNames, dynAttribute.getName())) {
				if (!dynAttribute.isNaming()&& !dynAttribute.isMultiple() && !dynAttribute.isReadonly()) {
					if (dynAttribute.getCreateOnly() == CREATEONLY.FALSE || createOnly == CREATEONLY.TRUE) {
						if (dynAttribute.getType() == String.class) {
							String value = StringUtils.trimToNull(dynamicObject.getAttribute(dynAttribute.getName()));
							if (nullable || value != null) {
								Attribute attribute = new BasicAttribute(dynAttribute.getExternalName(), value);
								myAttrs.put(attribute);
							}
						} else if (dynAttribute.getType() == byte[].class) {
							byte[] value = (byte[]) dynamicObject.get(dynAttribute.getName());
							if (nullable || value != null) {
								Attribute attribute = new BasicAttribute(dynAttribute.getExternalName(), value);
								myAttrs.put(attribute);
							}
						}
					}
				} else {
					log.debug("ignore attribute (naming or multiple or readonly): " + dynAttribute.getName());
				}
			} else {
				log.debug("ignore attribute: " + dynAttribute.getName());
			}
		}
		
		// multi attributes
		for (DynamicAttribute dynAttribute : getAttributes().values()) {
			if (attributeNames == null || attributeNames.length == 0 || DataHelper.contains(attributeNames, dynAttribute.getName())) {
				if (dynAttribute.isMultiple() && !dynAttribute.isReadonly()) {
					if (dynAttribute.getCreateOnly() == CREATEONLY.FALSE || createOnly == CREATEONLY.TRUE) {
						Attribute attribute = new BasicAttribute(dynAttribute.getExternalName());
						if (dynAttribute.getType() == String.class) {
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
						} else if (dynAttribute.getType() == byte[].class) {
							@SuppressWarnings("unchecked")
							List<byte[]> list = (List<byte[]>) dynamicObject.get(dynAttribute.getName());
							if (list != null) {
								for (byte[] value : list) {
									if (value != null) {
										attribute.add(value);
									}
								}
							}
							if (nullable || attribute.size() > 0) {
								myAttrs.put(attribute);
							}
						}
					}
				} else {
					log.debug("ignore attribute (not multiple or readonly): " + dynAttribute.getName());
				}
			} else {
				log.debug("ignore attribute: " + dynAttribute.getName());
			}
		}
		
	}

	public Map<DynamicAttribute, Object> getNonMandatoryAttributes(DynamicObject dynamicObject) {
		Map<DynamicAttribute, Object> map = new HashMap<DynamicAttribute, Object>();
		// single attributes (except namingAttribute)
		for (DynamicAttribute dynAttribute : getAttributes().values()) {
			if (!dynAttribute.isNaming()&& !dynAttribute.isMultiple()
					&& !dynAttribute.isReadonly() && !dynAttribute.isMandatory()) {
				if (dynAttribute.getType() == String.class) {
					String value = StringUtils.trimToNull(dynamicObject.getAttribute(dynAttribute.getName()));
					if (value != null) {
						map.put(dynAttribute, value);
					}
				} else {
					log.debug("unsupported type: " + dynAttribute.getType());
				}
			}
		}
		
		// multi attributes
		for (DynamicAttribute dynAttribute : getAttributes().values()) {
			if (dynAttribute.isMultiple() && !dynAttribute.isReadonly()
					&& !dynAttribute.isMandatory()) {
				if (dynAttribute.getType() == String.class) {
					@SuppressWarnings("unchecked")
					List<String> list = (List<String>) dynamicObject.get(dynAttribute.getName());
					if (list != null && list.size() > 0) {
						map.put(dynAttribute, list);
					}
				} else {
					log.debug("unsupported type: " + dynAttribute.getType());
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
			return getChildren().keySet().size() > 0;
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
	
	public void removeAttribute(String attrbuteName) {
		if (this.attributes.containsKey(attrbuteName)) {
			this.attributes.remove(attrbuteName);
		}
	}
	
	public void removeAdditionalObjectClass(String objectClass) {
		if (this.additionalObjectClasses.contains(objectClass)) {
			this.additionalObjectClasses.remove(objectClass);
		}
	}
	
	public void removeObjectClass(String objectClass) {
		if (this.objectClasses.contains(objectClass)) {
			this.objectClasses.remove(objectClass);
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
		complete &= StringUtils.isNotEmpty(dynamicObject.getPath());

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
