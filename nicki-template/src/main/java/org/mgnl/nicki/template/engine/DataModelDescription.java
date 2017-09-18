
package org.mgnl.nicki.template.engine;

/*-
 * #%L
 * nicki-template
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


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.helper.NameValue;
import org.mgnl.nicki.core.util.XmlHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author cna
 */
public class DataModelDescription {

	private static final String ELEM_DATAMODEL = "datamodel";
	private static final String ELEM_FUNCTION = "function";
	private static final String ELEM_ENTRY = "entry";
	private static final String ELEM_OBJECT = "object";
	private static final String ELEM_PARAM = "param";
	private List<DMFunction> functions = new ArrayList<DMFunction>();
	private List<DMObject> objects = new ArrayList<DMObject>();
	private List<DMEntry> entries = new ArrayList<DMEntry>();

	public DataModelDescription() {
	}

	public void addFunction(String name, String clazz, List<NameValue> param) {
		DMFunction f = new DMFunction();
		f.name = name;
		f.clazz = clazz;

		for (NameValue p : param) {
			f.addParam(p.getName(), p.getValue());
		}


		functions.add(f);
	}

	public void addEntry(String name, String value) {
		DMEntry entry = new DMEntry();
		entry.name = name;
		entry.value = value;

		entries.add(entry);
	}

	public void addObject(String name, String dn, String target) {
		DMObject obj = new DMObject();
		obj.dn = dn;
		obj.name = name;
		obj.target = target;

		objects.add(obj);
	}

	public List<DMFunction> getFunctions() {
		return Collections.unmodifiableList(functions);
	}

	public List<DMEntry> getEntries() {
		return Collections.unmodifiableList(entries);
	}

	public List<DMObject> getObjects() {
		return Collections.unmodifiableList(objects);
	}

	public static DataModelDescription fromXml(String xml) {

		DataModelDescription description = new DataModelDescription();

		Document doc;
		try {
			doc = XmlHelper.getDocumentFromXml(xml);
		} catch (SAXException ex) {
			System.err.println("could not parse data model description xml: " + ex.getMessage());
			return null;
		}

		Element model = XmlHelper.selectNode(Element.class, doc, ELEM_DATAMODEL);

		List<Element> functions = XmlHelper.selectNodes(Element.class, model, ELEM_FUNCTION);
		List<Element> entries = XmlHelper.selectNodes(Element.class, model, ELEM_ENTRY);
		List<Element> objects = XmlHelper.selectNodes(Element.class, model, ELEM_OBJECT);

		for (Element node : entries) {
			description.addEntry(node.getAttribute("name"), node.getAttribute("value"));
		}

		List<NameValue> parameter = new ArrayList<NameValue>();
		List<Element> params;
		for (Element node : functions) {
			parameter.clear();

			params = XmlHelper.selectNodes(Element.class, node, ELEM_PARAM);

			for (Element param : params) {
				parameter.add(new NameValue(param.getAttribute("name"), param.getAttribute("value")));
			}
			description.addFunction(node.getAttribute("name"), node.getAttribute("class"), parameter);
		}

		for (Element node : objects) {
			String target = node.getAttribute("target");
			if (StringUtils.isBlank(target)) {
				target = null;
			}
			description.addObject(node.getAttribute("name"), node.getAttribute("dn"), target);
		}

		return description;
	}

	public String toXml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<datamodel>");

		for (DMEntry e : entries) {
			sb.append(e.toXml());
		}
		for (DMFunction f : functions) {
			sb.append(f.toXml());
		}
		for (DMObject o : objects) {
			sb.append(o.toXml());
		}

		sb.append("</datamodel>");

		return sb.toString();
	}
}

class DMFunction {

	String clazz = null;
	String name = null;
	List<DMParam> param = new ArrayList<DMParam>();

	public String toXml() {
		StringBuilder sb = new StringBuilder();

		sb.append("<function ");
		sb.append("name=\"").append(name).append("\" ");
		sb.append("class=\"").append(clazz).append("\" ");

		if (!param.isEmpty()) {
			sb.append(">");
			for (DMParam p : param) {
				sb.append(p.toXml());
			}
			sb.append("</function>");
		} else {
			sb.append("/>");
		}

		return sb.toString();
	}

	public void addParam(String name, String value) {
		DMParam p = new DMParam();
		p.name = name;
		p.value = value;
		param.add(p);
	}
}

class DMParam {

	String name = null;
	String value = null;

	public DMParam clone() {
		DMParam p = new DMParam();
		p.name = this.name;
		p.value = this.value;

		return p;
	}

	public String toXml() {
		StringBuilder sb = new StringBuilder();

		sb.append("<param ");
		sb.append("name=\"").append(name).append("\" ");
		sb.append("value=\"").append(value).append("\" ");
		sb.append("/>");

		return sb.toString();
	}
}

class DMObject {

	String target = null;
	String dn = null;
	String name = null;

	public String toXml() {
		StringBuilder sb = new StringBuilder();

		sb.append("<object ");
		sb.append("name=\"").append(name).append("\" ");
		sb.append("dn=\"").append(dn).append("\" ");

		if (StringUtils.isNotBlank(target)) {
			sb.append("target=\"").append(target).append("\" ");
		}

		sb.append("/>");

		return sb.toString();
	}
}

class DMEntry {

	String name;
	String value;

	public String toXml() {
		StringBuilder sb = new StringBuilder();

		sb.append("<entry ");
		sb.append("name=\"").append(name).append("\" ");
		sb.append("value=\"").append(value).append("\" ");
		sb.append("/>");

		return sb.toString();
	}
}
