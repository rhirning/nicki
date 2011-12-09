/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.shop.attributes;

import java.util.Iterator;
import java.util.List;

import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.shop.catalog.CatalogArticleAttribute;
import org.mgnl.nicki.vaadin.base.shop.inventory.InventoryArticle;

import com.vaadin.ui.AbstractSelect.NewItemHandler;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;

@SuppressWarnings("serial")
public class FreeSelectComponent extends BasicAttributeComponent implements AttributeComponent, NewItemHandler {

	ComboBox field;
	public FreeSelectComponent() {
		field = new ComboBox();
		field.setImmediate(true);
		setField(field);
	}
	public Component getInstance(Person user, Person person, InventoryArticle article, CatalogArticleAttribute attribute) {
		setArticle(article);
		setAttribute(attribute);
		setCaption(attribute.getLabel());
		@SuppressWarnings("unchecked")
		List<String> content = getContent(List.class, user, person);
		if (content != null) {
			for (Iterator<String> iterator = content.iterator(); iterator.hasNext();) {
				field.addItem(iterator.next());
			}
		}
		if (isEnabled()) {
			getField().addListener(new CatalogAttributeInputListener(getArticle(), getAttribute()));
		}
		
		field.setNewItemsAllowed(true);
		field.setNewItemHandler(this);
		
		return getField();
	}
	public void addNewItem(String newItemCaption) {
        if (!field.containsId(newItemCaption)) {
            field.addItem(newItemCaption);
            field.setValue(newItemCaption);
        }
	}
	
}
