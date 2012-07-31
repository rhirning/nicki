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

import org.mgnl.nicki.core.i18n.I18n;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class EnterNameDialog extends CustomComponent {

	@AutoGenerated
	private AbsoluteLayout mainLayout;

	@AutoGenerated
	private HorizontalLayout horizontalLayout_1;

	@AutoGenerated
	private Button closeButton;

	@AutoGenerated
	private Button createButton;

	@AutoGenerated
	private TextField name;

	@AutoGenerated
	private Label headline;
	
	private String i18nBase;

	EnterNameHandler handler;
	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */
	
	public EnterNameDialog(String messageBase) {
		i18nBase = messageBase;
		buildMainLayout();
		setCompositionRoot(mainLayout);

		name.focus();
		applyI18n(messageBase);
		
		createButton.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {
					getHandler().setName((String) name.getValue());
					getWindow().getParent().removeWindow(getWindow());
				} catch (Exception e) {
					getWindow().showNotification(I18n.getText(i18nBase + ".error"),
							e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
				}
			}
		});
		
		closeButton.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				getWindow().getParent().removeWindow(getWindow());
			}
		});
		
		createButton.setClickShortcut(KeyCode.ENTER);
	}

	private void applyI18n(String messageBase) {
		headline.setValue(I18n.getText(messageBase + ".headline"));
		createButton.setCaption(I18n.getText(messageBase + ".button.create"));
		closeButton.setCaption(I18n.getText(messageBase + ".button.close"));
	}
		
	public EnterNameHandler getHandler() {
		return handler;
	}
	
	public void setName(String name) {
		this.name.setValue(name);
	}

	public void setHandler(EnterNameHandler handler) {
		this.handler = handler;
		handler.setDialog(this);
		this.name.setValue(handler.getName());
	}

	@AutoGenerated
	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// headline
		headline = new Label();
		headline.setWidth("400px");
		headline.setHeight("-1px");
		headline.setValue("Headline");
		headline.setImmediate(false);
		mainLayout.addComponent(headline, "top:20.0px;left:20.0px;");
		
		// name
		name = new TextField();
		name.setWidth("200px");
		name.setHeight("-1px");
		name.setImmediate(true);
		mainLayout.addComponent(name, "top:60.0px;left:20.0px;");
		
		// horizontalLayout_1
		horizontalLayout_1 = buildHorizontalLayout_1();
		mainLayout.addComponent(horizontalLayout_1, "top:100.0px;left:20.0px;");
		
		return mainLayout;
	}

	@AutoGenerated
	private HorizontalLayout buildHorizontalLayout_1() {
		// common part: create layout
		horizontalLayout_1 = new HorizontalLayout();
		horizontalLayout_1.setWidth("-1px");
		horizontalLayout_1.setHeight("-1px");
		horizontalLayout_1.setImmediate(false);
		horizontalLayout_1.setMargin(false);
		
		// createButton
		createButton = new Button();
		createButton.setWidth("-1px");
		createButton.setHeight("-1px");
		createButton.setCaption("Create");
		createButton.setImmediate(true);
		horizontalLayout_1.addComponent(createButton);
		
		// closeButton
		closeButton = new Button();
		closeButton.setWidth("-1px");
		closeButton.setHeight("-1px");
		closeButton.setCaption("Close");
		closeButton.setImmediate(true);
		horizontalLayout_1.addComponent(closeButton);
		
		return horizontalLayout_1;
	}
}
