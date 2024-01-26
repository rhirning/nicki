
package org.mgnl.nicki.ldap.context;

import java.io.IOException;

/*-
 * #%L
 * nicki-ldap
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.PagedResultsControl;
import javax.naming.ldap.PagedResultsResponseControl;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;
import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.context.BasicContext;
import org.mgnl.nicki.core.context.BeanQueryHandler;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.context.DynamicObjectFactory;
import org.mgnl.nicki.core.context.Target;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.data.QueryHandler;
import org.mgnl.nicki.core.data.SearchQueryHandler;
import org.mgnl.nicki.core.helper.AnnotationHelper;
import org.mgnl.nicki.core.helper.DataHelper;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LdapContext extends BasicContext implements NickiContext {

	private static final long serialVersionUID = -3079627211615613041L;

	private TargetObjectFactory objectFactory;
	private boolean tls;

	public LdapContext(DynamicObjectAdapter adapter, Target target, READONLY readonly) {
		super(adapter, target, readonly);
		tls = getTarget().getBoolean("securityTLS");
	}
	
	
	protected NickiLdapContext getLdapContext(String name, String password) throws NamingException {
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
		// Enable connection pooling
		env.put("com.sun.jndi.ldap.connect.pool",
				DataHelper.booleanOf(getTarget().getProperty("pool")) ? "true" : "false");

		return new NickiLdapContext(env, null);
	}

	public NickiLdapContext getLdapContext() throws DynamicObjectException {
		try {
			return getLdapContext(getPrincipal().getName(), getPrincipal().getPassword());
		} catch (NamingException e) {
			throw new DynamicObjectException(e);
		}
	}

	@Override
	public DynamicObject login(String username, String password) {
		log.debug("login: start for user " + username);
		DynamicObject user = loadObject(username);
		if (user == null) {
			log.info("login: loadObject not successful for user " + username);
			List<DynamicObject> list = loadObjects(getTarget().getBaseDn(), "cn=" + username);
			
			if (list != null && list.size() == 1) {
				log.debug("login: loadObjects successful");
				user = list.get(0);
			} else {
				log.debug("Loading Objects not successful: " 
						+ ((list == null)?"null":"size=" + list.size()));
			}
		} else {
			log.debug("login: loadObject successful");
		}
		if (user != null) {
			log.debug("try login for user " + user.getDisplayName());
			try (NickiLdapContext ctx = getLdapContext(user.getPath(), password)) {
				log.debug("login: after getDirContext");
				return user;
			} catch (Exception e) {
				log.debug("Could not login user " + username, e);
			}
		} else {
			log.debug("could not load user " + username);
		}
		log.debug("login: end");
		return null;
	}

	@Override
	public DynamicObject createObject(DynamicObject dynamicObject)
			throws DynamicObjectException {
		if (this.isReadonly()) {
			throw new DynamicObjectException("READONLY: could not create object: " + dynamicObject.getPath());
		}
		StartTlsResponse tls = null;
		try (NickiLdapContext ctx = getLdapContext()) {
			if (isTls()) {
				// Start TLS
				tls =
				    (StartTlsResponse) ctx.extendedOperation(new StartTlsRequest());
				tls.negotiate();
			}
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
		} catch (NamingException | IOException e) {
			log.error("Error", e);
			throw new DynamicObjectException(e);
		}
	}

	private boolean isTls() {
		return tls;
	}


	@Override
	public void search(QueryHandler queryHandler) throws DynamicObjectException {
		StartTlsResponse tls = null;
		NamingEnumeration<SearchResult> results = null;
		try (NickiLdapContext ctx = getLdapContext()) {
			if (isTls()) {
				// Start TLS
				tls =
				    (StartTlsResponse) ctx.extendedOperation(new StartTlsRequest());
				tls.negotiate();
			}
			if (queryHandler.getPageSize() > 0) {
				ctx.setRequestControls(new Control[]{ 
				         new PagedResultsControl(queryHandler.getPageSize(), Control.CRITICAL) });
			}
			byte[] cookie = null;
			int total;
			List<ContextSearchResult> list = new ArrayList<ContextSearchResult>();
			int pageNum = 0;
			do {
				results = ctx.search(queryHandler.getBaseDN(), queryHandler.getFilter(), (SearchControls) queryHandler.getConstraints());
				
				try {
					while (results != null && results.hasMore()) {
						list.add(new JndiSearchResult(results.next()));
					}
				} catch (NamingException e) {
					log.error("Error", e);
				}
				if (queryHandler.getPageSize() > 0) {
					pageNum++;
					// Examine the paged results control response
					Control[] controls = ctx.getResponseControls();
					if (controls != null) {
						for (int i = 0; i < controls.length; i++) {
							if (controls[i] instanceof PagedResultsResponseControl) {
								PagedResultsResponseControl prrc = (PagedResultsResponseControl) controls[i];
								total = prrc.getResultSize();
								log.debug("END-OF-PAGE " + pageNum + " (total : " + total + ")");
								cookie = prrc.getCookie();
							}
						}
					} else {
						log.debug("No controls were sent from the server");
					}
					// Re-activate paged results
					ctx.setRequestControls(new Control[] {
							new PagedResultsControl(queryHandler.getPageSize(), cookie, Control.CRITICAL) });
				}
			} while (cookie != null && !queryHandler.onePage());
			queryHandler.handle(list);
		} catch (Throwable e) {
			log.debug("Error", e);
			throw new DynamicObjectException(e.getMessage());
		} finally {
			if (results != null) {
				try {
					results.close();
				} catch (NamingException e) {
					
					log.error("Error", e);
				}
			}
		}
	}

	public void search(BeanQueryHandler queryHandler) throws NamingException {
		StartTlsResponse tls = null;
		NamingEnumeration<SearchResult> results = null;
		try (NickiLdapContext ctx = getLdapContext()) {
			if (isTls()) {
				// Start TLS
				tls =
				    (StartTlsResponse) ctx.extendedOperation(new StartTlsRequest());
				tls.negotiate();
			}
			results = ctx.search(queryHandler.getBaseDN(), queryHandler.getFilter(), (SearchControls) queryHandler.getConstraints());
			queryHandler.handle(this, results);
		} catch (IOException | NamingException | DynamicObjectException e) {
			log.error("Error searching LDAP", e);
			throw new NamingException(e.getMessage());
		} finally {
			if (results != null) {
				try {
					results.close();
				} catch (NamingException e) {
					
					log.error("Error", e);
				}
			}
		}
	}

	@Override
	public void updateObject(DynamicObject dynamicObject) throws DynamicObjectException {
		if (this.isReadonly()) {
			throw new DynamicObjectException("READONLY: could not modify object: " + dynamicObject.getPath());
		}
		StartTlsResponse tls = null;
		try (NickiLdapContext ctx = getLdapContext()) {
			if (isTls()) {
				// Start TLS
				tls =
				    (StartTlsResponse) ctx.extendedOperation(new StartTlsRequest());
				tls.negotiate();
			}
			ctx.modifyAttributes(dynamicObject.getPath(),DirContext.REPLACE_ATTRIBUTE, dynamicObject.getModel().getLdapAttributes(dynamicObject, CREATEONLY.FALSE));
		} catch (NamingException | IOException e) {
			String details = getDetails(dynamicObject, CREATEONLY.FALSE);
			log.error(details, e);
			throw new DynamicObjectException(e);
		}
	}

	@Override
	public void updateObject(DynamicObject dynamicObject, String[] attributeNames) throws DynamicObjectException {
		if (this.isReadonly()) {
			throw new DynamicObjectException("READONLY: could not modify object: " + dynamicObject.getPath());
		}
		StartTlsResponse tls = null;
		try (NickiLdapContext ctx = getLdapContext()) {
			if (isTls()) {
				// Start TLS
				tls =
				    (StartTlsResponse) ctx.extendedOperation(new StartTlsRequest());
				tls.negotiate();
			}
			ctx.modifyAttributes(dynamicObject.getPath(),DirContext.REPLACE_ATTRIBUTE,
					dynamicObject.getModel().getLdapAttributes(dynamicObject, attributeNames, CREATEONLY.FALSE));
		} catch (NamingException | IOException e) {
			String details = getDetails(dynamicObject, CREATEONLY.FALSE);
			log.error(details, e);
			throw new DynamicObjectException(e);
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
		StartTlsResponse tls = null;
		try (NickiLdapContext ctx = getLdapContext()) {
			if (isTls()) {
				// Start TLS
				tls =
				    (StartTlsResponse) ctx.extendedOperation(new StartTlsRequest());
				tls.negotiate();
			}
			ctx.unbind(dynamicObject.getPath());
			dynamicObject.setOriginal(null);
		} catch (NamingException | IOException e) {
			log.error("Error", e);
			throw new DynamicObjectException(e);
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
		StartTlsResponse tls = null;
		try (NickiLdapContext ctx = getLdapContext()) {
			if (isTls()) {
				// Start TLS
				tls =
				    (StartTlsResponse) ctx.extendedOperation(new StartTlsRequest());
				tls.negotiate();
			}
			ctx.rename(dynamicObject.getPath(), newPath);
			return loadObject(newPath);
		} catch (NamingException | IOException e) {
			log.error("Error", e);
			throw new DynamicObjectException(e);
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
			log.debug("Could not load object: " + path, e);
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
			log.debug("Could not load objects: " + filter + " under "+ baseDn, e);
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
		log.debug("loadObjects:" + classDefinition.getName() + "|" + baseDn + "|" + filter);
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
		log.debug("loadChildObjects:" + classDefinition.getName() + "|" + parent + "|" + filter);
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
			log.error("Error", e);
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
