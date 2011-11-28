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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.mgnl.nicki.core.helper.XMLHelper;
import org.mgnl.nicki.dynamic.objects.objects.Function;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.template.handler.TemplateHandler;

public class BasicTemplateHandler implements TemplateHandler{
	
	private Person person = null;
	private NickiContext context = null;
	private Object params = null;
	private Template template = null;
	
	public BasicTemplateHandler() {
	}

	@Override
	public void setTemplate(Template template) {
		this.template = template;
	}

	@Override
	public void setUser(Person person) {
		this.person = person;
	}

	@Override
	public void setContext(NickiContext context) {
		this.context = context;
	}

	@Override
	public Map<String, Object> getDataModel() {
		Map<String, Object> dataModel = new HashMap<String, Object>();
		Function function = null;
		try {
			function = new Function(context);
			function.setContext(context);
			function.initDataModel();
			dataModel.put("function", function);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
						for (Iterator<Element> iterator = params.iterator(); iterator.hasNext();) {
							Element attributeElement = iterator.next();
							list.add(new TemplateParameter(attributeElement));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

}
