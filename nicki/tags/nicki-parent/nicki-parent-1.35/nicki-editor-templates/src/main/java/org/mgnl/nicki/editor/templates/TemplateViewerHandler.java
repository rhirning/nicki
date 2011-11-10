package org.mgnl.nicki.editor.templates;


import javax.naming.NamingException;

import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;

public interface TemplateViewerHandler {

	void save(Template template) throws DynamicObjectException, NamingException;

}
