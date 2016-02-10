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
package org.mgnl.nicki.vaadin.base.application;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.auth.NickiPrincipal;
import org.mgnl.nicki.core.auth.SSOAdapter;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.context.AppContext;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.context.Target;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.core.util.Classes;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.vaadin.base.auth.LoginDialog;
import org.mgnl.nicki.vaadin.base.command.Command;
import org.mgnl.nicki.vaadin.base.components.ConfirmDialog;
import org.mgnl.nicki.vaadin.base.components.WelcomeDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public abstract class NickiApplication extends UI {
	private static final Logger LOG = LoggerFactory.getLogger(NickiApplication.class);
	public static final String JAAS_SSO_ENTRY = "NickiSSO";
	public static final String JAAS_ENTRY = "Nicki";
	public static final String ATTR_NICKI_CONTEXT = "NICKI_CONTEXT";

	private NickiContext nickiContext = null;
	private boolean useSystemContext = false;
	private boolean useWelcomeDialog = false;

	VerticalLayout view;

	@Override
	public void init(VaadinRequest vaadinRequest) {
		AppContext.setRequest(vaadinRequest);
		Map<String, String> map = (Map<String, String>) getSession().getSession().getAttribute(NickiServlet.NICKI_PARAMETERS);
		AppContext.setRequestParameters(map);

		for (String paramName : map.keySet()) {
			System.out.println(paramName + "=" + map.get(paramName));
		}

		view = new VerticalLayout();
		view.setHeight("100%");
		view.setWidth("100%");
		setContent(view);
		Page.getCurrent().setTitle(I18n.getText(getI18nBase() + ".main.title"));

		// try getting context from session
		try {
			//this.nickiContext = (NickiContext) getRequest().getSession(false).getAttribute(ATTR_NICKI_CONTEXT);
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
				LOG.error("Error", e);
			}
		}

		Component loginDialog = new LoginDialog(this);
		getView().addComponent(loginDialog);
	}
	
	public void logout() {
		setNickiContext(null);
		Component loginDialog = new LoginDialog(this);
		getView().removeAllComponents();
		getView().addComponent(loginDialog);
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
				SSOAdapter adapter = (SSOAdapter) Classes.newInstance(ssoLoginClass);
				String name = adapter.getName(getRequest());
				char[] password = adapter.getPassword(getRequest());
				if (name != null && password != null) {
					NickiPrincipal principal = new NickiPrincipal(name, new String(password));
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
			}
		} catch (Exception e) {
			LOG.error("Error", e);
		}
		return null;
	}

	public Object getRequest() {
		return AppContext.getRequest();
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
			LOG.debug("Login failed, user=" + name, e);
		}
		this.nickiContext = getTarget().getGuestContext();
		return false;
	}

	public void start() throws DynamicObjectException {
		getView().removeAllComponents();
		if (isUseWelcomeDialog()) {
			getView().addComponent(new WelcomeDialog(this));
		}
		if (isAllowed(this.nickiContext.getUser())) {
			Component editor = getEditor();
			getView().addComponent(editor);
			editor.setSizeFull();
			getView().setExpandRatio(editor, 1);
		} else {
			logout();
		}
	}
	
	private boolean isAllowed(DynamicObject user) {
		boolean allowed = false;
		AccessRole roleAnnotation = getClass().getAnnotation(AccessRole.class);
		AccessGroup groupAnnotation = getClass().getAnnotation(AccessGroup.class);
		if (roleAnnotation == null && groupAnnotation == null) {
			allowed =  true;
		} else if (roleAnnotation != null){
			try {
				AccessRoleEvaluator roleEvaluator = roleAnnotation.evaluator().newInstance();
				allowed = roleEvaluator.hasRole((Person) user, roleAnnotation.name());
			} catch (Exception e) {
				LOG.error("Could not create AccessRoleEvaluator", e);
				allowed = false;
			}
		}
		if (!allowed && groupAnnotation != null) {
			try {
				AccessGroupEvaluator groupEvaluator = groupAnnotation.evaluator().newInstance();
				allowed = groupEvaluator.isMemberOf((Person) user, groupAnnotation.name());
			} catch (Exception e) {
				LOG.error("Could not create AccessGroupEvaluator", e);
				allowed = false;
			}
		}
		if (!allowed) {
			StringBuilder errorMsg = new StringBuilder();
			errorMsg.append(user).append(" tried to access application ");
			errorMsg.append(getClass().getName()).append(". Allowed: ");
			if (roleAnnotation != null) {
				errorMsg.append("Role: ").append(roleAnnotation.name().toString());
			}
			if (groupAnnotation != null) {
				if (errorMsg.length() > 0) {
					errorMsg.append(", ");
				}
				errorMsg.append("Group: ");
				for (String groupName : groupAnnotation.name()) {
					errorMsg.append(" ").append(groupName);
				}
			}
			LOG.error(errorMsg.toString());
			Notification.show(I18n.getText("nicki.editor.access.denied", getClass().getName()),
					Type.ERROR_MESSAGE);
		}
		return allowed;
	}

	public String getEditorHeight() {
		return "100%";
	}
	
	public String getEditorWidth() {
		return "100%";
	}
	


	public abstract Component getEditor() throws DynamicObjectException;

	public void setNickiContext(NickiContext context) {
		this.nickiContext = context;
		if (getRequest() != null) {
			try {
				//getRequest().getSession(true).setAttribute(ATTR_NICKI_CONTEXT, this.nickiContext);
			} catch (Exception e) {
				LOG.error("Error", e);
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
		addWindow(new ConfirmDialog(command));
	}

	public VerticalLayout getView() {
		return view;
	}

	@Override
	public String getTheme() {
		return Config.getProperty("nicki.application.theme", "reindeer");
	}
}
