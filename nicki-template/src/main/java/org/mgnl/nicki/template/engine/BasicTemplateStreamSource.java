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
package org.mgnl.nicki.template.engine;

import java.io.InputStream;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.template.handler.TemplateHandler;


public class BasicTemplateStreamSource {

	Template template;
	Map<String, Object> params;
	private String templatePath;
	private TemplateHandler handler;
	
	public BasicTemplateStreamSource(Template template, NickiContext context, Map<String, Object> params) {
		this.template = template;
		this.params = params;
		// render template
		String parentPath = Config.getProperty("nicki.templates.basedn");
		templatePath = template.getSlashPath(parentPath);
		if (StringUtils.contains(templatePath, "_")) {
			templatePath = StringUtils.substringBefore(templatePath, "_");
		}
		handler = TemplateHelper.getTemplateHandler(template);

		handler.setUser((Person) template.getContext().getUser());
		handler.setContext(context);
		handler.setParams(params);
	}

	public Map<String, Object> getDataModel() {
		return handler.getDataModel();
	}

	public TemplateHandler getHandler() {
		return handler;
	}

	public String getTemplatePath() {
		return templatePath;
	}
	
	public InputStream getStringStream() {
		try {
			return TemplateEngine.getInstance().executeTemplate(getTemplatePath(), getDataModel(),
					TemplateEngine.DEFAULT_CHARSET);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public InputStream getPdfStream() {
		try {
			return TemplateEngine.getInstance().executeTemplateAsPdf(getTemplatePath(), getDataModel());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	public InputStream getCsVStream() {
		try {
			return TemplateEngine.getInstance().executeTemplateAsCsv(getTemplatePath(), getDataModel(),
					TemplateEngine.CSV_CHARSET);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public Template getTemplate() {
		return template;
	}

	public Map<String, Object> getParams() {
		return params;
	}
	


}
