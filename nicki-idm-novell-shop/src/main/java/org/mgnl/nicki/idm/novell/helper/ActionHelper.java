
package org.mgnl.nicki.idm.novell.helper;

/*-
 * #%L
 * nicki-idm-novell-shop
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mgnl.nicki.idm.novell.objects.Action;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import lombok.extern.slf4j.Slf4j;


/**
 * The Class ActionHelper.
 */
@Slf4j
public class ActionHelper {

	/** The action. */
	private Action action;
	
	/** The doc. */
	private Document doc;
	
	/** The target. */
	private String target;
	
	/** The actionname. */
	private String actionname;

	/**
	 * The Enum DN_FORMAT.
	 */
	public enum DN_FORMAT {

		/** The ldap. */
		LDAP("ldap"),
		
		/** The slash. */
		SLASH("slash");
		
		/** The value. */
		private String value;

		/**
		 * Instantiates a new dn format.
		 *
		 * @param value the value
		 */
		private DN_FORMAT(String value) {
			this.value = value;
		}

		/**
		 * Gets the value.
		 *
		 * @return the value
		 */
		public String getValue() {
			return value;
		}
	}

	/**
	 * Instantiates a new action helper.
	 *
	 * @param action the action
	 * @param actionname the actionname
	 * @param target the target
	 */
	public ActionHelper(Action action, String actionname, String target) {
		this.action = action;
		this.actionname = actionname;
		this.target = target;

		initDocument();
		update();
	}

	/**
	 * Sets the string param.
	 *
	 * @param name the name
	 * @param value the value
	 */
	public void setStringParam(String name, String value) {
		Element elem = doc.createElement("param");


		elem.setAttribute("name", name);
		elem.appendChild(doc.createTextNode(value));

		doc.getFirstChild().appendChild(elem);
		update();
	}

	/**
	 * Sets the dn param.
	 *
	 * @param name the name
	 * @param format the format
	 * @param value the value
	 */
	public void setDnParam(String name, DN_FORMAT format, String value) {
		Element elem = doc.createElement("param");

		elem.setAttribute("name", name);
		elem.setAttribute("type", "dn");
		elem.setAttribute("format", format.getValue());

		elem.appendChild(doc.createTextNode(value));

		doc.getFirstChild().appendChild(elem);
		update();
	}

	/**
	 * Sets the list param.
	 *
	 * @param name the name
	 * @param values the values
	 */
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

	/**
	 * Sets the dn list param.
	 *
	 * @param name the name
	 * @param format the format
	 * @param values the values
	 */
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

	/**
	 * Update.
	 */
	private void update()  {
		action.setParameter(getXml());
		action.setTarget(target);
		action.setActionName(actionname);

	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	protected String getName() {
		SimpleDateFormat sdf = new SimpleDateFormat("-yyyyMMdd-HH-mm-ss-SSS");
		return actionname + sdf.format(new Date());
	}

	/**
	 * Inits the document.
	 */
	protected final void initDocument() {
		DocumentBuilderFactory domfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = domfactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
//			Should never happen
			log.error("Error", e);
			assert 0 == 1;
		}
		doc = builder.newDocument();

		Element root = doc.createElement("parameterset");
		doc.appendChild(root);
	}

	/**
	 * Gets the xml.
	 *
	 * @return the xml
	 */
	private String getXml() {
		DOMImplementationLS impl = (DOMImplementationLS) doc.getImplementation();
		LSSerializer serializer = impl.createLSSerializer();
		return serializer.writeToString(doc);
	}
}
