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
package org.mgnl.nicki.ldap.methods;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.helper.LdapHelper;
import org.mgnl.nicki.ldap.objects.ContextSearchResult;
import org.mgnl.nicki.ldap.objects.DynamicObject;

import freemarker.template.TemplateMethodModel;

public class ListForeignKeyMethod implements TemplateMethodModel, Serializable {

	private static final long serialVersionUID = -81535049844368520L;
	private List<DynamicObject> objects = null;
	private List<Object> foreignKeys = new ArrayList<Object>();
	private NickiContext context;
	private Class<? extends DynamicObject> classDefinition;

	
	public ListForeignKeyMethod(NickiContext context, ContextSearchResult rs, String ldapName,
			Class<? extends DynamicObject> classDefinition) {
		this.context = context;
		this.foreignKeys = LdapHelper.getAttributes(rs, ldapName);
		this.classDefinition = classDefinition;
	}

	public List<DynamicObject> exec(@SuppressWarnings("rawtypes") List arguments) {
		if (objects == null) {
			objects = new ArrayList<DynamicObject>();
			for (Iterator<Object> iterator = this.foreignKeys.iterator(); iterator.hasNext();) {
				String path = (String) iterator.next();
				DynamicObject object = context.loadObject(classDefinition, path);
				if (object != null) {
					objects.add(context.loadObject(classDefinition, path));
				} else {
					System.out.println("Could not build object: " + path);
				}
			}
		}
		return objects;
	}

	protected List<DynamicObject> getObjects() {
		return objects;
	}

	protected void setObjects(List<DynamicObject> objects) {
		this.objects = objects;
	}

	protected List<Object> getForeignKeys() {
		return foreignKeys;
	}

	protected NickiContext getContext() {
		return context;
	}

}
