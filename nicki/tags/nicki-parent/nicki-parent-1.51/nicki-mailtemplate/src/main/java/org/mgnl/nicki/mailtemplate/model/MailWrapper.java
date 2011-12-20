/**
 * This file Copyright (c) 2011 deron Consulting GmbH
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.mailtemplate.model;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

public class MailWrapper extends HashMap<String, MailContainer>{

	private static final long serialVersionUID = -1564222899647476946L;
	
	public String getMailPart(String localeString, String part) {
		MailContainer container = get(localeString);
		if (container != null) {
			String result = container.get(part);
			if (StringUtils.isNotEmpty(result)) {
				return result;
			}
		}
		return "";
	}
	
	public String toXml() {
		return null;
	}
}
