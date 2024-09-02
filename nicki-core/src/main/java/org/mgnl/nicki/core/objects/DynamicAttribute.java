
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

@Slf4j
@SuppressWarnings("serial")
public class DynamicAttribute implements Serializable {
	
	public enum CREATEONLY {TRUE, FALSE}

	private String name;
	private String ldapName;
	private Class<?> attributeClass;
	private boolean naming;
	private boolean searchable;
	private boolean mandatory;
	private boolean multiple;
	private boolean foreignKey;
	private Class<? extends DynamicObject> foreignKeyClass;
	private boolean virtual;
	private boolean readonly;
	private String editorClass;
	private String searchFieldClass;
	private String caption;
	private String format;
	private Class<?> type;
	private CREATEONLY createOnly = CREATEONLY.FALSE;

	public String getExternalName() {
		return ldapName;
	}

	public DynamicAttribute(String name, String ldapName, Class<?> attributeClass) {
		this.name = name;
		this.ldapName = ldapName;
		this.attributeClass = attributeClass;
	}
	
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

	public static String getGetter(String name) {
		return "get" + StringUtils.capitalize(name);
	}

	public static String getMultipleGetter(String name) {
		return "get" + StringUtils.capitalize(name) + "s";
	}

	public static String getSetter(String name) {
		return "set" + StringUtils.capitalize(name);
	}

	public static String getsultipleGetter(String name) {
		return "set" + StringUtils.capitalize(name) + "s";
	}

	public Class<?> getAttributeClass() {
		return attributeClass;
	}

	public boolean isNaming() {
		return naming;
	}

	public void setNaming() {
		this.naming = true;
		this.mandatory = true;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory() {
		this.mandatory = true;
	}

	public boolean isMultiple() {
		return multiple;
	}

	public void setMultiple() {
		this.multiple = true;
	}

	public boolean isForeignKey() {
		return foreignKey;
	}

	public void setForeignKey(Class<? extends DynamicObject> classDefinition) {
		this.foreignKey = true;
		this.foreignKeyClass = classDefinition;
	}
	@SuppressWarnings("unchecked")
	public void setForeignKey(String className) {
		this.foreignKey = true;
		try {
			this.foreignKeyClass = (Class<? extends DynamicObject>) Class.forName(className);
		} catch (Exception e) {
			this.foreignKey = false;
		}
	}
	public String getName() {
		return name;
	}
	public void setVirtual() {
		virtual = true;
	}

	public boolean isVirtual() {
		return virtual;
	}
	
	public List<? extends DynamicObject> getOptions(DynamicObject dynamicObject) {
		return new ArrayList<DynamicObject>();
	}

	public void setReadonly() {
		this.readonly = true;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public Class<? extends DynamicObject> getForeignKeyClass() {
		return foreignKeyClass;
	}

	public void setEditorClass(String editorClass) {
		this.editorClass = editorClass;
	}
	
	public String getEditorClass() {
		return editorClass;
	}

	@Override
	public String toString() {
		return name + "(" + ldapName + ")";
	}

	public String getSearchFieldClass() {
		return searchFieldClass;
	}

	public void setSearchFieldClass(String searchFieldClass) {
		this.searchFieldClass = searchFieldClass;
	}

	public boolean isSearchable() {
		return searchable;
	}

	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public CREATEONLY getCreateOnly() {
		return createOnly;
	}

	public void setCreateOnly(CREATEONLY createOnly) {
		this.createOnly = createOnly;
	}

	public String getFormat() {
		return this.format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

}
