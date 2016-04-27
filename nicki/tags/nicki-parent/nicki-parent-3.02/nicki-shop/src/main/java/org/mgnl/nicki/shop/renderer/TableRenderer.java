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
package org.mgnl.nicki.shop.renderer;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;
import org.mgnl.nicki.shop.base.objects.CatalogArticleAttribute;
import org.mgnl.nicki.shop.core.ShopViewerComponent;
import org.mgnl.nicki.shop.base.inventory.Inventory;
import org.mgnl.nicki.shop.base.inventory.Inventory.SOURCE;
import org.mgnl.nicki.shop.base.inventory.InventoryArticle;
import org.mgnl.nicki.shop.base.inventory.InventoryArticle.STATUS;
import org.mgnl.nicki.shop.base.objects.MultipleInstancesCatalogArticle;
import org.mgnl.nicki.vaadin.base.editor.Icon;

import com.vaadin.data.Item;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Layout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class TableRenderer extends BaseShopRenderer implements ShopRenderer {
	
	private ShopViewerComponent shopViewerComponent;
	private Table table = null;

	public AbstractComponent render(ShopViewerComponent shopViewerComponent, Inventory inventory) {
		this.shopViewerComponent = shopViewerComponent;
		setInventory(inventory);
		render();
		return table;
	}

	public void render() {
		// collect all articles
		List<CatalogArticle> articles = shopViewerComponent.getAllArticles();
		// create Table
				
		table = new Table();
		table.setWidth("100%");
		table.setHeight("100%");
		table.addContainerProperty("checkbox", Component.class, "");
		table.setColumnWidth("checkbox", 64);
		table.setColumnHeader("checkbox", "");
		table.addContainerProperty("title", String.class, "");
		table.setColumnWidth("title", 200);
		table.setColumnHeader("title", I18n.getText("nicki.rights.attribute.title.label"));
		table.addContainerProperty("dateFrom", PopupDateField.class, "");
		table.setColumnWidth("dateFrom", 100);
		table.setColumnHeader("dateFrom", I18n.getText(CatalogArticle.CAPTION_START));
		table.addContainerProperty("dateTo", PopupDateField.class, "");
		table.setColumnWidth("dateTo", 100);
		table.setColumnHeader("dateTo", I18n.getText(CatalogArticle.CAPTION_END));
		
		table.addContainerProperty("attributes", Layout.class, "");
		table.setColumnHeader("attributes", I18n.getText("nicki.rights.attributes.label"));

		// add articles to table
		for (CatalogArticle article : articles) {
			if (!article.isMultiple()) {
				addArticle(article, getInventory().getArticle(article));
			} else {
		        Button button = new Button("+");
		        button.setData(article);
		        button.setImmediate(true);
		        button.setWidth("-1px");
				if (article.hasDescription()) {
					button.setIcon(Icon.HELP.getResource());
					button.setDescription(article.getDescription());
				}
		        button.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						CatalogArticle article = (CatalogArticle) event.getButton().getData();
						addInstance(article);
					}
				});

				Item item = table.addItem(article);
				item.getItemProperty("title").setValue(article.getDisplayName());
				item.getItemProperty("checkbox").setValue(button);
				
		        Map<String, InventoryArticle> articleMap = getInventory().getArticles(article);
		        if (articleMap != null) {
			        for (String specifier : articleMap.keySet()) {
						InventoryArticle iArticle = articleMap.get(specifier);
						addMultiArticle(iArticle, iArticle.getStatus());
					}
		        }	
			}
		}
	}
	
	protected void addInstance(CatalogArticle catalogArticle) {
		EnterSpecifierAsSelectDialog dialog = new EnterSpecifierAsSelectDialog("nicki.rights.specifier",
				I18n.getText("nicki.rights.specifier.define.window.title"));
		NewSpecifiedArticleHandler handler = new NewSpecifiedArticleHandler(catalogArticle, this);
		dialog.setHandler(handler);
		dialog.init((MultipleInstancesCatalogArticle) catalogArticle);
		dialog.setWidth(440, Unit.PIXELS);
		dialog.setHeight(500, Unit.PIXELS);
		dialog.setModal(true);
		UI.getCurrent().addWindow(dialog);		
	}

	public Item addMultiArticle(InventoryArticle iArticle, STATUS status) {
		CheckBox checkBox = new CheckBox();
		CatalogArticle article = iArticle.getArticle();
		checkBox.setData(iArticle);
		checkBox.setImmediate(true);
		checkBox.setWidth("-1px");
		if (article.hasDescription()) {
			checkBox.setIcon(Icon.HELP.getResource());
			checkBox.setDescription(article.getDescription());
		}
		checkBox.setValue(true);
		if (status == STATUS.NEW) {
			checkBox.setEnabled(true);
		} else {
			checkBox.setEnabled(false);
		}
		
		checkBox.addValueChangeListener(new MulitCheckBoxChangeListener(getInventory(), iArticle, this));

		Item item = table.addItem(iArticle);
		item.getItemProperty("title").setValue(iArticle.getSpecifier());
		item.getItemProperty("checkbox").setValue(checkBox);
		showEntry(item, article, iArticle);

		return item;
	}

	protected void cleanup() {
		// TODO Auto-generated method stub
		
	}

	public Item addArticle(CatalogArticle article, InventoryArticle inventoryArticle) {
		CheckBox checkBox = new CheckBox();
		checkBox.setData(article);

		checkBox.setImmediate(true);
		checkBox.setWidth("-1px");
		if (article.hasDescription()) {
			checkBox.setIcon(Icon.HELP.getResource());
			checkBox.setDescription(article.getDescription());
		}

		checkBox.addValueChangeListener(new CheckBoxChangeListener(getInventory(), article, this));

		Item item = table.addItem(article);
		item.getItemProperty("title").setValue(article.getDisplayName());
		item.getItemProperty("checkbox").setValue(checkBox);

		if (inventoryArticle != null) {
			checkBox.setValue(true);
			if (inventoryArticle.getStatus() == STATUS.NEW) {
				checkBox.setEnabled(true);
			} else {
				checkBox.setEnabled(false);
			}
			showEntry(item, article, inventoryArticle);
		}
		return item;
	}

	protected void hideEntry(Item item) {
		item.getItemProperty("dateFrom").setValue(null);
		item.getItemProperty("dateTo").setValue(null);
		item.getItemProperty("attributes").setValue(null);
//		removeExcept(parent, event.getButton());
	}

	public void showEntry(Item item, CatalogArticle article, InventoryArticle inventoryArticle) {
		SOURCE source = SOURCE.SHOP;
		Date start = new Date();
		Date end = null;
		boolean enabled = true;
		boolean toEnabled = true;
		if (inventoryArticle != null && inventoryArticle.getStatus() != STATUS.NEW) {
			start = inventoryArticle.getStart();
			end = inventoryArticle.getEnd();
			enabled = false;
			source = inventoryArticle.getSource();
			if (source == SOURCE.RULE) {
				toEnabled = false;
			}
		}

		item.getItemProperty("dateFrom").setValue(getStartDateComponent(inventoryArticle, enabled, start));
		item.getItemProperty("dateTo").setValue(getEndDateComponent(inventoryArticle, toEnabled, end));
		item.getItemProperty("attributes").setValue(getVerticalArticleAttributes(article, inventoryArticle, enabled, source));
//		showArticleAttributes(parent);
	}
	

	private AbstractOrderedLayout getVerticalArticleAttributes(CatalogArticle article, InventoryArticle inventoryArticle, boolean provisioned, SOURCE source) {
		AbstractOrderedLayout attrLayout = new VerticalLayout();
		if (article.hasAttributes()) {
			for (CatalogArticleAttribute pageAttribute : article.getAllAttributes()) {
				boolean enabled = true;
				attrLayout.addComponent(getAttributeComponent(article, inventoryArticle, pageAttribute, enabled));
			}
		}
		return attrLayout;
	}

	public Table getTable() {
		return table;
	}

}