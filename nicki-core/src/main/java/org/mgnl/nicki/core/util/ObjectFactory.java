package org.mgnl.nicki.core.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectFactory<T> {
	private static final Logger LOG = LoggerFactory.getLogger(ObjectFactory.class);
	public static final String PROPERTY_OBJECTS = "objects";
	public static final String SEPARATOR = ",";
	private Map<String, T> objectsMap;
	private String propertyBase;


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
		String objectsNamesString = Config.getProperty(base);
		if (StringUtils.isNotEmpty(objectsNamesString)) {
			String objectNames[] = StringUtils.split(objectsNamesString, SEPARATOR);
			for (String objectName : objectNames) {
				String className = Config.getProperty(base + "." + objectName);
				try {
					T object = Classes.newInstance(className);
					objectsMap.put(objectName, object);
					
				} catch (Exception e) {
					LOG.error("Error", e);
				}
			}
		}
	}

	public boolean hasObject(String objectName) {
		return objectsMap.containsKey(objectName);
	}
}
