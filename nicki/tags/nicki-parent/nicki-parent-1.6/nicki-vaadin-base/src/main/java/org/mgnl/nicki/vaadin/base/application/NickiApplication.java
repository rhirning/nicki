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
import org.mgnl.nicki.ldap.context.NickiContext.READONLY;
import org.mgnl.nicki.vaadin.base.auth.LoginDialog;

import com.vaadin.Application;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public abstract class NickiApplication extends Application  implements HttpServletRequestListener{
	public static final String JAAS_SSO_ENTRY = "NickiSSO";
	public static final String JAAS_ENTRY = "Nicki";
	public static final String ATTR_NICKI_CONTEXT = "NICKI_CONTEXT";

	private NickiContext context = null;


	@Override
	public void init() {
		setTheme(Config.getProperty("nicki.application.theme", "reindeer"));
		Window mainWindow = new Window(I18n.getText(getI18nBase() + ".main.title"));
		mainWindow.setHeight(800, Sizeable.UNITS_PIXELS);
		mainWindow.setWidth("100%");
		setMainWindow(mainWindow);

		// try getting context from session
		try {
			this.context = (NickiContext) getRequest().getSession(false).getAttribute(ATTR_NICKI_CONTEXT);
		} catch (Exception e) {
		}
		
		if (context == null) {
			// try SSO
			setNickiContext(loginSSO());
		}
		if (context == null) {
			Component loginDialog = new LoginDialog(this);
			getMainWindow().addComponent(loginDialog);
		} else {
			start();
		}
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
				if (principal != null && getTarget().login(principal) != null) {
					return getTarget().getNamedUserContext(principal, READONLY.FALSE);
				}
			}
		} catch (Exception e) {
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
				principal = getTarget().login(principal);
				if (principal != null) {
					setNickiContext(getTarget().getNamedUserContext(principal, READONLY.FALSE));
					return true;
				}
			}
		} catch (Exception e) {
			System.out.println("Login falied, user=" + name);
		}
		this.context = getTarget().getGuestContext();
		return false;
	}

	public void start() {
		getMainWindow().removeAllComponents();
		getMainWindow().addComponent(getEditor());
	}
	


	public abstract Component getEditor();

	public void setNickiContext(NickiContext context) {
		this.context = context;
		if (getRequest() != null) {
			try {
				getRequest().getSession(true).setAttribute(ATTR_NICKI_CONTEXT, this.context);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public NickiContext getNickiContext() {
		return context;
	}

	public abstract Target getTarget();
	
	public abstract String getI18nBase();

}