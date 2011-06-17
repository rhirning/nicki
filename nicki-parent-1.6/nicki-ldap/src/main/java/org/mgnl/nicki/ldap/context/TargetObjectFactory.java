package org.mgnl.nicki.ldap.context;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.ldap.objects.ContextSearchResult;
import org.mgnl.nicki.ldap.objects.DynamicObject;

public class TargetObjectFactory implements ObjectFactory {

	public static final String PROPERTY_BASE = "nicki.template.objectfactory";
	public static final String PROPERTY_OBJECTS = "objects";
	public static final String SEPARATOR = ",";
	
	private NickiContext context;
	private Target target;
	
	protected TargetObjectFactory(NickiContext context, Target target) {
		super();
		this.context = context;
		this.target = target;
	}

	public DynamicObject getObject(ContextSearchResult rs) throws InstantiateDynamicObjectException {
		DynamicObject object =  _getObject(rs);
		object.setContext(context);
		return object;
	}

	
	
	private synchronized  DynamicObject _getObject(ContextSearchResult rs) throws InstantiateDynamicObjectException {
		String dn = rs.getNameInNamespace();
		for (Iterator<DynamicObject> iterator = target.getDynamicObjects().iterator(); iterator.hasNext();) {
			DynamicObject dynamicObject = iterator.next();
			if (dynamicObject.accept(rs)) {
				DynamicObject result = getDynamicObject(dynamicObject, rs);
				if (result != null) {
					return result;
				}
			}
		}
		throw new InstantiateDynamicObjectException("Could not getObject " + dn);
	}

	// TODO
	private DynamicObject findDynamicObject(Class<?> classDefinition) throws InstantiateDynamicObjectException {
		for (Iterator<DynamicObject> iterator = target.getDynamicObjects().iterator(); iterator.hasNext();) {
			DynamicObject dynamicObject = iterator.next();
			if (dynamicObject.getClass() == classDefinition) {
				return dynamicObject;
			}
		}
		throw new InstantiateDynamicObjectException("Could not getObject " + classDefinition);
	}
	
	// TODO
	private DynamicObject findDynamicObject(String className) throws InstantiateDynamicObjectException {
		for (Iterator<DynamicObject> iterator = target.getDynamicObjects().iterator(); iterator.hasNext();) {
			DynamicObject dynamicObject = iterator.next();
			if (StringUtils.equals(dynamicObject.getClass().getName(), className)) {
				return dynamicObject;
			}
		}
		throw new InstantiateDynamicObjectException("Could not getObject " + className);
	}
	

	public DynamicObject getDynamicObject(Class<?> classDefinition) throws InstantiateDynamicObjectException {
		DynamicObject storedDynamicObject = findDynamicObject(classDefinition);
		DynamicObject dynamicObject = null;
		try {
			dynamicObject = getDynamicObject(storedDynamicObject);
			dynamicObject.setContext(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dynamicObject;
	}
	
	public DynamicObject getDynamicObject(String className) throws InstantiateDynamicObjectException {
		DynamicObject storedDynamicObject = findDynamicObject(className);
		DynamicObject dynamicObject = null;
		try {
			dynamicObject = getDynamicObject(storedDynamicObject);
			dynamicObject.setContext(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dynamicObject;
	}
	
	private DynamicObject getDynamicObject(DynamicObject pattern, ContextSearchResult rs) throws InstantiateDynamicObjectException {
		try {
			DynamicObject object = (DynamicObject ) getDynamicObject(pattern);
			object.init(context, rs);
			return object;
		} catch (Exception e) {
			throw new InstantiateDynamicObjectException(e);
		}
	}

	private DynamicObject getDynamicObject(DynamicObject pattern) throws InstantiateDynamicObjectException {
		try {
			DynamicObject object = (DynamicObject ) pattern.getClass().newInstance();
			object.setModel(pattern.getModel());
			return object;
		} catch (Exception e) {
			throw new InstantiateDynamicObjectException(e);
		}
	}

	@Override
	public DynamicObject getDynamicObject(Class<?> classDefinition, 
			String parentPath, String namingValue) throws InstantiateDynamicObjectException {
		try {
			DynamicObject object = getDynamicObject(classDefinition);
			object.setContext(context);
			object.init(parentPath, namingValue);
			return object;
		} catch (Exception e) {
			throw new InstantiateDynamicObjectException(e);
		}
	}
	
	public  DynamicObject createNewDynamicObject(Class<?> classDefinition, 
			String parentPath, String namingValue) throws InstantiateDynamicObjectException {
		try {
			DynamicObject object = getDynamicObject(classDefinition, parentPath, namingValue);
			object.setContext(context);
			if (!context.isExist(object.getPath())) {
				object.create();
				return context.loadObject(object.getPath());
			}
			return null;
		} catch (Exception e) {
			throw new InstantiateDynamicObjectException(e);
		}
	}
	
}
