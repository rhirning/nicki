/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.template.engine;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.context.Target;
import org.mgnl.nicki.ldap.context.TargetFactory;
import org.mgnl.nicki.ldap.objects.DynamicObject;

public class TestData {

	public static final String SEPARATOR = "=";
	private DynamicObject dynamicObject;
	
	public TestData(DynamicObject dynamicObject) {
		super();
		this.dynamicObject = dynamicObject;
	}

	public Target getTarget() {
		@SuppressWarnings("unchecked")
		List<String> testData = (List<String>) dynamicObject.get("testData");
		if (testData != null) {
			for (Iterator<String> iterator = testData.iterator(); iterator.hasNext();) {
				String value = iterator.next();
				String name = StringUtils.substringBefore(value, SEPARATOR);
				String data = StringUtils.substringAfter(value, SEPARATOR);
				if (StringUtils.equals(name, "target")) {
					if (StringUtils.isNotEmpty(data)) {
						Target t = TargetFactory.getTarget(data);
						if (t != null) {
							return t;
						}
					}
				}
			}
		}
		return TargetFactory.getDefaultTarget();
	}

	public Map<String, Object> getData(NickiContext context) {
		// Create the root hash
		Map<String, Object> root = new HashMap<String, Object>();
		return root;
	}
	
	public  Map<String, String> getObjects() {
		Map<String, String> objects = new HashMap<String, String>();
		objects.put("person", "cn=ablake,ou=users,o=utopia");
		return objects;
	}
	
	public Map<String, Object> getDataModel(NickiContext context) {
		Map<String, Object> dataModel = new HashMap<String, Object>();
		@SuppressWarnings("unchecked")
		List<String> testData = (List<String>) dynamicObject.get("testData");
		if (testData != null) {
			for (Iterator<String> iterator = testData.iterator(); iterator.hasNext();) {
				String value = iterator.next();
				String name = StringUtils.substringBefore(value, SEPARATOR);
				String data = StringUtils.substringAfter(value, SEPARATOR);
				if (StringUtils.isNotEmpty(data)) {
					if (context.isExist(data)) {
						dataModel.put(name, context.loadObject(data));
					} else {
						dataModel.put(name, data);
					}
				}
			}
		}
		return dataModel;
	}


}
	
