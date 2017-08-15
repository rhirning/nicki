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
import java.io.Writer;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Text;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.DynamicAttribute;
import org.mgnl.nicki.core.objects.DynamicObject;


public class XMLBuilder {
	Document document;
	String path;
	NickiContext context;

	public XMLBuilder(NickiContext context, String path, boolean selfOnly)  {
		this.context = context;
		this.path = path;
		DynamicObject root = context.loadObject(path);
		Element rootNode = getElement(root);
		document = new Document(rootNode);
		if (!selfOnly) {
			addChildren(rootNode, root);
		}
	}

	private void addChildren(Element parentNode, DynamicObject parent) {
		Collection<? extends DynamicObject> children = parent.getAllChildren();
		for (DynamicObject child : children) {
			Element childNode = getElement(child);
			parentNode.addContent(childNode);
			addChildren(childNode, child);
		}
	}

	private Element getElement(DynamicObject dynamicObject) {
		Element newNode = new Element("dynamicObject");
		String nodePath = StringUtils.substringBeforeLast(dynamicObject.getPath(), this.path);
		if (StringUtils.endsWith(nodePath, ",")) {
			nodePath = StringUtils.substringBeforeLast(nodePath, ",");
		}
		newNode.setAttribute("path", nodePath);
		newNode.setAttribute("class", dynamicObject.getClass().getName());
		for (String attributeName : dynamicObject.getModel().getAttributes().keySet()) {
			DynamicAttribute dynamicAttribute = dynamicObject.getModel().getDynamicAttribute(attributeName);
			if (!dynamicAttribute.isVirtual() && dynamicObject.get(attributeName) != null) {
				Element attributeNode = new Element("attribute");
				attributeNode.setAttribute("name", attributeName);
				attributeNode.setAttribute("ldapName", dynamicAttribute.getExternalName());
				if (dynamicAttribute.isMultiple()) {
					@SuppressWarnings("unchecked")
					Collection<Object> values = (Collection<Object>) dynamicObject.get(attributeName);
					if (values.size() > 0) {
						newNode.addContent(attributeNode);
						Element valuesNode = new Element("values");
						attributeNode.addContent(valuesNode);
						for (Object objectValue : values) {
							String value =  (String) objectValue;
							Element valueNode = new Element("value");
							if (dynamicAttribute.isForeignKey() && StringUtils.endsWith(value, this.path)) {
								valueNode.setAttribute("internalLink", "true");
								value = StringUtils.substringBeforeLast(value, "," + this.path);
							}
							valueNode.addContent(getText(value));
							valuesNode.addContent(valueNode);
						}
					}					
				} else {
					String value = dynamicObject.getAttribute(attributeName);
					if (StringUtils.isNotEmpty(value)) {
						newNode.addContent(attributeNode);
						Element valueNode = new Element("value");
						if (dynamicAttribute.isForeignKey() && StringUtils.endsWith(value, this.path)) {
							valueNode.setAttribute("internalLink", "true");
							value = StringUtils.substringBeforeLast(value, "," + this.path);
						}
						valueNode.addContent(getText(value));
						attributeNode.addContent(valueNode);
					}
				}
			}				
		}
		return newNode;
	}
	
	
	private Text getText(String value) {
		if (StringUtils.containsAny(value, "<>&")) {
			return new CDATA(value);
		} else {
			return new Text(value);
		}
	}
	
	public void write(Writer writer) throws IOException {
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		XMLOutputter fmt = new XMLOutputter(format);
		fmt.output(document, writer);
	}

	public Document getDocument() {
		return document;
	}

}
