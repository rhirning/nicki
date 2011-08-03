package org.mgnl.nicki.vaadin.base.shop.attributes;

import java.util.Date;

import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.shop.catalog.CatalogArticleAttribute;
import org.mgnl.nicki.vaadin.base.data.DateHelper;
import org.mgnl.nicki.vaadin.base.shop.inventory.InventoryArticle;

import com.vaadin.ui.Component;
import com.vaadin.ui.PopupDateField;


public class DateComponent extends BasicAttributeComponent implements AttributeComponent {

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

	public DateComponent() {
		PopupDateField field = new PopupDateField();
		field.setImmediate(true);
		DateHelper.init(field);
		setField(field);

	}

	@Override
	public String getStringValue(Object value) {
		try {
			Date newValue =(Date) value;
			return DataHelper.getDay(newValue);
		} catch (Exception e) {
			return "";
		}
	}

}
