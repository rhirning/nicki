package org.mgnl.nicki.vaadin.base.fields;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;

public class SimpleField<T> implements NickiField<T> {

	private Field<T> field;
	public SimpleField(Field<T> field) {
		this.field = field;
	}

	@Override
	public void setValue(T value) {
		field.setValue(value);
	}

	@Override
	public T getValue() {
		return field.getValue();
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
