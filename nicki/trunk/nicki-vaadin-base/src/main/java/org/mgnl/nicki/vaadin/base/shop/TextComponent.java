package org.mgnl.nicki.vaadin.base.shop;

import org.mgnl.nicki.dynamic.objects.objects.CatalogArticleAttribute;

import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;

public class TextComponent extends BasicAttributeComponent implements AttributeComponent {

	public TextComponent() {
		TextField field = new TextField();
		field.setWidth("200px");
		setField(field);
	}
	@Override
	public Component getInstance(CatalogArticleAttribute attribute) {
		setCaption(attribute.getLabel());
		return getField();
	}

}
