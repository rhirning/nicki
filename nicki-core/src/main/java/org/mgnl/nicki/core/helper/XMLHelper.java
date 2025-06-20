
package org.mgnl.nicki.core.helper;

/*-
 * #%L
 * nicki-core
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;


/**
 * The Class XMLHelper.
 */
public class XMLHelper {
	
	/**
	 * Document from classpath.
	 *
	 * @param refClass the ref class
	 * @param classLoaderPath the class loader path
	 * @return the document
	 * @throws JDOMException the JDOM exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	static public Document documentFromClasspath(Class<?> refClass, String classLoaderPath) throws JDOMException, IOException {
		InputStream inStream = refClass.getClassLoader().getResourceAsStream(classLoaderPath);
		Reader in = new InputStreamReader(inStream);
		SAXBuilder builder = new SAXBuilder();
		return builder.build(in);
	}

	/**
	 * Document from string.
	 *
	 * @param xmlString the xml string
	 * @return the document
	 * @throws JDOMException the JDOM exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	static public Document documentFromString(String xmlString) throws JDOMException, IOException  {
		Reader in = new StringReader(StringUtils.trimToEmpty(xmlString));
		SAXBuilder builder = new SAXBuilder();
		return builder.build(in);
	}

}
