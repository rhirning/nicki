package org.mgnl.nicki.shop.attributes;

import com.vaadin.ui.Component;

public interface VaadinComponent {

	void setValue(String value);

	void addValueChangeListener(
			CatalogAttributeInputListener catalogAttributeInputListener);

	Component getComponent();

	String getValue();

	void setCaption(String caption);

	void setEnabled(boolean enabled);

	boolean isEnabled();

}
