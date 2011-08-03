package org.mgnl.nicki.vaadin.base.shop.attributes;

import org.mgnl.nicki.shop.catalog.CatalogArticleAttribute;
import org.mgnl.nicki.vaadin.base.shop.inventory.InventoryArticle;

import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;

public class TextComponent extends BasicAttributeComponent implements AttributeComponent {

	public TextComponent() {
		TextField field = new TextField();
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

}
