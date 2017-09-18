
package org.mgnl.nicki.core.context;

/*-
 * #%L
 * nicki-core
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


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
