package org.mgnl.nicki.db.helper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.db.annotation.Attribute;
import org.mgnl.nicki.db.data.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public enum Type {
	STRING(String.class),
	TIMESTAMP(Date.class),
	DATE(Date.class),
	LONG(Long.class, long.class),
	INT(Integer.class, int.class),
	UNKONWN();
	
	private static final Logger LOG = LoggerFactory.getLogger(Type.class);
	
	private List<Class<?>> classes = new ArrayList<>();
	
	private Type(Class<?> ...classes){
		if (classes != null) {
			for (Class<?> clazz : classes) {
				this.classes.add(clazz);
			}
		}
		
	}

	public boolean match(Class<?> clazz) {
		for (Class<?> c : this.classes) {
			if (c.isAssignableFrom(clazz)) {
				return true;
			}
		}
		return false;
	}
	
	public Type getType(Class<?> clazz) {
		for (Type type : values()) {
			if (match(clazz)) {
				return type;
			}
		}
		return UNKONWN;
	}
	
	public static Type getBeanAttributeType(Class<?> beanClazz, String name){

		Type type = Type.UNKONWN;
		Field field;
		try {
			field = beanClazz.getDeclaredField(name);
			if (field.getAnnotation(Attribute.class) != null) {
				Attribute attribute = field.getAnnotation(Attribute.class);
				if (!attribute.autogen()) {
					try {
						if (field.getType() == String.class) {
							type = Type.STRING;
						} else if (field.getType() == Date.class) {
							if (attribute.type() == DataType.TIMESTAMP) {
								type = Type.TIMESTAMP;
							} else {
								type = Type.DATE;
							}
						} else if (field.getType() == long.class || field.getType() == Long.class) {
							type = Type.LONG;
						} else if (field.getType() == int.class || field.getType() == Integer.class) {
							type = Type.INT;
						}
					} catch (SecurityException | IllegalArgumentException e) {
						LOG.error("Error fill statement", e);
					}
				}
			}
		} catch (NoSuchFieldException | SecurityException e1) {
			type = Type.UNKONWN;
		}

		
		LOG.debug(name + " is = " + type + "'");
		return type;
	}


}
