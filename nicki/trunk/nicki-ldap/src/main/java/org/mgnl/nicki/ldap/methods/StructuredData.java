package org.mgnl.nicki.ldap.methods;

import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;

@SuppressWarnings("serial")
public class StructuredData implements Serializable {
	Document document = null;

	public StructuredData(String xml) {
		Reader in = new StringReader(xml);
		SAXBuilder builder = new SAXBuilder();
		try {
			document = builder.build(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Document getDocument() {
		return document;
	}	

}
