/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.shop.inventory;

import java.io.Serializable;

import org.mgnl.nicki.shop.catalog.CatalogArticle;
import org.mgnl.nicki.shop.catalog.CatalogArticleAttribute;


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

}
