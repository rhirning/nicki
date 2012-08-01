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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.shop.CatalogArticle;
import org.mgnl.nicki.dynamic.objects.shop.CatalogArticleAttribute;
import org.mgnl.nicki.shop.attributes.AttributeComponent;
import org.mgnl.nicki.shop.attributes.AttributeComponentFactory;
import org.mgnl.nicki.shop.attributes.LabelComponent;
import org.mgnl.nicki.shop.core.ShopViewerComponent;
import org.mgnl.nicki.shop.inventory.Inventory;
import org.mgnl.nicki.shop.inventory.Inventory.SOURCE;
import org.mgnl.nicki.shop.inventory.SpecifiedArticle;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class BaseShopRenderer {
	
	private Inventory inventory;
	
	protected Component getAttributeComponent(SpecifiedArticle specifiedArticle, CatalogArticleAttribute articleAttribute, boolean enabled) {
		try {
			AttributeComponent attributeComponent = AttributeComponentFactory.getAttributeComponent(articleAttribute.getType());
			attributeComponent.setEnabled(enabled);
			return attributeComponent.getInstance(inventory.getUser(), inventory.getPerson(),
					inventory.getInventoryArticle(specifiedArticle), articleAttribute);
		} catch (Exception e) {
			return new LabelComponent().getInstance(inventory.getUser(), inventory.getPerson(),
					inventory.getInventoryArticle(specifiedArticle), articleAttribute);
		}
	}

	protected Component getAttributeComponent(SpecifiedArticle specifiedArticle, CatalogArticleAttribute articleAttribute, boolean enabled, Object value) {
		try {
			AttributeComponent attributeComponent = AttributeComponentFactory.getAttributeComponent(articleAttribute.getType());
			attributeComponent.setValue(value);
			inventory.getInventoryArticle(specifiedArticle).setValue(articleAttribute, value);
			attributeComponent.setEnabled(enabled);
			return attributeComponent.getInstance(getInventory().getUser(), getInventory().getPerson(),
					getInventory().getInventoryArticle(specifiedArticle), articleAttribute);
		} catch (Exception e) {
			e.printStackTrace();
			return new LabelComponent().getInstance(getInventory().getUser(), getInventory().getPerson(),
					getInventory().getInventoryArticle(specifiedArticle), articleAttribute);
		}
	}

	protected void removeExcept(Layout parent, Component button) {
		List<Component> toBeRemoved = new ArrayList<Component>();
		for (Iterator<Component> iterator = parent.getComponentIterator(); iterator.hasNext();) {
			Component component = iterator.next();
			if (component != button) {
				toBeRemoved.add(component);
			}
		}
		for (Iterator<Component> iterator = toBeRemoved.iterator(); iterator.hasNext();) {
			Component component = iterator.next();
			parent.removeComponent(component);
		}
	}

	@SuppressWarnings("serial")
	protected Component getArticleComponent(CatalogArticle article) {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing(true);
		layout.setData(article);
		layout.setHeight("40px");
		CheckBox checkBox = new CheckBox(I18n.getText(article.getDisplayName()));
		checkBox.setImmediate(true);
		checkBox.setWidth("200px");
		layout.addComponent(checkBox);
		checkBox.addListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				  boolean enabled = event.getButton().booleanValue();
				  HorizontalLayout parent = (HorizontalLayout) event.getButton().getParent();
				  if (enabled) {
					  showArticleAttributes(parent);
				  } else {
//					  removeExcept(parent, event.getButton());
				  }
			}
		});

		return layout;
	}
	
	protected void showArticleAttributes(HorizontalLayout layout ) {
		SpecifiedArticle specifiedArticle = (SpecifiedArticle) layout.getData();
		CatalogArticle article = specifiedArticle.getCatalogArticle();
		
		if (article.hasAttributes()) {
			for (Iterator<CatalogArticleAttribute> iterator = article.getAllAttributes().iterator(); iterator.hasNext();) {
				CatalogArticleAttribute pageAttribute = iterator.next();
				// TODO
				boolean enabled = true;
				layout.addComponent(getAttributeComponent(specifiedArticle, pageAttribute, enabled));
			}
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

	/* TODO: kann das entfernt werden?
	protected AbstractOrderedLayout getHorizontalArticleAttributes(CatalogArticle article, boolean enabled) {
		AbstractOrderedLayout layout = new HorizontalLayout();
		layout.setSpacing(true);
		layout.setHeight("40px");
		if (article.hasAttributes()) {
			for (Iterator<CatalogArticleAttribute> iterator = article.getAllAttributes().iterator(); iterator.hasNext();) {
				CatalogArticleAttribute attribute = iterator.next();
				layout.addComponent(getAttributeComponent(article, attribute, enabled));
			}
		}
		return layout;
	}
	*/
	protected AbstractOrderedLayout getVerticalArticleAttributes(SpecifiedArticle specifiedArticle, boolean provisioned, SOURCE source) {
		AbstractOrderedLayout attrLayout = new VerticalLayout();
		CatalogArticle article = specifiedArticle.getCatalogArticle();
		if (article.hasAttributes()) {
			for (Iterator<CatalogArticleAttribute> iterator = article.getAllAttributes().iterator(); iterator.hasNext();) {
				CatalogArticleAttribute pageAttribute = iterator.next();
				boolean enabled = true;
				attrLayout.addComponent(getAttributeComponent(specifiedArticle, pageAttribute, enabled));
			}
		}
		return attrLayout;
	}
	
	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public Inventory getInventory() {
		return inventory;
	}






}
