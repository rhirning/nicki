/**
 * Copyright (c) 2003-2015 Dr. Ralf Hirning
 * All rights reserved.
 *  
 * This program is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 * 
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU Public License v3.0
 * which is available at
 * http://www.gnu.org/licenses/gpl.html
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 * 
 * Any modifications to this file must keep this entire header
 * intact.
*/
package org.mgnl.nicki.shop.base.objects;

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
		StringBuilder sb = new StringBuilder();
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
		StringBuilder sb = new StringBuilder();
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
