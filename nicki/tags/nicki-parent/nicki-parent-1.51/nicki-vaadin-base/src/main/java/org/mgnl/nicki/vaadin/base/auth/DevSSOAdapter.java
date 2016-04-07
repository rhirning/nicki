/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.auth;

import org.mgnl.nicki.ldap.auth.SSOAdapter;
import org.mgnl.nicki.ldap.context.AppContext;

public class DevSSOAdapter implements SSOAdapter {

	public String getName(Object request) {
		 try {
			return AppContext.getSystemContext().getPrincipal().getName();
		} catch (Exception e) {
			return null;
		}
	}

	public char[] getPassword(Object request) {
		 try {
				return AppContext.getSystemContext().getPrincipal().getPassword().toCharArray();
			} catch (Exception e) {
				return null;
			}
	}

}