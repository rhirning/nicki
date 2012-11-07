package org.mgnl.nicki.jcr.objects;

import javax.jcr.Node;

import org.mgnl.nicki.core.objects.BaseDynamicObject;
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

}
