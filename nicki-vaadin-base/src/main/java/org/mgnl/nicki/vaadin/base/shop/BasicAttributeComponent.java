package org.mgnl.nicki.vaadin.base.shop;

import org.mgnl.nicki.dynamic.objects.objects.CatalogArticleAttribute;

import com.vaadin.ui.Component;
import com.vaadin.ui.Field;

public abstract class BasicAttributeComponent implements AttributeComponent {
	private Field field;

	
	@Override
	public void setValue(Object value) {
		field.setValue(value);
	}

	@Override
	public Object getvalue() {
		return field.getValue();
	}

	@Override
	public abstract Component getInstance(CatalogArticleAttribute pageAttribute);

	public void setField(Field field) {
		this.field = field;
	}

	public Field getField() {
		return field;
	}
	
	@Override
	public void setCaption(String caption) {
		field.setCaption(caption);
	}

}
