/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
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
import org.mgnl.nicki.ldap.auth.NickiPrincipal;
import org.mgnl.nicki.ldap.core.LdapQuery;
import org.mgnl.nicki.ldap.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.ldap.data.IsExistLdapQueryHandler;
import org.mgnl.nicki.ldap.data.ObjectLoaderLdapQueryHandler;
import org.mgnl.nicki.ldap.data.ObjectsLoaderLdapQueryHandler;
import org.mgnl.nicki.ldap.data.SubObjectsLoaderLdapQueryHandler;
import org.mgnl.nicki.ldap.data.jndi.JndiSearchResult;
import org.mgnl.nicki.ldap.objects.ContextSearchResult;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;
import org.mgnl.nicki.ldap.objects.DynamicObjectWrapper;


@SuppressWarnings("serial")
public abstract class BasicContext implements NickiContext {
	public static final String PROPERTY_BASE = "nicki.ldap.";
	public static final String TARGET_DEFAULT = "edir";
	private Target target = null;
	private NickiPrincipal principal;
	private DynamicObject user;
	private READONLY readonly;

	protected BasicContext(Target target, READONLY readonly) {
		this.target = target;
		this.readonly = readonly;
	}

	public DirContext getDirContext() throws DynamicObjectException {
		try {
			return getDirContext(principal.getName(), principal.getPassword());
		} catch (NamingException e) {
			throw new DynamicObjectException(e);
		}
	}
	
	protected DirContext getDirContext(String name, String password) throws NamingException {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, target.getProperty("providerUrl"));
		env.put(Context.SECURITY_AUTHENTICATION, target.getProperty("securityAuthentication"));
		env.put(Context.SECURITY_PRINCIPAL, name);
		env.put(Context.SECURITY_CREDENTIALS, password);
		String binaries = target.getProperty("attributes.binary");
		if (StringUtils.isNotEmpty(binaries)) {
			env.put("java.naming.ldap.attributes.binary", binaries);
		}
		// Enable connection pooling
		env.put("com.sun.jndi.ldap.connect.pool", "true");

		return new InitialDirContext(env);
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

	public void loadObject(DynamicObject dynamicObject) throws DynamicObjectException {
		ObjectLoaderLdapQueryHandler handler = new ObjectLoaderLdapQueryHandler(dynamicObject);
		search(handler);
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

	public List<DynamicObject> loadReferenceObjects(LdapQuery query) {
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
			Target target = parent.getContext().getTarget();
			String namingAttribute = 
				target.getObjectFactory(parent.getContext()).getNamingLdapAttribute(class1);
			String path = namingAttribute + "=" + namingValue + "," + parent.getPath();
			return loadObject(class1, path);
		} catch (InstantiateDynamicObjectException e) {
			e.printStackTrace();
		}
		return null;
}


	public <T extends DynamicObject> List<T> loadChildObjects(Class<T> class1, DynamicObject parent, String filter) {
		return loadChildObjects(class1, parent.getPath(), filter);
}


	@SuppressWarnings("unchecked")
	public <T extends DynamicObject> List<T> loadReferenceObjects(Class<T> classDefinition, LdapQuery query) {
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
	
	public <T extends DynamicObject> T createDynamicObject(Class<T> classDefinition, String parentPath, String namingValue)
			throws  InstantiateDynamicObjectException, DynamicObjectException {
		if (this.readonly == READONLY.TRUE) {
			throw new DynamicObjectException("READONLY: could not create object: " + namingValue);
		}
		return getObjectFactory().createNewDynamicObject(classDefinition, parentPath, namingValue);
	}

	public Target getTarget() {
		return target;
	}

	public void updateObject(DynamicObject dynamicObject) throws DynamicObjectException {
		if (this.readonly == READONLY.TRUE) {
			throw new DynamicObjectException("READONLY: could not modify object: " + dynamicObject.getPath());
		}
		DirContext ctx = null;
		try {
			ctx = getDirContext();
			ctx.modifyAttributes(dynamicObject.getPath(),DirContext.REPLACE_ATTRIBUTE,dynamicObject.getModel().getLdapAttributes(dynamicObject));
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

	public DynamicObject createObject(DynamicObject dynamicObject)
			throws DynamicObjectException {
		if (this.readonly == READONLY.TRUE) {
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

	public void deleteObject(DynamicObject dynamicObject)
			throws DynamicObjectException {
		if (this.readonly == READONLY.TRUE) {
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
		if (this.readonly == READONLY.TRUE) {
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
		if (this.readonly == READONLY.TRUE) {
			throw new DynamicObjectException("READONLY: could not rename object: " + dynamicObject.getPath());
		}
		String newPath = dynamicObject.getModel().getNamingLdapAttribute() + "=" + newName + "," + dynamicObject.getParentPath();
		return moveObject(dynamicObject, newPath);
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
	public String getObjectClassFilter(Class<? extends DynamicObject> classDefinition) throws InstantiateDynamicObjectException {
		return getObjectFactory().getObjectClassFilter(classDefinition);
	}


	public ObjectFactory getObjectFactory() {
		return getTarget().getObjectFactory(this);
	}

	public NickiPrincipal getPrincipal() throws DynamicObjectException {
		return this.principal;
	}

	protected void setPrincipal(NickiPrincipal principal) {
		this.principal = principal;
	}

	public DynamicObject getUser() {
		return user;
	}

	public void setUser(DynamicObject user) {
		this.user = user;
	}

	public READONLY getReadonly() {
		return readonly;
	}

	public void setReadonly(READONLY readonly) {
		this.readonly = readonly;
	}
	
}
