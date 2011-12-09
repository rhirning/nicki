/**
 * This file Copyright (c) 2011 deron Consulting GmbH
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.shop.catalog;

import java.io.Serializable;

import org.jdom.Element;
import org.mgnl.nicki.core.i18n.I18n;

@SuppressWarnings("serial")
public class CatalogArticleAttribute implements Serializable{
	public static final String SEPARATOR = "|"; 
	private String name;
	private String key;
	private String type;
	private String contentClass;
	public CatalogArticleAttribute(String name, String key, String type) {
		this.name = name;
		this.key = key;
		this.type = type;
	}

	public CatalogArticleAttribute(Element attributeElement) {
		this.name = attributeElement.getAttributeValue("name");
		this.key = attributeElement.getAttributeValue("label");
		this.type = attributeElement.getAttributeValue("type");
		this.contentClass = attributeElement.getAttributeValue("contentClass");
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLabel() {
		return I18n.getText(key);
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getAttributeString() {
		StringBuffer sb = new StringBuffer();
		sb.append(name).append(SEPARATOR).append(key).append(SEPARATOR).append(type);
		return sb.toString();
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getKey() {
		return key;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[catalogArticleAttribute name='").append(getName());
		sb.append("' label='").append(getLabel());
		sb.append("' key='").append(getKey());
		sb.append("' type='").append(getType());
		sb.append("']\n");
		return sb.toString();
	}

	public String getContentClass() {
		return contentClass;
	}
}
