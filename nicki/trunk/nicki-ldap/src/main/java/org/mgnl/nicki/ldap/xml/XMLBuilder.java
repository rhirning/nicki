package org.mgnl.nicki.ldap.xml;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Text;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicObject;


public class XMLBuilder {
	Document document;
	String path;
	NickiContext context;

	public XMLBuilder(NickiContext context, String path)  {
		this.context = context;
		this.path = path;
		DynamicObject root = context.loadObject(path);
		Element rootNode = getElement(root);
		document = new Document(rootNode);
		DynamicObject parent = context.loadObject(path);
		addChildren(rootNode, parent);
	}

	private void addChildren(Element parentNode, DynamicObject parent) {
		List<DynamicObject> children = parent.getAllChildren();
		for (Iterator<DynamicObject> iterator = children.iterator(); iterator.hasNext();) {
			DynamicObject child = iterator.next();
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
		for (Iterator<String> iterator = dynamicObject.getModel().getAttributes().keySet().iterator(); iterator.hasNext();) {
			String attributeName = iterator.next();
			DynamicAttribute dynamicAttribute = dynamicObject.getModel().getDynamicAttribute(attributeName);
			if (!dynamicAttribute.isVirtual() && dynamicObject.get(attributeName) != null) {
				Element attributeNode = new Element("attribute");
				attributeNode.setAttribute("name", attributeName);
				attributeNode.setAttribute("ldapName", dynamicAttribute.getLdapName());
				if (dynamicAttribute.isMultiple()) {
					@SuppressWarnings("unchecked")
					List<Object> values = (List<Object>) dynamicObject.get(attributeName);
					if (values.size() > 0) {
						newNode.addContent(attributeNode);
						Element valuesNode = new Element("values");
						attributeNode.addContent(valuesNode);
						for (Iterator<Object> iterator2 = values.iterator(); iterator2
								.hasNext();) {
							String value =  (String) iterator2.next();
							Element valueNode = new Element("value");
							if (dynamicAttribute.isForeignKey()) {
								if (StringUtils.endsWith(value, this.path)) {
									valueNode.setAttribute("internalLink", "true");
									value = StringUtils.substringBeforeLast(value, "," + this.path);
								}
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
						if (dynamicAttribute.isForeignKey()) {
							if (StringUtils.endsWith(value, this.path)) {
								valueNode.setAttribute("internalLink", "true");
								value = StringUtils.substringBeforeLast(value, "," + this.path);
							}
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
