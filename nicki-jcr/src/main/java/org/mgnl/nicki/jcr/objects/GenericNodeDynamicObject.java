package org.mgnl.nicki.jcr.objects;

import java.io.Serializable;

import javax.jcr.Node;


public class GenericNodeDynamicObject extends NodeDynamicTemplateObject implements JcrDynamicObject, Serializable {
	private static final long serialVersionUID = -2438906061486993342L;
	public static final String ATTRIBUTE_SURNAME = "surname";
	public static final String ATTRIBUTE_GIVENNAME = "givenname";
	public static final String ATTRIBUTE_FULLNAME = "fullname";
	public static final String ATTRIBUTE_PASSWORD = "password";
	public static final String ATTRIBUTE_LANGUAGE = "language";
	
	@Override
	public boolean accept(Node node) {
		return true;
	}

}
