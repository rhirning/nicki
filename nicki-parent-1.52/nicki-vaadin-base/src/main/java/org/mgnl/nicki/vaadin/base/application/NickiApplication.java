/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
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
 */
package org.mgnl.nicki.vaadin.base.application;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.ldap.auth.NickiPrincipal;
import org.mgnl.nicki.ldap.auth.SSOAdapter;
import org.mgnl.nicki.ldap.context.AppContext;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.context.Target;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;
import org.mgnl.nicki.vaadin.base.auth.LoginDialog;
import org.mgnl.nicki.vaadin.base.command.Command;
import org.mgnl.nicki.vaadin.base.components.ConfirmDialog;
import org.mgnl.nicki.vaadin.base.components.WelcomeDialog;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public abstract class NickiApplication extends Application  implements HttpServletRequestListener{
	public static final String JAAS_SSO_ENTRY = "NickiSSO";
	public static final String JAAS_ENTRY = "Nicki";
	public static final String ATTR_NICKI_CONTEXT = "NICKI_CONTEXT";

	private NickiContext nickiContext = null;
	private boolean useSystemContext = false;
	private boolean useWelcomeDialog = false;


	@Override
	public void init() {
		setTheme(Config.getProperty("nicki.application.theme", "reindeer"));
		Window mainWindow = new Window(I18n.getText(getI18nBase() + ".main.title"));
//		mainWindow.setHeight(800, Sizeable.UNITS_PIXELS);
		mainWindow.setWidth("100%");
		setMainWindow(mainWindow);

		// try getting context from session
		try {
			this.nickiContext = (NickiContext) getRequest().getSession(false).getAttribute(ATTR_NICKI_CONTEXT);
		} catch (Exception e) {
		}
		
		if (nickiContext == null) {
			// try SSO
			setNickiContext(loginSSO());
		}
		if (nickiContext != null) {
			try {
				start();
				return;
			} catch (DynamicObjectException e) {
				e.printStackTrace();
			}
		}

		Component loginDialog = new LoginDialog(this);
		getMainWindow().addComponent(loginDialog);
	}
	
	public void logout() {
		setNickiContext(null);
		Component loginDialog = new LoginDialog(this);
		getMainWindow().removeAllComponents();
		getMainWindow().addComponent(loginDialog);
	}
	
	public void onRequestStart(HttpServletRequest request,
			HttpServletResponse response) {
		AppContext.setRequest(request);
		AppContext.setResponse(response);
	}

	public void onRequestEnd(HttpServletRequest request,
			HttpServletResponse response) {
		// nothing to do
	}

	/*
	public NickiContext loginSSO() {
		try {
			LoginContext loginContext = new LoginContext(JAAS_SSO_ENTRY, new NickiSSOLoginCallbackHandler(getRequest()));
			loginContext.login();
			NickiPrincipal principal = (NickiPrincipal) loginContext.getSubject().getPrincipals().iterator().next();
			if (principal != null && getTarget().login(principal) != null) {
				return getTarget().getNamedUserContext(principal, READONLY.FALSE);
			}
		} catch (Exception e) {
		}
		return null;
	}
	*/

	public NickiContext loginSSO() {
		try {
			String ssoLoginClass = Config.getProperty("nicki.login.sso");
			if (StringUtils.isNotEmpty(ssoLoginClass)) {
				SSOAdapter adapter = (SSOAdapter) Class.forName(ssoLoginClass).newInstance();
				NickiPrincipal principal = new NickiPrincipal(adapter.getName(getRequest()), new String(adapter.getPassword(getRequest())));
				if (principal != null) {
					DynamicObject user = getTarget().login(principal);
					if (user != null) {
						if (isUseSystemContext()) {
							NickiContext ctx = AppContext.getSystemContext(getTarget(), user.getPath(), principal.getPassword());
							return ctx;
						} else {
							return getTarget().getNamedUserContext(user, principal.getPassword());
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public HttpServletRequest getRequest() {
		return (HttpServletRequest) AppContext.getRequest();
	}

	public HttpServletResponse getResponse() {
		return (HttpServletResponse) AppContext.getResponse();
	}

	public boolean login(String name, String password) {
		try {
			NickiPrincipal principal = new NickiPrincipal(name, password);
			if (principal != null) {
				DynamicObject user = getTarget().login(principal);
				if (principal != null) {
					if (isUseSystemContext()) {
						setNickiContext(AppContext.getSystemContext(getTarget(),
								user.getPath(), password));
					} else {
						setNickiContext(getTarget().getNamedUserContext(user, password));
					}
					return true;
				}
			}
		} catch (Exception e) {
			System.out.println("Login failed, user=" + name);
		}
		this.nickiContext = getTarget().getGuestContext();
		return false;
	}

	public void start() throws DynamicObjectException {
		getMainWindow().removeAllComponents();
		if (isUseWelcomeDialog()) {
			AbsoluteLayout layout = new AbsoluteLayout();
			layout.setHeight("800px");
			layout.addComponent(new WelcomeDialog(this), "top:0.0px;left:20.0px;");
			layout.addComponent(getEditor(), "top:20.0px;left:20.0px;");
			getMainWindow().addComponent(layout);
		} else {
			getMainWindow().addComponent(getEditor());
		}
	}
	


	public abstract Component getEditor() throws DynamicObjectException;

	public void setNickiContext(NickiContext context) {
		this.nickiContext = context;
		if (getRequest() != null) {
			try {
				getRequest().getSession(true).setAttribute(ATTR_NICKI_CONTEXT, this.nickiContext);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public NickiContext getNickiContext() {
		return nickiContext;
	}

	public abstract Target getTarget();
	
	public abstract String getI18nBase();

	public void setUseSystemContext(boolean useSystemContext) {
		this.useSystemContext = useSystemContext;
	}

	public boolean isUseSystemContext() {
		return useSystemContext;
	}

	public void setUseWelcomeDialog(boolean useWelcomeDialog) {
		this.useWelcomeDialog = useWelcomeDialog;
	}

	public boolean isUseWelcomeDialog() {
		return useWelcomeDialog;
	}

	public void confirm(Command command) {
		Window confirmWindow = new Window(command.getTitle());
		confirmWindow.setModal(true);
	       // Configure the windws layout; by default a VerticalLayout
        VerticalLayout layout = (VerticalLayout) confirmWindow.getContent();
        layout.setMargin(true);
        layout.setSpacing(true);
        // make it undefined for auto-sizing window
        layout.setSizeUndefined();

		confirmWindow.addComponent(new ConfirmDialog(command));
		getMainWindow().addWindow(confirmWindow);
	}
}
