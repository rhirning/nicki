package org.mgnl.nicki.jcr.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mgnl.nicki.core.objects.DataModel;
import org.mgnl.nicki.core.objects.DynamicAttribute;
import org.mgnl.nicki.core.objects.DynamicObject;

public class JcrDataModel implements DataModel {
	

	@Override
	public <T extends DynamicAttribute> List<T> getMandatoryAttributes() {
		// TODO Auto-generated method stub
		return new ArrayList<T>();
	}

	@Override
	public <T extends DynamicAttribute> List<T> getOptionalAttributes() {
		// TODO Auto-generated method stub
		return new ArrayList<T>();
	}

	@Override
	public <T extends DynamicAttribute> List<T> getListOptionalAttributes() {
		// TODO Auto-generated method stub
		return new ArrayList<T>();
	}

	@Override
	public <T extends DynamicAttribute> List<T> getForeignKeys() {
		// TODO Auto-generated method stub
		return new ArrayList<T>();
	}

	@Override
	public <T extends DynamicAttribute> List<T> getListForeignKeys() {
		// TODO Auto-generated method stub
		return new ArrayList<T>();
	}

	@Override
	public Map<String, String> getChildren() {
		// TODO Auto-generated method stub
		return new HashMap<String, String>();
	}

	@Override
	public void addChild(String attribute, String filter) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addObjectClasses(String objectClass) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addAdditionalObjectClasses(String objectClass) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends DynamicAttribute> Map<T, Object> getNonMandatoryAttributes(
			DynamicObject dynamicObject) {
		// TODO Auto-generated method stub
		return new HashMap<T, Object>();
	}

	@Override
	public String getNamingLdapAttribute() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DynamicAttribute getDynamicAttribute(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean childrenAllowed() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isComplete(DynamicObject dynamicObject) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public <T extends DynamicAttribute> Map<String, T> getAttributes() {
		// TODO Auto-generated method stub
		return new HashMap<String, T>();
	}

	@Override
	public List<String> getAdditionalObjectClasses() {
		// TODO Auto-generated method stub
		return new ArrayList<String>();
	}

	@Override
	public String getObjectClassFilter() {
		// TODO Auto-generated method stub
		return "";
	}

}
