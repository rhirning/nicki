package org.mgnl.nicki.ws.base;

/*-
 * #%L
 * nicki-ws
 * %%
 * Copyright (C) 2017 - 2018 Ralf Hirning
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

import java.util.List;

import javax.annotation.Resource;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;

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

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class NickiWebService {

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
				log.debug("Authorization context:" + ctx);
				String baseDn = ctx.getTarget().getProperty("baseDn", Config.getString("nicki.users.basedn"));
				List<? extends DynamicObject> list = ctx.loadObjects(Person.class, baseDn, "cn=" + user.getName());
				
				if (list != null && list.size() == 1) {
					log.info("login: loadObjects successful");
					return (Person) list.get(0);
				}
			}
		} catch (Exception e) {
			log.error("Invalid SystemContext", e);
		}
		log.debug("Fallback authorization context:" + user.getContext());
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
		log.debug("LoginTarget=" + loginTargetName);
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
		log.debug("Target=" + targetName);
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
		log.debug("accessTarget=" + accessTargetName);
		return accessTargetName;

	}

	protected abstract boolean hasRight(DynamicObject user, String right);
	
	protected DoubleContext loginSSO(String ssoAdapterKey) {
		try {
			SSOAdapter adapter = Config.getClassInstance(ssoAdapterKey);
			if (adapter != null) {
				log.debug("ssoLoginClass: " + adapter.getClass());
				NickiPrincipal principal = new NickiPrincipal(adapter.getName(), new String(adapter
						.getPassword()));
				log.debug("principal: " + principal.getName() + "/" + principal.getPassword());
				log.debug("before target.login()");
				DoubleContext context = new DoubleContext();
				Target loginTarget = TargetFactory.getTarget(getLoginTargetName());
				log.debug("LoginTarget=" + loginTarget);

				context.setLoginContext(AppContext.getSystemContext(loginTarget, principal.getName(), principal
						.getPassword()));
				context.setContext(AppContext.getSystemContext(getTargetName(), context.getLoginContext().getUser()));
				log.debug("after AppContext.getSystemContext()");
				return context;
			}
		} catch (Exception e) {
			log.error("loginSSO error", e);
		}
		return null;
	}

	protected abstract Target getTarget();

	protected DoubleContext login() throws AuthenticationFailedException {
		MessageContext mctx = this.wsctx.getMessageContext();
		AppContext.setRequest(mctx);

		log.debug("pre login");
		DoubleContext context = this.loginSSO("nicki.login.sso.ws");
		log.debug("after login");
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
