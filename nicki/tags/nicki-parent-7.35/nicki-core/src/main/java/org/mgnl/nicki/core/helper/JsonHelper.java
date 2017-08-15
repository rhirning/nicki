package org.mgnl.nicki.core.helper;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JsonHelper {
    private static final Logger LOG = LoggerFactory.getLogger(JsonHelper.class);
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

	public static <T> JsonArray toJsonArray(List<T> list) {
		
		JsonArrayBuilder builder = Json.createArrayBuilder();
		if (list != null) {
			for (T entry : list) {
				builder.add(JsonHelper.toJsonObject(entry));
			}
		}
		return builder.build();
	}
	
	public static JsonObject toJsonObject(Map<String, String> data) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		for (String key : data.keySet()) {
			builder.add(key, data.get(key));
		}
		return builder.build();
	}
	
	public static Map<String, String> toMap(JsonObject data) {
		Map<String, String> map = new HashMap<>();
		if (data != null) {
			for (String key : data.keySet()) {
				map .put(key, data.getString(key));
			}
		}
		return map;
	}
	public static JsonObject toJsonObject(Properties data) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		Enumeration<?> keys = data.propertyNames();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			builder.add(key, data.getProperty(key));
		}
		return builder.build();
	}

	public static <T> JsonObject toJsonObject(T bean, String... attributes) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		fillJsonObject(builder, bean.getClass(), bean, attributes);
		return builder.build();
	}

	public static JsonObject toJsonObject(String stringData) {
		JsonReader reader = Json.createReader(new StringReader(stringData));
		return reader.readObject();
	}

	private static <T> void fillJsonObject(JsonObjectBuilder builder, Class<?> clazz, T bean, String... attributes) {
		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null) {
			fillJsonObject(builder, superClass, bean, attributes);
		}
		for (Field field : clazz.getDeclaredFields()) {
			if (attributes == null || attributes.length == 0 || contains(attributes, field.getName())) {
				try {
					Method getter = getGetter(clazz, field);
					if (getter != null) {
						Object value = getter.invoke(bean);
						if (value != null) {
							if (value instanceof String) {
								builder.add(field.getName(), (String) value);
							} else if (value instanceof Integer) {
								builder.add(field.getName(), Integer.toString((Integer) value));
							} else if (value instanceof Long) {
								builder.add(field.getName(), Long.toString((Long) value));
							} else if (value instanceof Boolean) {
								builder.add(field.getName(), Boolean.toString((Boolean) value).toUpperCase());
							} else if (value instanceof Date) {
								String format = getAnnotationData(field, "Format", "value");
								/*
								if (field.getAnnotation(Format.class) != null) {
									format = field.getAnnotation(Format.class).value();
								}
								*/
								if (format != null) {
									builder.add(field.getName(), getDateString((Date) value, format));
								} else {
									builder.add(field.getName(), DataHelper.getMilli((Date) value));
								}
							} else if (value instanceof Collection<?>) {
								builder.add(field.getName(), toJsonArray((Collection<?>) value));
							} else if (value instanceof Map) {
								builder.add(field.getName(), toJsonMap((Map<?,?>) value));
							} else if (value.getClass().isEnum()) {
								builder.add(field.getName(), value.toString());
							} else {
								builder.add(field.getName(), toJsonObject(value));
							}
						}
					}
				} catch (Exception e) {
					LOG.debug("Error using " + field.getName());
				}
			}
		}
		
	}

	private static String getAnnotationData(Field field, String className, String methodName) {
		for (Annotation annotation: field.getAnnotations()) {
			if (StringUtils.equals(annotation.annotationType().getSimpleName(), className)) {
				try {
					Method method = annotation.annotationType().getMethod(methodName);
					return (String) method.invoke(annotation);
				} catch (Exception  e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/*
	private static String getAnnotationData(Field field, Class<?> clazz, String methodName) {
		for (Annotation annotation: field.getAnnotations()) {
			if (clazz == annotation.getClass()) {
				try {
					Method method = annotation.annotationType().getMethod(methodName);
					return (String) method.invoke(annotation);
				} catch (Exception  e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	*/
	
	private static boolean contains(String[] attributes, String name) {
		return Arrays.asList(attributes).contains(name);
	}
	
	public static String getDateString(Date value, String formatString) {
		SimpleDateFormat format = new SimpleDateFormat(formatString);
		return format.format(value);
	}
	
	public static Date dateFromDateString(String stored, String formatString) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(formatString);
		return format.parse(stored);
	}
	
	private static JsonArray toJsonArray(Collection<?> value) {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		for (Object entry : value) {
			if (entry instanceof String) {
				builder.add((String) entry);
				
			} else {
				builder.add(toJsonObject(entry));
			}
		}
		return builder.build();
	}
	
	private static JsonObject toJsonMap(Map<?,?> map) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		for (Object key : map.keySet()) {
			if (key instanceof String) {
				builder.add((String) key, (String) map.get(key));
			}
		}
		return builder.build();
	}
	public static <T> JsonObject toJsonObject(Class<? super T> clazz, T bean) {
		Class<? super T> superClass = clazz.getSuperclass();
		if (superClass != null) {
			toJsonObject(superClass, bean);
		}
		JsonObjectBuilder builder = Json.createObjectBuilder();
		for (Field field : bean.getClass().getDeclaredFields()) {
			try {
				Method getter = getGetter(bean.getClass(), field);
				Object value = getter.invoke(bean);
				if (value instanceof String) {
					builder.add(field.getName(), (String) value);
				} else if (value instanceof Integer) {
					builder.add(field.getName(), (Integer) value);
				} else if (value instanceof Long) {
					builder.add(field.getName(), (Long) value);
				} else if (value instanceof Boolean) {
					builder.add(field.getName(), (Boolean) value);
				} else if (value instanceof Collection<?>) {
					builder.add(field.getName(), toJsonArray((Collection<?>) value));
				} else if (value instanceof Map) {
					builder.add(field.getName(), toJsonMap((Map<?,?>) value));
				} else {
					builder.add(field.getName(), toJsonObject(value));
				}
			} catch (Exception e) {
				LOG.debug("Error using " + field.getName());
			}
		}
		return builder.build();
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

	public static Method getSetter(Class<?> clazz, Field field) {
		String methodName = "set" + StringUtils.capitalize(field.getName());
		try {
			Method method = clazz.getMethod(methodName, field.getType());
			return method;
		} catch (NoSuchMethodException | SecurityException e) {
			LOG.debug("no setter for " + field.getName() + " in class " + clazz.getName());
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

	public static Field getField(Class<?> clazz, String name) {
		try {
			return clazz.getDeclaredField(name);
		} catch (NoSuchFieldException | SecurityException e) {
			LOG.debug("no field for " + name + " in class " + clazz.getName());
		}
		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null) {
			return getField(superClass, name);
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
			LOG.debug("no setter for " + field.getName() + " in class " + clazz.getName());
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
	
	/**
	 * Fills String type data from a Json Object into a bean. 
	 * @param beanClazz class of the bean to be filled
	 * @param data JsonObject
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException 
	 */
	public static <T> T toBean(Class<T> beanClazz, String stringData) throws IllegalAccessException, InvocationTargetException, InstantiationException {
		JsonReader reader = Json.createReader(new StringReader(stringData));
		JsonObject data = reader.readObject();
		return toBean(beanClazz, data);
	}
	public static <T> T toBean(Class<T> beanClazz, InputStream inputStream) throws IllegalAccessException, InvocationTargetException, InstantiationException {
		JsonReader reader = Json.createReader(new InputStreamReader(inputStream));
		JsonObject data = reader.readObject();
		return toBean(beanClazz, data);
	}

	public static <T> T toBean(Class<T> beanClazz, JsonObject data) throws IllegalAccessException, InvocationTargetException, InstantiationException {
		T bean = beanClazz.newInstance();
		fillBeanWithProperties(beanClazz, bean, data);
		return bean;
	}
	
	@SuppressWarnings("unchecked")
	private static void fillBeanWithProperties(Class<? extends Object> clazz, Object bean, JsonObject data) {
		Class<? extends Object> superClass = clazz.getSuperclass();
		if (superClass != null) {
			fillBeanWithProperties(superClass, bean, data);
		}
		
		for (Field field : clazz.getDeclaredFields()) {
			String key = field.getName();
			if (data.containsKey(key)) {
				LOG.debug(key + ":  nothing to do key is correct");
			} else if (data.containsKey(key.toLowerCase())) {
				key = key.toLowerCase();
			} else {
				key = null;
			}			
			
			if (key != null) {
				try {
					if (field.getType() == Date.class) {
						String format = getAnnotationData(field, "Format", "value");
						/*
						if (field.getAnnotation(Format.class) != null) {
							format = field.getAnnotation(Format.class).value();
						}
						*/
						if (format != null) {
							setProperty(bean, field, dateFromDateString(data.getString(key),
									format));
						} else {
							setProperty(bean, field, DataHelper.milliFromString(data.getString(key)));
						}
						//} catch (Exception e) {
						//	setProperty(bean, field, dateFromDisplayDay(data.getString(key)));
						//}
					} else if (field.getType() == int.class) {
						setProperty(bean, field, (int) Integer.parseInt(data.getString(key)));
					} else if (field.getType() == Integer.class) {
						if (data.get(key).getValueType() == ValueType.NUMBER) {
							setProperty(bean, field, data.getInt(key));
						} else {
							setProperty(bean, field, (Integer) Integer.parseInt(data.getString(key)));
						}
					} else if (field.getType() == long.class) {
						setProperty(bean, field, (long) Long.parseLong(data.getString(key)));
					} else if (field.getType() == Long.class) {
						setProperty(bean, field, (Long) Long.parseLong(data.getString(key)));
					} else if (field.getType() == boolean.class) {
						setProperty(bean, field, (boolean) DataHelper.booleanOf(data.getString(key)));
					} else if (field.getType() == Boolean.class) {
						setProperty(bean, field, (Boolean) DataHelper.booleanOf(data.getString(key)));
					} else if (field.getType() == Map.class) {
						Map<String, String> map = new HashMap<>();
//						for (JsonValue k : data.getJsonArray(key)) {
						if (data.get(key).getValueType() == ValueType.OBJECT) {
							JsonObject jsonMap = data.getJsonObject(key);
							for (String mapKey : jsonMap.keySet()) {
								map.put(mapKey, jsonMap.getString(mapKey));
							}
						} else if (data.get(key).getValueType() == ValueType.ARRAY) {
							JsonArray jsonMap = data.getJsonArray(key);
							for (JsonValue jsonEntry : jsonMap) {
								if (jsonEntry.getValueType() == ValueType.STRING) {
									String entry = ((JsonString)jsonEntry).getString();
									String[] parts = entry.split("=");
									if (parts != null && parts.length == 2) {
										map.put(parts[0], parts[1]);
									}
								}
							}
						} else if (data.get(key).getValueType() == ValueType.STRING) {
							String entry = data.getString(key);
							String[] parts = entry.split("=");
							if (parts != null && parts.length == 2) {
								map.put(parts[0], parts[1]);
							}
						}
						setProperty(bean, field, map);
					} else if (field.getType().isEnum()) {
						try {
							setProperty(bean, field, Enum.valueOf(field.getType().asSubclass(Enum.class), data.getString(key)));
						} catch (Exception e) {
							break;
						}
					} else if (field.getType() == String.class && data.get(key).getValueType() == ValueType.STRING) {
						setProperty(bean, field, data.getString(key));
					} else if (data.get(key).getValueType() == ValueType.STRING) {
						setProperty(bean, field, data.getString(key));
					} else if (data.get(key).getValueType() == ValueType.OBJECT) {
						JsonObject o = data.getJsonObject(key);
						try {
//							A entry = entryClazz.newInstance();
							Object entry = field.getType().newInstance();
							fillBeanWithProperties(entry.getClass(), entry, o);
							Method setter = getSetter(bean.getClass(), field, field.getType());
							if (setter != null) {
								setter.invoke(bean, entry);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}						
					} else if (data.get(key).getValueType() == ValueType.ARRAY
							&& field.getType().isAssignableFrom(List.class)) {

						Type genericFieldType = field.getGenericType();

						if(genericFieldType instanceof ParameterizedType){
						    ParameterizedType aType = (ParameterizedType) genericFieldType;
						    Type[] fieldArgTypes = aType.getActualTypeArguments();
							Class<?> entryClass = (Class<?>) fieldArgTypes[0];
						
							List<Object> list = new ArrayList<>();
							try {
								JsonArray array = data.getJsonArray(key);
								for (int i = 0; i < array.size(); i++) {
									try {
										if (entryClass == String.class) {
											list.add(array.getString(i));
										} else {
											Object entry = entryClass.newInstance();
											JsonObject o = array.getJsonObject(i);
											fillBeanWithProperties(entryClass, entry, o);
											list.add(entry);
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								Method setter = getSetter(bean.getClass(), field, field.getType());
								if (setter != null) {
									setter.invoke(bean, list);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}			
		}
	}
	/*
	private static <T, A> void XXXfillBeanWithEntry(Class<? super T> clazz, T bean, String attribute, Class<A> entryClazz, JsonObject data) {
		Class<? super T> superClass = clazz.getSuperclass();
		if (superClass != null) {
			XXXfillBeanWithEntry(superClass, bean, attribute, entryClazz, data);
		}
		
		for (Field field : clazz.getDeclaredFields()) {
			String key = field.getName();
			if (equals(attribute, key) 
					&& data.containsKey(key)
					&& data.get(key).getValueType() == ValueType.OBJECT) {
				// nothing to do key is correct
			} else if (data.containsKey(key.toLowerCase()) && data.get(key.toLowerCase()).getValueType() == ValueType.OBJECT) {
				key = key.toLowerCase();
			} else {
				key = null;
			}
			if (key != null) {
				JsonObject o = data.getJsonObject(key);
				try {
//					A entry = entryClazz.newInstance();
					A entry = (A) field.getType().newInstance();
					fillBeanWithProperties(entry.getClass(), entry, o);
					Method setter = getSetter(bean.getClass(), field, field.getType());
					if (setter != null) {
						setter.invoke(bean, entry);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static <T, A> void XXXfillBeanWithList(Class<? super T> clazz, T bean, Class<A> entryClazz, JsonObject data) {
		Class<? super T> superClass = clazz.getSuperclass();
		if (superClass != null) {
			XXXfillBeanWithList(superClass, bean, entryClazz, data);
		}
		

		for (Field field : clazz.getDeclaredFields()) {
			String key = field.getName();
			if (data.containsKey(key) && data.get(key).getValueType() == ValueType.ARRAY) {
				// nothing to do key ist correct
			} else if (data.containsKey(key.toLowerCase()) && data.get(key.toLowerCase()).getValueType() == ValueType.ARRAY) {
				key = key.toLowerCase();
			} else {
				key = null;
			}
			if (key != null) {
				List<A> list = new ArrayList<A>();
				try {
					JsonArray array = data.getJsonArray(key);
					for (int i = 0; i < array.size(); i++) {
						try {
							JsonObject o = array.getJsonObject(i);
							A entry = entryClazz.newInstance();
							fillBeanWithProperties(entryClazz, entry, o);
							list.add(entry);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					Method setter = getSetter(bean.getClass(), field, field.getType());
					if (setter != null) {
						setter.invoke(bean, list);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	*/
	
	
	/**`
	 * Fills String type data from a Json Object into a bean. Additionally a bean of type entryClazz will be filled
	 * @param beanClazz class of the bean to be filled
	 * @param attribute attribute name of the additional object
	 * @param entryClazz class of the entry to be filled
	 * @param data JsonObject
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException 
	 */
	/*
	public static <T, A> T toSingleEntryBean(Class<T> beanClazz, String attribute, Class<A> entryClazz, String stringData) throws IllegalAccessException, InvocationTargetException, InstantiationException {
		JsonReader reader = Json.createReader(new StringReader(stringData));
		JsonObject data = reader.readObject();
		T bean = beanClazz.newInstance();
		
		fillBeanWithProperties(beanClazz, bean, data);
		//fillBeanWithEntry(beanClazz, bean, attribute, entryClazz, data);
		return bean;
	}
	*/
	/**
	 * 
	 * @param beanClazz class of the bean to be filled. Additionally a List of beans of type entryClazz will be filled
	 * @param attribute attribute name of the List
	 * @param entryClazz class of the enties to be filled
	 * @param data JsonObject
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 */
	/*
	public static <T, A> T toListEntryBean(Class<T> beanClazz, String attribute, Class<A> entryClazz, String stringData) throws IllegalAccessException, InvocationTargetException, InstantiationException {
		JsonReader reader = Json.createReader(new StringReader(stringData));
		JsonObject data = reader.readObject();
		T bean = beanClazz.newInstance();
		
		fillBeanWithProperties(beanClazz, bean, data);
		//fillBeanWithList(beanClazz, bean, entryClazz, data);
		return bean;
	}
	*/
	
	private static <T> void setProperty(Object bean, Field field, T value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method setter = null;
		if (primitiveMap.containsKey(value.getClass())) {
			setter = getSetter(bean.getClass(), field, primitiveMap.get(value.getClass()));
		}
		
		if (setter == null) {
			setter = getSetter(bean.getClass(), field);
		}
		if (setter != null ) {
			setter.invoke(bean, value);
		}
	}

	public static String getString(JsonValue value) {
		if (value.getValueType() == ValueType.ARRAY) {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			int count = 0;
			for (JsonValue entry : (JsonArray)value) {
				if (count > 0) {
					sb.append(",");
				}
				count++;
				sb.append(getString(entry));
			}
			sb.append("]");
			return sb.toString();
		} else if (value.getValueType() == ValueType.OBJECT) {
			return value.toString();
		} else if (value.getValueType() == ValueType.NUMBER) {
			return Long.toString(((JsonNumber)value).longValue());
		} else if (value.getValueType() == ValueType.STRING) {
			return ((JsonString)value).getString();
		}
		return "";
	}

	public static <T> Collection<? extends T> toList(Class<T> clazz, JsonArray jsonArray) {
		List<T> list = new ArrayList<>();
		for (JsonValue jsonValue : jsonArray) {
			if (jsonValue.getValueType() == ValueType.OBJECT) {
				try {
					T object = toBean(clazz, jsonValue.toString());
					list.add(object);
				} catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
					LOG.error("Could not parse list object: ", e.getMessage());
				}
			}
		}
		return list;
	}

}
