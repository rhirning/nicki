package org.mgnl.nicki.template.engine;

import java.util.HashMap;
import java.util.Map;

import org.mgnl.nicki.dynamic.objects.objects.Function;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.template.handler.TemplateHandler;

public class BasicTemplateHandler implements TemplateHandler{
	
	private Person person = null;
	private NickiContext context = null;
	private Object params = null;
	

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

}
