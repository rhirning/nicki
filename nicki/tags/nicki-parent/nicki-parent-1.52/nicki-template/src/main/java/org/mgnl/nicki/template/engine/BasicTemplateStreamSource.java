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
	private static final long serialVersionUID = 4222973194514516918L;

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
			return TemplateEngine.getInstance().executeTemplate(getTemplatePath(), getDataModel());
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
			return TemplateEngine.getInstance().executeTemplateAsCsv(getTemplatePath(), getDataModel());
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
