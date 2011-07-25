package org.mgnl.nicki.vaadin.base.shop;

import org.mgnl.nicki.dynamic.objects.objects.CatalogArticleAttribute;

import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;

public class TextComponent implements AttributeComponent {

	@Override
	public Component getInstance(CatalogArticleAttribute attribute) {
		TextField field = new TextField(attribute.getLabel());
		field.setWidth("200px");
		return field;
	}

}
