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
package org.mgnl.nicki.vaadin.base.auth;


import org.mgnl.nicki.ldap.objects.DynamicObjectException;
import org.mgnl.nicki.vaadin.base.application.NickiApplication;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class LoginDialog extends CustomComponent {
	private static int MAX_COUNT = 3;

	@AutoGenerated
	private AbsoluteLayout mainLayout;
	@AutoGenerated
	private Button buttonLogin;
	@AutoGenerated
	private PasswordField password;
	@AutoGenerated
	private TextField username;
	@AutoGenerated
	private Label labelPassword;
	@AutoGenerated
	private Label labelUsername;
	
	private NickiApplication application;
	private int count = 0;
	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */
	
	public LoginDialog(NickiApplication nickiApplication) {
		this.application = nickiApplication;
		buildMainLayout();
		setCompositionRoot(mainLayout);

		buttonLogin.addListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				count++;
				if (count > MAX_COUNT) {
					application.login("", "");
					try {
						application.start();
					} catch (DynamicObjectException e) {
						e.printStackTrace();
					}
				}
				if (application.login(getUsername(), getPassword())) {
					try {
						application.start();
					} catch (DynamicObjectException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		buttonLogin.setClickShortcut(KeyCode.ENTER);
		username.focus();
	}

	@AutoGenerated
	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		
		// top-level component properties
		setWidth("640px");
		setHeight("200px");
		
		// labelUsername
		labelUsername = new Label();
		labelUsername.setWidth("-1px");
		labelUsername.setHeight("-1px");
		labelUsername.setValue("Username");
		labelUsername.setImmediate(true);
		mainLayout.addComponent(labelUsername, "top:20.0px;left:20.0px;");
		
		// labelPassword
		labelPassword = new Label();
		labelPassword.setWidth("-1px");
		labelPassword.setHeight("-1px");
		labelPassword.setValue("Passwort");
		labelPassword.setImmediate(true);
		mainLayout.addComponent(labelPassword, "top:60.0px;left:20.0px;");
		
		// username
		username = new TextField();
		username.setWidth("500px");
		username.setHeight("24px");
		username.setImmediate(false);
		mainLayout.addComponent(username, "top:20.0px;left:120.0px;");
		
		// password
		password = new PasswordField();
		password.setWidth("500px");
		password.setHeight("24px");
		password.setImmediate(false);
		mainLayout.addComponent(password, "top:60.0px;left:120.0px;");
		
		// buttonLogin
		buttonLogin = new Button();
		buttonLogin.setWidth("-1px");
		buttonLogin.setHeight("-1px");
		buttonLogin.setCaption("Login");
		buttonLogin.setImmediate(false);
		mainLayout.addComponent(buttonLogin, "top:100.0px;left:120.0px;");
		
		return mainLayout;
	}

	public String getPassword() {
		return (String) password.getValue();
	}

	public String getUsername() {
		return (String) username.getValue();
	}

}
