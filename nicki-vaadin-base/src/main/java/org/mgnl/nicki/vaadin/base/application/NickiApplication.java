
package org.mgnl.nicki.vaadin.base.application;

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


import java.io.Serializable;
import java.net.URL;
import java.security.Principal;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.auth.DynamicObjectPrincipal;
import org.mgnl.nicki.core.auth.InvalidPrincipalException;
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
import org.mgnl.nicki.vaadin.base.auth.ApplicationLoginDialog;
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

	private NickiContext nickiContext;
	private Context context;
	private boolean useSystemContext;
	private boolean useWelcomeDialog;

	VerticalLayout view;

	@Override
	public void init(VaadinRequest vaadinRequest) {
		AppContext.setRequest(vaadinRequest);
		@SuppressWarnings("unchecked")
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
		

		if (nickiContext == null) {
			// try SSO
			loginSSO();
		}
		
		if (context == null) {
			// set login.conf
//	        System.out.println("login.conf: " + System.getProperty("java.security.auth.login.config"));
//
//	        if(System.getProperty("java.security.auth.login.config") == null) {
//	            String jaasConfigFile = null;
//	            URL jaasConfigURL = this.getClass().getClassLoader().getResource("login.conf");
//	            if(jaasConfigURL != null) {
//	                jaasConfigFile = jaasConfigURL.getFile();
//	            }
//	            System.setProperty("java.security.auth.login.config", jaasConfigFile);
//		        System.out.println("login.conf: " + System.getProperty("java.security.auth.login.config"));
//	        }
//			loginJAAS();
		}
		if (context != null) {
			try {
				start();
				return;
			} catch (DynamicObjectException e) {
				LOG.error("Error", e);
			}
		}

		showLoginDialog();
	}
	
	public void logout() {
		setContext(null);
		showLoginDialog();
	}
	
	private void showLoginDialog() {
		LoginDialog loginDialog = null;
		String loginClass = Config.getString("nicki.application.login.class");
		if (StringUtils.isNotBlank(loginClass)) {
		try {
				loginDialog = Classes.newInstance(loginClass);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				LOG.error("Error creatin LoginDialog " + loginClass,e.getMessage());
			}
		}
		if (loginDialog == null) {
			loginDialog = new ApplicationLoginDialog();
		}
		loginDialog.setApplication(this);
		getView().removeAllComponents();
		getView().addComponent(loginDialog);
	}
	
	private void loginJAAS() {
		try {
			
			LoginContext loginContext = new LoginContext(Config.getProperty("nicki.login.context.name", "nicki"), new Subject());
			loginContext.login();
			Set<Principal> principals = loginContext.getSubject().getPrincipals();
			if (principals != null && principals.size() > 0) {
				DynamicObjectPrincipal dynamicObjectPrincipal = (DynamicObjectPrincipal) principals.iterator().next();
				Context context = new Context();
				context.setLoginContext(dynamicObjectPrincipal.getLoginContext());
				context.setContext(dynamicObjectPrincipal.getContext());
				setContext(context);
			}
		} catch (LoginException e) {
			LOG.error(e.getMessage());
		}
	}

	
	private void loginSSO() {
		try {
			String ssoLoginClass = Config.getString("nicki.login.sso");
			if (StringUtils.isNotEmpty(ssoLoginClass)) {
				SSOAdapter adapter = (SSOAdapter) Classes.newInstance(ssoLoginClass);
				String name = adapter.getName();
				char[] password = adapter.getPassword();
				if (name != null && password != null) {
					NickiPrincipal principal = new NickiPrincipal(name, new String(password));
					if (principal != null) {
						DynamicObject user = getTarget().login(principal);
						if (user != null) {
							setContext(getContext(user, principal.getPassword()));
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error", e.getMessage());
		}
	}
	

	private Context getContext(DynamicObject user, String password) throws InvalidPrincipalException, TargetException {
		Context context = new Context();
		if (isUseSystemContext()) {
			context.setLoginContext(AppContext.getSystemContext(getTarget(), user.getPath(), password));
		} else {
			context.setLoginContext(getTarget().getNamedUserContext(user, password));
		}
		TargetContext targetContext = getClass().getAnnotation(TargetContext.class);
		try {
			if (targetContext == null) {
				context.setContext(context.getLoginContext());
			} else {
				context.setContext(AppContext.getSystemContext(targetContext.value()));
			}
			if (context.getContext() == null) {
				Notification.show(I18n.getText("nicki.application.error.invalid.target", targetContext.value()),
						Type.ERROR_MESSAGE);
				return null;
			}
		} catch (InvalidPrincipalException e) {
			throw e;
		} catch (Exception e1) {
			throw new TargetException("Invalid Target");
		}
		return context;
	}

	public Object getRequest() {
		return AppContext.getRequest();
	}

	public boolean login(String name, String password) {
		try {
			NickiPrincipal principal = new NickiPrincipal(name, password);
			if (principal != null) {
				DynamicObject user = getTarget().login(principal);
				if (user != null) {
					setContext(getContext(user, principal.getPassword()));
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
		if (isAllowed(this.context.getLoginContext().getUser())) {
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
	
	private class Context implements Serializable {
		private NickiContext context;
		private NickiContext loginContext;

		public NickiContext getContext() {
			return this.context;
		}

		public void setContext(NickiContext context) {
			this.context = context;
		}

		public NickiContext getLoginContext() {
			return this.loginContext;
		}

		public void setLoginContext(NickiContext loginContext) {
			this.loginContext = loginContext;
		}

	}

	public void setContext(Context context) {
		this.context = context;
		if (context != null) {
			this.nickiContext = context.getContext();
		} else {
			this.nickiContext = null;
		}
	}
}
