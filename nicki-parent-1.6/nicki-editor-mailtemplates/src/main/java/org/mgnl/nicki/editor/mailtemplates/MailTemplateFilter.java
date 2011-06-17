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
