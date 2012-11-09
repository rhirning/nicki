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

import java.util.List;

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObject;

public interface DynamicAttribute {

//	public String getLdapName();	
	public <T extends NickiContext> void init(T context, DynamicObject dynamicObject, ContextSearchResult rs);
	public Class<?> getAttributeClass();
	public boolean isNaming();
	public void setNaming();
	public boolean isMandatory();
	public void setMandatory();
	public boolean isMultiple();
	public void setMultiple();
	public boolean isForeignKey();
	public void setForeignKey(Class<? extends DynamicObject> classDefinition);
	public void setForeignKey(String className);
	public String getName();
	public void setVirtual();
	public boolean isVirtual();
	public List<? extends DynamicObject> getOptions(DynamicObject dynamicObject);
	public void setReadonly();
	public boolean isReadonly();
	public void setStatic();
	public boolean isStatic();
	public Class<? extends DynamicObject> getForeignKeyClass();
	public void setEditorClass(String editorClass);
	public String getEditorClass();
}
