package org.mgnl.nicki.vaadin.base.shop;

import org.mgnl.nicki.dynamic.objects.objects.CatalogArticleAttribute;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

public class LabelComponent implements AttributeComponent {

	@Override
	public Component getInstance(CatalogArticleAttribute attribute) {
		Label label = new Label(attribute.getLabel());
		return label;
	}

}
