package org.mgnl.nicki.vaadin.base.shop.attributes;


import org.mgnl.nicki.shop.catalog.CatalogArticleAttribute;
import org.mgnl.nicki.vaadin.base.shop.inventory.InventoryArticle;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;

public class CheckboxComponent extends BasicAttributeComponent implements AttributeComponent {

	public CheckboxComponent() {
		CheckBox field = new CheckBox();
		field.setWidth("200px");
		field.setImmediate(true);
		setField(field);
	}
	@Override
	public Component getInstance(InventoryArticle article, CatalogArticleAttribute attribute) {
		setArticle(article);
		setAttribute(attribute);
		setCaption(attribute.getLabel());
		if (isEnabled()) {
			getField().addListener(new CatalogAttributeInputListener(getArticle(), getAttribute()));
		}
		return getField();
	}
	@Override
	public String getStringValue(Object value) {
		return ((Boolean) value)?"1":"0";
	}


}
