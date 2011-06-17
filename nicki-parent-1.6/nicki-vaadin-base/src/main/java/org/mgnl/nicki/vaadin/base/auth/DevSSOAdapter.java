package org.mgnl.nicki.vaadin.base.auth;

import org.mgnl.nicki.ldap.auth.SSOAdapter;
import org.mgnl.nicki.ldap.context.AppContext;

public class DevSSOAdapter implements SSOAdapter {

	@Override
	public String getName(Object request) {
		 try {
			return AppContext.getSystemContext().getPrincipal().getName();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public char[] getPassword(Object request) {
		 try {
				return AppContext.getSystemContext().getPrincipal().getPassword().toCharArray();
			} catch (Exception e) {
				return null;
			}
	}

}
