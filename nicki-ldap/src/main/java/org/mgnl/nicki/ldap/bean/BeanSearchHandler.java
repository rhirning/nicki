package org.mgnl.nicki.ldap.bean;

/*-
 * #%L
 * nicki-ldap
 * %%
 * Copyright (C) 2017 - 2024 Ralf Hirning
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.context.BeanQueryHandler;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.helper.AnnotationHelper;
import org.mgnl.nicki.core.helper.AttributeMapper;
import org.mgnl.nicki.core.helper.BeanUtilsHelper;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.util.Classes;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class BeanSearchHandler<T> extends BasicLdapHandler implements BeanQueryHandler {
	public enum FORMATTER {
		NO;
		
		public String format(String value) {
			return value;
		}
	}
	
	private @Getter @Setter String baseDN;
	private @Getter List<T> entries;
	private String[] fields;
	private Class<T> beanClass;
	private @Getter String filter;
	private Predicate<T> beanFilter;

	public BeanSearchHandler(Class<T> beanClass, String ldapFilter, Predicate<T> beanFilter) {
		super();
		this.beanClass = beanClass;
		this.filter = ldapFilter;
		this.beanFilter = beanFilter;
		this.baseDN = Config.getString("nicki.users.basedn");
	}
	
	public List<T> getEffectiveEntries() {
		if (beanFilter != null) {
			return getEntries().stream().filter(beanFilter).collect(Collectors.toList());
		} else {
			return getEntries();
		}

	}

	public void handle(NickiContext ctx, NamingEnumeration<SearchResult> results)
			throws NamingException {
		entries = new ArrayList<T>();
		while(results != null && results.hasMore()) {
			SearchResult result = results.next();
			try {
				T entry = Classes.newInstance(beanClass);
				for (Field field : beanClass.getDeclaredFields()) {
					Object value = getValue(result, field);
					if (field.isAnnotationPresent(LdapAttribute.class)) {
						LdapAttribute ldapAttribute = field.getAnnotation(LdapAttribute.class);
						if (ldapAttribute.dn()) {
							BeanUtilsHelper.setValue(entry, field.getName(), result.getNameInNamespace());
						} else {
							if (value != null) {
								if (field.getType() == List.class) {
									BeanUtilsHelper.setValue(entry, field.getName(), value);
								} else {
									BeanUtilsHelper.setValue(entry, field.getName(), format(ldapAttribute, (String) value));
								}
							}
						}
					} else {
						BeanUtilsHelper.setValue(entry, field.getName(), value);
					}
				}
				addContext(entry, ctx);
				entries.add(entry);
			} catch (InstantiationException | IllegalAccessException e) {
				throw new NamingException("Could not create " + beanClass.getName());
			}
		}
	}
	
	private void addContext(T entry, NickiContext ctx) {
		for (Field field : beanClass.getDeclaredFields()) {
			if (field.getType().isAssignableFrom(NickiContext.class)) {
				BeanUtilsHelper.setValue(entry, field.getName(), ctx);
			}
		}
	}

	private Object getValue(SearchResult r, Field field) {
		LdapAttribute ldapAttribute = field.getAnnotation(LdapAttribute.class);
		String attributeName = ldapAttribute == null || StringUtils.isBlank(ldapAttribute.ldapName()) ? field.getName() : ldapAttribute.ldapName();
		Attribute attribute = r.getAttributes().get(attributeName);
		if (null != attribute) {
			try {
				if (field.getType() == List.class) {
					List<Object> list = new ArrayList<Object>();
					if (attribute.size() > 1) {
						for (int i=0; i < attribute.size(); i++) {
							list.add(attribute.get(i));
						}
					} else {
						list.add(attribute.get());
					}
					return list;
				} else {
					return attribute.get();
				}
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private String format(LdapAttribute ldapAttribute, String value) {
		return ldapAttribute.formatter().format(value);
	}
	
	protected String[] getReturningAttributes() {
		if (this.fields == null) {
			List<String> fields = new ArrayList<String>();
			for (Field field : beanClass.getDeclaredFields()) {
				String name = field.getName();
				if (field.isAnnotationPresent(LdapAttribute.class)) {
					LdapAttribute ldapAttribute = field.getAnnotation(LdapAttribute.class);
					if (ldapAttribute.isLdapAttribute()&& StringUtils.isNotBlank(ldapAttribute.ldapName())) {
						name = ldapAttribute.ldapName();
					}
				}
				fields.add(name);
			}
			this.fields = fields.toArray(new String[0]);
		} return this.fields;
	}

	@Override
	public SCOPE getScope() {
		return SCOPE.SUBTREE;
	}

	public static String getGetter(String name) {
		return "get" + StringUtils.capitalize(name);
	}
	
	public JsonObject toJsonObject(T bean, AttributeMapper mapping) {
		return toJsonObjectBuilder(bean, mapping).build();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> JsonObjectBuilder toJsonObjectBuilder(T bean, AttributeMapper mapping) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		for (Field field : bean.getClass().getDeclaredFields()) {
			boolean hidden = mapping.isHiddenInternal(field.getName());
			String format = "";
			if (field.isAnnotationPresent(LdapAttribute.class)) {
				LdapAttribute ldapAttribute = field.getAnnotation(LdapAttribute.class);
				hidden |= ldapAttribute.hidden();
				format = ldapAttribute.format();
			}
			if (!hidden) {
				String key = null;
				if (mapping != null) {
					if (mapping.isStrict() && !mapping.hasInternal(field.getName())) {
						key = null;
					} else {
						key = mapping.toExternal(field.getName());
					}
				}
				if (key != null) {
					Method getter = null;
					try {
						getter = bean.getClass().getMethod(getGetter(field.getName()));
					} catch (NoSuchMethodException | SecurityException e) {
						log.debug("no getter for " + field.getName());
					}
					if (AnnotationHelper.isMultiple(field)) {

						List<String> list = null;
						if (getter != null) {
							try {
								list = (List<String>) getter.invoke(bean);
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								log.debug("wrong getter for " + field.getName(), e);
							}
						}
						if (list != null && list.size() > 0) {
							JsonArrayBuilder lb = Json.createArrayBuilder();
							for (String entry : list) {
								lb.add(entry);
							}
							builder.add(key, lb);
						}
					} else {
						Object value = null;
						if (getter != null) {
							try {
								value = getter.invoke(bean);
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								log.debug("wrong getter for " + field.getName(), e);
							}
						}
						if (value != null) {
							if (value instanceof String) {
								builder.add(key, mapping.correctValue(key, (String) value));
							} else if (value instanceof Date) {
								if (StringUtils.isNotBlank(format)) {
									SimpleDateFormat formater = new SimpleDateFormat(format);
									builder.add(key, formater.format((Date)value));
								} else {
									builder.add(key, DataHelper.getMilli((Date)value));
								}
							} else if (value instanceof Boolean) {
								builder.add(key, ((Boolean) value).toString());
							}
						}
					}
				}
			}
		}
		return builder;
	}
}
