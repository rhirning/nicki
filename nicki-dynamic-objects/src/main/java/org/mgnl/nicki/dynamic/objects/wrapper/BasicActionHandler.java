/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mgnl.nicki.dynamic.objects.wrapper;

import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.dynamic.objects.objects.BasicAction;
import org.mgnl.nicki.ldap.context.BasicContext;
import org.mgnl.nicki.ldap.data.InstantiateDynamicObjectException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

/**
 *
 * @author cna
 */
public abstract class BasicActionHandler {

	private BasicContext ctx;
	private Document doc;
	private String target;
	private String actionname;
	private boolean noexec;

	public enum DN_FORMAT {

		LDAP("ldap"),
		SLASH("slash");
		private String value;

		private DN_FORMAT(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	public BasicActionHandler(BasicContext ctx, String actionname, String target, boolean noexec) {
		this.ctx = ctx;
		this.actionname = actionname;
		this.target = target;
		this.noexec = noexec;

		initDocument();
	}

	public void setStringParam(String name, String value) {
		Node elem = doc.createElement("param");

		elem.appendChild(createAttr("name", name));
		elem.appendChild(doc.createTextNode(value));

		doc.getFirstChild().appendChild(elem);
	}

	public void setDnParam(String name, DN_FORMAT format, String value) {
		Node elem = doc.createElement("param");

		elem.appendChild(createAttr("name", name));
		elem.appendChild(createAttr("type", "dn"));
		elem.appendChild(createAttr("format", format.getValue()));

		elem.appendChild(doc.createTextNode(value));

		doc.getFirstChild().appendChild(elem);
	}

	public void setListParam(String name, List<String> values) {
		Node list = doc.createElement("paramlist");
		Node node;

		list.appendChild(createAttr("name", name));

		for (String string : values) {
			node = doc.createElement("param");
			node.appendChild(doc.createTextNode(string));

			list.appendChild(node);
		}

		doc.getFirstChild().appendChild(list);
	}

	public void setDnListParam(String name, DN_FORMAT format, List<String> values) {
		Node list = doc.createElement("paramlist");
		Node node;

		list.appendChild(createAttr("name", name));
		list.appendChild(createAttr("type", "dn"));
		list.appendChild(createAttr("format", format.getValue()));

		for (String string : values) {
			node = doc.createElement("param");
			node.appendChild(doc.createTextNode(string));

			list.appendChild(node);
		}

		doc.getFirstChild().appendChild(list);
	}

	public BasicAction create() throws InstantiateDynamicObjectException {
		BasicAction action = (BasicAction) ctx.getObjectFactory().getDynamicObject(BasicAction.class, Config.getProperty("nicki.actions.basedn"), getName());
		action.setParameter(getXml());
		action.setNoExec(noexec);
		action.setTarget(target);
		action.setActionName(actionname);

		return action;
	}

	protected abstract String getName();

	protected void initDocument() {
		DocumentBuilderFactory domfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = domfactory.newDocumentBuilder();
		} catch (ParserConfigurationException ex) {
//			Should never happen
			assert 0 == 1;
		}
		doc = builder.newDocument();

		Node root = doc.createElement("parameterset");
		doc.appendChild(root);
	}

	private String getXml() {
		DOMImplementationLS impl = (DOMImplementationLS) doc.getImplementation();
		LSSerializer serializer = impl.createLSSerializer();
		return serializer.writeToString(doc);
	}

	private Attr createAttr(String name, String value) {
		Attr attribute = doc.createAttribute(name);
		attribute.setValue(value);
		return attribute;
	}
}
