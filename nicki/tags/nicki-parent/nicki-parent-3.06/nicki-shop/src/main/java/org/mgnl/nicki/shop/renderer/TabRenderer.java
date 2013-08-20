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

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.util.Classes;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;
import org.mgnl.nicki.shop.core.ShopArticle;
import org.mgnl.nicki.shop.core.ShopPage;
import org.mgnl.nicki.shop.core.ShopPage.TYPE;
import org.mgnl.nicki.shop.core.ShopViewerComponent;
import org.mgnl.nicki.shop.base.inventory.Inventory;
import org.mgnl.nicki.vaadin.base.editor.Icon;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.Tab;

@SuppressWarnings("serial")
public class TabRenderer extends BaseShopRenderer implements ShopRenderer {

	private ShopViewerComponent shopViewerComponent;
	private TabSheet tabSheet;

	public AbstractComponent render(ShopViewerComponent shopViewerComponent, Inventory inventory) {
		this.shopViewerComponent = shopViewerComponent;
		setInventory(inventory);
		tabSheet = new TabSheet();
		tabSheet.setHeight("100%");
		render();
		return tabSheet;
	}
	
	public void render() {
		tabSheet.removeAllComponents();
		addTabs(tabSheet);
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
		ShopArticle shopArticle = new ShopArticle(article);
		Tab tab = tabSheet.addTab(renderer.render(shopArticle, getInventory()), article.getDisplayName(), Icon.SETTINGS.getResource());
		((AbstractComponent)tab.getComponent()).setData(renderer);
		tabSheet.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
			
			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				TabSheet source = (TabSheet) event.getSource();
				AbstractComponent tab = (AbstractComponent) source.getSelectedTab();
				ShopRenderer renderer = (ShopRenderer) tab.getData();
				renderer.render();
			}
		});
		
	}

	
	private void addPageTab(TabSheet tabSheet, ShopPage page) {
		ShopRenderer renderer = null;
		if (page.getType() == TYPE.SHOP_ARTICLE_PAGE && StringUtils.isEmpty(page.getRenderer())) {
			renderer = new TableRenderer();
		} else {
			try {
				renderer = (ShopRenderer) Classes.newInstance(page.getRenderer());
			} catch (Exception e) {
				renderer = new TabRenderer();
			}
		}
		
		
		Tab tab = tabSheet.addTab(renderer.render(page, getInventory()), page.getLabel(), Icon.SETTINGS.getResource());
		((AbstractComponent)tab.getComponent()).setData(renderer);
		tabSheet.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
			
			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				TabSheet source = (TabSheet) event.getSource();
				AbstractComponent tab = (AbstractComponent) source.getSelectedTab();
				ShopRenderer renderer = (ShopRenderer) tab.getData();
				renderer.render();
			}
		});
		

	}
}
