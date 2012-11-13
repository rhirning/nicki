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
package org.mgnl.nicki.ldap.methods;


import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.ldap.helper.LdapHelper;
import org.mgnl.nicki.ldap.helper.LdapHelper.LOGIC;

import freemarker.template.TemplateMethodModel;

public class LoadObjectsMethod implements TemplateMethodModel, Serializable {

	private static final long serialVersionUID = -81535049844368520L;
	List<? extends DynamicObject> objects = null;
	private String baseDn;
	private String filter;
	private DynamicObject reference = null;
	private Class<? extends DynamicObject> classDefinition;
	
	public LoadObjectsMethod(Class<? extends DynamicObject> classDefinition, DynamicObject reference, String baseDn, String filter) {
		this.classDefinition = classDefinition;
		this.reference = reference;
		this.baseDn = baseDn;
		this.filter = filter;
	}

	public LoadObjectsMethod(Class<? extends DynamicObject> classDefinition, DynamicObject reference, String filter) {
		this.classDefinition = classDefinition;
		this.reference = reference;
		this.filter = filter;
	}

	public List<? extends DynamicObject> exec(@SuppressWarnings("rawtypes") List arguments) {
		if (objects == null) {
			StringBuffer sb = new StringBuffer();
			if (StringUtils.isNotEmpty(filter)) {
				LdapHelper.addQuery(sb, filter, LOGIC.AND);
			}
			if (arguments != null && arguments.size() > 0) {
				LdapHelper.addQuery(sb, (String) arguments.get(0), LOGIC.AND);
			}
			if (baseDn == null) {
				this.baseDn = reference.getPath();
			}
			objects = reference.getContext().loadObjects(classDefinition, baseDn, sb.toString());
		}
		return objects;
	}

}
