/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
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
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.context.Target;
import org.mgnl.nicki.core.context.TargetFactory;
import org.mgnl.nicki.core.objects.DynamicObject;

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
	
