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
import java.util.ArrayList;
import java.util.List;

import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.objects.DynamicObject;

import freemarker.template.TemplateMethodModel;

@SuppressWarnings("serial")
public class DynamicLoadObjectsMethod implements TemplateMethodModel, Serializable {

	private NickiContext context;
	
	public DynamicLoadObjectsMethod(NickiContext context) {
		super();
		this.context = context;
	}

	public List<DynamicObject> exec(@SuppressWarnings("rawtypes") List arguments) {
		if (arguments != null && arguments.size() == 2) {
			String dynamicBaseDn = (String) arguments.get(0);
			String dynamicFilter = (String) arguments.get(1);
			return context.loadObjects(dynamicBaseDn, dynamicFilter);
		} else {
			return new ArrayList<DynamicObject>();
		}
	}

}
