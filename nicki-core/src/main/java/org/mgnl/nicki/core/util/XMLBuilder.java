
package org.mgnl.nicki.core.util;

/*-
 * #%L
 * nicki-core
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


import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.CDATA;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Text;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.DynamicAttribute;
import org.mgnl.nicki.core.objects.DynamicObject;


// TODO: Auto-generated Javadoc
/**
 * The Class XMLBuilder.
 */
public class XMLBuilder {
	
	/** The document. */
	Document document;
	
	/** The path. */
	String path;
	
	/** The context. */
	NickiContext context;

	/**
	 * Instantiates a new XML builder.
	 *
	 * @param context the context
	 * @param path the path
	 * @param selfOnly the self only
	 */
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

	/**
	 * Adds the children.
	 *
	 * @param parentNode the parent node
	 * @param parent the parent
	 */
	private void addChildren(Element parentNode, DynamicObject parent) {
		Collection<? extends DynamicObject> children = parent.getAllChildren();
		for (DynamicObject child : children) {
			Element childNode = getElement(child);
			parentNode.addContent(childNode);
			addChildren(childNode, child);
		}
	}

	/**
	 * Gets the element.
	 *
	 * @param dynamicObject the dynamic object
	 * @return the element
	 */
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
	
	
	/**
	 * Gets the text.
	 *
	 * @param value the value
	 * @return the text
	 */
	private Text getText(String value) {
		if (StringUtils.containsAny(value, "<>&")) {
			return new CDATA(value);
		} else {
			return new Text(value);
		}
	}
	
	/**
	 * Write.
	 *
	 * @param writer the writer
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void write(Writer writer) throws IOException {
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		XMLOutputter fmt = new XMLOutputter(format);
		fmt.output(document, writer);
	}

	/**
	 * Gets the document.
	 *
	 * @return the document
	 */
	public Document getDocument() {
		return document;
	}

}
