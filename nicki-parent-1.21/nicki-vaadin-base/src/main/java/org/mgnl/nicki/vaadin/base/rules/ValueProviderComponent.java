package org.mgnl.nicki.vaadin.base.rules;

import org.mgnl.nicki.shop.rules.ValueProvider;

import com.vaadin.ui.Component;

public interface ValueProviderComponent extends ValueProvider {

	Component getValueList();

	String getValue();
	
}
