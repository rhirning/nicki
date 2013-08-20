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
package org.mgnl.nicki.shop.core;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.shop.base.objects.Catalog;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

@SuppressWarnings("serial")
public class ArticleShopContainer implements ShopContainer{

	private String STATUS_ORDERED; 
	private String STATUS_CANCELED; 
	private String STATUS_PROVISIONED; 

    private String[] visibleColumns = new String[] {
    	PROPERTY_STATUS,
    	PROPERTY_NAME,
    	PROPERTY_PATH
    };
	
	private IndexedContainer container = new IndexedContainer();
	private DynamicObject person;
	private Catalog catalog;

	public ArticleShopContainer(Catalog catalog, DynamicObject person, String i18nBase) {
		super();
		this.catalog = catalog;
		this.person = person;
		STATUS_ORDERED = I18n.getText(i18nBase + ".status.ordered");
		STATUS_CANCELED = I18n.getText(i18nBase + ".status.canceled");
		STATUS_PROVISIONED = I18n.getText(i18nBase + ".status.provisioned");
	}



	public IndexedContainer getArticles() throws DynamicObjectException {
		container = new IndexedContainer();
		container.addContainerProperty(PROPERTY_STATUS, String.class, null);
		container.addContainerProperty(PROPERTY_NAME, String.class, null);
		container.addContainerProperty(PROPERTY_CATEGORY, String.class, null);

		container.addContainerProperty(PROPERTY_PATH, String.class, null);
		List<CatalogArticle> loadedArticles = catalog.getContext().loadObjects(CatalogArticle.class, catalog.getPath(), "objectClass=nickiShopArticle");

	    if (loadedArticles != null) {
		    for (CatalogArticle article : loadedArticles) {
				Item item = container.addItem(article);
				item.getItemProperty(PROPERTY_NAME).setValue(article.getDisplayName());
				item.getItemProperty(PROPERTY_PATH).setValue(article.getCatalogPath());
				item.getItemProperty(PROPERTY_CATEGORY).setValue(article.get(PROPERTY_CATEGORY).toString());
				if (catalog.hasArticle(getPerson(), article)) {
					item.getItemProperty(PROPERTY_STATUS).setValue(STATUS_PROVISIONED);
				}
			}
	    }
		
		return container;
	}



	public void orderItem(Object target) {
		Item item = container.getItem(target);
		item.getItemProperty(PROPERTY_STATUS).setValue(STATUS_ORDERED);
	}
	
	public void cancelItem(Object target) {
		Item item = container.getItem(target);
		String oldValue = (String) item.getItemProperty(PROPERTY_STATUS).getValue();

		if (StringUtils.equals(oldValue, STATUS_ORDERED)) {
			item.getItemProperty(PROPERTY_STATUS).setValue("");
		} else if (StringUtils.equals(oldValue, STATUS_PROVISIONED)) {
			item.getItemProperty(PROPERTY_STATUS).setValue(STATUS_CANCELED);
		}
	}



	public void setCategoryFilter(List<Object> values) {
		this.container.removeAllContainerFilters();
		if (values != null && values.size() > 0) {
			for (Object object : values) {
				String filterString = (String) object;
				this.container.addContainerFilter(PROPERTY_CATEGORY, filterString, true, false);
			}
		}
	}



	public Person getPerson() {
		return (Person) person;
	}



	public String[] getVisibleColumns() {
		return visibleColumns;
	}
	

}
