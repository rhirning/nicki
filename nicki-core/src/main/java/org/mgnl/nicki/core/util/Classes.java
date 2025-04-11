
package org.mgnl.nicki.core.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.stream.Collectors;

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


import org.mgnl.nicki.core.config.Config;

// TODO: Auto-generated Javadoc
/**
 * The Class Classes.
 */
public class Classes {

    /**
     * New instance.
     *
     * @param <T> the generic type
     * @param className the class name
     * @return the t
     * @throws ClassNotFoundException the class not found exception
     * @throws InstantiationException the instantiation exception
     * @throws IllegalAccessException the illegal access exception
     */
    public static <T> T newInstance(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        final Class<T> clazz = forName(className);
        try {
			return clazz.getDeclaredConstructor().newInstance();
		} catch (InvocationTargetException
				| NoSuchMethodException e) {
			throw new InstantiationException();
		}
    }

    /**
     * New instance.
     *
     * @param <T> the generic type
     * @param clazz the clazz
     * @return the t
     * @throws InstantiationException the instantiation exception
     * @throws IllegalAccessException the illegal access exception
     */
    public static <T> T newInstance(Class<T> clazz) throws InstantiationException, IllegalAccessException {
        try {
			return clazz.getDeclaredConstructor().newInstance();
		} catch (InvocationTargetException
				| NoSuchMethodException e) {
			throw new InstantiationException();
		}
    }

    /**
     * For name.
     *
     * @param <C> the generic type
     * @param className the class name
     * @return the class
     * @throws ClassNotFoundException the class not found exception
     */
    @SuppressWarnings("unchecked")
    private static <C> Class<C> forName(String className) throws ClassNotFoundException {
    	String effectiveClassName = Config.getString(className, className);
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
    
    /**
     * Find all classes in package.
     *
     * @param packageName the package name
     * @return the sets the
     */
    public static Set<Class<?>> findAllClassesInPackage(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
          .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
          .filter(line -> line.endsWith(".class"))
          .map(line -> getClass(line, packageName))
          .collect(Collectors.toSet());
    }
 
    /**
     * Gets the class.
     *
     * @param className the class name
     * @param packageName the package name
     * @return the class
     */
    private static Class<?> getClass(String className, String packageName) {
        try {
            return forName(packageName + "."
              + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            // handle the exception
        }
        return null;
    }



}
