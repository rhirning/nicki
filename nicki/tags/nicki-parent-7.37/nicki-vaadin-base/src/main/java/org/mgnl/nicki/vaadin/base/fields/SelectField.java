package org.mgnl.nicki.vaadin.base.fields;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Component;

public class SelectField implements NickiField<String> {

	private AbstractSelect field;
	
	public SelectField(AbstractSelect field) {
		this.field = field;
	}

	@Override
	public void setValue(String value) {
		field.setValue(value);
	}

	@Override
	public String getValue() {
		return (String) field.getValue();
	}

	@Override
	public void setCaption(String caption) {
		field.setCaption(caption);
	}

	@Override
	public Component getComponent() {
		return field;
	}

	@Override
	public void addValueChangeListener(ValueChangeListener listener) {
		field.addValueChangeListener(listener);
	}

	@Override
	public void setEnabled(boolean enabled) {
		field.setEnabled(enabled);
	}

	@Override
	public void focus() {
		this.field.focus();
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		field.setReadOnly(readOnly);
	}

}
