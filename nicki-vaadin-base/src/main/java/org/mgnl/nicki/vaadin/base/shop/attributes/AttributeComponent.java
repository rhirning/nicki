package org.mgnl.nicki.vaadin.base.shop.attributes;

import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.shop.catalog.CatalogArticleAttribute;
import org.mgnl.nicki.vaadin.base.shop.inventory.InventoryArticle;

import com.vaadin.ui.Component;

public interface AttributeComponent {

	void setValue(Object value);
	Object getvalue();
	void setCaption(String caption);
	void setEnabled(boolean enabled);
	boolean isEnabled();
	String getStringValue(Object value);
	Component getInstance(Person user, Person person, InventoryArticle article,
			CatalogArticleAttribute attribute);
}
