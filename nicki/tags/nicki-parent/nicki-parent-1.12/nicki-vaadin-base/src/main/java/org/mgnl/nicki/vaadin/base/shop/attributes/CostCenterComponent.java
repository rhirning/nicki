package org.mgnl.nicki.vaadin.base.shop.attributes;

import org.mgnl.nicki.shop.catalog.CatalogArticleAttribute;
import org.mgnl.nicki.vaadin.base.shop.inventory.InventoryArticle;

import com.vaadin.ui.Component;
import com.vaadin.ui.NativeSelect;

public class CostCenterComponent extends BasicAttributeComponent implements AttributeComponent {

	public CostCenterComponent() {
		NativeSelect field = new NativeSelect();
		field.addItem("12345");
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
	
}
