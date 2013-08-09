package org.mgnl.nicki.jcr.objects;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.dynamic.objects.objects.Person;

@DynamicObject
public class JcrPerson extends Person implements JcrDynamicObject {

	private static final long serialVersionUID = -7661216202997423875L;
	
	private NodeDynamicTemplateObject baseObject = new NodeDynamicTemplateObject();

	@Override
	public boolean accept(Node node) {
		try {
			return StringUtils.startsWith(node.getPath(), "/users/");
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Node getNode() {
		return baseObject.getNode();
	}

	@Override
	public void setNode(Node node) {
		baseObject.setNode(node);
	}

	@Override
	public void init(NickiContext context, Node node) throws DynamicObjectException {
		baseObject.init(context, node);
	}


}
