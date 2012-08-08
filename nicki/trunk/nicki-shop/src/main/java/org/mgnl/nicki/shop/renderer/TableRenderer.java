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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.shop.core.ShopViewerComponent;
import org.mgnl.nicki.shop.inventory.Inventory;
import org.mgnl.nicki.shop.inventory.Inventory.SOURCE;
import org.mgnl.nicki.shop.inventory.InventoryArticle;
import org.mgnl.nicki.shop.inventory.InventoryArticle.STATUS;
import org.mgnl.nicki.shop.objects.CatalogArticle;
import org.mgnl.nicki.shop.objects.CatalogArticleAttribute;
import org.mgnl.nicki.shop.objects.MultipleInstancesCatalogArticle;
import org.mgnl.nicki.vaadin.base.editor.Icon;

import com.vaadin.data.Item;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Layout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class TableRenderer extends BaseShopRenderer implements ShopRenderer {
	
	private ShopViewerComponent shopViewerComponent;
	private Table table = null;
	private AbsoluteLayout layout = new AbsoluteLayout();

	public AbstractComponent render(ShopViewerComponent shopViewerComponent, Inventory inventory) {
		this.shopViewerComponent = shopViewerComponent;
		setInventory(inventory);
		render();
		return layout;
	}

	public void render() {
		// collect all articles
		List<CatalogArticle> articles = shopViewerComponent.getAllArticles();
		// create Table
		
		if (table != null) {
			layout.removeAllComponents();
			table = null;
		}
		
		table = new Table();
		layout.addComponent(table, "top:0.0px;left:0.0px;");
		table.setWidth("100%");
		table.setHeight("100%");
		table.addContainerProperty("checkbox", Button.class, "");
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
		for (Iterator<CatalogArticle> iterator = articles.iterator(); iterator.hasNext();) {
			CatalogArticle article = (CatalogArticle) iterator.next();
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
		        button.addListener(new Button.ClickListener() {
					
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
		EnterSpecifierAsSelectDialog dialog = new EnterSpecifierAsSelectDialog("nicki.rights.specifier");
//		EnterSpecifierAsTextDialog dialog = new EnterSpecifierAsTextDialog("nicki.rights.specifier");
		NewSpecifiedArticleHandler handler = new NewSpecifiedArticleHandler(catalogArticle, this);
		dialog.setHandler(handler);
		dialog.init((MultipleInstancesCatalogArticle) catalogArticle);

		Window newWindow = new Window(I18n.getText("nicki.rights.specifier.define.window.title"),
				dialog);
		newWindow.setWidth(440, Sizeable.UNITS_PIXELS);
		newWindow.setHeight(500, Sizeable.UNITS_PIXELS);
		newWindow.setModal(true);
		table.getWindow().addWindow(newWindow);		
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

		checkBox.addListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				InventoryArticle iArticle = (InventoryArticle) event.getButton().getData();
				boolean enabled = event.getButton().booleanValue();
				if (!enabled) {
					getInventory().removeArticle(iArticle);
					render();
				}
			}

		});
		Item item = table.addItem(iArticle);
		item.getItemProperty("title").setValue(iArticle.getSpecifier());
		item.getItemProperty("checkbox").setValue(checkBox);
		showEntry(item, article, iArticle);

		return item;
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

		checkBox.addListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				CatalogArticle article = (CatalogArticle) event.getButton().getData();
				  Item item = table.getItem(article);
				  boolean enabled = event.getButton().booleanValue();
				  if (enabled) {
					  InventoryArticle inventoryArticle = getInventory().addArticle(article);
					  showEntry(item, article, inventoryArticle);
				  } else {
					  getInventory().removeArticle(article);
					  hideEntry(item);
				  }
			}

		});
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

	private void showEntry(Item item, CatalogArticle article, InventoryArticle inventoryArticle) {
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
			for (Iterator<CatalogArticleAttribute> iterator = article.getAllAttributes().iterator(); iterator.hasNext();) {
				CatalogArticleAttribute pageAttribute = iterator.next();
				boolean enabled = true;
				attrLayout.addComponent(getAttributeComponent(article, inventoryArticle, pageAttribute, enabled));
			}
		}
		return attrLayout;
	}

	public Window getWindow() {
		return table.getWindow();
	}

}
