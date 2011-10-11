package org.mgnl.nicki.template.engine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.template.handler.TemplateHandler;

import com.lowagie.text.List;

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
			handler = new BasicTemplateHandler(template);
		}
		return handler;
	}

	public static boolean isComplete(Template template, Map<String, Object> params) {
		TemplateHandler handler = getTemplateHandler(template);
		java.util.List<TemplateParameter> list = handler.getTemplateParameters();
		if (list != null) {
			for (TemplateParameter templateParameter : list) {
				if (!params.containsKey(templateParameter.getName())) {
					return false;
				} else {
					if (null == params.get(templateParameter.getName())) {
						return false;
					}
				}
			}
		}
		return true;
	}
}
