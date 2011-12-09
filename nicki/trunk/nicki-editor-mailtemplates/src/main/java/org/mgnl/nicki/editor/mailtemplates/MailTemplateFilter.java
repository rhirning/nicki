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


import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.editor.EntryFilter;

@SuppressWarnings("serial")
public class MailTemplateFilter implements EntryFilter, Serializable {

	public boolean accepts(DynamicObject object) {
		if (StringUtils.contains(object.getPath(), "_")) {
			return false;
		}
		return true;
	}

}
