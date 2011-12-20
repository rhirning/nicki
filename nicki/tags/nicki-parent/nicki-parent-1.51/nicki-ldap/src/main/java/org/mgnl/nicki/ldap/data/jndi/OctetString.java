/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.ldap.data.jndi;

public class OctetString {
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
			e.printStackTrace();
		}
		return null;
	}

}