package org.mgnl.nicki.vaadin.base.shop;

import org.mgnl.nicki.dynamic.objects.objects.CatalogArticleAttribute;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

public class LabelComponent implements AttributeComponent {

	Label label;

	public LabelComponent() {
		label = new Label();
	}
	@Override
	public Component getInstance(CatalogArticleAttribute attribute) {
		setCaption(attribute.getLabel());
		return label;
	}

	@Override
	public void setValue(Object value) {
	}

	@Override
	public Object getvalue() {
		return null;
	}
	@Override
	public void setCaption(String caption) {
		label.setCaption(caption);
	}

}
