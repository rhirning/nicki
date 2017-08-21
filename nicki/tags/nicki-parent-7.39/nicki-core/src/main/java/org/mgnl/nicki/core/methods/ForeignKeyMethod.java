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
import java.util.List;

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObject;

import freemarker.template.TemplateMethodModel;

public class ForeignKeyMethod implements Serializable,TemplateMethodModel {

	private static final long serialVersionUID = -5726598490077862331L;
	private DynamicObject object;
	private String foreignKey;
	private NickiContext context;
	private Class<? extends DynamicObject> classDefinition;
	
	public ForeignKeyMethod(NickiContext context, ContextSearchResult rs, String ldapName,
			Class<? extends DynamicObject> classDefinition) {
		this.context = context;
		this.foreignKey = (String) rs.getValue(String.class, ldapName);
		this.classDefinition = classDefinition;
	}

	public DynamicObject exec(@SuppressWarnings("rawtypes") List arguments) {
		if (object == null) {
			object = context.loadObject(this.classDefinition, this.foreignKey);
		}
		return object;
	}

	protected DynamicObject getObject() {
		return object;
	}

	protected void setObject(DynamicObject object) {
		this.object = object;
	}

	protected String getForeignKey() {
		return foreignKey;
	}

	protected NickiContext getContext() {
		return context;
	}

	public Class<? extends DynamicObject> getClassDefinition() {
		return classDefinition;
	}

}
