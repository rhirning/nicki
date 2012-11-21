package org.mgnl.nicki.jcr.objects;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.helper.PathHelper;
import org.mgnl.nicki.core.objects.BaseDynamicObject;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DataModel;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.jcr.context.JcrContext;


public class BaseJcrDynamicObject extends BaseDynamicObject {


	@Override
	public String getNamingValue() {
		return namingValue;
	}

	private static final long serialVersionUID = -2552751504033170225L;
	private JcrDataModel model = null;
	private Node node;
	private String namingValue = null;
	// cached attributes
	private List<DynamicObject> childObjects = null;
	
	public void init(Node node) throws DynamicObjectException {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	public Node getNode() {
		return node;
	}

	@Override
	public void setModel(DataModel model) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getInfo(String xml, String infoPath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void loadChildren() {
		init();
		if (childObjects == null) {
			childObjects = new ArrayList<DynamicObject>();
			@SuppressWarnings("unchecked")
			List<BaseJcrDynamicObject> list = (List<BaseJcrDynamicObject>) getContext().loadChildObjects(getPath(), "*");
			if (list != null) {
				childObjects.addAll(list);
			}
		}
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
	public void unLoadChildren() {
		this.childObjects = null;
		
	}

	@Override
	public DataModel getModel() {
		if (model == null) {
			model = new JcrDataModel();
		}
		return model;
	}

	@Override
	public List<? extends DynamicObject> getAllChildren() {
		loadChildren();
		return childObjects;
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
	public void initDataModel() {
		// TODO Auto-generated method stub
		
	}
}
