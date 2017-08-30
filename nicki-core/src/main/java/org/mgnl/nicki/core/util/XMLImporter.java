/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mgnl.nicki.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.DOMBuilder;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.xml.sax.SAXException;

public class XMLImporter {
	Document document;
	String rootParentPath;
	DynamicObject rootObject; 
	List<ToDo> unresolved = new ArrayList<ToDo>();
	NickiContext context;

	public XMLImporter(NickiContext context, String parentPath, InputStream inputStream) throws SAXException, IOException, ParserConfigurationException {
		this.context = context;
		this.rootParentPath = parentPath;
		this.document = new DOMBuilder().build(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream));
		
	}
	
	public void create() throws InstantiateDynamicObjectException, DynamicObjectException, NamingException {
		Element root = this.document.getRootElement();
		rootObject = createDynamicObject(this.rootParentPath, root);
		createChildren(rootObject.getPath(), root);
		handleUnresolved();
		
	}
	private void createChildren(String parentPath, Element parentNode) throws InstantiateDynamicObjectException, DynamicObjectException {
		@SuppressWarnings("unchecked")
		List<Element> children = parentNode.getChildren("dynamicObject");
		if (children != null &&  children.size() > 0) {
			for (Element child : children) {
				DynamicObject dynamicObject = createDynamicObject(parentPath, child);
				createChildren(dynamicObject.getPath(), child);
			}
		}
	}

	private DynamicObject createDynamicObject(String parentPath, Element element) throws InstantiateDynamicObjectException, DynamicObjectException {
		String className = element.getAttributeValue("class");
		String path = element.getAttributeValue("path");
		DynamicObject dynamicObject = context.getObjectFactory().getDynamicObject(className);
		addAttributes(parentPath, path, dynamicObject, element);
		dynamicObject.create();
		return dynamicObject;
	}

	private void addAttributes(String parentPath, String path, DynamicObject dynamicObject, Element element) {
		@SuppressWarnings("unchecked")
		List<Element> attributes = element.getChildren("attribute");
		if (attributes != null && attributes.size() > 0) {
			for (Element attribute : attributes) {
				String attributeName = attribute.getAttributeValue("name");
				if (dynamicObject.getDynamicAttribute(attributeName).isMultiple()) {
					Element values = attribute.getChild("values");
					if (values != null) {
						List<String> valueList = new ArrayList<String>();
						for (Object objectValue : values.getChildren("value")) {
							Element value = (Element) objectValue;
							if (StringUtils.isNotEmpty(value.getTextTrim())) {
								if (dynamicObject.getDynamicAttribute(attributeName).isForeignKey()) {
									if (StringUtils.equals("true", value.getAttributeValue("internalLink"))) {
										this.unresolved.add(new ToDo(path, attributeName, value.getTextTrim()));
									} else {
										valueList.add(value.getTextTrim());
									}
								} else {
									valueList.add(value.getTextTrim());
								}
							}
						}
						dynamicObject.put(attributeName, valueList);
					}
				} else {
					Element valueNode = attribute.getChild("value");

					String value = valueNode.getTextTrim();

					if (dynamicObject.getDynamicAttribute(attributeName).isForeignKey()) {
						if (StringUtils.equals("true", valueNode.getAttributeValue("internalLink"))) {
							this.unresolved.add(new ToDo(path, attributeName, value));
						} else {
							dynamicObject.put(attributeName, value);
						}
					} else {
						dynamicObject.put(attributeName, value);
						if (dynamicObject.getDynamicAttribute(attributeName).isNaming()) {
							dynamicObject.getContext().getAdapter().initNew(dynamicObject, parentPath, value);
						}
					}
				}
			}
		}
	}


	private void handleUnresolved() throws DynamicObjectException, NamingException {
		for (ToDo toDo : unresolved) {
			String path = getPath(toDo.getPath());
			DynamicObject dynamicObject = context.loadObject(path);
			if (dynamicObject.getDynamicAttribute(toDo.getAttributeName()).isMultiple()) {
				@SuppressWarnings("unchecked")
				List<String> values = (List<String>) dynamicObject.get(toDo.getAttributeName());
				if (values == null) {
					values = new ArrayList<String>();
				}
				values.add(getPath(toDo.getInternalLink()));
				dynamicObject.put(toDo.getAttributeName(), values);
			} else {
				dynamicObject.put(toDo.getAttributeName(), getPath(toDo.getInternalLink()));
			}
			dynamicObject.update();
		}
	}

	private String getPath(String path) {
		if (StringUtils.isEmpty(path)) {
			return rootObject.getPath();
		} else {
			return path + "," + rootObject.getPath();
		}
	}


}
