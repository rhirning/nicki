package org.mgnl.nicki.template.handler;

import java.util.List;
import java.util.Map;

import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.template.engine.TemplateParameter;

public interface TemplateHandler {
	void setUser(Person person);
	void setContext(NickiContext context);
	void setParams(Object params);

	Map<String, Object> getDataModel();
	List<TemplateParameter> getTemplateParameters();
}
