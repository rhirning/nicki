/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.template.handler;

import java.util.List;
import java.util.Map;

import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.template.engine.TemplateParameter;

public interface TemplateHandler {
	void setUser(Person person);
	void setContext(NickiContext context);
	void setParams(Object params);

	Map<String, Object> getDataModel();
	List<TemplateParameter> getTemplateParameters();
	void setTemplate(Template template);
}
