
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
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.data.OctetString;
import org.mgnl.nicki.core.methods.ForeignKeyMethod;
import org.mgnl.nicki.core.methods.ListForeignKeyMethod;

import lombok.extern.slf4j.Slf4j;

// TODO: Auto-generated Javadoc
/**
 * The Class DynamicAttribute.
 */
@Slf4j
@SuppressWarnings("serial")
public class DynamicAttribute implements Serializable {
	
	/**
	 * The Enum CREATEONLY.
	 */
	public enum CREATEONLY {/** The true. */
TRUE, /** The false. */
 FALSE}

	/** The name. */
	private String name;
	
	/** The ldap name. */
	private String ldapName;
	
	/** The attribute class. */
	private Class<?> attributeClass;
	
	/** The naming. */
	private boolean naming;
	
	/** The searchable. */
	private boolean searchable;
	
	/** The mandatory. */
	private boolean mandatory;
	
	/** The multiple. */
	private boolean multiple;
	
	/** The foreign key. */
	private boolean foreignKey;
	
	/** The foreign key class. */
	private Class<? extends DynamicObject> foreignKeyClass;
	
	/** The virtual. */
	private boolean virtual;
	
	/** The readonly. */
	private boolean readonly;
	
	/** The editor class. */
	private String editorClass;
	
	/** The search field class. */
	private String searchFieldClass;
	
	/** The caption. */
	private String caption;
	
	/** The format. */
	private String format;
	
	/** The type. */
	private Class<?> type;
	
	/** The create only. */
	private CREATEONLY createOnly = CREATEONLY.FALSE;

	/**
	 * Gets the external name.
	 *
	 * @return the external name
	 */
	public String getExternalName() {
		return ldapName;
	}

	/**
	 * Instantiates a new dynamic attribute.
	 *
	 * @param name the name
	 * @param ldapName the ldap name
	 * @param attributeClass the attribute class
	 */
	public DynamicAttribute(String name, String ldapName, Class<?> attributeClass) {
		this.name = name;
		this.ldapName = ldapName;
		this.attributeClass = attributeClass;
	}
	
	/**
	 * Inits the.
	 *
	 * @param <T> the generic type
	 * @param context the context
	 * @param dynamicObject the dynamic object
	 * @param rs the rs
	 */
	public <T extends NickiContext> void init(T context, DynamicObject dynamicObject, ContextSearchResult rs) {
		if (isVirtual()) {
			return;
		}
		// mandatory
		if (isMandatory()) {
			Object attribute = rs.getValue(getType(), getExternalName());
			if (attribute != null) {
				dynamicObject.put(name, attribute);
			}
		}
		// optional
		if (!isMandatory() && !isMultiple() && !isForeignKey() && rs.hasAttribute(getExternalName())) {
			if (attributeClass == OctetString.class) {
				dynamicObject.put(name, new OctetString((byte[]) rs.getValue(byte[].class, getExternalName())));
			} else if (attributeClass == byte[].class) {
				dynamicObject.put(name, rs.getValue(byte[].class, getExternalName()));
			} else {
				dynamicObject.put(name, rs.getValue(getType(), getExternalName()));
			}
		}
		// optional list
		if (!isMandatory() && isMultiple() && !isForeignKey()) {
			List<Object> attributes = rs.getValues(getExternalName());
			dynamicObject.put(name, attributes);
		}
		// foreign key
		if (!isMandatory() && !isMultiple() && isForeignKey()) {
			String value = (String) rs.getValue(getType(), getExternalName());
			if (StringUtils.isNotEmpty(value)) {
				dynamicObject.put(name, value);
				dynamicObject.put(getGetter(name),
						new ForeignKeyMethod(context, rs, ldapName, getForeignKeyClass()));
			}
		}
		// list foreign key
		if (!isMandatory() && isMultiple() && isForeignKey()) {
			List<Object> values = rs.getValues(getExternalName());
			dynamicObject.put(name, values);
			dynamicObject.put(getMultipleGetter(name),
					new ListForeignKeyMethod(context, rs, ldapName, getForeignKeyClass()));
		}

	}
	
	

	/**
	 * Sets the property value.
	 *
	 * @param dynamicObject the dynamic object
	 * @param value the value
	 */
	@SuppressWarnings("unused")
	private void setPropertyValue(DynamicObject dynamicObject, Object value) {
		Class<?> clazz = dynamicObject.getClass();
		while (clazz.getSuperclass() != null) {
			try {
				BeanUtils.setProperty(dynamicObject, name, value);
				return;
			} catch (Exception e) {
				log.debug(value.getClass().toString());
				clazz = clazz.getSuperclass();
			}
		}
		log.error("Class: " + dynamicObject.getClass() + ". Could not set Property " + name + " with value " + value);

	}

	/**
	 * Gets the getter.
	 *
	 * @param name the name
	 * @return the getter
	 */
	public static String getGetter(String name) {
		return "get" + StringUtils.capitalize(name);
	}

	/**
	 * Gets the multiple getter.
	 *
	 * @param name the name
	 * @return the multiple getter
	 */
	public static String getMultipleGetter(String name) {
		return "get" + StringUtils.capitalize(name) + "s";
	}

