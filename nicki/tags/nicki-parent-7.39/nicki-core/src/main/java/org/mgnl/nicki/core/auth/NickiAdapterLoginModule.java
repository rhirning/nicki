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
package org.mgnl.nicki.core.auth;

import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.util.Classes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NickiAdapterLoginModule extends NickiLoginModule implements LoginModule {
	private static final Logger LOG = LoggerFactory.getLogger(NickiAdapterLoginModule.class);
	
	private SSOAdapter adapter;
	
	@Override
	public boolean login() throws LoginException {
		LOG.debug("Using " + getClass().getCanonicalName() +  " with Adapter " + getAdapter().getClass().getCanonicalName());

		NickiPrincipal principal;
		try {
			if (StringUtils.isBlank(getAdapter().getName()) || getAdapter().getPassword() == null) {
				LOG.debug("No valid principal");
			}
		} catch (Exception e1) {
			LOG.debug("No valid principal");
		}
		try {
			principal = new NickiPrincipal(getAdapter().getName(), new String(getAdapter().getPassword()));
			setContext(login(principal));
		} catch (Exception e) {
			LOG.debug("Invalid Principal", e);
			return false;
		}

		// TODO: separate context / loginContext
		DynamicObjectPrincipal dynamicObjectPrincipal = new DynamicObjectPrincipal(principal, getContext(), getContext());
		setPrincipal(dynamicObjectPrincipal);
		setSucceeded(true);
		return true;
	}

	private SSOAdapter getAdapter() {
		if (this.adapter == null) {
			String adapterClass =(String) getOptions().get("adapter");
			try {
				this.adapter = Classes.newInstance(adapterClass);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				LOG.error("Could not create adapter " + adapterClass, e);
			}
		}
		return adapter;
	}
}
