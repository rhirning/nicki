
package org.mgnl.nicki.core.helper;

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


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.util.Classes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonHelper extends BeanUtilsHelper {

	public static <T> JsonArray toJsonArray(List<T> list) {
		
		JsonArrayBuilder builder = Json.createArrayBuilder();
		if (list != null) {
			for (T entry : list) {
				if (entry instanceof Map<?,?>) {
					@SuppressWarnings("unchecked")
					Map<String, String> mapEntry = (Map<String,String>) entry;
					builder.add(JsonHelper.toJsonObject(mapEntry));
				} else {
					builder.add(JsonHelper.toJsonObject(entry));
				}
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
	
	public static String prettyPrint(JsonStructure json) {
	    return jsonFormat(json, JsonGenerator.PRETTY_PRINTING);
	}

	public static String jsonFormat(JsonStructure json, String... options) {
	    StringWriter stringWriter = new StringWriter();
	    Map<String, Boolean> config = buildConfig(options);
	    JsonWriterFactory writerFactory = Json.createWriterFactory(config);
	    JsonWriter jsonWriter = writerFactory.createWriter(stringWriter);

	    jsonWriter.write(json);
	    jsonWriter.close();

	    return stringWriter.toString();
	}

	private static Map<String, Boolean> buildConfig(String... options) {
	    Map<String, Boolean> config = new HashMap<String, Boolean>();

	    if (options != null) {
	        for (String option : options) {
	            config.put(option, true);
	        }
	    }

	    return config;
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

	public static JsonArray toJsonArray(String stringData) {
		JsonReader reader = Json.createReader(new StringReader(stringData));
		return reader.readArray();
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
					log.debug("Error using " + field.getName());
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
	
	private static boolean contains(String[] attributes, String name) {
		return Arrays.asList(attributes).contains(name);
	}
	
	private static JsonArray toJsonArray(Collection<?> value) {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		for (Object entry : value) {
			if (entry instanceof String) {
				builder.add((String) entry);
				
			} else {
				if (Map.class.isAssignableFrom(entry.getClass())) {
					builder.add(toJsonMap((Map<?, ?>) entry));
				} else {
					builder.add(toJsonObject(entry));
				}
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
				log.debug("Error using " + field.getName());
			}
		}
		return builder.build();
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
		T bean;
		bean = Classes.newInstance(beanClazz);
		fillBeanWithProperties(beanClazz, bean, data);
		return bean;
	}
	
	private static void fillBeanWithProperties(Class<? extends Object> clazz, Object bean, JsonObject data) {
		Class<? extends Object> superClass = clazz.getSuperclass();
		if (superClass != null) {
			fillBeanWithProperties(superClass, bean, data);
		}
		
		for (Field field : clazz.getDeclaredFields()) {
			String key = field.getName();
			if (data.containsKey(key)) {
				log.debug(key + ":  nothing to do key is correct");
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
							Object entry = Classes.newInstance(field.getType());
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
							if(fieldArgTypes[0] instanceof ParameterizedType){
							    //ParameterizedType bType = (ParameterizedType) fieldArgTypes[0];								
								List<Object> list = new ArrayList<>();
								try {
									JsonArray array = data.getJsonArray(key);
									for (int i = 0; i < array.size(); i++) {
										try {
											JsonObject o = array.getJsonObject(i);
											list.add(getMap(o));
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
							} else {
								Class<?> entryClass = (Class<?>) fieldArgTypes[0];
							
								List<Object> list = new ArrayList<>();
								try {
									JsonArray array = data.getJsonArray(key);
									for (int i = 0; i < array.size(); i++) {
										try {
											if (entryClass == String.class) {
												list.add(array.getString(i));
											} else {
												Object entry = Classes.newInstance(entryClass);
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
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}			
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
					log.error("Could not parse list object: ", e.getMessage());
				}
			}
		}
		return list;
	}

	public static Collection<String> toList(JsonArray jsonArray) {
		List<String> list = new ArrayList<>();
		for (int i = 0; i < jsonArray.size(); i++) {
			list.add(jsonArray.getString(i));
		}
		return list;
	}
	
	public static JsonObject copy(JsonObject jsonObject) {
		return JsonHelper.toJsonObject(jsonObject.toString());
	}
	
	public static Map<String,String> getMap(JsonObject o) {
		Map<String,String> map = new HashMap<>();
		if (o != null) {
			for (String key : o.keySet()) {
				map.put(key, o.getString(key));
			}
		}
		return map;
	}

}
