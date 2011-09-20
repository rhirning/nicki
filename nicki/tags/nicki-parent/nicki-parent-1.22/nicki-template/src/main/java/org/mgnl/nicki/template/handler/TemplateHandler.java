package org.mgnl.nicki.template.handler;

import java.util.Map;

import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.ldap.context.NickiContext;

public interface TemplateHandler {
	void setUser(Person person);
	void setContext(NickiContext context);

	Map<String, Object> getDataModel();
}
