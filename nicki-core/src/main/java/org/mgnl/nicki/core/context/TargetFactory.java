
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

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.AnnotationHelper;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.util.Classes;

import lombok.extern.slf4j.Slf4j;


/**
 * A factory for creating Target objects.
 */
@Slf4j
public final class TargetFactory {

	/** The Constant PROPERTY_BASE. */
	public static final String PROPERTY_BASE = "nicki.targets";
	
	/** The Constant PROPERTY_OBJECTS. */
	public static final String PROPERTY_OBJECTS = "objects";
	
	/** The Constant PROPERTY_FACTORY. */
	public static final String PROPERTY_FACTORY = "factory";
	
	/** The Constant SEPARATOR. */
	public static final String SEPARATOR = ",";
	
	
	/** The instance. */
	private static TargetFactory instance = new TargetFactory();
	
	/** The targets. */
	private Map<String, Target> targets= new HashMap<String, Target>();
	
	/** The default target. */
	private String defaultTarget;
		
	/**
	 * Instantiates a new target factory.
	 */
	private TargetFactory() {
		super();
		String targetNamesString = Config.getString(PROPERTY_BASE);
		if (StringUtils.isNotEmpty(targetNamesString)) {
			List<String> targetNames = Config.getList(PROPERTY_BASE, SEPARATOR);
			defaultTarget = targetNames.get(0);
			for (String targetName : targetNames) {
				String base = PROPERTY_BASE + "." + targetName;
				Target target = new Target(targetName, base);
				initDynamicObjects(target);
				try {
					initContextFactory(target);
				} catch (Exception e) {
					log.error("Error", e);
				}
				targets.put(targetName, target);
			}
		}
	}


	/**
	 * Inits the context factory.
	 *
	 * @param target the target
	 * @throws ClassNotFoundException the class not found exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	private void initContextFactory(Target target) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String base = PROPERTY_BASE + "." + target.getName() + "." + PROPERTY_FACTORY;
		String factoryClassName = Config.getString(base);
		ContextFactory contextFactory = Classes.newInstance(factoryClassName);
		target.setContextFactory(contextFactory);
	}


	/**
	 * Inits the dynamic objects.
	 *
	 * @param target the target
	 */
	private void initDynamicObjects(Target target) {
		List<String> dynamicObjects = new ArrayList<String>();
		Map<String, DynamicObject> map = new HashMap<String, DynamicObject>();
		String base = PROPERTY_BASE + "." + target.getName() + "." + PROPERTY_OBJECTS;
		String objectsNames = Config.getString(base);
		if (StringUtils.isNotEmpty(objectsNames)) {
			for (String entry : Config.getList(base, SEPARATOR)) {
				String className = Config.getString(base + "." + entry);
				try {
					DynamicObject dynamicObject = getDynamicObject(className);
					initDataModel(dynamicObject);
					map.put(entry, dynamicObject);
					
					dynamicObjects.add(entry);
				} catch (Exception e) {
					log.error("Error", e);
				}
			}
		}
		target.setDynamicObjects(dynamicObjects);
		target.setDynamicObjectsMap(map);
	}
	
	/**
	 * Inits the data model.
	 *
	 * @param dynamicObject the dynamic object
	 */
	public static void initDataModel(DynamicObject dynamicObject) {

		if (dynamicObject.isAnnotated()) {
			AnnotationHelper.initAnnotationDataModel(dynamicObject);
		} else {
			log.error("only annotated dynamic objects supported: " + dynamicObject.getClass().getName());
		}
	}
	
	/**
	 * Gets the dynamic object.
	 *
	 * @param className the class name
	 * @return the dynamic object
	 * @throws ClassNotFoundException the class not found exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	private DynamicObject getDynamicObject(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return Classes.newInstance(className);
	}

	/**
	 * Gets the target.
	 *
	 * @param targetName the target name
	 * @return the target
	 */
	public static Target getTarget(String targetName) {
		return instance.targets.get(targetName);
	}

	/**
	 * Gets the default target.
	 *
	 * @return the default target
	 */
	public static Target getDefaultTarget() {
		return getTarget(instance.defaultTarget);
	}

}
