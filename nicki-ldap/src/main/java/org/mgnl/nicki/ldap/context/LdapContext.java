package org.mgnl.nicki.ldap.context;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.context.BasicContext;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.context.DynamicObjectFactory;
import org.mgnl.nicki.core.context.Target;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.data.QueryHandler;
import org.mgnl.nicki.core.data.SearchQueryHandler;
import org.mgnl.nicki.core.helper.AnnotationHelper;
import org.mgnl.nicki.core.methods.ReferenceMethod;
import org.mgnl.nicki.core.objects.ChildFilter;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicAttribute.CREATEONLY;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectAdapter;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.ldap.core.DynamicObjectWrapper;
import org.mgnl.nicki.ldap.core.LdapQuery;
import org.mgnl.nicki.core.data.Query;
import org.mgnl.nicki.ldap.data.jndi.JndiSearchResult;
import org.mgnl.nicki.ldap.helper.LdapHelper;
import org.mgnl.nicki.ldap.helper.LdapHelper.LOGIC;
import org.mgnl.nicki.ldap.query.AttributeLoaderLdapQueryHandler;
import org.mgnl.nicki.ldap.query.IsExistLdapQueryHandler;
import org.mgnl.nicki.ldap.query.LdapSearchHandler;
import org.mgnl.nicki.ldap.query.ObjectLoaderLdapQueryHandler;
import org.mgnl.nicki.ldap.query.ObjectsLoaderQueryHandler;
import org.mgnl.nicki.ldap.query.SubObjectsLoaderQueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapContext extends BasicContext implements NickiContext {
	private static final Logger LOG = LoggerFactory.getLogger(LdapContext.class);

	private static final long serialVersionUID = -3079627211615613041L;

	private TargetObjectFactory objectFactory;

	public LdapContext(DynamicObjectAdapter adapter, Target target, READONLY readonly) {
		super(adapter, target, readonly);
	}
	
	
	protected DirContext getDirContext(String name, String password) throws NamingException {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, getTarget().getProperty("providerUrl"));
		if (password.length() < 100) {
			env.put(Context.SECURITY_AUTHENTICATION, getTarget().getProperty("securityAuthentication"));
		} else {
			env.put(Context.SECURITY_AUTHENTICATION, getTarget().getProperty("ticketSecurityAuthentication",
					getTarget().getSecurityAuthentication()));
		}
		env.put(Context.SECURITY_PRINCIPAL, name);
		env.put(Context.SECURITY_CREDENTIALS, password);
		String binaries = getTarget().getProperty("attributes.binary");
		if (StringUtils.isNotEmpty(binaries)) {
			env.put("java.naming.ldap.attributes.binary", binaries);
		}
		String referral = getTarget().getProperty("context.referral");
		if (StringUtils.isNotEmpty(referral)) {
			env.put(Context.REFERRAL, referral);
		}
		// Set pooling timeout
		String connectionPoolTimeout = getTarget().getProperty("connect.pool.timeout", "1800000");
		env.put("com.sun.jndi.ldap.connect.pool.timeout", connectionPoolTimeout);

		return new InitialDirContext(env);
	}

	public DirContext getDirContext() throws DynamicObjectException {
		try {
			return getDirContext(getPrincipal().getName(), getPrincipal().getPassword());
		} catch (NamingException e) {
			throw new DynamicObjectException(e);
		}
	}

	@Override
	public DynamicObject login(String username, String password) {
		LOG.info("login: start");
		DynamicObject user = loadObject(username);
		if (user == null) {
			LOG.info("login: loadObject not successful");
			List<DynamicObject> list = loadObjects(Config.getProperty("nicki.users.basedn"), "cn=" + username);
			
			if (list != null && list.size() == 1) {
				LOG.info("login: loadObjectssuccessful");
				user = list.get(0);
			} else {
				LOG.info("login: loadObjects not successful");
				LOG.debug("Loading Objects not successful: " 
						+ ((list == null)?"null":"size=" + list.size()));
			}
		} else {
			LOG.info("login: loadObject successful");
		}
		if (user != null) {
			LOG.info("login: before getDirContex)");
			LOG.debug("try login for user " + user.getDisplayName());
			try {
				getDirContext(user.getPath(), password);
				LOG.info("login: after getDirContext");
				return user;
			} catch (Exception e) {
				LOG.debug("Could not login user " + username, e);
			}
		} else {
			LOG.debug("could not load user " + username);
		}
		LOG.info("login: end");
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
			ctx.bind(dynamicObject.getPath(), new DynamicObjectWrapper(dynamicObject));
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

	@Override
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
				LOG.error("Error", e);
			}
			queryHandler.handle(list);
		} catch (Throwable e) {
			throw new DynamicObjectException(e.getMessage());
		} finally {
			if (results != null) {
				try {
					results.close();
				} catch (NamingException e) {
					
					LOG.error("Error", e);
				}
			}
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					
					LOG.error("Error", e);
				}
			}
		}
	}

	@Override
	public void updateObject(DynamicObject dynamicObject) throws DynamicObjectException {
		if (this.isReadonly()) {
			throw new DynamicObjectException("READONLY: could not modify object: " + dynamicObject.getPath());
		}
		DirContext ctx = null;
		try {
			ctx = getDirContext();
			ctx.modifyAttributes(dynamicObject.getPath(),DirContext.REPLACE_ATTRIBUTE, dynamicObject.getModel().getLdapAttributes(dynamicObject, CREATEONLY.FALSE));
		} catch (NamingException e) {
			String details = getDetails(dynamicObject, CREATEONLY.FALSE);
			LOG.error(details);
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

	@Override
	public void updateObject(DynamicObject dynamicObject, String[] attributeNames) throws DynamicObjectException {
		if (this.isReadonly()) {
			throw new DynamicObjectException("READONLY: could not modify object: " + dynamicObject.getPath());
		}
		DirContext ctx = null;
		try {
			ctx = getDirContext();
			ctx.modifyAttributes(dynamicObject.getPath(),DirContext.REPLACE_ATTRIBUTE,
					dynamicObject.getModel().getLdapAttributes(dynamicObject, attributeNames, CREATEONLY.FALSE));
		} catch (NamingException e) {
			String details = getDetails(dynamicObject, CREATEONLY.FALSE);
			LOG.error(details);
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

	private String getDetails(DynamicObject dynamicObject, CREATEONLY createOnly) {
		Attributes attributes = dynamicObject.getModel().getLdapAttributes(dynamicObject, createOnly);
		return attributes.toString();
	}


	@Override
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

	@Override
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

	@Override
	public DynamicObject renameObject(DynamicObject dynamicObject,
			String newName) throws DynamicObjectException {
		if (this.isReadonly()) {
			throw new DynamicObjectException("READONLY: could not rename object: " + dynamicObject.getPath());
		}
		String newPath = dynamicObject.getModel().getNamingLdapAttribute() + "=" + newName + "," + dynamicObject.getParentPath();
		return moveObject(dynamicObject, newPath);
	}


	@Override
	public DynamicObject loadObject(String path) {
		ObjectLoaderLdapQueryHandler handler = new ObjectLoaderLdapQueryHandler(this, path);
		try {
			search(handler);
			return handler.getDynamicObject(); 
		} catch (DynamicObjectException e) {
			LOG.debug("Could not load object: " + path, e);
		}
		return null;
	}
	
	@Override
	public List<DynamicObject> loadObjects(String baseDn, String filter) {
		try {
			ObjectsLoaderQueryHandler handler = new ObjectsLoaderQueryHandler(this, baseDn, filter);
			search(handler);
			return handler.getList();
		} catch (DynamicObjectException e) {
			LOG.debug("Could not load objects: " + filter + " under "+ baseDn, e);
		} 
		return null;
	}

	@Override
	public List<DynamicObject> loadChildObjects(String parent,
			ChildFilter filter) {
		try {
			StringBuilder sb = new StringBuilder();
			if (filter.hasObjectFilters()) {
				for (Class<? extends DynamicObject> objecFilter : filter.getObjectFilters()) {
					StringBuilder sb2 = new StringBuilder();
					for (String objectClass : AnnotationHelper.getObjectClasses(objecFilter)) {
						LdapHelper.addQuery(sb2, "objectClass=" + objectClass, LOGIC.AND);
					}
					LdapHelper.addQuery(sb, sb2.toString(), LOGIC.OR);
				}
			} else if (filter.hasFilter()) {
				sb.append(filter.getFilter());
			} else {
				sb.append("objectClass=*");
			}

			SubObjectsLoaderQueryHandler handler = new SubObjectsLoaderQueryHandler(this, parent, sb.toString());
			search(handler);
			return handler.getList();
		} catch (DynamicObjectException e) {
			return null;
		}
	}

	@Override
	public List<DynamicObject> loadReferenceObjects(Query query) {
		try {
			ObjectsLoaderQueryHandler handler = new ObjectsLoaderQueryHandler(this, query);
			search(handler);
			return handler.getList();
		} catch (DynamicObjectException e) {
			return null;
		} 
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public 	<T extends DynamicObject> T loadObject(Class<T> classDefinition, String path) {
		ObjectLoaderLdapQueryHandler handler = new ObjectLoaderLdapQueryHandler(this, path);
		handler.setClassDefinition(classDefinition);
		try {
			search(handler);
			return (T) handler.getDynamicObject(); 
		} catch (DynamicObjectException e) {
			return null;
		}
	}

	@Override
	public void loadObject(DynamicObject dynamicObject) throws DynamicObjectException {
		ObjectLoaderLdapQueryHandler handler = new ObjectLoaderLdapQueryHandler(dynamicObject);
		search(handler);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends DynamicObject> List<T> loadObjects(Class<T> classDefinition, String baseDn, String filter) {
		try {
			ObjectsLoaderQueryHandler handler = new ObjectsLoaderQueryHandler(this, baseDn, filter);
			handler.setClassDefinition(classDefinition);
			search(handler);
			return (List<T>) handler.getList();
		} catch (DynamicObjectException e) {
			return null;
		} 
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends DynamicObject> List<T> loadChildObjects(Class<T> classDefinition, String parent,	String filter) {
		try {
			SubObjectsLoaderQueryHandler handler = new SubObjectsLoaderQueryHandler(this, parent, filter);
			handler.setClassDefinition(classDefinition);
			search(handler);
			return (List<T>) handler.getList();
		} catch (DynamicObjectException e) {
			return null;
		}
	}
	

	@Override
	public <T extends DynamicObject> T loadChildObject(Class<T> class1, DynamicObject parent, String namingValue) {
		try {
			String namingAttribute = 
				getLdapObjectFactory().getNamingLdapAttribute(class1);
			String path = namingAttribute + "=" + namingValue + "," + parent.getPath();
			return loadObject(class1, path);
		} catch (InstantiateDynamicObjectException e) {
			LOG.error("Error", e);
			return null;
		}
	}


	@Override
	public DynamicObjectFactory getObjectFactory() {
		return getLdapObjectFactory();
	}

	public TargetObjectFactory getLdapObjectFactory() {
		if (objectFactory == null) {
			objectFactory = new TargetObjectFactory(this, getTarget());
		}
		return objectFactory;
	}


	@Override
	public <T extends DynamicObject> List<T> loadChildObjects(Class<T> class1, DynamicObject parent, String filter) {
		return loadChildObjects(class1, parent.getPath(), filter);
	}

	@Override
	public void loadAttributes(DynamicObject dynamicObject, Class<?> requester, String[] attributes) throws DynamicObjectException {
		try {
			AttributeLoaderLdapQueryHandler handler = new AttributeLoaderLdapQueryHandler(dynamicObject, attributes);
			search(handler);
			for (String attribute : handler.getLists().keySet()) {
				dynamicObject.put(requester, attribute, handler.getLists().get(attribute));
			}
		} catch (DynamicObjectException e) {
			return;
		} 
	}


	@Override
	@SuppressWarnings("unchecked")
	public <T extends DynamicObject> List<T> loadReferenceObjects(Class<T> classDefinition, Query query) {
		try {
			ObjectsLoaderQueryHandler handler = new ObjectsLoaderQueryHandler(this, query);
			handler.setClassDefinition(classDefinition);
			search(handler);
			return (List<T>) handler.getList();
		} catch (DynamicObjectException e) {
			return null;
		}
	}

	@Override
	public boolean isExist(String dn) {
		if (StringUtils.isEmpty(dn)) {
			return false;
		}
		try {
			IsExistLdapQueryHandler handler = new IsExistLdapQueryHandler(this, dn);
			search(handler);
			return handler.isExist();
		} catch (DynamicObjectException e) {
			return false;
		}
	}


	@Override
	public <T extends DynamicObject> List<T> loadReferenceObjects(Class<T> classDefinition, ReferenceMethod ref) {
		LdapQuery query = new LdapQuery(ref.getPath(), ref.getReference());
		
		return loadReferenceObjects(classDefinition, query);
	}


	@Override
	public SearchQueryHandler getSearchHandler(Query query) {
		return new LdapSearchHandler(this, query);
	}


	@Override
	public Query getQuery(String base) {
		return new LdapQuery(base);
	}
}
