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
import java.util.Date;
import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.dynamic.objects.shop.AssignedArticle;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mgnl.nicki.shop.base.objects.CartEntry.ACTION;
import org.mgnl.nicki.shop.base.inventory.Inventory.SOURCE;

@SuppressWarnings("serial")
public class InventoryArticle implements Serializable{
	private static final Logger LOG = LoggerFactory.getLogger(InventoryArticle.class);
	public enum STATUS {NONE, PROVISIONED, NEW, MODIFIED, DELETED};
	
	private STATUS originalStatus = STATUS.NONE;
	private STATUS status;
	private CatalogArticle catalogArticle;
	private SOURCE source = SOURCE.NONE;
	private Date start;
	private Date end;
	private Date orgEnd;
	private String specifier;
	private String comment;
	private boolean readOnly = false;

	public InventoryArticle(CatalogArticle catalogArticle) {
		this.catalogArticle = catalogArticle;
		this.status = STATUS.NEW;
	}
	

	public InventoryArticle(CatalogArticle catalogArticle, String specifier) {
		this.catalogArticle = catalogArticle;
		this.specifier = specifier;
		this.status = STATUS.NEW;
	}
	
	public String getDisplayName() {
		StringBuilder sb = new StringBuilder();
		sb.append(catalogArticle.getDisplayName());
		if (specifier != null) {
			sb.append(": ").append(specifier);
		}
		return sb.toString();
	}

	public InventoryArticle(CatalogArticle catalogArticle, Date start, Date end) {
		this.catalogArticle = catalogArticle;
		this.start = start;
		this.end = end;
		this.orgEnd = end;
		this.status = STATUS.PROVISIONED;
		originalStatus = STATUS.PROVISIONED;
	}

	public InventoryArticle(CatalogArticle catalogArticle, String specifier, Date start, Date end) {
		this.catalogArticle = catalogArticle;
		this.specifier = specifier;
		this.start = start;
		this.end = end;
		this.orgEnd = end;
		this.status = STATUS.PROVISIONED;
		originalStatus = STATUS.PROVISIONED;
	}
	

	public InventoryArticle(CatalogArticle catalogArticle, AssignedArticle assignedArticle) {
		this.catalogArticle = catalogArticle;
		this.specifier = assignedArticle.getSpecifier();
		this.start = assignedArticle.getStart();
		this.end = assignedArticle.getEnd();
		this.orgEnd = end;
		this.status = STATUS.PROVISIONED;
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
		StringBuilder sb = new StringBuilder();
		sb.append("[Article path=").append(getArticle().getPath());
		sb.append(" target=").append(getArticle().getArticlePath());
		sb.append(" start=").append(start);
		sb.append(" end=").append(end);
		sb.append(" status=").append(getStatus()).append("]");
		return sb.toString();
	}

	public CatalogArticle getArticle() {
		return catalogArticle;
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
			LOG.error("Could not set end date", e);
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


	public String getComment() {
		return comment;
	}


	public void setComment(String comment) {
		this.comment = comment;
	}


	public boolean isReadOnly() {
		return readOnly;
	}


	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

}
