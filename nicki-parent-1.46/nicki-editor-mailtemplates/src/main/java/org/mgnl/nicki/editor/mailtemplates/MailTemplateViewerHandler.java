/**
 * This file Copyright (c) 2011 deron Consulting GmbH
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.editor.mailtemplates;


import javax.naming.NamingException;

import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;

public interface MailTemplateViewerHandler {

	void save(Template template) throws DynamicObjectException, NamingException;

}
