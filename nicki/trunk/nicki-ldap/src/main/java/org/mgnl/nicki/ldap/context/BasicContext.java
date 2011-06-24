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
import org.mgnl.nicki.ldap.objects.DynamicReference;


@SuppressWarnings("serial")
public abstract class BasicContext implements NickiContext {
	public static final String PROPERTY_BASE = "nicki.ldap.";
	public static final String TARGET_DEFAULT = "edir";
	private Target target = null;
	private NickiPrincipal principal;
	private READONLY readonly;
	private DynamicObject person;

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
	
	@Override
	public NickiPrincipal login(String username, String password) {
		if (null == loadObject(username)) {
			List<DynamicObject> list = loadObjects(Config.getProperty("nicki.users.basedn"), "cn=" + username);
			if (list != null && list.size() == 1) {
				String name = list.get(0).getPath();
				try {
					getDirContext(name, password);
					return new NickiPrincipal(name, password);
				} catch (Exception e) {
					return null;
				}
			}
		}
		try {
			getDirContext(username, password);
			return new NickiPrincipal(username, password);
		} catch (Exception e) {
			return null;
		}
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

	public List<DynamicObject> loadReferenceObjects(String path,
			DynamicReference reference) {
		try {
			LdapQuery query = new LdapQuery(path, reference);
			ObjectsLoaderLdapQueryHandler handler = new ObjectsLoaderLdapQueryHandler(this, query);
			search(handler);
			return handler.getList();
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
	
	public DynamicObject createDynamicObject(Class<?> classDefinition, String parentPath, String namingValue)
			throws  InstantiateDynamicObjectException, DynamicObjectException {
		if (this.readonly == READONLY.TRUE) {
			throw new DynamicObjectException("READONLY: could not create object: " + namingValue);
		}
		return getObjectFactory().createNewDynamicObject(classDefinition, parentPath, namingValue);
	}

	public Target getTarget() {
		return target;
	}

	@Override
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

	@Override
	public void createObject(DynamicObject dynamicObject)
			throws DynamicObjectException {
		if (this.readonly == READONLY.TRUE) {
			throw new DynamicObjectException("READONLY: could not create object: " + dynamicObject.getPath());
		}
		DirContext ctx = null;
		try {
			ctx = getDirContext();
			ctx.bind(dynamicObject.getPath(), new DynamicObjectWrapper(dynamicObject));
			updateObject(dynamicObject);
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

	@Override
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

	@Override
	public DynamicObject renameObject(DynamicObject dynamicObject,
			String newName) throws DynamicObjectException {
		if (this.readonly == READONLY.TRUE) {
			throw new DynamicObjectException("READONLY: could not rename object: " + dynamicObject.getPath());
		}
		String newPath = dynamicObject.getModel().getNamingLdapAttribute() + "=" + newName + "," + dynamicObject.getParentPath();
		return moveObject(dynamicObject, newPath);
	}

	@Override
	public void search(QueryHandler queryHandler) throws DynamicObjectException {
		DirContext ctx = null;
		NamingEnumeration<SearchResult> results = null;
		try {
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
			} catch (NamingException e) {
				throw new DynamicObjectException(e);
			}
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

	@Override
	public ObjectFactory getObjectFactory() {
		return getTarget().getObjectFactory(this);
	}

	@Override
	public NickiPrincipal getPrincipal() throws DynamicObjectException {
		return this.principal;
	}

	protected void setPrincipal(NickiPrincipal principal) {
		this.principal = principal;
		this.person = loadObject(principal.getName());
	}

	public DynamicObject getPerson() {
		return person;
	}
	
}
