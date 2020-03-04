
package org.mgnl.nicki.shop.base.inventory;

/*-
 * #%L
 * nicki-shop-base
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.dynamic.objects.shop.AssignedArticle;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;

import lombok.extern.slf4j.Slf4j;

import org.mgnl.nicki.shop.base.objects.CartEntry.ACTION;
import org.mgnl.nicki.shop.base.inventory.Inventory.SOURCE;

@Slf4j
@SuppressWarnings("serial")
public class InventoryArticle implements Serializable{
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
			log.error("Could not set end date", e);
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
