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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.context.AppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BasicAuthAdapter implements SSOAdapter {
	private static final Logger LOG = LoggerFactory.getLogger(BasicAuthAdapter.class);
	private Object request;

	public String getName() {
		return getAuthPart(0);
	}

	public char[] getPassword() {
		String password = getAuthPart(1);
		if (password != null) {
			return password.toCharArray();
		}
		return new char[]{};
	}

	@Override
	public TYPE getType() {
		return TYPE.BASIC;
	}

	@Override
	public void setRequest(Object request) {
		this.request = request;
	}
	
	public Object getRequest() {
		if (this.request != null) {
			return this.request;
		} else {
			return AppContext.getRequest();
		}
	}

	protected String[] decode(final String encodedString) {
		if (StringUtils.isNotBlank(encodedString)) {
			final byte[] decodedBytes = Base64.decodeBase64(encodedString
					.getBytes());
			final String pair = new String(decodedBytes);
			final String[] userDetails = pair.split(":", 2);
			return userDetails;
		}
		return new String[]{};
	}
	
	protected String getAuthPart(int num) {
		try {
			if (getRequest() instanceof HttpServletRequest) {
				HttpServletRequest httpServletRequest = (HttpServletRequest) getRequest();
				String header = httpServletRequest.getHeader("Authorization");
				String encodedCredentials = StringUtils.substringAfter(header, " ");
				String[] decodedCredentials = decode(encodedCredentials);
				if (decodedCredentials != null) {
					return decodedCredentials[num];
				}
			}
		} catch (Exception e) {
			LOG.error("Error reading Basic Authentication data", e.getMessage());
		}
		return null;
	}
	
}
