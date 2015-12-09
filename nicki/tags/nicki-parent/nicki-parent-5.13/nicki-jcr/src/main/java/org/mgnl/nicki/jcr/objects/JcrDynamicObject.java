package org.mgnl.nicki.jcr.objects;

import javax.jcr.Node;

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectException;

public interface JcrDynamicObject extends DynamicObject {

	boolean accept(Node node);

	Node getNode();
	
	void setNode(Node node);
	
	void init(NickiContext ontext, Node node) throws DynamicObjectException;

}
