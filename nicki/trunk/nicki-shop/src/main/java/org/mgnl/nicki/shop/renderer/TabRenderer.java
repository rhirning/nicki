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
package org.mgnl.nicki.shop.renderer;

import java.util.ArrayList;
import java.util.List;

import org.mgnl.nicki.shop.base.objects.CatalogArticle;
import org.mgnl.nicki.shop.core.ShopArticle;
import org.mgnl.nicki.shop.core.ShopPage;
import org.mgnl.nicki.shop.core.ShopViewerComponent;
import org.mgnl.nicki.shop.base.inventory.Inventory;
import org.mgnl.nicki.vaadin.base.editor.Icon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.Tab;

@SuppressWarnings("serial")
public class TabRenderer extends BaseShopRenderer implements ShopRenderer {
	private static final Logger LOG = LoggerFactory.getLogger(TabRenderer.class);

	private ShopViewerComponent shopViewerComponent;
	private TabSheet tabSheet;
	private List<ShopRenderer> pageRenderers = new ArrayList<ShopRenderer>();

	public AbstractComponent render(ShopViewerComponent shopViewerComponent, Inventory inventory) {
		this.shopViewerComponent = shopViewerComponent;
		setInventory(inventory);
		tabSheet = new TabSheet();
		tabSheet.setHeight("100%");
		render();
		return tabSheet;
	}
	
	public void render() {
		setInit(true);
		tabSheet.removeAllComponents();
		addTabs(tabSheet);
		setInit(false);
	}
	
	private void addTabs(TabSheet tabSheet) {
		for (CatalogArticle article : shopViewerComponent.getArticles()) {
			addArticleTab(tabSheet, article);
		}
		for (ShopPage page : shopViewerComponent.getPageList()) {
			addPageTab(tabSheet, page);
		}
	}
	
	private void addArticleTab(TabSheet tabSheet, CatalogArticle article) {
		ShopRenderer renderer = new TableRenderer();
		pageRenderers.add(renderer);
		renderer.setParentRenderer(this);
		ShopArticle shopArticle = new ShopArticle(article);
		Tab tab = tabSheet.addTab(renderer.render(shopArticle, getInventory()), article.getDisplayName(), Icon.SETTINGS.getResource());
		((AbstractComponent)tab.getComponent()).setData(renderer);
		tab.getComponent().setHeight("100%");
		tabSheet.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
			
			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				TabSheet source = (TabSheet) event.getSource();
				AbstractComponent component = (AbstractComponent) source.getSelectedTab();
				LOG.debug(component.getClass().getName());
				ShopRenderer renderer = (ShopRenderer) component.getData();
				if (renderer != null) {
					renderer.render();
				}
			}
		});
		
	}

	
	private void addPageTab(TabSheet tabSheet, ShopPage page) {
		ShopRenderer renderer = page.getRenderer();
		
		Tab tab = tabSheet.addTab(renderer.render(page, getInventory()), page.getLabel());
		((AbstractComponent)tab.getComponent()).setData(renderer);
		((AbstractComponent)tab.getComponent()).setHeight("100%");
		tabSheet.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
			
			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				TabSheet source = (TabSheet) event.getSource();
				AbstractComponent tab = (AbstractComponent) source.getSelectedTab();
				ShopRenderer renderer = (ShopRenderer) tab.getData();
				if (renderer != null) {
					renderer.render();
				}
			}
		});
	}
}
