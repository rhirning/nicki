package org.mgnl.nicki.vaadin.base.shop.attributes;

import org.mgnl.nicki.shop.catalog.CatalogArticleAttribute;
import org.mgnl.nicki.vaadin.base.shop.inventory.InventoryArticle;

import com.vaadin.ui.Component;

public interface AttributeComponent {

	Component getInstance(InventoryArticle article, CatalogArticleAttribute attribute);
	void setValue(Object value);
	Object getvalue();
	void setCaption(String caption);
	void setEnabled(boolean enabled);
	boolean isEnabled();
	String getStringValue(Object value);
}
