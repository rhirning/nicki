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
package org.mgnl.nicki.shop.inventory;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.dynamic.objects.shop.AssignedArticle;
import org.mgnl.nicki.shop.attributes.AttributeComponentFactory;
import org.mgnl.nicki.shop.inventory.Inventory.SOURCE;
import org.mgnl.nicki.shop.objects.CartEntry.ACTION;
import org.mgnl.nicki.shop.objects.CatalogArticle;
import org.mgnl.nicki.shop.objects.CatalogArticleAttribute;

@SuppressWarnings("serial")
public class InventoryArticle implements Serializable{
	public enum STATUS {NONE, PROVISIONED, NEW, MODIFIED, DELETED};
	
	private STATUS originalStatus = STATUS.NONE;
	private STATUS status;
	private CatalogArticle catalogArticle;
	private SOURCE source = SOURCE.NONE;
	private Date start = null;
	private Date end = null;
	private Date orgEnd = null;
	private String specifier = null;
	private Map<String, InventoryAttribute> attributes = new HashMap<String, InventoryAttribute>();

	public InventoryArticle(CatalogArticle catalogArticle) {
		this.catalogArticle = catalogArticle;
		addEmptyAttributes();
		setStatus(STATUS.NEW);
	}
	

	public InventoryArticle(CatalogArticle catalogArticle, String specifier) {
		this.catalogArticle = catalogArticle;
		this.specifier = specifier;
		addEmptyAttributes();
		setStatus(STATUS.NEW);
	}

	private void addEmptyAttributes() {
		for (CatalogArticleAttribute attribute : getArticle().getAllAttributes()) {
			this.attributes.put(attribute.getName(), new InventoryAttribute(getArticle(), attribute, ""));
		}
	}

	public InventoryArticle(CatalogArticle catalogArticle, Date start, Date end,
			List<InventoryAttribute> attributes) {
		this.catalogArticle = catalogArticle;
		addEmptyAttributes();
		if (attributes != null) {
			for (InventoryAttribute attribute : attributes) {
				this.attributes.put(attribute.getName(), attribute);
			}
		}
		this.start = start;
		this.end = end;
		this.orgEnd = end;
		setStatus(STATUS.PROVISIONED);
		originalStatus = STATUS.PROVISIONED;
	}

	public InventoryArticle(CatalogArticle catalogArticle, String specifier, Date start, Date end,
			List<InventoryAttribute> attributes) {
		this.catalogArticle = catalogArticle;
		this.setSpecifier(specifier);
		addEmptyAttributes();
		if (attributes != null) {
			for (InventoryAttribute attribute : attributes) {
				this.attributes.put(attribute.getName(), attribute);
			}
		}
		this.start = start;
		this.end = end;
		this.orgEnd = end;
		setStatus(STATUS.PROVISIONED);
		originalStatus = STATUS.PROVISIONED;
	}
	

	public InventoryArticle(CatalogArticle catalogArticle, AssignedArticle assignedArticle,
			List<InventoryAttribute> attributes) {
		this.catalogArticle = catalogArticle;
		this.setSpecifier(assignedArticle.getSpecifier());
		addEmptyAttributes();
		if (attributes != null) {
			for (InventoryAttribute attribute : attributes) {
				this.attributes.put(attribute.getName(), attribute);
			}
		}
		this.start = assignedArticle.getStart();
		this.end = assignedArticle.getEnd();
		this.orgEnd = end;
		setStatus(STATUS.PROVISIONED);
		originalStatus = STATUS.PROVISIONED;
	}


	public void setStatus(STATUS status) {
		this.status = status;
	}

	public STATUS getStatus() {
		return status;
	}

	public STATUS getOriginalStatus() {
		return originalStatus;
	}

	public void reset() {
		setStatus(STATUS.PROVISIONED);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[Article path=").append(getArticle().getPath());
		sb.append(" target=").append(getArticle().getArticlePath());
		sb.append(" start=").append(start);
		sb.append(" end=").append(end);
		sb.append(" status=").append(getStatus()).append("]");
		for (String key : attributes.keySet()) {
			sb.append("\n").append(attributes.get(key).toString());
		}
		return sb.toString();
	}

	public CatalogArticle getArticle() {
		return catalogArticle;
	}

	public String getValue(CatalogArticleAttribute attribute) {
		return getValue(attribute.getName());
	}

	public String getValue(String attributeName) {
		InventoryAttribute iAttribute = attributes.get(attributeName);
		return iAttribute.getValue();
	}

	public void setValue(CatalogArticleAttribute attribute, Object value) {
		InventoryAttribute iAttribute = attributes.get(attribute.getName());
		setValue(iAttribute, value);
	}

	private void setValue(InventoryAttribute iAttribute, Object value) {
		CatalogArticleAttribute attribute = iAttribute.getAttribute();
		String stringValue = AttributeComponentFactory.getAttributeComponent(attribute.getType()).getStringValue(value);
		if (iAttribute != null) {
			if (getStatus() == STATUS.PROVISIONED && !StringUtils.equals(stringValue, iAttribute.getOldValue())) {
				setStatus(STATUS.MODIFIED);
			}
			attributes.get(attribute.getName()).setValue(stringValue);
		}
	}

	public boolean hasChanged() {
		if (getStatus() == STATUS.NEW
				|| getStatus() == STATUS.MODIFIED
				|| getStatus() == STATUS.DELETED) {
			return true;
		}
		return false;
	}

	public static ACTION getAction(STATUS status) {
		if (status == STATUS.DELETED) {
			return ACTION.DELETE;
		}
		else if (status == STATUS.MODIFIED) {
			return ACTION.MODIFY;
		}
		else if (status == STATUS.NEW) {
			return ACTION.ADD;
		}
		return null;
	}

	public static STATUS getStatus(ACTION action) {
		if (action == ACTION.DELETE) {
			return STATUS.DELETED;
		}
		else if (action == ACTION.MODIFY) {
			return STATUS.MODIFIED;
		}
		else if (action == ACTION.ADD) {
			return STATUS.NEW;
		}
		return null;
	}

	public Map<String, InventoryAttribute> getAttributes() {
		return attributes;
	}

	public void setStart(Date start) {
		if (STATUS.NEW == getStatus()) {
			this.start = start;
		}
	}

	public void setEnd(Date end) {
		this.end = end;
		try {
			if (!StringUtils.equals(DataHelper.getDay(end), DataHelper.getDay(orgEnd))) {
				setStatus(STATUS.MODIFIED);
			}
		} catch (Exception e) {
		}
	}

	public Date getStart() {
		return start;
	}
	
	public Date getEnd() {
		return end;
	}

	public SOURCE getSource() {
		return source;
	}

	public void setSource(SOURCE source) {
		this.source = source;
	}

	public String getSpecifier() {
		return specifier;
	}

	public void setSpecifier(String specifier) {
		this.specifier = specifier;
	}

}
