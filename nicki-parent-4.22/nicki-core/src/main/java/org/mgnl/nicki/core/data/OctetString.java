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
package org.mgnl.nicki.core.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OctetString {
	private static final Logger LOG = LoggerFactory.getLogger(OctetString.class);
	byte octetString[];

	public OctetString(byte octet[]) {
		this.octetString = octet;
	}

	public Object getValue() {
		return octetString;
	}

	public String toString() {
		try {
			String result = "";
			for (int i = 0; i < octetString.length; i++) {
				result += Integer.toString((octetString[i] & 0xff) + 0x100, 16)
						.substring(1);
			}
			return result;
		} catch (Exception e) {
			LOG.error("Error", e);
		}
		return null;
	}

}