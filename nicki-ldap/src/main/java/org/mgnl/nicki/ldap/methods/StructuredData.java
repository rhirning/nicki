/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.ldap.methods;

import java.io.Serializable;

import org.jdom.Document;
import org.mgnl.nicki.core.helper.XMLHelper;

@SuppressWarnings("serial")
public class StructuredData implements Serializable {
	Document document = null;

	public StructuredData(String xml) {
		try {
			document = XMLHelper.documentFromString(xml);
		} catch (Exception e) {
			document = null;
		}
	}

	public Document getDocument() {
		return document;
	}	

}
