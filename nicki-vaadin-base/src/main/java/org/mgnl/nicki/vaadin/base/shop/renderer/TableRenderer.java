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
package org.mgnl.nicki.vaadin.base.shop.renderer;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.shop.catalog.CatalogArticle;
import org.mgnl.nicki.vaadin.base.editor.Icon;
import org.mgnl.nicki.vaadin.base.shop.core.ShopViewerComponent;
import org.mgnl.nicki.vaadin.base.shop.inventory.Inventory;
import org.mgnl.nicki.vaadin.base.shop.inventory.InventoryArticle;
import org.mgnl.nicki.vaadin.base.shop.inventory.InventoryArticle.STATUS;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Layout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class TableRenderer extends BaseShopRenderer implements ShopRenderer{
	
	private Table table;

	@Override
	public Component render(ShopViewerComponent shopViewerComponent, Inventory inventory) {
		setInventory(inventory);
		// collect all articles
		List<CatalogArticle> articles = shopViewerComponent.getAllArticles();
		// create Table
		table = new Table();
		table.setWidth("100%");
		table.setHeight("100%");
		table.addContainerProperty("checkbox", CheckBox.class, "");
		table.setColumnWidth("checkbox", 36);
		table.setColumnHeader("checkbox", "");
		table.addContainerProperty("title", String.class, "");
		table.setColumnWidth("title", 200);
		table.setColumnHeader("title", I18n.getText("nicki.rights.attribute.title.label"));
		table.addContainerProperty("dateFrom", PopupDateField.class, "");
		table.setColumnWidth("dateFrom", 100);
		table.setColumnHeader("dateFrom", CatalogArticle.getFixedAttribute("dateFrom").getLabel());
		table.addContainerProperty("dateTo", PopupDateField.class, "");
		table.setColumnWidth("dateTo", 100);
		table.setColumnHeader("dateTo", CatalogArticle.getFixedAttribute("dateTo").getLabel());
		
		table.addContainerProperty("attributes", Layout.class, "");
		table.setColumnHeader("attributes", I18n.getText("nicki.rights.attributes.label"));

		// add articles to table
		for (Iterator<CatalogArticle> iterator = articles.iterator(); iterator.hasNext();) {
			CatalogArticle article = (CatalogArticle) iterator.next();
			CheckBox checkBox = new CheckBox();
			checkBox.setData(article);
			checkBox.setImmediate(true);
			checkBox.setWidth("-1px");
			if (article.hasDescription()) {
				checkBox.setIcon(Icon.HELP.getResource());
				checkBox.setDescription(article.getDescription());
			}

			checkBox.addListener(new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					  CatalogArticle article = (CatalogArticle) event.getButton().getData();
					  Item item = table.getItem(article);
					  boolean enabled = event.getButton().booleanValue();
					  if (enabled) {
						  getInventory().addArticle(article);
						  showEntry(item, article);
					  } else {
						  getInventory().removeArticle(article);
						  hideEntry(item);
					  }
				}

			});
			Item item = table.addItem(article);
			item.getItemProperty("title").setValue(article.getDisplayName());
			item.getItemProperty("checkbox").setValue(checkBox);
//			item.getItemProperty("attributes").setValue(getArticleAttributes(article));
			if (getInventory().hasArticle(article)) {
				checkBox.setValue(true);
				checkBox.setEnabled(false);
				showEntry(item, article);
			}

		}
		return table;
	}

	protected void hideEntry(Item item) {
		item.getItemProperty("dateFrom").setValue(null);
		item.getItemProperty("dateTo").setValue(null);
		item.getItemProperty("attributes").setValue(null);
//		removeExcept(parent, event.getButton());
	}

	private void showEntry(Item item, CatalogArticle article) {
		InventoryArticle inventoryArticle = getInventory().getArticle(article);

		Date start = new Date();
		Date end = null;
		boolean enabled = true;
		if (inventoryArticle != null && inventoryArticle.getStatus() != STATUS.NEW) {
			start = inventoryArticle.getStart();
			end = inventoryArticle.getEnd();
			enabled = false;
		}
		item.getItemProperty("dateFrom").setValue(getAttributeComponent(article, CatalogArticle.getFixedAttribute("dateFrom"), enabled, start));
		item.getItemProperty("dateTo").setValue(getAttributeComponent(article, CatalogArticle.getFixedAttribute("dateTo"), true, end));
		item.getItemProperty("attributes").setValue(getVerticalArticleAttributes(article, enabled));
//		showArticleAttributes(parent);
	}

}
