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
package org.mgnl.nicki.ldap.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.ldap.objects.DynamicObject;

public class TargetFactory {

	public static final String PROPERTY_BASE = "nicki.targets";
	public static final String PROPERTY_OBJECTS = "objects";
	public static final String SEPARATOR = ",";
	
	
	private static TargetFactory instance = new TargetFactory();
	private Map<String, Target> targets= new HashMap<String, Target>();
	private String defaultTarget = null;
		
	private TargetFactory() {
		super();
		String targetNamesString = Config.getProperty(PROPERTY_BASE);
		if (StringUtils.isNotEmpty(targetNamesString)) {
			String targetNames[] = StringUtils.split(targetNamesString, SEPARATOR);
			defaultTarget = targetNames[0];
			for (int i = 0; i < targetNames.length; i++) {
				String targetName = targetNames[i];
				String base = PROPERTY_BASE + "." + targetName;
				Target target = new Target(targetName, base);
				target.setDynamicObjects(initDynamicObjects(targetName));
				targets.put(targetName, target);
			}
		}
	}

	private List<DynamicObject> initDynamicObjects(String target) {
		List<DynamicObject> list = new ArrayList<DynamicObject>();
		String base = PROPERTY_BASE + "." + target + "." + PROPERTY_OBJECTS;
		String objectsNames = Config.getProperty(base);
		if (StringUtils.isNotEmpty(objectsNames)) {
			String objects[] = StringUtils.split(objectsNames, SEPARATOR);
			for (int i = 0; i < objects.length; i++) {
				String className = Config.getProperty(base + "." + objects[i]);
				try {
					list.add(getDynamicObject(className));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}


	private DynamicObject getDynamicObject(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> classDefinition = Class.forName(className);
		DynamicObject object = (DynamicObject) classDefinition.newInstance();
		return object;
	}

	public static Target getTarget(String targetName) {
		return instance.targets.get(targetName);
	}

	public static Target getDefaultTarget() {
		return getTarget(instance.defaultTarget);
	}

}
