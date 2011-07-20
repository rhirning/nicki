package org.mgnl.nicki.rights.core;

import org.jdom.Element;
import org.mgnl.nicki.core.i18n.I18n;

public class RightAttribute {
	private String name;
	private String label;
	private String type;

	public RightAttribute(Element attributeElement) throws ClassNotFoundException {
		this.name = attributeElement.getAttributeValue("name");
		this.label = I18n.getText(attributeElement.getAttributeValue("label"));
		this.type = attributeElement.getAttributeValue("type");
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("    [attribute name='").append(name);
		sb.append("' label='").append(label);
		sb.append("' type='").append(type);
		sb.append("']");
		return sb.toString();
	}

	public String getName() {
		return name;
	}

	public String getLabel() {
		return label;
	}

	public String getType() {
		return type;
	}



}
