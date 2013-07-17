/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.components;


import java.io.Serializable;
import java.util.Map;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.vaadin.base.components.EnterNameDialog;
import org.mgnl.nicki.vaadin.base.components.EnterNameHandler;
import org.mgnl.nicki.vaadin.base.listener.TestDataValueChangeListener;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class TestDataView extends CustomComponent {
	@AutoGenerated
	private AbsoluteLayout mainLayout;

	@AutoGenerated
	private VerticalLayout verticalLayout_1;

	@AutoGenerated
	private Button newButton;

	@AutoGenerated
	private VerticalLayout testData;

	public static final String SEPARATOR = "=";

	private Property data;
	private ValueChangeListener listener;
	private Window newFieldWindow;
	private String messageKeyBase;
	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */
	public TestDataView(Property dataContainer, String messageKeyBase) {
		this.data = dataContainer;
		this.messageKeyBase = messageKeyBase + ".testdata";
		buildMainLayout();
		setCompositionRoot(mainLayout);
		listener = new TestDataValueChangeListener(dataContainer, testData, SEPARATOR);
		@SuppressWarnings("unchecked")
		Map<String, String> values = (Map<String, String>) data.getValue();
		for (String name : values.keySet()) {
			addField(name, values.get(name));
		}
		newButton.addListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				addNewField();
			}
		});
	}
	
	protected void addField(String name, String value) {
		TextField field = new TextField(name, value);
		field.addListener(listener);
		field.setWidth("100%");
		testData.addComponent(field);

	}

	protected void addNewField() {
		EnterNameDialog dialog = new EnterNameDialog(messageKeyBase);
		dialog.setHandler(new NewFieldHandler());
		newFieldWindow = new Window(I18n.getText(messageKeyBase + ".window.title"),
				dialog);
		newFieldWindow.setPositionX(300);
		newFieldWindow.setPositionY(100);
		newFieldWindow.setWidth(440, Sizeable.UNITS_PIXELS);
		newFieldWindow.setHeight(500, Sizeable.UNITS_PIXELS);
		newFieldWindow.setModal(true);
		this.getParent().getWindow().addWindow(newFieldWindow);
	}
	
	public class NewFieldHandler extends EnterNameHandler implements Serializable {

		public void setName(String name) {
			addField(name, "");
		}

		public void closeEnterNameDialog() {
			getParent().getWindow().removeWindow(newFieldWindow);
		}

	}


	@AutoGenerated
	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// verticalLayout_1
		verticalLayout_1 = buildVerticalLayout_1();
		mainLayout.addComponent(verticalLayout_1, "top:0.0px;left:0.0px;");
		
		return mainLayout;
	}

	@AutoGenerated
	private VerticalLayout buildVerticalLayout_1() {
		// common part: create layout
		verticalLayout_1 = new VerticalLayout();
		verticalLayout_1.setWidth("100.0%");
		verticalLayout_1.setHeight("-1px");
		verticalLayout_1.setImmediate(false);
		verticalLayout_1.setMargin(false);
		
		// testData
		testData = new VerticalLayout();
		testData.setWidth("100.0%");
		testData.setHeight("-1px");
		testData.setImmediate(false);
		testData.setMargin(false);
		verticalLayout_1.addComponent(testData);
		
		// newButton
		newButton = new Button();
		newButton.setWidth("-1px");
		newButton.setHeight("-1px");
		newButton.setCaption("Neu");
		newButton.setImmediate(true);
		verticalLayout_1.addComponent(newButton);
		
		return verticalLayout_1;
	}

}
