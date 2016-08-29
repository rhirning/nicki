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

import org.mgnl.nicki.shop.core.ShopPage;
import org.mgnl.nicki.shop.core.ShopViewerComponent;
import org.mgnl.nicki.shop.base.inventory.Inventory;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class PanelRenderer extends BaseShopRenderer implements ShopRenderer {

	private ShopViewerComponent shopViewerComponent;
	private VerticalLayout layout;
	private List<ShopRenderer> pageRenderers = new ArrayList<ShopRenderer>();

	public AbstractComponent render(ShopViewerComponent shopViewerComponent, Inventory inventory) {
		this.shopViewerComponent = shopViewerComponent;
		setInventory(inventory);
		layout = new VerticalLayout();
		layout.setHeight("100%");
		render();
		return layout;
	}
	
	@Override
	public void render() {
		setInit(true);
		layout.removeAllComponents();
		addPanels();
		setInit(false);
	}
	
	private void addPanels() {
		for (ShopPage page : shopViewerComponent.getPageList()) {
			ShopRenderer renderer = page.getRenderer();
			pageRenderers.add(renderer);
			renderer.setParentRenderer(this);
			Panel panel = new Panel(page.getLabel());
			Component component = renderer.render(page, getInventory());
			component.setHeight("100%");
			panel.setContent(component);
			layout.addComponent(panel);
		}
	}
}
