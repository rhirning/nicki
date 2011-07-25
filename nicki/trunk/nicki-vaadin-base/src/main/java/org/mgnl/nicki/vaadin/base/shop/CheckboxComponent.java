package org.mgnl.nicki.vaadin.base.shop;

import org.mgnl.nicki.dynamic.objects.objects.CatalogArticleAttribute;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;

public class CheckboxComponent implements AttributeComponent {

	@Override
	public Component getInstance(CatalogArticleAttribute attribute) {
		CheckBox field = new CheckBox(attribute.getLabel());
		field.setWidth("200px");
		return field;
	}

}
