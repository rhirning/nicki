package org.mgnl.nicki.vaadin.base.search;

import java.util.Map;

import org.mgnl.nicki.core.objects.DynamicAttribute;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;

public class ComboBoxSearchField<T> implements DynamicAttributeSearchField<T> {
	private ComboBox comboBox = new ComboBox();

	@Override
	public Component getComponent() {
		return comboBox;
	}

	@Override
	public void init(DynamicAttribute dynAttribute,
			Map<DynamicAttribute, String> map) {
		comboBox.setCaption(dynAttribute.getName());
	}

	@Override
	public void setWidth(String width) {
		comboBox.setWidth(width);
	}


}
