
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
import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.dynamic.objects.shop.AssignedArticle;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;

import lombok.extern.slf4j.Slf4j;

import org.mgnl.nicki.shop.base.objects.CartEntry.ACTION;
import org.mgnl.nicki.shop.base.inventory.Inventory.SOURCE;

// TODO: Auto-generated Javadoc
/**
 * The Class InventoryArticle.
 */
@Slf4j
@SuppressWarnings("serial")
public class InventoryArticle implements Serializable{
	
	/**
	 * The Enum STATUS.
	 */
	public enum STATUS {
/** The none. */
NONE, 
 /** The provisioned. */
 PROVISIONED, 
 /** The new. */
 NEW, 
 /** The modified. */
 MODIFIED, 
 /** The deleted. */
 DELETED};
	
	/** The original status. */
	private STATUS originalStatus = STATUS.NONE;
	
	/** The status. */
	private STATUS status;
	
	/** The catalog article. */
	private CatalogArticle catalogArticle;
	
	/** The source. */
	private SOURCE source = SOURCE.NONE;
	
	/** The start. */
	private Date start;
	
	/** The end. */
	private Date end;
	
	/** The org end. */
	private Date orgEnd;
	
	/** The specifier. */
	private String specifier;
	
	/** The comment. */
	private String comment;
	
	/** The read only. */
	private boolean readOnly = false;

	/**
	 * Instantiates a new inventory article.
	 *
	 * @param catalogArticle the catalog article
	 */
	public InventoryArticle(CatalogArticle catalogArticle) {
		this.catalogArticle = catalogArticle;
		this.status = STATUS.NEW;
	}
	

	/**
	 * Instantiates a new inventory article.
	 *
	 * @param catalogArticle the catalog article
	 * @param specifier the specifier
	 */
	public InventoryArticle(CatalogArticle catalogArticle, String specifier) {
		this.catalogArticle = catalogArticle;
		this.specifier = specifier;
		this.status = STATUS.NEW;
	}
	
	/**
	 * Gets the display name.
	 *
	 * @return the display name
	 */
	public String getDisplayName() {
		StringBuilder sb = new StringBuilder();
		sb.append(catalogArticle.getDisplayName());
		if (specifier != null) {
			sb.append(": ").append(specifier);
		}
		return sb.toString();
	}

	/**
	 * Instantiates a new inventory article.
	 *
	 * @param catalogArticle the catalog article
	 * @param start the start
	 * @param end the end
	 */
	public InventoryArticle(CatalogArticle catalogArticle, Date start, Date end) {
		this.catalogArticle = catalogArticle;
		this.start = start;
		this.end = end;
		this.orgEnd = end;
		this.status = STATUS.PROVISIONED;
		originalStatus = STATUS.PROVISIONED;
	}

	/**
	 * Instantiates a new inventory article.
	 *
	 * @param catalogArticle the catalog article
	 * @param specifier the specifier
	 * @param start the start
	 * @param end the end
	 */
	public InventoryArticle(CatalogArticle catalogArticle, String specifier, Date start, Date end) {
		this.catalogArticle = catalogArticle;
		this.specifier = specifier;
		this.start = start;
		this.end = end;
		this.orgEnd = end;
		this.status = STATUS.PROVISIONED;
		originalStatus = STATUS.PROVISIONED;
	}
	

	/**
	 * Instantiates a new inventory article.
	 *
	 * @param catalogArticle the catalog article
	 * @param assignedArticle the assigned article
	 */
	public InventoryArticle(CatalogArticle catalogArticle, AssignedArticle assignedArticle) {
		this.catalogArticle = catalogArticle;
		this.specifier = assignedArticle.getSpecifier();
		this.start = assignedArticle.getStart();
		this.end = assignedArticle.getEnd();
		this.orgEnd = end;
		this.status = STATUS.PROVISIONED;
		originalStatus = STATUS.PROVISIONED;
	}


	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(STATUS status) {
		this.status = status;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public STATUS getStatus() {
		return status;
	}

	/**
	 * Gets the original status.
	 *
	 * @return the original status
	 */
	public STATUS getOriginalStatus() {
		return originalStatus;
	}

	/**
	 * Reset.
	 */
	public void reset() {
		setStatus(STATUS.PROVISIONED);
	}
	
	/**
	 * To string.
	 *
	 * @return the string
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[Article path=").append(getArticle().getPath());
		sb.append(" target=").append(getArticle().getArticlePath());
		sb.append(" start=").append(start);
		sb.append(" end=").append(end);
		sb.append(" status=").append(getStatus()).append("]");
		return sb.toString();
	}

	/**
	 * Gets the article.
	 *
	 * @return the article
	 */
	public CatalogArticle getArticle() {
		return catalogArticle;
	}

	/**
	 * Checks for changed.
	 *
	 * @return true, if successful
	 */
	public boolean hasChanged() {
		if (getStatus() == STATUS.NEW
				|| getStatus() == STATUS.MODIFIED
				|| getStatus() == STATUS.DELETED) {
			return true;
		}
		return false;
	}

	/**
	 * Gets the action.
	 *
	 * @param status the status
	 * @return the action
	 */
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

	/**
	 * Gets the status.
	 *
	 * @param action the action
	 * @return the status
	 */
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

	/**
	 * Sets the start.
	 *
	 * @param start the new start
	 */
	public void setStart(Date start) {
		if (STATUS.NEW == getStatus()) {
			this.start = start;
		}
	}

	/**
	 * Sets the end.
	 *
	 * @param end the new end
	 */
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

	/**
	 * Gets the start.
	 *
	 * @return the start
	 */
	public Date getStart() {
		return start;
	}
	
	/**
	 * Gets the end.
	 *
	 * @return the end
	 */
	public Date getEnd() {
		return end;
	}

	/**
	 * Gets the source.
	 *
	 * @return the source
	 */
	public SOURCE getSource() {
		return source;
	}

	/**
	 * Sets the source.
	 *
	 * @param source the new source
	 */
	public void setSource(SOURCE source) {
		this.source = source;
	}

	/**
	 * Gets the specifier.
	 *
	 * @return the specifier
	 */
	public String getSpecifier() {
		return specifier;
	}

	/**
	 * Sets the specifier.
	 *
	 * @param specifier the new specifier
	 */
	public void setSpecifier(String specifier) {
		this.specifier = specifier;
	}


	/**
	 * Gets the comment.
	 *
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}


	/**
	 * Sets the comment.
	 *
	 * @param comment the new comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}


	/**
	 * Checks if is read only.
	 *
	 * @return true, if is read only
	 */
	public boolean isReadOnly() {
		return readOnly;
	}


	/**
	 * Sets the read only.
	 *
	 * @param readOnly the new read only
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

}
