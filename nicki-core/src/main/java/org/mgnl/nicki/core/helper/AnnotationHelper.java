package org.mgnl.nicki.core.helper;

import java.lang.reflect.Field;

import org.mgnl.nicki.core.annotation.AdditionalObjectClass;
import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicReferenceAttribute;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.annotation.StructuredDynamicAttribute;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotationHelper {
	
	private static final Logger LOG = LoggerFactory.getLogger(AnnotationHelper.class);

	public static String[] getObjectClasses(Class<?> clazz) {
		ObjectClass oClass = clazz.getAnnotation(ObjectClass.class);
		if (oClass != null) {
			return oClass.value();
		}
		return new String[]{};
	}

	public static String[] getAdditionalObjectClasses(Class<?> clazz) {
		AdditionalObjectClass oClass = clazz.getAnnotation(AdditionalObjectClass.class);
		if (oClass != null) {
			return oClass.value();
		}
		return new String[]{};
	}

	private static boolean initObjectClass(Class<?> clazz) {
		ObjectClass oClass = clazz.getAnnotation(ObjectClass.class);
		if (oClass != null) {
			return oClass.init();
		}
		return false;
	}

	public static void initAnnotationDataModel(DynamicObject dynamicObject) {
		initAnnotationDataModel(dynamicObject, dynamicObject.getClass());
	}

	public static void initAnnotationDataModel(DynamicObject dynamicObject, Class<?> modelClass) {
		if (modelClass.getSuperclass() != null) {
			initAnnotationDataModel(dynamicObject, modelClass.getSuperclass());
		}
		LOG.debug("initAnnotationDataModel: " + modelClass);
		if (initObjectClass(modelClass)) {
			dynamicObject.getModel().getObjectClasses().clear();
		}
		for (String objectClass : getObjectClasses(modelClass)) {
			dynamicObject.addObjectClass(objectClass);
		}
		for (String objectClass : getAdditionalObjectClasses(modelClass)) {
			dynamicObject.addAdditionalObjectClass(objectClass);
		}
	
		for (Field field : modelClass.getDeclaredFields()) {
			if (field.isAnnotationPresent(DynamicAttribute.class)) {
				LOG.debug("DynamicAttribute: " + field.getName());
				DynamicAttribute dAttribute = field
						.getAnnotation(DynamicAttribute.class);
				org.mgnl.nicki.core.objects.DynamicAttribute dynAttribute = new org.mgnl.nicki.core.objects.DynamicAttribute(
						field.getName(), dAttribute.externalName(),
						field.getClass());
				if (field.getClass().isArray()) {
					dynAttribute.setMultiple();
				}
				if (dAttribute.naming()) {
					dynAttribute.setNaming();
				}
				if (dAttribute.mandatory()) {
					dynAttribute.setMandatory();
				}
				if (dAttribute.virtual()) {
					dynAttribute.setVirtual();
				}
				if (dAttribute.readonly()) {
					dynAttribute.setReadonly();
				}
				if (dAttribute.foreignKey() != null && dAttribute.foreignKey().length > 0) {
					for (Class<? extends DynamicObject> clazz : dAttribute.foreignKey()) {
						dynAttribute.setForeignKey(clazz);
					}
				}
				
				dynamicObject.addAttribute(dynAttribute);
			} else if (field.isAnnotationPresent(StructuredDynamicAttribute.class)) {
				LOG.debug("StructuredDynamicAttribute: " + field.getName());
				DynamicAttribute dAttribute = field
						.getAnnotation(DynamicAttribute.class);
				org.mgnl.nicki.core.objects.StructuredDynamicAttribute dynAttribute
					= new org.mgnl.nicki.core.objects.StructuredDynamicAttribute(
						field.getName(), dAttribute.externalName(),
						field.getClass());
				if (field.getClass().isArray()) {
					dynAttribute.setMultiple();
				}
				if (dAttribute.naming()) {
					dynAttribute.setNaming();
				}
				if (dAttribute.mandatory()) {
					dynAttribute.setMandatory();
				}
				if (dAttribute.virtual()) {
					dynAttribute.setVirtual();
				}
				if (dAttribute.readonly()) {
					dynAttribute.setReadonly();
				}
				if (dAttribute.foreignKey() != null && dAttribute.foreignKey().length > 0) {
					for (Class<? extends DynamicObject> clazz : dAttribute.foreignKey()) {
						dynAttribute.setForeignKey(clazz);
					}
				}
				
				dynamicObject.addAttribute(dynAttribute);
			} else if (field.isAnnotationPresent(DynamicReferenceAttribute.class)) {
				LOG.debug("DynamicReferenceAttribute: " + field.getName());
				DynamicReferenceAttribute dAttribute = field
						.getAnnotation(DynamicReferenceAttribute.class);
				DynamicReference dynAttribute = new DynamicReference(dAttribute.reference(),
						field.getName(),
						Config.getProperty(dAttribute.baseProperty()),
						dAttribute.externalName(),
						field.getClass());
				if (field.getClass().isArray()) {
					dynAttribute.setMultiple();
				}
				if (dAttribute.naming()) {
					dynAttribute.setNaming();
				}
				if (dAttribute.mandatory()) {
					dynAttribute.setMandatory();
				}
				if (dAttribute.virtual()) {
					dynAttribute.setVirtual();
				}
				if (dAttribute.readonly()) {
					dynAttribute.setReadonly();
				}
				if (dAttribute.foreignKey() != null && dAttribute.foreignKey().length > 0) {
					for (Class<? extends DynamicObject> clazz : dAttribute.foreignKey()) {
						dynAttribute.setForeignKey(clazz);
					}
				}
				
				dynamicObject.addAttribute(dynAttribute);
			}
		}
	}
	



}
