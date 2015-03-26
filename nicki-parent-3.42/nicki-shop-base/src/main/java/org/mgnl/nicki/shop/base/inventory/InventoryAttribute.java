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
package org.mgnl.nicki.shop.base.inventory;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;
import org.mgnl.nicki.shop.base.objects.CatalogArticleAttribute;


@SuppressWarnings("serial")
public class InventoryAttribute implements Serializable{

	private CatalogArticle article;
	private CatalogArticleAttribute attribute;
	private String oldValue;
	private String value;
	
	public InventoryAttribute(CatalogArticle article,
			CatalogArticleAttribute attribute, String value) {
		super();
		this.article = article;
		this.attribute = attribute;
		this.oldValue = value;
		this.value = value;
	}

	public String getName() {
		return attribute.getName();
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public void setOldValue(String value) {
		this.oldValue = value;
	}
	
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("  [Attribute name=").append(getName());
		sb.append(" value=").append(getValue());
		sb.append(" old=").append(getOldValue()).append("]");
		return sb.toString();
	}

	public CatalogArticle getArticle() {
		return article;
	}

	public CatalogArticleAttribute getAttribute() {
		return attribute;
	}

	public String getOldValue() {
		return oldValue;
	}

	public String getValue() {
		return value;
	}
	
	public boolean hasChanged() {
		return !StringUtils.equals(value, oldValue);
	}

}
