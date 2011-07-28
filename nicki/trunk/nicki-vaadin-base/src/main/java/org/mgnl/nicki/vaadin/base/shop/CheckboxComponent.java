package org.mgnl.nicki.vaadin.base.shop;

import org.mgnl.nicki.dynamic.objects.objects.CatalogArticleAttribute;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;

public class CheckboxComponent extends BasicAttributeComponent implements AttributeComponent {

	public CheckboxComponent() {
		CheckBox field = new CheckBox();
		field.setWidth("200px");
		setField(field);
	}
	@Override
	public Component getInstance(CatalogArticleAttribute attribute) {
		setCaption(attribute.getLabel());
		return getField();
	}


}
