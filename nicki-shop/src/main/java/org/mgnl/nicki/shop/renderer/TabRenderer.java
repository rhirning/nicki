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

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.core.util.Classes;
import org.mgnl.nicki.dynamic.objects.shop.CatalogArticle;
import org.mgnl.nicki.shop.core.ShopPage;
import org.mgnl.nicki.shop.core.ShopPage.TYPE;
import org.mgnl.nicki.shop.core.ShopViewerComponent;
import org.mgnl.nicki.shop.inventory.Inventory;
import org.mgnl.nicki.shop.inventory.SpecifiedArticle;
import org.mgnl.nicki.shop.inventory.Inventory.SOURCE;
import org.mgnl.nicki.shop.inventory.InventoryArticle;
import org.mgnl.nicki.shop.inventory.InventoryArticle.STATUS;
import org.mgnl.nicki.vaadin.base.editor.Icon;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class TabRenderer extends BaseShopRenderer implements ShopRenderer {

	private ShopViewerComponent shopViewerComponent;

	public Component render(ShopViewerComponent shopViewerComponent, Inventory inventory) {
		this.shopViewerComponent = shopViewerComponent;
		setInventory(inventory);
		TabSheet tabSheet = new TabSheet();
		tabSheet.setHeight("100%");
		addTabs(tabSheet);
		return tabSheet;
	}
	
	private void addTabs(TabSheet tabSheet) {
		for (Iterator<CatalogArticle> iterator = shopViewerComponent.getArticles().iterator(); iterator.hasNext();) {
			CatalogArticle article = iterator.next();
			addArticleTab(tabSheet, article);
		}
		for (Iterator<ShopPage> iterator = shopViewerComponent.getPageList().iterator(); iterator.hasNext();) {
			ShopPage page = iterator.next();
			addPageTab(tabSheet, page);
		}
	}
	
	private void addPageTab(TabSheet tabSheet, ShopPage page) {
		if (page.getType() == TYPE.SHOP_ARTICLE_PAGE) {
			tabSheet.addTab(getSingleArticlePageComponent(page), page.getLabel(), Icon.SETTINGS.getResource());
		} else {
			ShopRenderer renderer = null;
			try {
				renderer = (ShopRenderer) Classes.newInstance(page.getRenderer());
			} catch (Exception e) {
				renderer = new TabRenderer();
			}
			tabSheet.addTab(renderer.render(page, getInventory()), page.getLabel(), Icon.SETTINGS.getResource());
		}

	}
	
	private void addArticleTab(TabSheet tabSheet, CatalogArticle article) {
		tabSheet.addTab(getArticleComponent(article), article.getDisplayName(), Icon.SETTINGS.getResource());
		
	}


	// TODO: use renderer
	private Component getSingleArticlePageComponent(ShopPage page) {
		if (page.hasArticles()) {
			return getArticleComponent(page.getArticleList().get(0));
		}
		return null;
	}

	protected Component getArticleComponent(SpecifiedArticle specifiedArticle) {
		CatalogArticle article = specifiedArticle.getCatalogArticle();
		AbsoluteLayout layout = new AbsoluteLayout();
		layout.setData(specifiedArticle);
		layout.setHeight("420px");
		CheckBox checkBox = new CheckBox(I18n.getText("nicki.rights.checkbox.label"));
		checkBox.setImmediate(true);
		if (article.hasDescription()) {
			checkBox.setIcon(Icon.HELP.getResource());
			checkBox.setDescription(article.getDescription());
		}
		layout.addComponent(checkBox, "top:20.0px;left:20.0px;right:20.0px;");

		checkBox.addListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				AbsoluteLayout parent = (AbsoluteLayout) event.getButton().getParent();

				SpecifiedArticle specifiedArticle = (SpecifiedArticle) parent.getData();
				  boolean enabled = event.getButton().booleanValue();
				  if (enabled) {
					  getInventory().addArticle(specifiedArticle);
					  boolean provisioned = false;
					  parent.addComponent(getVerticalArticleAttributes(specifiedArticle, provisioned, SOURCE.NONE),
							  "top:60.0px;left:20.0px;right:20.0px;");
				  } else {
					  getInventory().removeArticle(specifiedArticle);
					  removeExcept(parent, event.getButton());
				  }
			}
		});

		if (getInventory().hasArticle(article)) {
			InventoryArticle inventoryArticle = getInventory().getInventoryArticle(specifiedArticle);
			checkBox.setValue(true);
			checkBox.setEnabled(false);
			// TODO
			boolean provisioned = true;
			layout.addComponent(getVerticalArticleAttributes(specifiedArticle, provisioned, inventoryArticle.getSource()),
					"top:60.0px;left:20.0px;right:20.0px;");
		}
		return layout;
	}
	
	/* TODO: kann das entfernt werden?

	@Override
	protected AbstractOrderedLayout getHorizontalArticleAttributes(
			CatalogArticle article, boolean enabled) {
		Map<String, InventoryArticle> iArticles = getInventory().getArticles(article);
		for (String specifier : iArticles.keySet()) {
			InventoryArticle inventoryArticle = iArticles.get(specifier);
			Date start = new Date();
			Date end = null;
			if (inventoryArticle != null && inventoryArticle.getStatus() != STATUS.NEW) {
				start = inventoryArticle.getStart();
				end = inventoryArticle.getEnd();
			}
			AbstractOrderedLayout layout = super.getHorizontalArticleAttributes(article, enabled);
			layout.addComponentAsFirst(getAttributeComponent(article, CatalogArticle.getFixedAttribute("dateTo"), enabled, end));
			layout.addComponentAsFirst(getAttributeComponent(article, CatalogArticle.getFixedAttribute("dateFrom"), enabled, start));
			
		}

		return layout;
	}
	*/

	@Override
	protected AbstractOrderedLayout getVerticalArticleAttributes(
			SpecifiedArticle specifiedArticle, boolean provisioned, SOURCE source) {
		CatalogArticle article = specifiedArticle.getCatalogArticle();
		AbstractOrderedLayout layout =  super.getVerticalArticleAttributes(specifiedArticle, provisioned, source);
		InventoryArticle inventoryArticle = getInventory().getFirstInventoryArticle(article);
		Date start = new Date();
		Date end = null;
		if (inventoryArticle != null && inventoryArticle.getStatus() != STATUS.NEW) {
			start = inventoryArticle.getStart();
			end = inventoryArticle.getEnd();
		}
		boolean enabled = true;
		if (source == SOURCE.RULE) {
			enabled = false;
		}

		layout.addComponentAsFirst(getAttributeComponent(specifiedArticle, CatalogArticle.getFixedAttribute("dateTo"), enabled, end));
		layout.addComponentAsFirst(getAttributeComponent(specifiedArticle, CatalogArticle.getFixedAttribute("dateFrom"), !provisioned, start));

		return layout;
	}

}
