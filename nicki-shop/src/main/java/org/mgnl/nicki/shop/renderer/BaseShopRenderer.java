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

import org.mgnl.nicki.shop.core.ShopViewerComponent;
import org.mgnl.nicki.shop.base.inventory.Inventory;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextArea;

@SuppressWarnings("serial")
public abstract class BaseShopRenderer implements ShopRenderer {
	
	private Inventory inventory;
	private ShopRenderer parentRenderer;
	private boolean init;
	

	protected void removeExcept(Layout parent, Component button) {
		List<Component> toBeRemoved = new ArrayList<Component>();
		for (Component component : parent) {
			if (component != button) {
				toBeRemoved.add(component);
			}
		}
		for (Component component : toBeRemoved) {
			parent.removeComponent(component);
		}
	}
	

	public Component getXMLComponent(ShopViewerComponent shopViewerComponent) {
		AbsoluteLayout layout = new AbsoluteLayout();
		layout.setHeight("420px");
		// textArea_1
		TextArea xml = new TextArea();
		xml.setWidth("100%");
		xml.setHeight("100%");
		xml.setImmediate(false);
		xml.setValue(shopViewerComponent.toString());
		layout.addComponent(xml, "top:20.0px;left:20.0px;right:20.0px;");

		return layout;
	}
	
	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public Inventory getInventory() {
		return inventory;
	}


	public ShopRenderer getParentRenderer() {
		return parentRenderer;
	}


	public void setParentRenderer(ShopRenderer parentRenderer) {
		this.parentRenderer = parentRenderer;
	}

	@Override
	public void handleChange() {
		if (!init) {
			render();
			if (getParentRenderer() != null) {
				getParentRenderer().render();
			}
		}
	}


	public boolean isInit() {
		return init;
	}


	public void setInit(boolean init) {
		this.init = init;
	}

}
