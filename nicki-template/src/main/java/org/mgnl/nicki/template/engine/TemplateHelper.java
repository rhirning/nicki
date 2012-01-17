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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.template.handler.TemplateHandler;

public class TemplateHelper {

	public static Map<String, Object> getDataModel(NickiContext context, Map<String, Object> data, Map<String, String> objects) {
		Map<String, Object> dataModel = new HashMap<String, Object>();
		dataModel.putAll(data);
		for (Iterator<String> iterator = objects.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			DynamicObject object = context.loadObject(objects.get(key));
			if (object != null) {
				dataModel.put(key, object);
			}
		}
		return dataModel;
	}
	
	public static TemplateHandler getTemplateHandler(Template template) {
		TemplateHandler handler = null;
		if (template.hasHandler()) {
			try {
				handler = (TemplateHandler) Class.forName(template.getHandler()).newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (handler == null) {
			handler = new BasicTemplateHandler();
			handler.setTemplate(template);
		}
		return handler;
	}

}
