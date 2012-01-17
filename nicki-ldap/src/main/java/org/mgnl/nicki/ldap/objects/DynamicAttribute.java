/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
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
 */
package org.mgnl.nicki.ldap.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.data.jndi.OctetString;
import org.mgnl.nicki.ldap.helper.LdapHelper;
import org.mgnl.nicki.ldap.methods.ForeignKeyMethod;
import org.mgnl.nicki.ldap.methods.ListForeignKeyMethod;

@SuppressWarnings("serial")
public class DynamicAttribute implements Serializable {

	private String name;
	private String ldapName;
	private Class<?> attributeClass;
	private boolean naming = false;
	private boolean mandatory = false;
	private boolean multiple = false;
	private boolean foreignKey = false;
	private Class<? extends DynamicObject> foreignKeyClass;
	private boolean virtual = false;
	private boolean readonly = false;
	private boolean staticAttribute = false;
	private String editorClass = null;

	public String getLdapName() {
		return ldapName;
	}

	public DynamicAttribute(String name, String ldapName, Class<?> attributeClass) {
		this.name = name;
		this.ldapName = ldapName;
		this.attributeClass = attributeClass;
	}
	
	public void init(NickiContext context, DynamicObject dynamicObject, ContextSearchResult rs) {
		if (isVirtual()) {
			return;
		}
		// mandatory
		if (isMandatory()) {
			Object attribute = LdapHelper.getAttribute(rs, getLdapName());
			if (attribute != null) {
				dynamicObject.put(name, attribute);
			}
		}
		// optional
		if (!isMandatory() && !isMultiple() && !isForeignKey()) {
			Object attribute = LdapHelper.getAttribute(rs, getLdapName());
			if (attribute != null) {
				if (attributeClass == OctetString.class) {
					dynamicObject.put(name, new OctetString(((byte[])attribute)));
				} else {
					dynamicObject.put(name, attribute);
				}
			}
		}
		// optional list
		if (!isMandatory() && isMultiple() && !isForeignKey()) {
			List<Object> attributes = LdapHelper.getAttributes(rs, getLdapName());
			dynamicObject.put(name, attributes);
		}
		// foreign key
		if (!isMandatory() && !isMultiple() && isForeignKey()) {
			String value = (String) LdapHelper.getAttribute(rs, ldapName);
			if (StringUtils.isNotEmpty(value)) {
				dynamicObject.put(name, value);
				dynamicObject.put(getGetter(name),
						new ForeignKeyMethod(context, rs, ldapName, getForeignKeyClass()));
			}
		}
		// list foreign key
		if (!isMandatory() && isMultiple() && isForeignKey()) {
			List<Object> values = LdapHelper.getAttributes(rs, ldapName);
			dynamicObject.put(name, values);
			dynamicObject.put(getMultipleGetter(name),
					new ListForeignKeyMethod(context, rs, ldapName, getForeignKeyClass()));
		}

	}

	public static String getGetter(String name) {
		return "get" + StringUtils.capitalize(name);
	}

	public static String getMultipleGetter(String name) {
		return "get" + StringUtils.capitalize(name) + "s";
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

	public void setStatic() {
		staticAttribute = true;
	}

	public boolean isStatic() {
		return staticAttribute;
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
}
