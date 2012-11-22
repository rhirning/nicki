package org.mgnl.nicki.jcr.objects;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.helper.PathHelper;
import org.mgnl.nicki.core.objects.BaseDynamicObject;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.jcr.context.JcrContext;


public class BaseJcrDynamicObject extends BaseDynamicObject {

	private static final long serialVersionUID = -2552751504033170225L;
	private Node node;

	private String namingValue = null;

	@Override
	public Object get(String key) {
		try {
			return node.getProperty(key).toString();
		} catch (Exception e) {
		}
		return null;
	}
	
	

	@Override
	public String getName() {
		try {
			return node.getName();
		} catch (RepositoryException e) {
			return null;
		}
	}



	@Override
	public String getNamingValue() {
		return namingValue;
	}

	
	public void init(NickiContext context, Node node) throws DynamicObjectException {
		setContext(context);
		this.node = node;
		try {
			setPath(node.getPath());
			try {
				this.setParentPath(node.getParent().getPath());
			} catch (Exception e) {
				// Root node does not have a parent
				this.setParentPath("");
			}
			this.namingValue = node.getName();

			setOriginal((DynamicObject) this.clone());
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
	
	

	public Node getNode() {
		return node;
	}
	
	public void setNode(Node node) {
		this.node = node;
	}

	@Override
	public boolean accept(ContextSearchResult rs) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void initNew(String parentPath, String namingValue) {
		this.setStatus(STATUS.NEW);
		this.setParentPath(parentPath);
		setPath(parentPath + SEPARATOR + namingValue);
		this.namingValue = namingValue;
	}

	@Override
	public void initExisting(NickiContext context, String path) {
		this.setStatus(STATUS.EXISTS);
		setContext(context);
		setPath(path);
	}

	@Override
	public String getSlashPath(DynamicObject parent) {
		if (parent != null) {
			return getSlashPath(parent.getPath());
		} else {
			return getSlashPath("");
		}
	}

	@Override
	public String getSlashPath(String parentPath) {
		return PathHelper.getSlashPath(parentPath, getPath());
	}

	public String getPath(String parentPath, String name) {
		if (StringUtils.equals(parentPath, JcrContext.PATH_SEPARATOR)) {
			return JcrContext.PATH_SEPARATOR + name;
		} else {
			return parentPath + JcrContext.PATH_SEPARATOR + name; 
		}
	}


	@Override
	public String getObjectClassFilter() {
		// TODO Auto-generated method stub
		return null;
	}
}
