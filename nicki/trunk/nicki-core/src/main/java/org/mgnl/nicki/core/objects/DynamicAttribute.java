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
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.data.OctetString;
import org.mgnl.nicki.core.methods.ForeignKeyMethod;
import org.mgnl.nicki.core.methods.ListForeignKeyMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class DynamicAttribute implements Serializable {
	private static final Logger LOG = LoggerFactory.getLogger(DynamicAttribute.class);
	
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
				LOG.debug(value.getClass().toString());
				clazz = clazz.getSuperclass();
			}
		}
		LOG.error("Class: " + dynamicObject.getClass() + ". Could not set Property " + name + " with value " + value);

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
