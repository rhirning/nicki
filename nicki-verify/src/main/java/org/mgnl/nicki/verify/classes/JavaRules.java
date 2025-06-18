
package org.mgnl.nicki.verify.classes;

/*-
 * #%L
 * nicki-verify
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


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mgnl.nicki.core.util.Classes;
import org.mgnl.nicki.verify.annotations.Attribute;
import org.mgnl.nicki.verify.annotations.VerifyCommand;
import org.mgnl.nicki.verify.annotations.VerifyRule;

import lombok.extern.slf4j.Slf4j;


/**
 * The Class JavaRules.
 */
@Slf4j
public class JavaRules {
	
	/**
	 * Evaluate.
	 *
	 * @param data the data
	 * @param classNames the class names
	 * @return the list
	 * @throws MissingAttributeException the missing attribute exception
	 * @throws ClassNotFoundException the class not found exception
	 */
	public static List<ReferencedError> evaluate(Map<String, Object> data, String ...classNames) throws MissingAttributeException, ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<>();
		for (String className : classNames) {
			classes.add(Class.forName(className));
		}
		return evaluate(data, classes.toArray(new Class[0]));
	}
	
	/**
	 * Evaluate.
	 *
	 * @param data the data
	 * @param classes the classes
	 * @return the list
	 * @throws MissingAttributeException the missing attribute exception
	 */
	public static List<ReferencedError> evaluate(Map<String, Object> data, Class<?> ...classes) throws MissingAttributeException {
		List<ReferencedError> errors = new ArrayList<>();
		for (Class<?> clazz : classes) {
			try {
				errors.addAll(evaluateClass(data, clazz));
			} catch (InstantiationException | IllegalAccessException e) {
				log.error("invalid class " + clazz.getName() + ": " + e.getMessage());
			}
		}
		return errors;
	}

	/**
	 * Evaluate class.
	 *
	 * @param <T> the generic type
	 * @param data the data
	 * @param clazz the clazz
	 * @return the list
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws MissingAttributeException the missing attribute exception
	 */
	private static <T> List<ReferencedError> evaluateClass(Map<String, Object> data, Class<T> clazz) throws InstantiationException, IllegalAccessException, MissingAttributeException {
		List<ReferencedError> errors = new ArrayList<>();
		T instance = Classes.newInstance(clazz);
		injectData(instance, data);
		Method[] methods = clazz.getDeclaredMethods();
		if (methods != null) {
			for (Method method : methods) {
				if (method.isAnnotationPresent(VerifyRule.class)) {
					try {
						method.invoke(instance);
					} catch (IllegalArgumentException e) {
						log.error("invalid method " + clazz.getName() + "." + method.getName() + ": " + e.getClass());
					} catch (InvocationTargetException e) {
						if (e.getTargetException() instanceof ReferenceVerifyException) {
							ReferenceVerifyException verifyException = (ReferenceVerifyException) e.getTargetException();
							errors.addAll(verifyException.getErrors());
						}
					}
				}
			}
		}
		return errors;
	}

	/**
	 * Inject data.
	 *
	 * @param <T> the generic type
	 * @param instance the instance
	 * @param data the data
	 * @throws MissingAttributeException the missing attribute exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	private static <T> void injectData(T instance, Map<String, Object> data) throws MissingAttributeException, IllegalArgumentException, IllegalAccessException {
		for (Field field : instance.getClass().getDeclaredFields()) {
			if (field.isAnnotationPresent(Attribute.class)) {
				if (data.containsKey(field.getName()) && data.get(field.getName()) != null &&
						field.getType().isAssignableFrom(data.get(field.getName()).getClass())) {
					field.setAccessible(true);
					field.set(instance, data.get(field.getName()));
				}
			}
		}
	}
	
	/**
	 * Execute commands.
	 *
	 * @param data the data
	 * @param commands the commands
	 * @return the list
	 * @throws MissingAttributeException the missing attribute exception
	 */
	public static List<String> executeCommands(Map<String, Object> data, Object ...commands) throws MissingAttributeException {
		List<String> messages = new ArrayList<>();
		if (commands != null) {
			for (Object command : commands) {
				try {
					messages.addAll(executeCommand(data, command));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					log.error("invalid class " + command.getClass().getName() + ": " + e.getMessage());
				}
			}
		}
		return messages;
	}

	/**
	 * Execute command.
	 *
	 * @param data the data
	 * @param command the command
	 * @return the list
	 * @throws MissingAttributeException the missing attribute exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	public static List<String> executeCommand(Map<String, Object> data, Object command) throws MissingAttributeException, IllegalArgumentException, IllegalAccessException {
		List<String> messages = new ArrayList<>();
		injectData(command, data);
		Class<?> clazz = command.getClass();
		Method[] methods = clazz.getDeclaredMethods();
		if (methods != null) {
			for (Method method : methods) {
				if (method.isAnnotationPresent(VerifyCommand.class)) {
					try {
						method.invoke(command);
					} catch (IllegalArgumentException e) {
						log.error("invalid method " + clazz.getName() + "." + method.getName() + ": " + e.getClass());
					} catch (InvocationTargetException e) {
						if (e.getTargetException() instanceof MessageVerifyException) {
							MessageVerifyException verifyException = (MessageVerifyException) e.getTargetException();
							messages.addAll(verifyException.getMessages());
						}
					}
				}
			}
		}
		return messages;
	}

}
