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
