package org.mgnl.nicki.vaadin.base.shop;

import org.mgnl.nicki.dynamic.objects.objects.CatalogArticleAttribute;
import org.mgnl.nicki.vaadin.base.data.DateHelper;

import com.vaadin.ui.Component;
import com.vaadin.ui.PopupDateField;


public class DateComponent extends BasicAttributeComponent implements AttributeComponent {

	@Override
	public Component getInstance(CatalogArticleAttribute attribute) {
		setCaption(attribute.getLabel());
		return getField();
	}

	public DateComponent() {
		PopupDateField field = new PopupDateField();
		DateHelper.init(field);
		setField(field);		
	}

}
