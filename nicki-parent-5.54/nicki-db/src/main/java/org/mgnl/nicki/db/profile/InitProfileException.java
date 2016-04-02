package org.mgnl.nicki.db.profile;

import javax.naming.NamingException;

@SuppressWarnings("serial")
public class InitProfileException extends Exception {

	public InitProfileException(NamingException e) {
		super(e);
	}

}