	/**
	 * Gets the setter.
	 *
	 * @param name the name
	 * @return the setter
	 */
	public static String getSetter(String name) {
		return "set" + StringUtils.capitalize(name);
	}

	/**
	 * Gets the sultiple getter.
	 *
	 * @param name the name
	 * @return the sultiple getter
	 */
	public static String getsultipleGetter(String name) {
		return "set" + StringUtils.capitalize(name) + "s";
	}

	/**
	 * Gets the attribute class.
	 *
	 * @return the attribute class
	 */
	public Class<?> getAttributeClass() {
		return attributeClass;
	}

	/**
	 * Checks if is naming.
	 *
	 * @return true, if is naming
	 */
	public boolean isNaming() {
		return naming;
	}

	/**
	 * Sets the naming.
	 */
	public void setNaming() {
		this.naming = true;
		this.mandatory = true;
	}

	/**
	 * Checks if is mandatory.
	 *
	 * @return true, if is mandatory
	 */
	public boolean isMandatory() {
		return mandatory;
	}

	/**
	 * Sets the mandatory.
	 */
	public void setMandatory() {
		this.mandatory = true;
	}

	/**
	 * Checks if is multiple.
	 *
	 * @return true, if is multiple
	 */
	public boolean isMultiple() {
		return multiple;
	}

	/**
	 * Sets the multiple.
	 */
	public void setMultiple() {
		this.multiple = true;
	}

	/**
	 * Checks if is foreign key.
	 *
	 * @return true, if is foreign key
	 */
	public boolean isForeignKey() {
		return foreignKey;
	}

	/**
	 * Sets the foreign key.
	 *
	 * @param classDefinition the new foreign key
	 */
	public void setForeignKey(Class<? extends DynamicObject> classDefinition) {
		this.foreignKey = true;
		this.foreignKeyClass = classDefinition;
	}
	
	/**
	 * Sets the foreign key.
	 *
	 * @param className the new foreign key
	 */
	@SuppressWarnings("unchecked")
	public void setForeignKey(String className) {
		this.foreignKey = true;
		try {
			this.foreignKeyClass = (Class<? extends DynamicObject>) Class.forName(className);
		} catch (Exception e) {
			this.foreignKey = false;
		}
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the virtual.
	 */
	public void setVirtual() {
		virtual = true;
	}

	/**
	 * Checks if is virtual.
	 *
	 * @return true, if is virtual
	 */
	public boolean isVirtual() {
		return virtual;
	}
	
	/**
	 * Gets the options.
	 *
	 * @param dynamicObject the dynamic object
	 * @return the options
	 */
	public List<? extends DynamicObject> getOptions(DynamicObject dynamicObject) {
		return new ArrayList<DynamicObject>();
	}

	/**
	 * Sets the readonly.
	 */
	public void setReadonly() {
		this.readonly = true;
	}

	/**
	 * Checks if is readonly.
	 *
	 * @return true, if is readonly
	 */
	public boolean isReadonly() {
		return readonly;
	}

	/**
	 * Gets the foreign key class.
	 *
	 * @return the foreign key class
	 */
	public Class<? extends DynamicObject> getForeignKeyClass() {
		return foreignKeyClass;
	}

	/**
	 * Sets the editor class.
	 *
	 * @param editorClass the new editor class
	 */
	public void setEditorClass(String editorClass) {
		this.editorClass = editorClass;
	}
	
	/**
	 * Gets the editor class.
	 *
	 * @return the editor class
	 */
	public String getEditorClass() {
		return editorClass;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return name + "(" + ldapName + ")";
	}

	/**
	 * Gets the search field class.
	 *
	 * @return the search field class
	 */
	public String getSearchFieldClass() {
		return searchFieldClass;
	}

	/**
	 * Sets the search field class.
	 *
	 * @param searchFieldClass the new search field class
	 */
	public void setSearchFieldClass(String searchFieldClass) {
		this.searchFieldClass = searchFieldClass;
	}

	/**
	 * Checks if is searchable.
	 *
	 * @return true, if is searchable
	 */
	public boolean isSearchable() {
		return searchable;
	}

	/**
	 * Sets the searchable.
	 *
	 * @param searchable the new searchable
	 */
	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}

	/**
	 * Gets the caption.
	 *
	 * @return the caption
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * Sets the caption.
	 *
	 * @param caption the new caption
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public Class<?> getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(Class<?> type) {
		this.type = type;
	}

	/**
	 * Gets the creates the only.
	 *
	 * @return the creates the only
	 */
	public CREATEONLY getCreateOnly() {
		return createOnly;
	}

	/**
	 * Sets the creates the only.
	 *
	 * @param createOnly the new creates the only
	 */
	public void setCreateOnly(CREATEONLY createOnly) {
		this.createOnly = createOnly;
	}

	/**
	 * Gets the format.
	 *
	 * @return the format
	 */
	public String getFormat() {
		return this.format;
	}

	/**
	 * Sets the format.
	 *
	 * @param format the new format
	 */
	public void setFormat(String format) {
		this.format = format;
	}

}
