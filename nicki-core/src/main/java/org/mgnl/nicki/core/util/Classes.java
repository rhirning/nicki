
package org.mgnl.nicki.core.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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

public class Classes {

    public static <T> T newInstance(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        final Class<T> clazz = forName(className);
        return clazz.newInstance();
    }

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
    
    public static Set<Class<?>> findAllClassesInPackage(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
          .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
          .filter(line -> line.endsWith(".class"))
          .map(line -> getClass(line, packageName))
          .collect(Collectors.toSet());
    }
 
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
