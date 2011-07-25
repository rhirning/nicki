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
