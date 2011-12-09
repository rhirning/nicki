/**
 * This file Copyright (c) 2011 deron Consulting GmbH
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.mailtemplate.test;

import java.util.HashMap;
import java.util.Map;

import org.mgnl.nicki.dynamic.objects.objects.Function;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.template.engine.TemplateHelper;

public class TestData {

	public static Map<String, Object> getData(NickiContext context) {
		

		// Create the root hash
		Map<String, Object> root = new HashMap<String, Object>();
		Function function = null;
		try {
			function = new Function(context);
			function.setContext(context);
			function.initDataModel();
			root.put("function", function);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return root;
	}
	
	public static Map<String, String> getObjects() {
		Map<String, String> objects = new HashMap<String, String>();
		objects.put("person", "cn=ablake,ou=users,o=data");
		return objects;
	}
	
	public static Map<String, Object> getDataModel(NickiContext context) {
		return TemplateHelper.getDataModel(context, getData(context), getObjects());
	}


}
