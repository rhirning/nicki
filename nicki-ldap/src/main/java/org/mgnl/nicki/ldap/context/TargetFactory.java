/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
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
