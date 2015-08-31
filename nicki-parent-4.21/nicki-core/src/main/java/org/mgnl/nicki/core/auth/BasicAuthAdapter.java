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
import org.mgnl.nicki.core.auth.SSOAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

;

public class BasicAuthAdapter implements SSOAdapter {
	private static Logger LOG = LoggerFactory.getLogger(BasicAuthAdapter.class);

	public String getName(Object request) {
		try {
			return getAuthPart(request, 0);
		} catch (Exception e) {
			LOG.error("Error reading Basic Authentication data", e);
			return null;
		}
	}

	public char[] getPassword(Object request) {
		try {
			return getAuthPart(request, 1).toCharArray();
		} catch (Exception e) {
			LOG.error("Error reading Basic Authentication data", e);
			return null;
		}
	}

	@Override
	public TYPE getType() {
		return TYPE.BASIC;
	}

	@Override
	public void init(Object request) {
	}

	private static String[] decode(final String encodedString) {
		final byte[] decodedBytes = Base64.decodeBase64(encodedString
				.getBytes());
		final String pair = new String(decodedBytes);
		final String[] userDetails = pair.split(":", 2);
		return userDetails;
	}
	
	private static String getAuthPart(Object request, int num) {
		try {
			if (request instanceof HttpServletRequest) {
				HttpServletRequest httpServletRequest = (HttpServletRequest) request;
				String header = httpServletRequest.getHeader("Authorization");
				String encodedCredentials = StringUtils.substringAfter(header, " ");
				return decode(encodedCredentials)[num];
			}
		} catch (Exception e) {
			LOG.error("Error reading Basic Authentication data", e);
		}
		return null;
	}
	
}
