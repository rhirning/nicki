/**
 * Copyright (c) 2003-2015 Dr. Ralf Hirning
 * All rights reserved.
 *  
 * This program is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 * 
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU Public License v3.0
 * which is available at
 * http://www.gnu.org/licenses/gpl.html
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 * 
 * Any modifications to this file must keep this entire header
 * intact.
*/
package org.mgnl.nicki.template.loader;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.data.QueryHandler;
import org.mgnl.nicki.ldap.context.LdapContext;
import org.mgnl.nicki.ldap.query.BasicLdapHandler;
import org.mgnl.nicki.template.engine.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateLoaderLdapQueryHandler extends BasicLdapHandler implements QueryHandler {
	private static final Logger LOG = LoggerFactory.getLogger(TemplateLoaderLdapQueryHandler.class);
	
	public static final String TEMPLATE_NAME = "ou";
	public static final String ATTRIBUTE_DATA = "nickiTemplateData";
	public static final String ATTRIBUTE_PART = "nickiTemplatePart";
	public static final String PART_SEPARATOR = "=";
	private String name;
	private String dn;
	private Template template;

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

		try {
			template.setData(getAttribute(rs, ATTRIBUTE_DATA));
		} catch (Exception e) {
			LOG.debug("Error", e);
		}

		for (Object value : rs.getValues(ATTRIBUTE_PART)) {
			String partString = (String) value;
			String partName = StringUtils.substringBefore(partString, PART_SEPARATOR);
			String partValue = StringUtils.substringAfter(partString, PART_SEPARATOR);
			template.putPart(partName, partValue);
		}
	}

	public Template getTemplate() {
		return template;
	}


	private String getAttribute(ContextSearchResult rs, String attributeName) throws DynamicObjectException {
		String result = (String) rs.getValue(String.class, attributeName);
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
