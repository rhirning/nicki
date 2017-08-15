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
package org.mgnl.nicki.core.methods;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.TemplateMethodModel;

public class ListForeignKeyMethod implements TemplateMethodModel, Serializable {

	private static final long serialVersionUID = -81535049844368520L;
	private static final Logger LOG = LoggerFactory.getLogger(ListForeignKeyMethod.class);
	private List<DynamicObject> objects;
	private List<Object> foreignKeys = new ArrayList<Object>();
	private NickiContext context;
	private Class<? extends DynamicObject> classDefinition;

	
	public ListForeignKeyMethod(NickiContext context, ContextSearchResult rs, String ldapName,
			Class<? extends DynamicObject> classDefinition) {
		this.context = context;
		this.foreignKeys = rs.getValues(ldapName);
		this.classDefinition = classDefinition;
	}

	public List<DynamicObject> exec(@SuppressWarnings("rawtypes") List arguments) {
		if (objects == null) {
			objects = new ArrayList<DynamicObject>();
			for (Object key : this.foreignKeys) {
				String path = (String) key;
				DynamicObject object = context.loadObject(classDefinition, path);
				if (object != null) {
					objects.add(context.loadObject(classDefinition, path));
				} else {
					LOG.debug("Could not build object: " + path);
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
