
package org.mgnl.nicki.template.handler;

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


import java.util.List;
import java.util.Map;

import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.template.engine.TemplateParameter;


/**
 * The Interface TemplateHandler.
 */
public interface TemplateHandler {
	
	/**
	 * Sets the user.
	 *
	 * @param person the new user
	 */
	void setUser(Person person);
	
	/**
	 * Sets the context.
	 *
	 * @param context the new context
	 */
	void setContext(NickiContext context);
	
	/**
	 * Sets the params.
	 *
	 * @param params the new params
	 */
	void setParams(Object params);

	/**
	 * Gets the data model.
	 *
	 * @return the data model
	 */
	Map<String, Object> getDataModel();
	
	/**
	 * Gets the template parameters.
	 *
	 * @return the template parameters
	 */
	List<TemplateParameter> getTemplateParameters();
	
	/**
	 * Sets the template.
	 *
	 * @param template the new template
	 */
	void setTemplate(Template template);
}
