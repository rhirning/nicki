package org.mgnl.nicki.ldap.data;

@SuppressWarnings("serial")
public class InstantiateDynamicObjectException extends Exception {

	public InstantiateDynamicObjectException(Exception e) {
		super(e);
	}

	public InstantiateDynamicObjectException(String text) {
		super(text);
	}

}
