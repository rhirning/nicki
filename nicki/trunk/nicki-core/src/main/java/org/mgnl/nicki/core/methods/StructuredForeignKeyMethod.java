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

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObject;

import freemarker.template.TemplateMethodModel;

public class StructuredForeignKeyMethod extends ForeignKeyMethod implements Serializable,TemplateMethodModel {

	private static final long serialVersionUID = -5726598490077862331L;
	private String path;
	private String flag;
	private String xml;
	
	
	public StructuredForeignKeyMethod(NickiContext context, ContextSearchResult rs, String ldapName,
			Class<? extends DynamicObject> classDefinition) {
		super(context, rs, ldapName,classDefinition);
		this.path = StringUtils.substringBefore(getForeignKey(), "#");
		String rest = StringUtils.substringAfter(getForeignKey(), "#");
		this.flag = StringUtils.substringBefore(rest, "#");
		this.xml = StringUtils.substringAfter(rest, "#");
		
	}

	@Override
	public DynamicObject exec(@SuppressWarnings("rawtypes") List arguments) {
		if (getObject() == null) {
			setObject(getContext().loadObject(getClassDefinition(), this.path));
			getObject().put("struct:flag" , flag);
			getObject().put("struct:xml" , xml);
			getObject().put("struct" , new StructuredData(xml));
		}
		return getObject();
	}

}
