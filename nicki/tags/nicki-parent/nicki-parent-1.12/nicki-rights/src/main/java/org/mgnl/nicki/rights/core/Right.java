package org.mgnl.nicki.rights.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.mgnl.nicki.core.i18n.I18n;

@SuppressWarnings("serial")
public class Right implements Serializable {
	private String name;
	private String label;
	private List<RightAttribute> attributeList = new ArrayList<RightAttribute>();

	public Right(Element rightElement) throws ClassNotFoundException {
		this.name = rightElement.getAttributeValue("name");
		this.label = I18n.getText(rightElement.getAttributeValue("label"));
		@SuppressWarnings("unchecked")
		List<Element> attributes = rightElement.getChildren("attribute");
		if (attributes != null && attributes.size() > 0) {
			for (Iterator<Element> iterator = attributes.iterator(); iterator.hasNext();) {
				Element attributeElement = iterator.next();
				try {
					attributeList.add(new RightAttribute(attributeElement));
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}
	
	public Right(Element rightElement, List<RightAttribute> attributeList) {
		this.name = rightElement.getAttributeValue("name");
		this.label = I18n.getText(rightElement.getAttributeValue("label"));
		this.attributeList = attributeList;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[right name='").append(getName());
		sb.append("' label='").append(getLabel());
		sb.append("']\n");
		for (Iterator<RightAttribute> iterator = getAttributeList().iterator(); iterator.hasNext();) {
			RightAttribute attribute = iterator.next();
			sb.append(attribute.toString()).append("\n");
		}
		return sb.toString();
	}

	public String getName() {
		return name;
	}

	public String getLabel() {
		return label;
	}

	public List<RightAttribute> getAttributeList() {
		return attributeList;
	}

	public boolean hasAttributes() {
		return attributeList != null && attributeList.size() > 0;
	}

}
