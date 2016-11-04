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
package org.mgnl.nicki.core.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.AnnotationHelper;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.util.Classes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TargetFactory {

	private static final Logger LOG = LoggerFactory.getLogger(TargetFactory.class);
	public static final String PROPERTY_BASE = "nicki.targets";
	public static final String PROPERTY_OBJECTS = "objects";
	public static final String PROPERTY_FACTORY = "factory";
	public static final String SEPARATOR = ",";
	
	
	private static TargetFactory instance = new TargetFactory();
	private Map<String, Target> targets= new HashMap<String, Target>();
	private String defaultTarget;
		
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
				initDynamicObjects(target);
				try {
					initContextFactory(target);
				} catch (Exception e) {
					LOG.error("Error", e);
				}
				targets.put(targetName, target);
			}
		}
	}


	private void initContextFactory(Target target) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String base = PROPERTY_BASE + "." + target.getName() + "." + PROPERTY_FACTORY;
		String factoryClassName = Config.getProperty(base);
		ContextFactory contextFactory = Classes.newInstance(factoryClassName);
		target.setContextFactory(contextFactory);
	}


	private void initDynamicObjects(Target target) {
		List<String> dynamicObjects = new ArrayList<String>();
		Map<String, DynamicObject> map = new HashMap<String, DynamicObject>();
		String base = PROPERTY_BASE + "." + target.getName() + "." + PROPERTY_OBJECTS;
		String objectsNames = Config.getProperty(base);
		if (StringUtils.isNotEmpty(objectsNames)) {
			String objects[] = StringUtils.split(objectsNames, SEPARATOR);
			for (int i = 0; i < objects.length; i++) {
				String className = Config.getProperty(base + "." + objects[i]);
				try {
					DynamicObject dynamicObject = getDynamicObject(className);
					initDataModel(dynamicObject);
					map.put(objects[i], dynamicObject);
					
					dynamicObjects.add(objects[i]);
				} catch (Exception e) {
					LOG.error("Error", e);
				}
			}
		}
		target.setDynamicObjects(dynamicObjects);
		target.setDynamicObjectsMap(map);
	}
	
	public static void initDataModel(DynamicObject dynamicObject) {

		if (dynamicObject.isAnnotated()) {
			AnnotationHelper.initAnnotationDataModel(dynamicObject);
		} else {
			LOG.error("only annotated dynamic objects supported: " + dynamicObject.getClass().getName());
		}
	}
	
	private DynamicObject getDynamicObject(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return Classes.newInstance(className);
	}

	public static Target getTarget(String targetName) {
		return instance.targets.get(targetName);
	}

	public static Target getDefaultTarget() {
		return getTarget(instance.defaultTarget);
	}

}
