package org.mgnl.nicki.vaadin.base.shop;

import org.mgnl.nicki.dynamic.objects.objects.CatalogArticleAttribute;

import com.vaadin.ui.Component;
import com.vaadin.ui.NativeSelect;

public class CostCenterComponent extends BasicAttributeComponent implements AttributeComponent {

	public CostCenterComponent() {
		NativeSelect field = new NativeSelect();
		field.addItem("12345");
		setField(field);
	}
	@Override
	public Component getInstance(CatalogArticleAttribute attribute) {
		setCaption(attribute.getLabel());
		return getField();
	}
	
}
