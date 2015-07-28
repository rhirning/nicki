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
