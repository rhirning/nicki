
package org.mgnl.nicki.vaadin.base.auth;

/*-
 * #%L
 * nicki-vaadin-base
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */



import com.vaadin.annotations.AutoGenerated;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class NickiLoginDialog extends CustomComponent {
	@AutoGenerated
	private VerticalLayout mainLayout;
	@AutoGenerated
	private HorizontalLayout horizontalLayout_3;
	@AutoGenerated
	private Button buttonLogin;
	@AutoGenerated
	private Label label_1;
	@AutoGenerated
	private HorizontalLayout horizontalLayout_2;
	@AutoGenerated
	private PasswordField password;
	@AutoGenerated
	private Label labelPassword;
	@AutoGenerated
	private HorizontalLayout horizontalLayout_1;
	@AutoGenerated
	private TextField username;
	@AutoGenerated
	private Label labelUsername;

	private NickiCallbackHandler callbackHandler;

	public NickiLoginDialog(NickiCallbackHandler callbackHandler) {
		this.callbackHandler = callbackHandler;
		buildMainLayout();
		setCompositionRoot(mainLayout);

		buttonLogin.addClickListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				getCallbackHandler().setCredentials(getUsername(), getPassword());

			}
		});
		
		buttonLogin.setClickShortcut(KeyCode.ENTER);
		username.focus();
	}

	public String getPassword() {
		return (String) password.getValue();
	}

	public String getUsername() {
		return (String) username.getValue();
	}

	@AutoGenerated
	private VerticalLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("-1px");
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("-1px");
		
		// horizontalLayout_1
		horizontalLayout_1 = buildHorizontalLayout_1();
		mainLayout.addComponent(horizontalLayout_1);
		
		// horizontalLayout_2
		horizontalLayout_2 = buildHorizontalLayout_2();
		mainLayout.addComponent(horizontalLayout_2);
		
		// horizontalLayout_3
		horizontalLayout_3 = buildHorizontalLayout_3();
		mainLayout.addComponent(horizontalLayout_3);
		
		return mainLayout;
	}

	@AutoGenerated
	private HorizontalLayout buildHorizontalLayout_1() {
		// common part: create layout
		horizontalLayout_1 = new HorizontalLayout();
		horizontalLayout_1.setImmediate(false);
		horizontalLayout_1.setWidth("100.0%");
		horizontalLayout_1.setHeight("-1px");
		horizontalLayout_1.setMargin(false);
		horizontalLayout_1.setSpacing(true);
		
		// labelUsername
		labelUsername = new Label();
		labelUsername.setImmediate(true);
		labelUsername.setWidth("200px");
		labelUsername.setHeight("-1px");
		labelUsername.setValue("Username");
		horizontalLayout_1.addComponent(labelUsername);
		
		// username
		username = new TextField();
		username.setImmediate(false);
		username.setWidth("100.0%");
		username.setHeight("24px");
		horizontalLayout_1.addComponent(username);
		horizontalLayout_1.setExpandRatio(username, 1.0f);
		
		return horizontalLayout_1;
	}

	@AutoGenerated
	private HorizontalLayout buildHorizontalLayout_2() {
		// common part: create layout
		horizontalLayout_2 = new HorizontalLayout();
		horizontalLayout_2.setImmediate(false);
		horizontalLayout_2.setWidth("100.0%");
		horizontalLayout_2.setHeight("-1px");
		horizontalLayout_2.setMargin(false);
		horizontalLayout_2.setSpacing(true);
		
		// labelPassword
		labelPassword = new Label();
		labelPassword.setImmediate(true);
		labelPassword.setWidth("200px");
		labelPassword.setHeight("-1px");
		labelPassword.setValue("Passwort");
		horizontalLayout_2.addComponent(labelPassword);
		
		// password
		password = new PasswordField();
		password.setImmediate(false);
		password.setWidth("100.0%");
		password.setHeight("-1px");
		horizontalLayout_2.addComponent(password);
		horizontalLayout_2.setExpandRatio(password, 1.0f);
		
		return horizontalLayout_2;
	}

	@AutoGenerated
	private HorizontalLayout buildHorizontalLayout_3() {
		// common part: create layout
		horizontalLayout_3 = new HorizontalLayout();
		horizontalLayout_3.setImmediate(false);
		horizontalLayout_3.setWidth("-1px");
		horizontalLayout_3.setHeight("-1px");
		horizontalLayout_3.setMargin(false);
		horizontalLayout_3.setSpacing(true);
		
		// label_1
		label_1 = new Label();
		label_1.setImmediate(false);
		label_1.setWidth("200px");
		label_1.setHeight("-1px");
		horizontalLayout_3.addComponent(label_1);
		
		// buttonLogin
		buttonLogin = new Button();
		buttonLogin.setCaption("Login");
		buttonLogin.setImmediate(true);
		buttonLogin.setWidth("-1px");
		buttonLogin.setHeight("-1px");
		horizontalLayout_3.addComponent(buttonLogin);
		
		return horizontalLayout_3;
	}

	public NickiCallbackHandler getCallbackHandler() {
		return callbackHandler;
	}

	public void setCallbackHandler(NickiCallbackHandler callbackHandler) {
		this.callbackHandler = callbackHandler;
	}

}
