package org.mgnl.nicki.ldap.objects;

@SuppressWarnings("serial")
public class DynamicObjectException extends Exception {

	public DynamicObjectException(String message) {
		super(message);
	}

	public DynamicObjectException(Exception e) {
		super(e);
	}

}
