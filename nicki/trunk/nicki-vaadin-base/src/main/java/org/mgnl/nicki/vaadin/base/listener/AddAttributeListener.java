/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.listener;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class AddAttributeListener implements ClickListener {
	private AbstractComponentContainer container;
	ListAttributeListener listener;

	public AddAttributeListener(AbstractComponentContainer container, ListAttributeListener listener) {
		this.container = container;
		this.listener = listener;
	}

	public void buttonClick(ClickEvent event) {
		String value = "";
		TextField input = new TextField(null, value);
		input.setImmediate(true);
		input.addListener(listener);
		container.addComponent(input);
	}

}
