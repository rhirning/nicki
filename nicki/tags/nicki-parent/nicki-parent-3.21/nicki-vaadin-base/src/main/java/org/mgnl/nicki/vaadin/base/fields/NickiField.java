package org.mgnl.nicki.vaadin.base.fields;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Component;

public interface NickiField<F> {
	
	void setValue(F value);
	F getValue();

	void setCaption(String caption);

	Component getComponent();

	void addValueChangeListener(ValueChangeListener listener);
	void setEnabled(boolean enabled);
	void focus();
	void setReadOnly(boolean readOnly);

}
