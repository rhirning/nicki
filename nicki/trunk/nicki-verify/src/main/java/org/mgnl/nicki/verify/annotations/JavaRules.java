package org.mgnl.nicki.verify.annotations;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mgnl.nicki.verify.VerifyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaRules {
	private static final Logger LOG = LoggerFactory.getLogger(JavaRules.class);
	
	public static List<String> evaluate(Map<String, Object> data, String ...classNames) throws MissingAttribute, ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<>();
		for (String className : classNames) {
			classes.add(Class.forName(className));
		}
		return evaluate(data, classes.toArray(new Class[]{}));
	}
	
	public static List<String> evaluate(Map<String, Object> data, Class<?> ...classes) throws MissingAttribute {
		List<String> errors = new ArrayList<>();
		for (Class<?> clazz : classes) {
			try {
				errors.addAll(evaluateClass(data, clazz));
			} catch (InstantiationException | IllegalAccessException e) {
				LOG.error("invalid class " + clazz.getName() + ": " + e.getMessage());
			}
		}
		return errors;
	}

	private static <T> List<String> evaluateClass(Map<String, Object> data, Class<T> clazz) throws InstantiationException, IllegalAccessException, MissingAttribute {
		List<String> errors = new ArrayList<>();
		checkAttributes(clazz, data);
		T instance = clazz.newInstance();
		Method[] methods = clazz.getDeclaredMethods();
		if (methods != null) {
			for (Method method : methods) {
				if (method.isAnnotationPresent(VerifyRule.class)) {
					try {
						method.invoke(instance, data);
					} catch (IllegalArgumentException e) {
						LOG.error("invalid method " + clazz.getName() + "." + method.getName() + ": " + e.getClass());
					} catch (InvocationTargetException e) {
						if (e.getTargetException() instanceof VerifyException) {
							VerifyException verifyException = (VerifyException) e.getTargetException();
							errors.addAll(verifyException.getErrors());
						}
					}
				}
			}
		}
		return errors;
	}

	private static void checkAttributes(Class<?> clazz, Map<String, Object> data) throws MissingAttribute {
		if (clazz.isAnnotationPresent(Verify.class)) {
			Verify verify = clazz.getAnnotation(Verify.class);
			for (Attribute attribute : verify.value()) {
				if (!data.containsKey(attribute.name()) ||
						!(attribute.type().isAssignableFrom(data.get(attribute.name()).getClass()))) {
					throw new MissingAttribute(attribute.name());
				}
			}
		}
	}

}
