
package org.mgnl.nicki.jcr.context;

/*-
 * #%L
 * nicki-jcr
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
import java.util.List;
import java.util.Properties;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.naming.InitialContext;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.context.Target;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.data.Query;
import org.mgnl.nicki.core.data.QueryHandler;
import org.mgnl.nicki.core.data.SearchQueryHandler;
import org.mgnl.nicki.core.methods.ReferenceMethod;
import org.mgnl.nicki.core.objects.ChildFilter;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectAdapter;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.jcr.objects.JcrDynamicObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JcrContext extends BasicJcrContext implements NickiContext {

	private static final long serialVersionUID = -3079627211615613041L;
	private static final Logger LOG = LoggerFactory.getLogger(BasicJcrContext.class);

	public static final String PATH_SEPARATOR = "/";
	
	static Logger logger = LoggerFactory.getLogger(JcrContext.class);
	public JcrObjectFactory objectFactory;

	public JcrObjectFactory getJcrObjectFactory() {
		if (this.objectFactory == null) {
			this.objectFactory = new JcrObjectFactory(this, getTarget());
		}
		return objectFactory;
	}
	private Session session;
	private Node root;
	private static boolean hasAdminUser = false;

	public JcrContext(DynamicObjectAdapter adapter, Target target, READONLY readonly) {
		super(adapter, target, readonly);
		
		try {
			Properties env = new Properties();
			env.put("java.naming.provider.url", "http://www.apache.org/jackrabbit");
			env.put("java.naming.factory.initial", "org.apache.jackrabbit.core.jndi.provider.DummyInitialContextFactory");
			InitialContext context = new InitialContext(env);
//			Context environment = (Context) context.lookup("java:comp/env");
			Repository repository = (Repository) context.lookup("jcr.repository");
			session = repository.login(new SimpleCredentials("username",
							"password".toCharArray()));
			root = session.getRootNode();
			
			if (!hasAdminUser) {
				hasAdminUser = true;
				Node node = null;
				try {
					node = root.getNode("scripts");
				} catch (Exception e) {
					LOG.error("Error", e);
				}
				if (node == null) {
					Node root = session.getRootNode();

					// Store content
					Node scripts = root.addNode("scripts");
					session.save();

					// Retrieve content
					LOG.debug(scripts.getPath());

					session.save();
				}
			}
			
		} catch (Exception e) {
			LOG.error("Error", e);
		}
	}
	
	// TODO: implement authorization mechanism
	@Override
	public DynamicObject login(String username, String password) {
		DynamicObject user = loadObject(username);
		if (user == null) {
			// TODO: how to find objects?
			List<DynamicObject> list = loadObjects(Config.getString("nicki.users.basedn"), "cn=" + username);
			if (list != null && list.size() == 1) {
				user = list.get(0);
			}
		}
		if (user != null) {
			try {
				login(user, password);
				return user;
			} catch (Exception e) {
				LOG.error("Error", e);
			}
		}
		return null;
	}

	// TODO: implement login mechanism
	private void login(DynamicObject user, String password) {
		LOG.error("not implemented: " + user + "/" + password);
	}

	@Override
	public DynamicObject createObject(DynamicObject dynamicObject)
			throws DynamicObjectException {
		if (this.isReadonly()) {
			throw new DynamicObjectException("READONLY: could not create object: " + dynamicObject.getPath());
		}
		try {
			Node parent = session.getNode(dynamicObject.getParentPath());
			Node node = parent.addNode(dynamicObject.getName());
			updateNode(node, dynamicObject);
			session.save();
		} catch (Exception e) {
			throw new DynamicObjectException(e);
		}
		return loadObject(dynamicObject.getPath());
	}
	

	
	public <T extends DynamicObject> T createDynamicObject(Class<T> classDefinition, String parentPath, String namingValue)
			throws  InstantiateDynamicObjectException, DynamicObjectException {
		if (this.isReadonly()) {
			throw new DynamicObjectException("READONLY: could not create object: " + namingValue);
		}
		return createNodeDynamicObject(classDefinition, parentPath, namingValue);
	}


	// TODO implement
	private void updateNode(Node node, DynamicObject dynamicObject) {
		LOG.error("not implemented: " + node + "/" + dynamicObject.getName());
	}

	public List<DynamicObject> search(JcrQueryHandler queryHandler) throws DynamicObjectException {
		try {
			QueryManager queryManager = session.getWorkspace().getQueryManager();
			javax.jcr.query.Query query = queryHandler.createQuery(queryManager);
			QueryResult queryResult = query.execute();
			return queryHandler.handle(queryResult);
		} catch (RepositoryException e) {
			throw new DynamicObjectException(e);
		}
	}

	@Override
	public void updateObject(DynamicObject dynamicObject) throws DynamicObjectException {
		if (this.isReadonly()) {
			throw new DynamicObjectException("READONLY: could not modify object: " + dynamicObject.getPath());
		}
		try {
			((JcrDynamicObject)dynamicObject).getNode().getSession().save();
			/*
			Node node = session.getNode(dynamicObject.getPath());
			updateNode(node, dynamicObject);
			session.save();
			*/
		} catch (Exception e) {
			throw new DynamicObjectException(e);
		}
	}

	@Override
	public void updateObject(DynamicObject dynamicObject, String[] attributeNames) throws DynamicObjectException {
		if (this.isReadonly()) {
			throw new DynamicObjectException("READONLY: could not modify object: " + dynamicObject.getPath());
		}
		try {
			((JcrDynamicObject)dynamicObject).getNode().getSession().save();
			/*
			Node node = session.getNode(dynamicObject.getPath());
			updateNode(node, dynamicObject);
			session.save();
			*/
		} catch (Exception e) {
			throw new DynamicObjectException(e);
		}
	}

	@Override
	public void deleteObject(DynamicObject dynamicObject)
			throws DynamicObjectException {
		if (this.isReadonly()) {
			throw new DynamicObjectException("READONLY: could not delete object: " + dynamicObject.getPath());
		}
		try {
			Node node = session.getNode(dynamicObject.getPath());
			node.remove();
			session.save();
			dynamicObject.setOriginal(null);
		} catch (Exception e) {
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
		try {
			session.move(dynamicObject.getPath(), newPath);
			session.save();
		} catch (Exception e) {
			throw new DynamicObjectException(e);
		}
		return loadObject(newPath);
	}

	@Override
	public DynamicObject renameObject(DynamicObject dynamicObject,
			String newName) throws DynamicObjectException {
		if (this.isReadonly()) {
			throw new DynamicObjectException("READONLY: could not rename object: " + dynamicObject.getPath());
		}
		String newPath = dynamicObject.getPath(dynamicObject.getParentPath(), newName);
		return moveObject(dynamicObject, newPath);
	}

	@Override
	public DynamicObject loadObject(String path) {
		try {
			if (StringUtils.equals(path, PATH_SEPARATOR)) {
				return getDynamicObject(session.getRootNode());
			}
			return getDynamicObject(session.getNode(path));
		} catch (Exception e) {
			LOG.error("Error", e);
			logger.debug("Could not load object " + path, e);
		}
		return null;
	}

	private DynamicObject getDynamicObject(Node node) {
		return getDynamicObject(null, node);
	}

	// TODO: implement
	@SuppressWarnings("unchecked")
	private <T extends JcrDynamicObject> T getDynamicObject(Class<T> classDefinition, Node node) {
		T dynamicObject = null;
		try {
			if (node != null) {
				if (classDefinition != null) {
					dynamicObject = getJcrObjectFactory().getObject(node, classDefinition);
				} else {
					dynamicObject = (T) getJcrObjectFactory().getObject(node);
				}
				dynamicObject.init(this, node);
			}
		} catch (Exception e) {
			LOG.error("Error", e);
		}
		return dynamicObject;
	}

	private List<DynamicObject> getDynamicObjects(QueryResult queryResult) throws RepositoryException {
		return getDynamicObjects(null, queryResult);
	}

	@SuppressWarnings("unchecked")
	private <T extends DynamicObject> List<T>  getDynamicObjects(Class<T> classDefinition, QueryResult queryResult) throws RepositoryException {
		List<T> list = new ArrayList<T>();
		for (NodeIterator nodes = queryResult.getNodes(); nodes.hasNext();) {
			  Node node = nodes.nextNode();
			  DynamicObject dynamicObject = getDynamicObject(node);
			  if (dynamicObject != null
					  && (classDefinition == null ||classDefinition.isAssignableFrom(dynamicObject.getClass()))) {
				  list.add((T) dynamicObject);
			  }
		}
		return list;
	}

	@Override
	public List<DynamicObject> loadObjects(String workspace, String statement) {
		try {
			QueryManager qm = session.getWorkspace().getQueryManager();
			javax.jcr.query.Query query = qm.createQuery(statement, javax.jcr.query.Query.JCR_JQOM);
			return getDynamicObjects(query.execute());
		} catch (Exception e) {
			logger.debug("Could not load objects " + statement, e);

		} 
		return new ArrayList<DynamicObject>();
	}

	@Override
	public List<DynamicObject> loadChildObjects(String parentPath,
			ChildFilter filter) {
		List<DynamicObject> list = new ArrayList<DynamicObject>();
		try {
			Node parent = session.getNode(parentPath);
			for (NodeIterator nodes = parent.getNodes(); nodes.hasNext();) {
				  Node node = nodes.nextNode();
				  DynamicObject dynamicObject = getDynamicObject(node);
				  if (dynamicObject != null) {
					  list.add(dynamicObject);
				  }
			}
		} catch (Exception e) {
			logger.debug("Could not load childobject " + parentPath, e);

		} 
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends DynamicObject> List<T> loadChildObjects(Class<T> classDefinition, String parentPath,
			String filter) {
		List<T> list = new ArrayList<T>();
		try {
			Node parent = session.getNode(parentPath);
			for (NodeIterator nodes = parent.getNodes(); nodes.hasNext();) {
				  Node node = nodes.nextNode();
				  DynamicObject dynamicObject = getDynamicObject(node);
				  if (dynamicObject != null
						  && (classDefinition == null ||classDefinition.isAssignableFrom(dynamicObject.getClass()))) {
					  list.add((T) dynamicObject);
				  }
			}
		} catch (Exception e) {
			logger.debug("Could not load childobject " + parentPath, e);
		} 
		return list;
	}

	@SuppressWarnings("unchecked") // if casting fails, then null is returned. That's fine
	@Override
	public 	<T extends DynamicObject> T loadObject(Class<T> classDefinition, String path) {
		try {
			DynamicObject dynamicObject = loadObject(path);
			return (T) dynamicObject;
		} catch (Exception e) {
			LOG.error("Error", e);
		}
		return null;
	}

	@Override
	public void loadObject(DynamicObject dynamicObject) throws DynamicObjectException {
		// nothing to do
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DynamicObject> List<T> loadObjects(Class<T> classDefinition, String baseDn, String statement) {
		try {
			QueryManager qm = session.getWorkspace().getQueryManager();
			javax.jcr.query.Query query = qm.createQuery(statement, javax.jcr.query.Query.JCR_JQOM);
			return getDynamicObjects(classDefinition, query.execute());
		} catch (Exception e) {
			logger.debug("Could not load objects " + statement, e);
		} 
		return (List<T>) new ArrayList<DynamicObject>();
	}

	@Override
	public <T extends DynamicObject> T loadChildObject(Class<T> class1, DynamicObject parent, String namingValue) {

		String path = parent.getPath() + PATH_SEPARATOR + namingValue;
		return loadObject(class1, path);
	}

	@Override
	public <T extends DynamicObject> List<T> loadChildObjects(Class<T> class1, DynamicObject parent, String statement) {
		// TODO implement
		return null;
	}

	@Override
	public void loadAttributes(DynamicObject dynamicObject, Class<?> requester, String[] attributes) throws DynamicObjectException {
		// not implemented
	}

	@Override
	public boolean isExist(String dn) {
		if (StringUtils.isEmpty(dn)) {
			return false;
		}
		try {
			return session.itemExists(dn);
		} catch (Exception e) {
			LOG.error("Error", e);
		}
		return false;
	}

	public Session getSession() {
		return this.session;
	}

	@Override
	public <T extends DynamicObject> List<T> loadReferenceObjects(
			Class<T> classDefinition, Query query) {
		return null;
	}

	@Override
	public void search(QueryHandler handler) throws DynamicObjectException {
		
	}

	@Override
	public SearchQueryHandler getSearchHandler(Query query) {
		return null;
	}

	@Override
	public Query getQuery(String base) {
		return null;
	}

	@Override
	public <T extends DynamicObject> List<T> loadReferenceObjects(Query query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends DynamicObject> List<T> loadReferenceObjects(
			Class<T> classDefinition, ReferenceMethod referenceMethod) {
		// TODO Auto-generated method stub
		return null;
	}


}
