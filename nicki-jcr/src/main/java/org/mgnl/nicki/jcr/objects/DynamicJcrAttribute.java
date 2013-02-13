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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isNaming() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setNaming() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isMandatory() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setMandatory() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isMultiple() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setMultiple() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isForeignKey() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setForeignKey(Class<? extends DynamicObject> classDefinition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setForeignKey(String className) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setVirtual() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isVirtual() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<? extends DynamicObject> getOptions(DynamicObject dynamicObject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setReadonly() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isReadonly() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void XXXsetStatic() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean XXXisStatic() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Class<? extends DynamicObject> getForeignKeyClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEditorClass(String editorClass) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getEditorClass() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getExternalName() {
		// TODO Auto-generated method stub
		return null;
	}

}
