package org.mgnl.nicki.core.util;

import org.mgnl.nicki.core.config.Config;

public class Classes {

    public static <T> T newInstance(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        final Class<T> clazz = forName(className);
        return clazz.newInstance();
    }

    @SuppressWarnings("unchecked")
    private static <C> Class<C> forName(String className) throws ClassNotFoundException {
    	String effectiveClassName = Config.getProperty(className, className);
        Class<C> loadedClass = null;
        try {
            loadedClass = (Class<C>) Class.forName(effectiveClassName);
        } catch (ClassNotFoundException e) {
        	loadedClass = null;
        }
        if (loadedClass == null) {
            loadedClass = (Class<C>) Class.forName(className);
        }
        return loadedClass;
    }



}
