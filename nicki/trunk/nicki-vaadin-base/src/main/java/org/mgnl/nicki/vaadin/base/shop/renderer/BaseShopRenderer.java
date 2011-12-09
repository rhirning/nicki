/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.shop.renderer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.shop.catalog.CatalogArticle;
import org.mgnl.nicki.shop.catalog.CatalogArticleAttribute;
import org.mgnl.nicki.vaadin.base.shop.attributes.AttributeComponent;
import org.mgnl.nicki.vaadin.base.shop.attributes.AttributeComponentFactory;
import org.mgnl.nicki.vaadin.base.shop.inventory.Inventory;
import org.mgnl.nicki.vaadin.base.shop.attributes.LabelComponent;
import org.mgnl.nicki.vaadin.base.shop.core.ShopViewerComponent;

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
	
	protected Component getAttributeComponent(CatalogArticle article, CatalogArticleAttribute articleAttribute, boolean enabled) {
		try {
			AttributeComponent attributeComponent = AttributeComponentFactory.getAttributeComponent(articleAttribute.getType());
			attributeComponent.setEnabled(enabled);
			return attributeComponent.getInstance(getInventory().getUser(), getInventory().getPerson(),
					getInventory().getArticle(article), articleAttribute);
		} catch (Exception e) {
			return new LabelComponent().getInstance(getInventory().getUser(), getInventory().getPerson(),
					getInventory().getArticle(article), articleAttribute);
		}
	}

	protected Component getAttributeComponent(CatalogArticle article, CatalogArticleAttribute articleAttribute, boolean enabled, Object value) {
		try {
			AttributeComponent attributeComponent = AttributeComponentFactory.getAttributeComponent(articleAttribute.getType());
			attributeComponent.setValue(value);
			inventory.getArticle(article).setValue(articleAttribute, value);
			attributeComponent.setEnabled(enabled);
			return attributeComponent.getInstance(getInventory().getUser(), getInventory().getPerson(),
					getInventory().getArticle(article), articleAttribute);
		} catch (Exception e) {
			e.printStackTrace();
			return new LabelComponent().getInstance(getInventory().getUser(), getInventory().getPerson(),
					getInventory().getArticle(article), articleAttribute);
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
		CatalogArticle article = (CatalogArticle) layout.getData(); 
		
		if (article.hasAttributes()) {
			for (Iterator<CatalogArticleAttribute> iterator = article.getAllAttributes().iterator(); iterator.hasNext();) {
				CatalogArticleAttribute pageAttribute = iterator.next();
				// TODO
				boolean enabled = true;
				layout.addComponent(getAttributeComponent(article, pageAttribute, enabled));
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

	protected AbstractOrderedLayout getVerticalArticleAttributes(CatalogArticle article, boolean provisioned) {
		AbstractOrderedLayout attrLayout = new VerticalLayout();
		
		if (article.hasAttributes()) {
			for (Iterator<CatalogArticleAttribute> iterator = article.getAllAttributes().iterator(); iterator.hasNext();) {
				CatalogArticleAttribute pageAttribute = iterator.next();
				boolean enabled = true;
				attrLayout.addComponent(getAttributeComponent(article, pageAttribute, enabled));
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
