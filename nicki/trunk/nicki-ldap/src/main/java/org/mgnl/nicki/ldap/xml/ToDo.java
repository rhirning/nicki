/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.ldap.xml;

public class ToDo {
	public String path;
	public String attributeName;
	public String internalLink;
	public ToDo(String path, String attributeName, String internalLink) {
		super();
		this.path = path;
		this.attributeName = attributeName;
		this.internalLink = internalLink;
	}
	public String getPath() {
		return path;
	}
	public String getAttributeName() {
		return attributeName;
	}
	public String getInternalLink() {
		return internalLink;
	}

}
