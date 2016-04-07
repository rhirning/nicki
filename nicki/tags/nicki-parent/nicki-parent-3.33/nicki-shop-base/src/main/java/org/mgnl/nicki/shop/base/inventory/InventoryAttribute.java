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