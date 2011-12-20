/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
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
