
package org.mgnl.nicki.template.engine;

/*-
 * #%L
 * nicki-template
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
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
 * #L%
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.mgnl.nicki.core.helper.XMLHelper;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.template.handler.TemplateHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BasicTemplateHandler implements TemplateHandler{
	
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
			log.error("Error", e);
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
					List<Element> params = document.getRootElement().getChildren("parameter");
					if (params != null) {
						for (Element attributeElement : params) {
							list.add(new TemplateParameter(attributeElement));
						}
					}
				} catch (Exception e) {
					log.error("Error", e);
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
