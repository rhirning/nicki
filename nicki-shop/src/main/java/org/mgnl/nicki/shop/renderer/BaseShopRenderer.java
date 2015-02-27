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
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.shop.attributes.AttributeComponent;
import org.mgnl.nicki.shop.attributes.AttributeComponentFactory;
import org.mgnl.nicki.shop.attributes.LabelComponent;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;
import org.mgnl.nicki.shop.base.objects.CatalogArticleAttribute;
import org.mgnl.nicki.shop.core.ShopViewerComponent;
import org.mgnl.nicki.shop.inventory.EndInputListener;
import org.mgnl.nicki.shop.base.inventory.Inventory;
import org.mgnl.nicki.shop.base.inventory.InventoryArticle;
import org.mgnl.nicki.shop.inventory.StartInputListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextArea;

public class BaseShopRenderer {
	private static final Logger LOG = LoggerFactory.getLogger(BaseShopRenderer.class);
	
	private Inventory inventory;
	private ShopRenderer parentRenderer;
	
	protected Component getAttributeComponent(CatalogArticle article, InventoryArticle inventoryArticle, CatalogArticleAttribute articleAttribute, boolean enabled) {
		try {
			AttributeComponent<?> attributeComponent = AttributeComponentFactory.getAttributeComponent(articleAttribute.getType());
			attributeComponent.setEnabled(enabled);
			return attributeComponent.getInstance(inventory.getUser(), inventory.getPerson(),
					inventoryArticle, articleAttribute);
		} catch (Exception e) {
			LOG.error("Could not create Instance", e);
			return new LabelComponent().getInstance(inventory.getUser(), inventory.getPerson(),
					inventoryArticle, articleAttribute);
		}
	}

	
	@SuppressWarnings("unchecked")
	protected Component getAttributeComponent(CatalogArticle article, CatalogArticleAttribute articleAttribute, boolean enabled, Object value) {
		try {
			AttributeComponent<?> attributeComponent = AttributeComponentFactory.getAttributeComponent(articleAttribute.getType());
			try {
				((AttributeComponent<Date>)attributeComponent).setValue((Date)value);
			} catch (Exception e) {
				((AttributeComponent<String>)attributeComponent).setValue((String)value);
			}
			inventory.getInventoryArticle(article).setValue(articleAttribute, value);
			attributeComponent.setEnabled(enabled);
			return attributeComponent.getInstance(getInventory().getUser(), getInventory().getPerson(),
					getInventory().getInventoryArticle(article), articleAttribute);
		} catch (Exception e) {
			LOG.error("Error", e);
			return new LabelComponent().getInstance(getInventory().getUser(), getInventory().getPerson(),
					getInventory().getInventoryArticle(article), articleAttribute);
		}
	}
	
	
	protected Component getStartDateComponent(InventoryArticle inventoryArticle, boolean enabled, Date start) {
		try {
			AttributeComponent<Date> attributeComponent = AttributeComponentFactory.getAttributeComponent("DATE");
			attributeComponent.setValue(start);
			inventoryArticle.setStart(start);
			attributeComponent.setEnabled(enabled);
			ValueChangeListener listener = new StartInputListener(inventoryArticle);
			return attributeComponent.getInstance(I18n.getText(CatalogArticle.CAPTION_START), start, listener);
		} catch (Exception e) {
			LOG.error("Error", e);
			// TODO: convert date
			return new LabelComponent().getInstance(I18n.getText(CatalogArticle.CAPTION_START), start.toString(), null);
		}
	}

	protected Component getEndDateComponent(InventoryArticle inventoryArticle, boolean enabled, Date end) {
		try {
			AttributeComponent<Date> attributeComponent = AttributeComponentFactory.getAttributeComponent("DATE");
			attributeComponent.setValue(end);
			inventoryArticle.setEnd(end);
			attributeComponent.setEnabled(enabled);
			ValueChangeListener listener = new EndInputListener(inventoryArticle);
			return attributeComponent.getInstance(I18n.getText(CatalogArticle.CAPTION_END), end, listener);
		} catch (Exception e) {
			LOG.error("Error", e);
			// TODO: convert date
			return new LabelComponent().getInstance(I18n.getText(CatalogArticle.CAPTION_END), end.toString(), null);
		}
	}

	protected void removeExcept(Layout parent, Component button) {
		List<Component> toBeRemoved = new ArrayList<Component>();
		for (Iterator<Component> iterator = parent.iterator(); iterator.hasNext();) {
			Component component = iterator.next();
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






}
