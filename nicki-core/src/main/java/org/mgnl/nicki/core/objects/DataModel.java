
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

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.objects.DynamicAttribute.CREATEONLY;

import lombok.extern.slf4j.Slf4j;


/**
 * The Class DataModel.
 */
@Slf4j
@SuppressWarnings("serial")
public class DataModel implements Serializable {

	/** The attributes. */
	private Map<String, DynamicAttribute> attributes = new HashMap<String, DynamicAttribute>();
	
	/** The object classes. */
	private List<String> objectClasses = new ArrayList<String>();
	
	/** The additional object classes. */
	private List<String> additionalObjectClasses = new ArrayList<String>();
	
	/** The naming attribute. */
	private String namingAttribute;

	/** The mandatory attributes. */
	private List<DynamicAttribute> mandatoryAttributes;
	
	/** The optional attributes. */
	private List<DynamicAttribute> optionalAttributes;
	
	/** The list optional attributes. */
	private List<DynamicAttribute> listOptionalAttributes;
	
	/** The foreign keys. */
	private List<DynamicAttribute> foreignKeys;
	
	/** The list foreign keys. */
	private List<DynamicAttribute> listForeignKeys;

	/** The children. */
	private Map<String, ChildFilter> children = new HashMap<String, ChildFilter>();
	
	/** The references. */
	private Map<String, DynamicReference> references = new HashMap<String, DynamicReference>();
	

	/**
	 * The Enum ATTRIBUTE_TYPE.
	 */
	public enum ATTRIBUTE_TYPE {/** The mandatory. */
MANDATORY, /** The optional. */
 OPTIONAL, /** The optional list. */
 OPTIONAL_LIST, /** The foreign key. */
 FOREIGN_KEY, /** The foreign key list. */
 FOREIGN_KEY_LIST, /** The structured. */
 STRUCTURED, /** The undefined. */
 UNDEFINED};
	
	/**
	 * Gets the object classes.
	 *
	 * @return the object classes
	 */
	public List<String> getObjectClasses() {
		return objectClasses;
	}
	
	/**
	 * Gets the naming attribute.
	 *
	 * @return the naming attribute
	 */
	public String getNamingAttribute() {
		return namingAttribute;
	}
	
	/**
	 * Gets the mandatory attributes.
	 *
	 * @return the mandatory attributes
	 */
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
	
	/**
	 * Gets the optional attributes.
	 *
	 * @return the optional attributes
	 */
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
	
	/**
	 * Gets the list optional attributes.
	 *
	 * @return the list optional attributes
	 */
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
	
	/**
	 * Gets the foreign keys.
	 *
	 * @return the foreign keys
	 */
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
	
	/**
	 * Gets the list foreign keys.
	 *
	 * @return the list foreign keys
	 */
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
	
	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public Map<String, ChildFilter> getChildren() {
		return children;
	}
	
	/**
	 * Gets the references.
	 *
	 * @return the references
	 */
	public Map<String, DynamicReference> getReferences() {
		return references;
	}

	/**
	 * Adds the child.
	 *
	 * @param attribute the attribute
	 * @param filter the filter
	 */
	public void addChild(String attribute, ChildFilter filter) {
		children.put(attribute, filter);		
	}
	
	/**
	 * Adds the object classes.
	 *
	 * @param objectClass the object class
	 */
	public void addObjectClasses(String objectClass) {
		objectClasses.add(objectClass);
	}
	
	/**
	 * Adds the additional object classes.
	 *
	 * @param objectClass the object class
	 */
	public void addAdditionalObjectClasses(String objectClass) {
		additionalObjectClasses.add(objectClass);
	}

	/**
	 * Gets the ldap attributes.
	 *
	 * @param dynamicObject the dynamic object
	 * @param createOnly the create only
	 * @return the ldap attributes
	 */
	public Attributes getLdapAttributes(DynamicObject dynamicObject, CREATEONLY createOnly) {
		Attributes myAttrs = new BasicAttributes(true);
		addLdapAttributes(myAttrs, dynamicObject, true, createOnly);
		return myAttrs;
	}

