package org.mgnl.nicki.core.helper;

/*-
 * #%L
 * nicki-core
 * %%
 * Copyright (C) 2017 - 2018 Ralf Hirning
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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BeanUtilsHelper {
	public static Map<Class<?>, Class<?>> primitiveMap = new HashMap<>();
	static {
		primitiveMap.put(Boolean.class, boolean.class);
		//primitiveMap.put(Byte.class, byte.class);
		//primitiveMap.put(Character.class, char.class);
		//primitiveMap.put(Short.class, short.class);
		//primitiveMap.put(Integer.class, int.class);
		//primitiveMap.put(Long.class, long.class);
		//primitiveMap.put(Float.class, float.class);
		//primitiveMap.put(Double.class, double.class);
	}

	static String[] prefixes = new String[]{"get", "is"};
	public static Method getGetter(Class<?> clazz, Field field) {
		
		for (String prefix : prefixes) {
			String methodName = prefix + StringUtils.capitalize(field.getName());
			for (Method method : clazz.getDeclaredMethods()) {
				if (StringUtils.equals(methodName, method.getName())) {
					return method;
				}
			}
		}
		
		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null) {
			return getGetter(superClass, field);
		} else {
			return null;
		}
	}
	public static Method getGetter(Class<?> clazz, String fieldName) {
		
		for (String prefix : prefixes) {
			String methodName = prefix + StringUtils.capitalize(fieldName);
			for (Method method : clazz.getDeclaredMethods()) {
				if (StringUtils.equals(methodName, method.getName())) {
					return method;
				}
			}
		}
		
		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null) {
			return getGetter(superClass, fieldName);
		} else {
			return null;
		}
	}

	public static Method getSetter(Class<?> clazz, Field field) {
		String methodName = "set" + StringUtils.capitalize(field.getName());
		try {
			Method method = clazz.getMethod(methodName, field.getType());
			return method;
		} catch (NoSuchMethodException | SecurityException e) {
			log.debug("no setter for " + field.getName() + " in class " + clazz.getName());
		}
		/*
		for (Method method : clazz.getDeclaredMethods()) {
			if (equals(methodName, method.getName()) 
					&& method.getParameterTypes() == new Class[]{paramClazz}) {
				return method;
			}
		}
		*/
		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null) {
			return getSetter(superClass, field, field.getType());
		} else {
			return null;
		}
	}

	public static Method getSetter(Class<?> clazz, Field field, Class<? extends Object> paramClazz) {
		String methodName = "set" + StringUtils.capitalize(field.getName());
		try {
			Method method = clazz.getMethod(methodName, paramClazz);
			return method;
		} catch (NoSuchMethodException | SecurityException e) {
			log.debug("no setter for " + field.getName() + " in class " + clazz.getName());
		}
		/*
		for (Method method : clazz.getDeclaredMethods()) {
			if (equals(methodName, method.getName()) 
					&& method.getParameterTypes() == new Class[]{paramClazz}) {
				return method;
			}
		}
		*/
		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null) {
			return getSetter(superClass, field, paramClazz);
		} else {
			return null;
		}
	}
	
	public static String getDateString(Date value, String formatString) {
		SimpleDateFormat format = new SimpleDateFormat(formatString);
		return format.format(value);
	}
	
	public static Date dateFromDateString(String stored, String formatString) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(formatString);
		return format.parse(stored);
	}

	public static Field getField(Class<?> clazz, String name) {
		try {
			return clazz.getDeclaredField(name);
		} catch (NoSuchFieldException | SecurityException e) {
			log.debug("no field for " + name + " in class " + clazz.getName());
		}
		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null) {
			return getField(superClass, name);
		} else {
			return null;
		}
	}
	
	public static <T> void setProperty(Object bean, Field field, T value) {
		Method setter = null;
		if (value != null && primitiveMap.containsKey(value.getClass())) {
			setter = getSetter(bean.getClass(), field, primitiveMap.get(value.getClass()));
		}
		
		if (setter == null) {
			setter = getSetter(bean.getClass(), field);
		}
		if (setter != null ) {
			try {
				setter.invoke(bean, value);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				log.error("Error setting property", e);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getProperty(Object bean, Field field, Class<T> clazz) {
		if (bean != null) {
			Method getter = null;
			getter = getGetter(bean.getClass(), field);
			if (getter != null ) {
				try {
					return (T) getter.invoke(bean);
				} catch (Exception e) {
					log.error("Error reading property", e);
				}
			}
		}
		return null;
	}
	
	public static Object getProperty(Object bean, Field field) {
		if (bean != null) {
			Method getter = null;
			getter = getGetter(bean.getClass(), field);
			if (getter != null ) {
				try {
					return getter.invoke(bean);
				} catch (Exception e) {
					log.error("Error reading property", e);
				}
			}
		}
		return null;
	}
	
	public static Object getProperty(Object bean, String fieldName) {
		if (bean != null) {
			Method getter = null;
			getter = getGetter(bean.getClass(), fieldName);
			if (getter != null ) {
				try {
					return getter.invoke(bean);
				} catch (Exception e) {
					log.error("Error reading property", e);
				}
			}
		}
		return null;
	}
}
