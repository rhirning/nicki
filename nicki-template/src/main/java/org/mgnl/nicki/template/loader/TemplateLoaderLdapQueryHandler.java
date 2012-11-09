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
package org.mgnl.nicki.template.loader;

import java.util.Enumeration;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.objects.ContextAttribute;
import org.mgnl.nicki.core.objects.ContextAttributes;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.ldap.context.LdapContext;
import org.mgnl.nicki.ldap.data.QueryHandler;
import org.mgnl.nicki.ldap.query.BasicLdapHandler;
import org.mgnl.nicki.template.engine.Template;

public class TemplateLoaderLdapQueryHandler extends BasicLdapHandler implements QueryHandler {
	
	public static final String TEMPLATE_NAME = "ou";
	public static final String ATTRIBUTE_DATA = "nickiTemplateData";
	public static final String ATTRIBUTE_PART = "nickiTemplatePart";
	public static final String PART_SEPARATOR = "=";
	private String name = null;
	private String dn = null;
	private Template template = null;

	public TemplateLoaderLdapQueryHandler(NickiContext context, String name, String dn) {
		super((LdapContext) context);
		this.name = name;
		this.dn = dn;
		this.setFilter("objectClass=nickiTemplate");
	}


	public String getBaseDN() {
		return this.dn;
	}

	public void handle(List<ContextSearchResult> results) {
		try {
			template = new Template(this.name);
			if (results != null && results.size() > 0) {
				handle(results.get(0));
			}
		} catch (Exception e) {
			template = null;
		}
	}


	public void handle(ContextSearchResult rs) throws DynamicObjectException {
		template = new Template(this.name);
			
		ContextAttributes attrs = rs.getAttributes();
		try {
			template.setData(getAttribute(rs, ATTRIBUTE_DATA));
		} catch (Exception e) {
		}
		ContextAttribute attr = attrs.get(ATTRIBUTE_PART);
		if (attr != null) {
			for ( Enumeration<Object> vals = (Enumeration<Object>) attr.getAll(); vals.hasMoreElements();) {
				String partString = (String) vals.nextElement();
				String partName = StringUtils.substringBefore(partString, PART_SEPARATOR);
				String partValue = StringUtils.substringAfter(partString, PART_SEPARATOR);
				template.putPart(partName, partValue);
			}
		}
	}

	public Template getTemplate() {
		return template;
	}


	private String getAttribute(ContextSearchResult rs, String attributeName) throws DynamicObjectException {
		ContextAttributes attributes = rs.getAttributes();
		String result = attributes.get(attributeName).get().toString();
		if (StringUtils.isNotEmpty(result)) {
			return result;
		}

		return null;
	}


	@Override
	public SCOPE getScope() {
		return SCOPE.OBJECT;
	}
}
