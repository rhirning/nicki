package org.mgnl.nicki.jcr.objects;

import javax.jcr.Node;

public class NodeDynamicObject extends NodeDynamicTemplateObject {
	private static final long serialVersionUID = -2438906061486993342L;
	public static final String ATTRIBUTE_SURNAME = "surname";
	public static final String ATTRIBUTE_GIVENNAME = "givenname";
	public static final String ATTRIBUTE_FULLNAME = "fullname";
	public static final String ATTRIBUTE_PASSWORD = "password";
	public static final String ATTRIBUTE_LANGUAGE = "language";
	
	
	@Override
	public void initDataModel() {
		/*
		addObjectClass("Person");
		DynamicAttribute dynAttribute = new DynamicAttribute(ATTRIBUTE_NAME, "cn",
				String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_SURNAME, "name", String.class);
		dynAttribute.setMandatory();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_GIVENNAME, "givenName",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_FULLNAME, "fullName",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_LANGUAGE, "language",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_PASSWORD, "password",
				String.class);
		addAttribute(dynAttribute);
		*/

	}

	public boolean accept(Node node2) {
		// TODO Auto-generated method stub
		return true;
	}

	public void setJcrNode(Node node2) {
		// TODO Auto-generated method stub
		
	}

}
