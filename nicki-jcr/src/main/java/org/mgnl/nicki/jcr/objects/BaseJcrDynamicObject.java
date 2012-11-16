package org.mgnl.nicki.jcr.objects;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.helper.PathHelper;
import org.mgnl.nicki.core.objects.BaseDynamicObject;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DataModel;
import org.mgnl.nicki.core.objects.DynamicAttribute;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectException;


public class BaseJcrDynamicObject extends BaseDynamicObject {

	public String getNamingValue() {
		return namingValue;
	}

	private static final long serialVersionUID = -2552751504033170225L;
	private JcrDataModel model = null;
	private Node node;
	private String namingValue = null;
	private String path = null;
	private String parentPath = null;
	// cached attributes
	private List<BaseJcrDynamicObject> childObjects = null;
	
	@Override
	public void initDataModel() {
	}

	public void init(Node node) throws DynamicObjectException {
		this.node = node;
		try {
			this.path = node.getPath();
			this.parentPath = node.getParent().getPath();
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
	public String getPath() {
		return path;
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
			childObjects = new ArrayList<BaseJcrDynamicObject>();
			@SuppressWarnings("unchecked")
			List<BaseJcrDynamicObject> list = (List<BaseJcrDynamicObject>) getContext().loadChildObjects(path, "*");
			if (list != null) {
				childObjects.addAll(list);
			}
		}
	}
	
	private void init() {
		if (getStatus() == STATUS.EXISTS) {
			try {
				getContext().loadObject(this);
			} catch (DynamicObjectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public <T extends DynamicAttribute> void addAttribute(T dynAttribute) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean accept(ContextSearchResult rs) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void initNew(String parentPath, String namingValue) {
		this.setStatus(STATUS.NEW);
		this.parentPath = parentPath;
		this.path = parentPath + SEPARATOR + namingValue;
		this.namingValue = namingValue;
	}

	@Override
	public String getParentPath() {
		return parentPath;
	}

	@Override
	public void initExisting(NickiContext context, String path) {
		this.setStatus(STATUS.EXISTS);
		setContext(context);
		this.path = path;
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

}
