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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.Tab;

@SuppressWarnings("serial")
public class AccordionRenderer extends BaseShopRenderer implements ShopRenderer {
	private static final Logger LOG = LoggerFactory.getLogger(AccordionRenderer.class);

	private ShopViewerComponent shopViewerComponent;
	private Accordion accordion;
	private List<ShopRenderer> pageRenderers = new ArrayList<ShopRenderer>();

	public AbstractComponent render(ShopViewerComponent shopViewerComponent, Inventory inventory) {
		this.shopViewerComponent = shopViewerComponent;
		setInventory(inventory);
		accordion = new Accordion();
		accordion.setHeight("100%");
		render();
		return accordion;
	}
	
	public void render() {
		setInit(true);
		accordion.removeAllComponents();
		addPanels();
		setInit(false);
	}
	
	private void addPanels() {
		for (ShopPage page : shopViewerComponent.getPageList()) {
			ShopRenderer renderer = page.getRenderer();
			pageRenderers.add(renderer);
			renderer.setParentRenderer(this);
			Component component =  renderer.render(page, getInventory());
			Tab tab = accordion.addTab(component, page.getLabel());
			((AbstractComponent)tab.getComponent()).setData(renderer);
			accordion.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
				
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
	}
}
