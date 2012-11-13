package org.mgnl.nicki.ldap.context;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.context.BasicContext;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.context.ObjectFactory;
import org.mgnl.nicki.core.context.Target;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.ldap.core.DynamicObjectWrapper;
import org.mgnl.nicki.ldap.data.Query;
import org.mgnl.nicki.ldap.data.QueryHandler;
import org.mgnl.nicki.ldap.data.jndi.JndiSearchResult;
import org.mgnl.nicki.ldap.objects.BaseLdapDynamicObject;
import org.mgnl.nicki.ldap.query.AttributeLoaderLdapQueryHandler;
import org.mgnl.nicki.ldap.query.IsExistLdapQueryHandler;
import org.mgnl.nicki.ldap.query.ObjectLoaderLdapQueryHandler;
import org.mgnl.nicki.ldap.query.ObjectsLoaderLdapQueryHandler;
import org.mgnl.nicki.ldap.query.SubObjectsLoaderLdapQueryHandler;

public class LdapContext extends BasicContext implements NickiContext {

	private static final long serialVersionUID = -3079627211615613041L;

	private TargetObjectFactory objectFactory = null;

	public LdapContext(Target target, READONLY readonly) {
		super(target, readonly);
		// TODO Auto-generated constructor stub
	}
	
	
	protected DirContext getDirContext(String name, String password) throws NamingException {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, getTarget().getProperty("providerUrl"));
		env.put(Context.SECURITY_AUTHENTICATION, getTarget().getProperty("securityAuthentication"));
		env.put(Context.SECURITY_PRINCIPAL, name);
		env.put(Context.SECURITY_CREDENTIALS, password);
		String binaries = getTarget().getProperty("attributes.binary");
		if (StringUtils.isNotEmpty(binaries)) {
			env.put("java.naming.ldap.attributes.binary", binaries);
		}
		// Enable connection pooling
		env.put("com.sun.jndi.ldap.connect.pool", "true");

