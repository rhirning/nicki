
package org.mgnl.nicki.core.util;

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


import java.util.HashMap;
import java.util.Map;

import org.mgnl.nicki.core.config.Config;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ObjectFactory<T> {
	public static final String PROPERTY_OBJECTS = "objects";
	public static final String SEPARATOR = ",";
	private Map<String, T> objectsMap;
	private String propertyBase;


	/**
	 * ObjectFactory with configured object classes
	 * Each object has a NAME. All names are configured in the property with name propertyBase + ".objects"
	 * The object classe are  configured as
	 * 		propertyBase + ".objects." NAME
	 * 
	 * @param propertyBase Name of the property base
	 */
	public ObjectFactory(String propertyBase) {
		this.propertyBase = propertyBase;
		initObjects();
	}
	
	public T getObject(String objectName) {
		return objectsMap.get(objectName);
	}

	private void initObjects() {
		objectsMap = new HashMap<>();
		String base = this.propertyBase + "." + PROPERTY_OBJECTS;
		for (String objectName : Config.getList(base, SEPARATOR)) {
			String className = Config.getString(base + "." + objectName);
			try {
				T object = Classes.newInstance(className);
				objectsMap.put(objectName, object);
				
			} catch (Exception e) {
				log.error("Error", e);
			}
		}
	}

	public boolean hasObject(String objectName) {
		return objectsMap.containsKey(objectName);
	}
}
