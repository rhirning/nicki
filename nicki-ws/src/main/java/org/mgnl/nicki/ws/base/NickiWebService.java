package org.mgnl.nicki.ws.base;

import java.util.List;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.auth.AccessTargetContext;
import org.mgnl.nicki.core.auth.LoginTargetContext;
import org.mgnl.nicki.core.auth.NickiPrincipal;
import org.mgnl.nicki.core.auth.SSOAdapter;
import org.mgnl.nicki.core.auth.TargetContext;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.context.AppContext;
import org.mgnl.nicki.core.context.DoubleContext;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.context.Target;
import org.mgnl.nicki.core.context.TargetFactory;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.ws.base.annotations.Right;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class NickiWebService {

	private static Logger LOG = LoggerFactory.getLogger(NickiWebService.class);

	@Resource
	WebServiceContext wsctx;
	
	protected boolean isAllowed(DynamicObject loginUser, Class<?> clazz) {
		DynamicObject user = getUser(loginUser, getAccessTargetName());
		if (clazz.getAnnotation(Right.class) != null) {
			String right = clazz.getAnnotation(Right.class).value();
			if (!this.hasRight(user, right)) {
				return false;
			}
		}
		return true;
	}

	protected Person getUser(DynamicObject user, String targetName) {
		try {
			if (!StringUtils.equals(targetName, user.getContext().getTarget().getName())) {
				NickiContext ctx = AppContext.getSystemContext(targetName);
				LOG.debug("Authorization context:" + ctx);
				String baseDn = ctx.getTarget().getProperty("baseDn", Config.getString("nicki.users.basedn"));
				List<? extends DynamicObject> list = ctx.loadObjects(Person.class, baseDn, "cn=" + user.getName());
				
				if (list != null && list.size() == 1) {
					LOG.info("login: loadObjects successful");
					return (Person) list.get(0);
				}
			}
		} catch (Exception e) {
			LOG.error("Invalid SystemContext", e);
		}
		LOG.debug("Fallback authorization context:" + user.getContext());
		return (Person) user;
	}

	protected String getLoginTargetName() {
		String loginTargetName = null;
		LoginTargetContext annotation = this.getClass().getAnnotation(LoginTargetContext.class);
		if (annotation != null) {
			if (StringUtils.isNotBlank(annotation.configName())) {
				loginTargetName = Config.getString(annotation.configName(), "");
			} else if (StringUtils.isNotBlank(annotation.name())) {
				loginTargetName = annotation.name();
			}
		}
		if (StringUtils.isBlank(loginTargetName)) {
			loginTargetName = getTarget().getName();
		}
		LOG.debug("LoginTarget=" + loginTargetName);
		return loginTargetName;

	}

	protected String getTargetName() {
		String targetName = null;
		TargetContext annotation = this.getClass().getAnnotation(TargetContext.class);
		if (annotation != null) {
			if (StringUtils.isNotBlank(annotation.configName())) {
				targetName = Config.getString(annotation.configName(), "");
			} else if (StringUtils.isNotBlank(annotation.name())) {
				targetName = annotation.name();
			}
		}
		if (StringUtils.isBlank(targetName)) {
			targetName = getTarget().getName();
		}
		LOG.debug("Target=" + targetName);
		return targetName;

	}

	protected String getAccessTargetName() {
		String accessTargetName = null;
		AccessTargetContext annotation = this.getClass().getAnnotation(AccessTargetContext.class);
		if (annotation != null) {
			if (StringUtils.isNotBlank(annotation.configName())) {
				accessTargetName = Config.getString(annotation.configName(), "");
			} else if (StringUtils.isNotBlank(annotation.name())) {
				accessTargetName = annotation.name();
			}
		}
		if (StringUtils.isBlank(accessTargetName)) {
			accessTargetName = getTarget().getName();
		}
		LOG.debug("accessTarget=" + accessTargetName);
		return accessTargetName;

	}

	protected abstract boolean hasRight(DynamicObject user, String right);
	
	protected DoubleContext loginSSO(String ssoAdapterKey) {
		try {
			SSOAdapter adapter = Config.getClassInstance(ssoAdapterKey);
			if (adapter != null) {
				LOG.debug("ssoLoginClass: " + adapter.getClass());
				NickiPrincipal principal = new NickiPrincipal(adapter.getName(), new String(adapter
						.getPassword()));
				LOG.debug("principal: " + principal.getName() + "/" + principal.getPassword());
				LOG.info("before target.login()");
				DoubleContext context = new DoubleContext();
				Target loginTarget = TargetFactory.getTarget(getLoginTargetName());
				LOG.debug("LoginTarget=" + loginTarget);

				context.setLoginContext(AppContext.getSystemContext(loginTarget, principal.getName(), principal
						.getPassword()));
				context.setContext(AppContext.getSystemContext(getTargetName(), context.getLoginContext().getUser()));
				LOG.info("after AppContext.getSystemContext()");
				return context;
			}
		} catch (Exception e) {
			LOG.error("loginSSO error", e);
		}
		return null;
	}

	protected abstract Target getTarget();

	protected DoubleContext login() throws AuthenticationFailedException {
		MessageContext mctx = this.wsctx.getMessageContext();
		AppContext.setRequest(mctx);

		LOG.info("pre login");
		DoubleContext context = this.loginSSO("nicki.login.sso.ws");
		LOG.info("after login");
		if (context == null) {
			throw new AuthenticationFailedException();
		} else {
			return context;
		}
	}

	public void setWsctx(WebServiceContext wsctx) {
		this.wsctx = wsctx;
	}
}
