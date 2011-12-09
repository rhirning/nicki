/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.ldap.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.DOMBuilder;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;
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
			for (Iterator<Element> iterator = children.iterator(); iterator.hasNext();) {
				Element child = (Element) iterator.next();
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
			for (Iterator<Element> iterator = attributes.iterator(); iterator.hasNext();) {
				Element attribute = iterator.next();
				String attributeName = attribute.getAttributeValue("name");
				if (dynamicObject.getDynamicAttribute(attributeName).isMultiple()) {
					Element values = attribute.getChild("values");
					if (values != null) {
						List<String> valueList = new ArrayList<String>();
						for (@SuppressWarnings("unchecked")
						Iterator<Element> iterator2 = values.getChildren("value").iterator(); iterator2
								.hasNext();) {
							Element value = iterator2.next();
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
							dynamicObject.initNew(parentPath, value);
						}
					}
				}
			}
		}
	}


	private void handleUnresolved() throws DynamicObjectException, NamingException {
		for (Iterator<ToDo> iterator = unresolved.iterator(); iterator.hasNext();) {
			ToDo toDo = iterator.next();
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
