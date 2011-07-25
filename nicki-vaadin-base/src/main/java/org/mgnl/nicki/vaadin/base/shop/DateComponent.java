package org.mgnl.nicki.vaadin.base.shop;

import org.mgnl.nicki.dynamic.objects.objects.CatalogArticleAttribute;
import org.mgnl.nicki.vaadin.base.data.DateHelper;

import com.vaadin.ui.Component;
import com.vaadin.ui.PopupDateField;


public class DateComponent implements AttributeComponent {
	@Override
	public Component getInstance(CatalogArticleAttribute attribute) {
		PopupDateField field = new PopupDateField(attribute.getLabel());
		DateHelper.init(field);
		return field;
	}

}
