
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


/**
 * The Class BasicTemplateHandler.
 */
@Slf4j
public class BasicTemplateHandler implements TemplateHandler{
	
	/** The person. */
	private Person person;
	
	/** The context. */
	private NickiContext context;

	/** The params. */
	private Object params;
	
	/** The template. */
	private Template template;
	
	/**
	 * Gets the context.
	 *
	 * @return the context
	 */
	public NickiContext getContext() {
		return context;
	}
	
	/**
	 * Instantiates a new basic template handler.
	 */
	public BasicTemplateHandler() {
	}

	/**
	 * Sets the template.
	 *
	 * @param template the new template
	 */
	public void setTemplate(Template template) {
		this.template = template;
	}

	/**
	 * Sets the user.
	 *
	 * @param person the new user
	 */
	public void setUser(Person person) {
		this.person = person;
	}

	/**
	 * Sets the context.
	 *
	 * @param context the new context
	 */
	public void setContext(NickiContext context) {
		this.context = context;
	}

	/**
	 * Gets the data model.
	 *
	 * @return the data model
	 */
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

	/**
	 * Sets the params.
	 *
	 * @param params the new params
	 */
	public void setParams(Object params) {
		this.params = params;
	}

	/**
	 * Gets the template parameters.
	 *
	 * @return the template parameters
	 */
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

	/**
	 * Gets the person.
	 *
	 * @return the person
	 */
	public Person getPerson() {
		return person;
	}

	/**
	 * Gets the template.
	 *
	 * @return the template
	 */
	public Template getTemplate() {
		return template;
	}

}
