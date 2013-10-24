package org.mgnl.nicki.vaadin.base.search;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.core.objects.DynamicAttribute;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;

public class TextFieldSearchField<T> implements DynamicAttributeSearchField<T> {
	private TextField textField = new TextField();
	private DynamicAttribute dynAttribute;
	private Map<DynamicAttribute, String> map;

	@Override
	public Component getComponent() {
		return textField;
	}

	@Override
	public void init(DynamicAttribute dynamicAttribute,
			Map<DynamicAttribute, String> searchMap) {
		this.dynAttribute = dynamicAttribute;
		this.map = searchMap;
		textField.setCaption(I18n.getText(dynAttribute.getCaption(), dynAttribute.getName()));
		textField.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 5368806006111504521L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				String value = (String) event.getProperty().getValue();
				if (StringUtils.isNotBlank(value)) {
					map.put(dynAttribute, value);
				} else if (map.containsKey(dynAttribute)) {
					map.remove(dynAttribute);
				}
			}
		});
	}

	@Override
	public void setWidth(String width) {
		textField.setWidth(width);
	}

}
