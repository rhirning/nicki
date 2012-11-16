package org.mgnl.nicki.jcr.context;

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
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.jcr.objects.NodeDynamicObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JcrContext extends BasicJcrContext implements NickiContext {

	private static final long serialVersionUID = -3079627211615613041L;

	public static final String PATH_SEPARATOR = "/";
	
	static Logger logger = LoggerFactory.getLogger(JcrContext.class);
	public JcrObjectFactory objectFactory = null;

	public JcrObjectFactory getJcrObjectFactory() {
		if (this.objectFactory == null) {
			this.objectFactory = new JcrObjectFactory(this, getTarget());
		}
		return objectFactory;
	}
	private Session session= null;
	private Node root = null;
	private static boolean hasAdminUser = false;

	public JcrContext(Target target, READONLY readonly) {
		super(target, readonly);
		
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (node == null) {
					Node root = session.getRootNode();

					// Store content
					Node scripts = root.addNode("scripts");
					session.save();

					// Retrieve content
					System.out.println(scripts.getPath());

					session.save();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// TODO: implement authorization mechanism
	@Override
	public DynamicObject login(String username, String password) {
		DynamicObject user = loadObject(username);
		if (user == null) {
			// TODO: how to find objects?
			List<DynamicObject> list = loadObjects(Config.getProperty("nicki.users.basedn"), "cn=" + username);
			if (list != null && list.size() == 1) {
				user = list.get(0);
			}
		}
		if (user != null) {
			try {
				login(user, password);
				return user;
			} catch (Exception e) {
			}
		}
		return null;
	}

	// TODO: implement login mechanism
	private void login(DynamicObject user, String password) {
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
			((NodeDynamicObject)dynamicObject).getNode().getSession().save();
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
		String newPath = dynamicObject.getParentPath() + PATH_SEPARATOR + newName;
		return moveObject(dynamicObject, newPath);
	}


	@Override
	public DynamicObject loadObject(String path) {
		try {
			return getDynamicObject(session.getNode(path));
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("Could not load object " + path, e);
		}
		return null;
	}

	private DynamicObject getDynamicObject(Node node) {
		return getDynamicObject(null, node);
	}

	// TODO: implement
	@SuppressWarnings("unchecked")
	private <T extends NodeDynamicObject> T getDynamicObject(Class<T> classDefinition, Node node) {
		T dynamicObject = null;
		try {
			if (node != null) {
				if (classDefinition != null) {
					dynamicObject = getJcrObjectFactory().getObject(node, classDefinition);
				} else {
					dynamicObject = (T) getJcrObjectFactory().getObject(node);
				}
				dynamicObject.init(node);
			}
		} catch (Exception e) {
			e.printStackTrace();
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
			  if (dynamicObject != null) {
				  if (classDefinition == null ||classDefinition.isAssignableFrom(dynamicObject.getClass())) {
					  list.add((T) dynamicObject);
				  }
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
			String filter) {
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
				  if (dynamicObject != null) {
					  if (classDefinition == null ||classDefinition.isAssignableFrom(dynamicObject.getClass())) {
						  list.add((T) dynamicObject);
					  }
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
		}
		return false;
	}

	public Session getSession() {
		return this.session;
	}


}
