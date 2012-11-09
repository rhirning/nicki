package org.mgnl.nicki.jcr.objects;

import java.util.List;

import javax.jcr.Node;

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.BaseDynamicObject;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DataModel;
import org.mgnl.nicki.core.objects.DynamicAttribute;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectException;

public class BaseJcrDynamicObject extends BaseDynamicObject {

	private static final long serialVersionUID = -2552751504033170225L;
	private Node node;
	
	@Override
	public void initDataModel() {
	}

	public void init(Node node) throws DynamicObjectException {
		this.node = node;
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T extends DynamicAttribute> void addAttribute(T dynAttribute) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean accept(ContextSearchResult rs) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void initNew(String parentPath, String namingValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getNamingValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParentPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initExisting(NickiContext context, String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unLoadChildren() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DataModel getModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends DynamicObject> getAllChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends DynamicObject> getChildren(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSlashPath(DynamicObject parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSlashPath(String parentPath) {
		// TODO Auto-generated method stub
		return null;
	}

}
