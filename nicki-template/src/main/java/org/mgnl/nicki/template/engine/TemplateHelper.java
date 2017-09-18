
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


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.util.Classes;
import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.core.context.AppContext;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.template.handler.TemplateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateHelper {
	private static final Logger LOG = LoggerFactory.getLogger(TemplateHelper.class);

	public static Map<String, Object> getDataModel(NickiContext context, Map<String, Object> data, Map<String, String> objects) {
		Map<String, Object> dataModel = new HashMap<String, Object>();
		dataModel.putAll(data);
		for (String key : objects.keySet()) {
			DynamicObject object = context.loadObject(objects.get(key));
			if (object != null) {
				dataModel.put(key, object);
			}
		}
		return dataModel;
	}

	public static Map<String, Object> getDataModel(String xmlDataModel) {
		return getDataModel(DataModelDescription.fromXml(xmlDataModel));
	}
	
	/**
	 *
	 * @param xmlDataModel 
	 *	<datamodel> 
	 *		<entry name="" value="" /> 
	 *		<function name="" class=""> 
	 *			<param name="" value="" />
	 *		</function>
	 *		<object name="" dn="" target="" <= optional />
	 *	</datamodel>
	 * @return dataModel
	 */
	public static Map<String, Object> getDataModel(DataModelDescription dataModelDescr) {
		Map<String, Object> dataModel = new HashMap<String, Object>();

		for (DMEntry entry : dataModelDescr.getEntries()) {
			dataModel.put(entry.name, entry.value);
		}

		for (DMFunction function : dataModelDescr.getFunctions()) {
			try {
				Object f = Classes.newInstance((String) function.clazz);

				Method m;
				for (DMParam param : (List<DMParam>) function.param) {
					m = f.getClass().getDeclaredMethod("set" + StringUtils.capitalize(param.name), String.class);
					m.invoke(param.value);
				}

				dataModel.put((String) function.name, f);
			} catch (Exception e) {
				System.err.println("could not create class " + (String) function.clazz + ": " + e.getMessage());
			}
		}
		
		NickiContext defaultContext = null;
		try {
			defaultContext = AppContext.getSystemContext();
		} catch (InvalidPrincipalException e) {
			System.err.println("could not create default system context: " + e.getMessage());
			return null;
		}
		
		NickiContext actualContext = null;
		DynamicObject obj = null;

		for (DMObject object : dataModelDescr.getObjects()) {
			try {
				if (StringUtils.isNotBlank(object.target)) {
					actualContext = AppContext.getSystemContext(object.target);
				} else {
					actualContext = defaultContext;
				}
				obj = actualContext.loadObject(object.dn);
				if (obj != null) {
					dataModel.put(object.name, obj);
				}
			} catch (Exception e) {
				System.err.println("could not load " + object.dn + ": " + e.getMessage());
			}

		}


		return dataModel;
	}

	public static TemplateHandler getTemplateHandler(Template template) {
		TemplateHandler handler = null;
		if (template.hasHandler()) {
			try {
				handler = (TemplateHandler) Classes.newInstance(template.getHandler());
			} catch (Exception e) {
				LOG.error("Error", e);
			}
		}
		if (handler == null) {
			handler = new BasicTemplateHandler();
			handler.setTemplate(template);
		}
		return handler;
	}
}
