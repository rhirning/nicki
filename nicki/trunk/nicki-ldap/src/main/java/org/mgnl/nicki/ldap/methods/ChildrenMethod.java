/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.ldap.methods;


import java.io.Serializable;
import java.util.List;

import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.objects.ContextSearchResult;
import org.mgnl.nicki.ldap.objects.DynamicObject;

import freemarker.template.TemplateMethodModel;

public class ChildrenMethod implements Serializable, TemplateMethodModel {

	private static final long serialVersionUID = -81535049844368520L;
	private List<DynamicObject> objects = null;
	private String parent;
	private String filter;
	private NickiContext context;
	
	public ChildrenMethod(NickiContext context, ContextSearchResult rs, String filter) {
		this.context = context;
		this.parent = rs.getNameInNamespace();
		this.filter = filter;
	}

	public List<DynamicObject> exec(@SuppressWarnings("rawtypes") List arguments) {
		if (objects == null) {
			objects = context.loadChildObjects(parent, filter);
		}
		return objects;
	}

}
