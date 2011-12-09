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

public class MailContainer extends HashMap<String, String> {

	private static final long serialVersionUID = -4610531292533738824L;
	private String localeName;
	public String getLocaleName() {
		return localeName;
	}
	public MailContainer(String localeName) {
		this.localeName = localeName;
	}

}
