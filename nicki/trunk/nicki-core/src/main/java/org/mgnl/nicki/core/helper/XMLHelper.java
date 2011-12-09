/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.core.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class XMLHelper {
	
	static public Document documentFromClasspath(Class<?> refClass, String classLoaderPath) throws JDOMException, IOException {
		InputStream inStream = refClass.getClassLoader().getResourceAsStream(classLoaderPath);
		Reader in = new InputStreamReader(inStream);
		SAXBuilder builder = new SAXBuilder();
		return builder.build(in);
	}

	static public Document documentFromString(String xmlString) throws JDOMException, IOException  {
		Reader in = new StringReader(StringUtils.trimToEmpty(xmlString));
		SAXBuilder builder = new SAXBuilder();
		return builder.build(in);
	}

}
