/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General Public License and an individual license
 * with Dr. Ralf Hirning.
 *
 * This file is distributed in the hope that it will be useful, but AS-IS and WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE,
 * or NONINFRINGEMENT. Redistribution, except as permitted by whichever of the GPL or the individual
 * license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this file under the terms of the
 * GNU General Public License, Version 3, as published by the Free Software Foundation. You should
 * have received a copy of the GNU General Public License, Version 3 along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying materials are made available under
 * the terms of the individual license.
 *
 * Any modifications to this file must keep this entire header intact.
 *
 */
package org.mgnl.nicki.template.engine;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.ldap.auth.InvalidPrincipalException;
import org.mgnl.nicki.ldap.context.AppContext;
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
				Object f = Class.forName((String) function.clazz).newInstance();

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
