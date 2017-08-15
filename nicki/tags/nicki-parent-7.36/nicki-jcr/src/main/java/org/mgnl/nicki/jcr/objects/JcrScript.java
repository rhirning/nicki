package org.mgnl.nicki.jcr.objects;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.dynamic.objects.objects.NickiScript;
import org.mgnl.nicki.dynamic.objects.objects.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JcrScript extends NickiScript implements JcrDynamicObject, Script {
	private static final Logger LOG = LoggerFactory.getLogger(JcrScript.class);

	private static final long serialVersionUID = -4137088698173077190L;
	
	private NodeDynamicTemplateObject baseObject = new NodeDynamicTemplateObject();

	@Override
	public boolean accept(Node node) {
		try {
			return StringUtils.startsWith(node.getPath(), "/users");
		} catch (RepositoryException e) {
			LOG.error("Error", e);
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