		return new InitialDirContext(env);
	}

	public DirContext getDirContext() throws DynamicObjectException {
		try {
			return getDirContext(getPrincipal().getName(), getPrincipal().getPassword());
		} catch (NamingException e) {
			throw new DynamicObjectException(e);
		}
	}
	
	public DynamicObject login(String username, String password) {
		DynamicObject user = loadObject(username);
		if (user == null) {
			List<DynamicObject> list = loadObjects(Config.getProperty("nicki.users.basedn"), "cn=" + username);
			if (list != null && list.size() == 1) {
				user = list.get(0);
			}
		}
		if (user != null) {
			try {
				getDirContext(user.getPath(), password);
				return user;
			} catch (Exception e) {
			}
		}
		return null;
	}

	@Override
	public DynamicObject createObject(DynamicObject dynamicObject)
			throws DynamicObjectException {
		if (this.isReadonly()) {
			throw new DynamicObjectException("READONLY: could not create object: " + dynamicObject.getPath());
		}
		DirContext ctx = null;
		try {
			ctx = getDirContext();
			ctx.bind(dynamicObject.getPath(), new DynamicObjectWrapper((BaseLdapDynamicObject) dynamicObject));
			// load new object
			DynamicObject newObject = loadObject(dynamicObject.getPath());
			/*
			// generate merge attributes Map from original object
			Map<DynamicAttribute, Object> changeAttributes = dynamicObject.getModel().getNonMandatoryAttributes(dynamicObject);
			// merge to new object
			newObject.merge(changeAttributes);
			// update new
			updateObject(newObject);
			*/
			return newObject;
		} catch (NamingException e) {
			throw new DynamicObjectException(e);
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					throw new DynamicObjectException(e);
				}
			}
		}
	}

	public void search(QueryHandler queryHandler) throws DynamicObjectException {
		DirContext ctx = null;
		NamingEnumeration<SearchResult> results = null;
		try {
			ctx = getDirContext();
			results = ctx.search(queryHandler.getBaseDN(), queryHandler.getFilter(), (SearchControls) queryHandler.getConstraints());
			List<ContextSearchResult> list = new ArrayList<ContextSearchResult>();
			try {
				while (results != null && results.hasMore()) {
					list.add(new JndiSearchResult(results.next()));
				}
			} catch (NamingException e) {
				e.printStackTrace();
			}
			queryHandler.handle(list);
		} catch (Throwable e) {
			throw new DynamicObjectException(e.getMessage());
		} finally {
			if (results != null) {
				try {
					results.close();
				} catch (NamingException e) {
					
					e.printStackTrace();
				}
			}
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					
					e.printStackTrace();
				}
			}
		}
	}

	public <T extends DynamicObject> void updateObject(T dynamicObject) throws DynamicObjectException {
		if (this.isReadonly()) {
			throw new DynamicObjectException("READONLY: could not modify object: " + dynamicObject.getPath());
		}
		BaseLdapDynamicObject ldapDynamicObject = (BaseLdapDynamicObject) dynamicObject;
		DirContext ctx = null;
		try {
			ctx = getDirContext();
			ctx.modifyAttributes(dynamicObject.getPath(),DirContext.REPLACE_ATTRIBUTE,ldapDynamicObject.getModel().getLdapAttributes(dynamicObject));
		} catch (NamingException e) {
			throw new DynamicObjectException(e);
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					throw new DynamicObjectException(e);
				}
			}
		}
	}

	public void deleteObject(DynamicObject dynamicObject)
			throws DynamicObjectException {
		if (this.isReadonly()) {
			throw new DynamicObjectException("READONLY: could not delete object: " + dynamicObject.getPath());
		}
		DirContext ctx = null;
		try {
			ctx = getDirContext();
			ctx.unbind(dynamicObject.getPath());
			dynamicObject.setOriginal(null);
		} catch (NamingException e) {
			throw new DynamicObjectException(e);
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					throw new DynamicObjectException(e);
				}
			}
		}
	}

	public DynamicObject moveObject(DynamicObject dynamicObject, String newPath)
			throws DynamicObjectException {
		if (this.isReadonly()) {
			throw new DynamicObjectException("READONLY: could not move object: " + dynamicObject.getPath());
		}
		if (isExist(newPath)) {
			throw new DynamicObjectException("Object exists: " + newPath);
		}
		DirContext ctx = null;
		try {
			ctx = getDirContext();
			ctx.rename(dynamicObject.getPath(), newPath);
			return loadObject(newPath);
		} catch (NamingException e) {
			throw new DynamicObjectException(e);
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					throw new DynamicObjectException(e);
				}
			}
		}
	}

	public DynamicObject renameObject(DynamicObject dynamicObject,
			String newName) throws DynamicObjectException {
		if (this.isReadonly()) {
			throw new DynamicObjectException("READONLY: could not rename object: " + dynamicObject.getPath());
		}
		String newPath = dynamicObject.getModel().getNamingLdapAttribute() + "=" + newName + "," + dynamicObject.getParentPath();
		return moveObject(dynamicObject, newPath);
	}



	public DynamicObject loadObject(String path) {
		ObjectLoaderLdapQueryHandler handler = new ObjectLoaderLdapQueryHandler(this, path);
		try {
			search(handler);
			return handler.getDynamicObject(); 
		} catch (DynamicObjectException e) {
		}
		return null;
	}

	public List<DynamicObject> loadObjects(String baseDn, String filter) {
		try {
			ObjectsLoaderLdapQueryHandler handler = new ObjectsLoaderLdapQueryHandler(this, baseDn, filter);
			search(handler);
			return handler.getList();
		} catch (DynamicObjectException e) {
		} 
		return null;
	}

	public List<DynamicObject> loadChildObjects(String parent,
			String filter) {
		try {
			SubObjectsLoaderLdapQueryHandler handler = new SubObjectsLoaderLdapQueryHandler(this, parent, filter);
			search(handler);
			return handler.getList();
		} catch (DynamicObjectException e) {
		} 
		return null;
	}

	public List<DynamicObject> loadReferenceObjects(Query query) {
		try {
			ObjectsLoaderLdapQueryHandler handler = new ObjectsLoaderLdapQueryHandler(this, query);
			search(handler);
			return handler.getList();
		} catch (DynamicObjectException e) {
		} 
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public 	<T extends DynamicObject> T loadObject(Class<T> classDefinition, String path) {
		ObjectLoaderLdapQueryHandler handler = new ObjectLoaderLdapQueryHandler(this, path);
		handler.setClassDefinition(classDefinition);
		try {
			search(handler);
			return (T) handler.getDynamicObject(); 
		} catch (DynamicObjectException e) {
		}
		return null;
	}

	public void loadObject(DynamicObject dynamicObject) throws DynamicObjectException {
		ObjectLoaderLdapQueryHandler handler = new ObjectLoaderLdapQueryHandler((BaseLdapDynamicObject) dynamicObject);
		search(handler);
	}

	@SuppressWarnings("unchecked")
	public <T extends DynamicObject> List<T> loadObjects(Class<T> classDefinition, String baseDn, String filter) {
		try {
			ObjectsLoaderLdapQueryHandler handler = new ObjectsLoaderLdapQueryHandler(this, baseDn, filter);
			handler.setClassDefinition(classDefinition);
			search(handler);
			return (List<T>) handler.getList();
		} catch (DynamicObjectException e) {
		} 
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T extends DynamicObject> List<T> loadChildObjects(Class<T> classDefinition, String parent,	String filter) {
		try {
			SubObjectsLoaderLdapQueryHandler handler = new SubObjectsLoaderLdapQueryHandler(this, parent, filter);
			handler.setClassDefinition(classDefinition);
			search(handler);
			return (List<T>) handler.getList();
		} catch (DynamicObjectException e) {
		} 
		return null;
	}
	
	
	public <T extends DynamicObject> T loadChildObject(Class<T> class1, DynamicObject parent, String namingValue) {
		try {
			String namingAttribute = 
				getLdapObjectFactory().getNamingLdapAttribute(class1);
			String path = namingAttribute + "=" + namingValue + "," + parent.getPath();
			return loadObject(class1, path);
		} catch (InstantiateDynamicObjectException e) {
			e.printStackTrace();
		}
		return null;
	}


	public ObjectFactory getObjectFactory() {
		return getLdapObjectFactory();
	}

	public TargetObjectFactory getLdapObjectFactory() {
		if (objectFactory == null) {
			objectFactory = new TargetObjectFactory(this, getTarget());
		}
		return objectFactory;
	}


	public <T extends DynamicObject> List<T> loadChildObjects(Class<T> class1, DynamicObject parent, String filter) {
		return loadChildObjects(class1, parent.getPath(), filter);
	}

	public void loadAttributes(DynamicObject dynamicObject, Class<?> requester, String[] attributes) throws DynamicObjectException {
		try {
			AttributeLoaderLdapQueryHandler handler = new AttributeLoaderLdapQueryHandler(dynamicObject, attributes);
			search(handler);
			for (String attribute : handler.getLists().keySet()) {
				dynamicObject.put(requester, attribute, handler.getLists().get(attribute));
			}
		} catch (DynamicObjectException e) {
		} 
	}


	@SuppressWarnings("unchecked")
	public <T extends DynamicObject> List<T> loadReferenceObjects(Class<T> classDefinition, Query query) {
		try {
			ObjectsLoaderLdapQueryHandler handler = new ObjectsLoaderLdapQueryHandler(this, query);
			handler.setClassDefinition(classDefinition);
			search(handler);
			return (List<T>) handler.getList();
		} catch (DynamicObjectException e) {
		} 
		return null;
	}
	
	public boolean isExist(String dn) {
		if (StringUtils.isEmpty(dn)) {
			return false;
		}
		try {
			IsExistLdapQueryHandler handler = new IsExistLdapQueryHandler(this, dn);
			search(handler);
			return handler.isExist();
		} catch (DynamicObjectException e) {
		}
		return false;
	}


}
