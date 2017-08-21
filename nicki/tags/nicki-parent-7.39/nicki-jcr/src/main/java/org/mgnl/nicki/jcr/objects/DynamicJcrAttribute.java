package org.mgnl.nicki.jcr.objects;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicAttribute;
import org.mgnl.nicki.core.objects.DynamicObject;

public class DynamicJcrAttribute extends DynamicAttribute implements Serializable {

	private static final long serialVersionUID = -8882067850828962053L;

	public DynamicJcrAttribute(String name, String ldapName,
			Class<?> attributeClass) {
		super(name, ldapName, attributeClass);
	}

	@Override
	public Class<?> getAttributeClass() {
		return null;
	}

	@Override
	public boolean isNaming() {
		return false;
	}

	@Override
	public void setNaming() {

	}

	@Override
	public boolean isMandatory() {
		return false;
	}

	@Override
	public void setMandatory() {

	}

	@Override
	public boolean isMultiple() {
		return false;
	}

	@Override
	public void setMultiple() {

	}

	@Override
	public boolean isForeignKey() {
		return false;
	}

	@Override
	public void setForeignKey(Class<? extends DynamicObject> classDefinition) {

	}

	@Override
	public void setForeignKey(String className) {

	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void setVirtual() {

	}

	@Override
	public boolean isVirtual() {
		return false;
	}

	@Override
	public List<? extends DynamicObject> getOptions(DynamicObject dynamicObject) {
		return null;
	}

	@Override
	public void setReadonly() {

	}

	@Override
	public boolean isReadonly() {
		return false;
	}

	@Override
	public Class<? extends DynamicObject> getForeignKeyClass() {
		return null;
	}

	@Override
	public void setEditorClass(String editorClass) {

	}

	@Override
	public String getEditorClass() {
		return null;
	}
	
	public static String getGetter(String name) {
		return "get" + StringUtils.capitalize(name);
	}

	public static String getMultipleGetter(String name) {
		return "get" + StringUtils.capitalize(name) + "s";
	}

	@Override
	public <T extends NickiContext> void init(T context,
			DynamicObject dynamicObject, ContextSearchResult rs) {
		
	}

	@Override
	public String getExternalName() {
		return null;
	}

}