	/**
	 * Gets the ldap attributes.
	 *
	 * @param dynamicObject the dynamic object
	 * @param attributeNames the attribute names
	 * @param createOnly the create only
	 * @return the ldap attributes
	 */
	public Attributes getLdapAttributes(DynamicObject dynamicObject, String[] attributeNames, CREATEONLY createOnly) {
		Attributes myAttrs = new BasicAttributes(true);
		addLdapAttributes(myAttrs, dynamicObject, true, attributeNames, createOnly);
		return myAttrs;
	}
	
	/**
	 * Adds the ldap attributes.
	 *
	 * @param myAttrs the my attrs
	 * @param dynamicObject the dynamic object
	 * @param nullable the nullable
	 * @param createOnly the create only
	 */
	public void addLdapAttributes(Attributes myAttrs, DynamicObject dynamicObject, boolean nullable, CREATEONLY createOnly) {
		addLdapAttributes(myAttrs, dynamicObject, nullable, null, createOnly);
	}
	
	/**
	 * Adds the ldap attributes.
	 *
	 * @param myAttrs the my attrs
	 * @param dynamicObject the dynamic object
	 * @param nullable the nullable
	 * @param attributeNames the attribute names
	 * @param createOnly the create only
	 */
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

	/**
	 * Gets the non mandatory attributes.
	 *
	 * @param dynamicObject the dynamic object
	 * @return the non mandatory attributes
	 */
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

	
	/**
	 * Gets the naming ldap attribute.
	 *
	 * @return the naming ldap attribute
	 */
	public String getNamingLdapAttribute() {
		return getAttributes().get(getNamingAttribute()).getExternalName();
	}
	
	/**
	 * Gets the dynamic attribute.
	 *
	 * @param name the name
	 * @return the dynamic attribute
	 */
	public DynamicAttribute getDynamicAttribute(String name) {
		return getAttributes().get(name);
	}
	
	/**
	 * Children allowed.
	 *
	 * @return true, if successful
	 */
	public boolean childrenAllowed() {
		try {
			return getChildren().keySet().size() > 0;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Adds the attribute.
	 *
	 * @param dynAttribute the dyn attribute
	 */
	public void addAttribute(DynamicAttribute dynAttribute) {
		String attributeName =  dynAttribute.getName();
		this.attributes.put(attributeName, dynAttribute);
		if (dynAttribute.isNaming()) {
			this.namingAttribute = attributeName;
		}
	}
	
	/**
	 * Removes the attribute.
	 *
	 * @param attrbuteName the attrbute name
	 */
	public void removeAttribute(String attrbuteName) {
		if (this.attributes.containsKey(attrbuteName)) {
			this.attributes.remove(attrbuteName);
		}
	}
	
	/**
	 * Removes the additional object class.
	 *
	 * @param objectClass the object class
	 */
	public void removeAdditionalObjectClass(String objectClass) {
		if (this.additionalObjectClasses.contains(objectClass)) {
			this.additionalObjectClasses.remove(objectClass);
		}
	}
	
	/**
	 * Removes the object class.
	 *
	 * @param objectClass the object class
	 */
	public void removeObjectClass(String objectClass) {
		if (this.objectClasses.contains(objectClass)) {
			this.objectClasses.remove(objectClass);
		}
	}
	
	/**
	 * Inits the.
	 *
	 * @param context the context
	 * @param dynamicObject the dynamic object
	 * @param rs the rs
	 */
	public void init(NickiContext context, DynamicObject dynamicObject, ContextSearchResult rs) {
		for (DynamicAttribute dynamicAttribute : this.attributes.values()) {
			dynamicAttribute.init(context, dynamicObject, rs);
		}
	}
	
	/**
	 * Checks if is complete.
	 *
	 * @param dynamicObject the dynamic object
	 * @return true, if is complete
	 */
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
	
	/**
	 * Gets the attributes.
	 *
	 * @return the attributes
	 */
	public Map<String, DynamicAttribute> getAttributes() {
		return attributes;
	}
	
	/**
	 * Gets the additional object classes.
	 *
	 * @return the additional object classes
	 */
	public List<String> getAdditionalObjectClasses() {
		return additionalObjectClasses;
	}

}
