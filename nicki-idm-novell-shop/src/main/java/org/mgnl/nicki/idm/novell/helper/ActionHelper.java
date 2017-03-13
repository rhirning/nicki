/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.idm.novell.helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mgnl.nicki.idm.novell.objects.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

public class ActionHelper {
	private static final Logger LOG = LoggerFactory.getLogger(ActionHelper.class);

	private Action action;
	private Document doc;
	private String target;
	private String actionname;

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

	public ActionHelper(Action action, String actionname, String target) {
		this.action = action;
		this.actionname = actionname;
		this.target = target;

		initDocument();
		update();
	}

	public void setStringParam(String name, String value) {
		Element elem = doc.createElement("param");


		elem.setAttribute("name", name);
		elem.appendChild(doc.createTextNode(value));

		doc.getFirstChild().appendChild(elem);
		update();
	}

	public void setDnParam(String name, DN_FORMAT format, String value) {
		Element elem = doc.createElement("param");

		elem.setAttribute("name", name);
		elem.setAttribute("type", "dn");
		elem.setAttribute("format", format.getValue());

		elem.appendChild(doc.createTextNode(value));

		doc.getFirstChild().appendChild(elem);
		update();
	}

	public void setListParam(String name, List<String> values) {
		Element list = doc.createElement("paramlist");
		Node node;

		list.setAttribute("name", name);

		for (String string : values) {
			node = doc.createElement("param");
			node.appendChild(doc.createTextNode(string));

			list.appendChild(node);
		}

		doc.getFirstChild().appendChild(list);
		update();
	}

	public void setDnListParam(String name, DN_FORMAT format, List<String> values) {
		Element list = doc.createElement("paramlist");
		Element node;

		list.setAttribute("name", name);
		list.setAttribute("type", "dn");
		list.setAttribute("format", format.getValue());

		for (String string : values) {
			node = doc.createElement("param");
			node.appendChild(doc.createTextNode(string));

			list.appendChild(node);
		}

		doc.getFirstChild().appendChild(list);
		update();
	}

	private void update()  {
		action.setParameter(getXml());
		action.setTarget(target);
		action.setActionName(actionname);

	}

	protected String getName() {
		SimpleDateFormat sdf = new SimpleDateFormat("-yyyyMMdd-HH-mm-ss-SSS");
		return actionname + sdf.format(new Date());
	}

	protected final void initDocument() {
		DocumentBuilderFactory domfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = domfactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
//			Should never happen
			LOG.error("Error", e);
			assert 0 == 1;
		}
		doc = builder.newDocument();

		Element root = doc.createElement("parameterset");
		doc.appendChild(root);
	}

	private String getXml() {
		DOMImplementationLS impl = (DOMImplementationLS) doc.getImplementation();
		LSSerializer serializer = impl.createLSSerializer();
		return serializer.writeToString(doc);
	}
}
