package org.mgnl.nicki.core.helper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.annotation.AdditionalObjectClass;
import org.mgnl.nicki.core.annotation.Child;
import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicReferenceAttribute;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.annotation.RemoveDynamicAttribute;
import org.mgnl.nicki.core.annotation.StructuredDynamicAttribute;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.objects.ChildFilter;
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
		
		RemoveDynamicAttribute removeDynamicAttribute = modelClass.getAnnotation(RemoveDynamicAttribute.class);
		if (removeDynamicAttribute != null) {
			for (String attributeName : removeDynamicAttribute.value()) {
				dynamicObject.removeAttribute(attributeName);
			}
		}
		
		Annotation[] annotations = modelClass.getAnnotations();
		for (Annotation annotation : annotations) {
			if (annotation instanceof Child) {
				Child childAnnotation = (Child) annotation;
				ChildFilter childFilter = new ChildFilter();
				if (StringUtils.isNotBlank(childAnnotation.filter())) {
					childFilter.setFilter(childAnnotation.filter());
				}
				if (childAnnotation.objectFilter() != null && childAnnotation.objectFilter().length > 0) {
					for (Class<? extends DynamicObject> objectFilter : childAnnotation.objectFilter()) {
						childFilter.addObjectFilter(objectFilter);
					}
				}
				dynamicObject.addChild(childAnnotation.name(), childFilter);
			}
		}

	
		for (Field field : modelClass.getDeclaredFields()) {
			if (field.isAnnotationPresent(DynamicAttribute.class)) {
				LOG.debug("DynamicAttribute: " + field.getName());
				DynamicAttribute dAttribute = field
						.getAnnotation(DynamicAttribute.class);
				org.mgnl.nicki.core.objects.DynamicAttribute dynAttribute = new org.mgnl.nicki.core.objects.DynamicAttribute(
						field.getName(), dAttribute.externalName(),
						field.getType());
				if (field.getType().isArray()) {
					dynAttribute.setMultiple();
				}
				if (dAttribute.naming()) {
					dynAttribute.setNaming();
				}
				if (dAttribute.search()) {
					dynAttribute.setSearchable(true);
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
				if (StringUtils.isNotBlank(dAttribute.editorClass())) {
					dynAttribute.setEditorClass(dAttribute.editorClass());
				}
				if (StringUtils.isNotBlank(dAttribute.searchFieldClass())) {
					dynAttribute.setSearchFieldClass(dAttribute.searchFieldClass());
				}
				if (dAttribute.foreignKey() != null && dAttribute.foreignKey().length > 0) {
					for (Class<? extends DynamicObject> clazz : dAttribute.foreignKey()) {
						dynAttribute.setForeignKey(clazz);
					}
				}
				if (StringUtils.isNotBlank(dAttribute.caption())) {
					dynAttribute.setCaption(dAttribute.caption());
				}
				
				dynamicObject.addAttribute(dynAttribute);
			} else if (field.isAnnotationPresent(StructuredDynamicAttribute.class)) {
				LOG.debug("StructuredDynamicAttribute: " + field.getName());
				StructuredDynamicAttribute dAttribute = field
						.getAnnotation(StructuredDynamicAttribute.class);
				org.mgnl.nicki.core.objects.StructuredDynamicAttribute dynAttribute = null;
				try {
					dynAttribute = new org.mgnl.nicki.core.objects.StructuredDynamicAttribute(
						field.getName(), dAttribute.externalName(),
						field.getType());
				} catch (Exception e) {
					LOG.error("Field=" + field.getName(), e);
				}
				if (field.getType().isArray()) {
					dynAttribute.setMultiple();
				}
				if (dAttribute.naming()) {
					dynAttribute.setNaming();
				}
				if (dAttribute.search()) {
					dynAttribute.setSearchable(true);
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
				if (StringUtils.isNotBlank(dAttribute.editorClass())) {
					dynAttribute.setEditorClass(dAttribute.editorClass());
				}
				if (StringUtils.isNotBlank(dAttribute.searchFieldClass())) {
					dynAttribute.setSearchFieldClass(dAttribute.searchFieldClass());
				}
				if (dAttribute.foreignKey() != null && dAttribute.foreignKey().length > 0) {
					for (Class<? extends DynamicObject> clazz : dAttribute.foreignKey()) {
						dynAttribute.setForeignKey(clazz);
					}
				}
				if (StringUtils.isNotBlank(dAttribute.caption())) {
					dynAttribute.setCaption(dAttribute.caption());
				}
				
				dynamicObject.addAttribute(dynAttribute);
			} else if (field.isAnnotationPresent(DynamicReferenceAttribute.class)) {
				LOG.debug("DynamicReferenceAttribute: " + field.getName() + "(" + field.getType() + ")");
				DynamicReferenceAttribute dAttribute = field
						.getAnnotation(DynamicReferenceAttribute.class);
				DynamicReference dynAttribute = new DynamicReference(dAttribute.reference(),
						field.getName(),
						Config.getProperty(dAttribute.baseProperty()),
						dAttribute.externalName(),
						field.getType());
				if (field.getType().isArray()) {
					dynAttribute.setMultiple();
				}
				if (dAttribute.naming()) {
					dynAttribute.setNaming();
				}
				if (dAttribute.search()) {
					dynAttribute.setSearchable(true);
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
				if (StringUtils.isNotBlank(dAttribute.editorClass())) {
					dynAttribute.setEditorClass(dAttribute.editorClass());
				}
				if (StringUtils.isNotBlank(dAttribute.searchFieldClass())) {
					dynAttribute.setSearchFieldClass(dAttribute.searchFieldClass());
				}
				if (dAttribute.foreignKey() != null && dAttribute.foreignKey().length > 0) {
					for (Class<? extends DynamicObject> clazz : dAttribute.foreignKey()) {
						dynAttribute.setForeignKey(clazz);
					}
				}
				if (StringUtils.isNotBlank(dAttribute.caption())) {
					dynAttribute.setCaption(dAttribute.caption());
				}
				
				dynamicObject.addAttribute(dynAttribute);
			}
		}
	}
	



}
