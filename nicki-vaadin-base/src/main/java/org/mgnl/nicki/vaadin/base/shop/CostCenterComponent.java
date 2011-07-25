package org.mgnl.nicki.vaadin.base.shop;

import org.mgnl.nicki.dynamic.objects.objects.CatalogArticleAttribute;

import com.vaadin.ui.Component;
import com.vaadin.ui.NativeSelect;

public class CostCenterComponent implements AttributeComponent {

	@Override
	public Component getInstance(CatalogArticleAttribute attribute) {
		NativeSelect select = new NativeSelect(attribute.getLabel());
		select.addItem("12345");
		return select;
	}

}
