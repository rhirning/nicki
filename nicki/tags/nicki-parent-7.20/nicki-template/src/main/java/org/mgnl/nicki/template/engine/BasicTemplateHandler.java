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
package org.mgnl.nicki.template.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.mgnl.nicki.core.helper.XMLHelper;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.template.handler.TemplateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicTemplateHandler implements TemplateHandler{
	private static final Logger LOG = LoggerFactory.getLogger(BasicTemplateHandler.class);
	
	private Person person;
	private NickiContext context;

	private Object params;
	private Template template;
	public NickiContext getContext() {
		return context;
	}
	
	public BasicTemplateHandler() {
	}

	public void setTemplate(Template template) {
		this.template = template;
	}

	public void setUser(Person person) {
		this.person = person;
	}

	public void setContext(NickiContext context) {
		this.context = context;
	}

	public Map<String, Object> getDataModel() {
		Map<String, Object> dataModel = new HashMap<String, Object>();
		/*
		Function function = null;
		try {
			function = new Function(context);
			function.setContext(context);
			function.initDataModel();
			dataModel.put("function", function);
		} catch (Exception e) {
			LOG.error("Error", e);
		}
		*/
		dataModel.put("person", person);
		if (params != null) {
			dataModel.put("params", params);
		}
		return dataModel;
	}

	public void setParams(Object params) {
		this.params = params;
	}

	public List<TemplateParameter> getTemplateParameters() {
		List<TemplateParameter> list = new ArrayList<TemplateParameter>();
		if (template != null) {
			String parameters = template.getAttribute("params");
			if (StringUtils.isNotEmpty(parameters)) {
				try {
					Document document = XMLHelper.documentFromString(parameters);
					@SuppressWarnings("unchecked")
					List<Element> params = document.getRootElement().getChildren("parameter");
					if (params != null) {
						for (Element attributeElement : params) {
							list.add(new TemplateParameter(attributeElement));
						}
					}
				} catch (Exception e) {
					LOG.error("Error", e);
				}
			}
		}
		return list;
	}

	public Person getPerson() {
		return person;
	}

	public Template getTemplate() {
		return template;
	}

}
