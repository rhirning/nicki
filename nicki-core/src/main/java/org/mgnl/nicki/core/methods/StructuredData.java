
package org.mgnl.nicki.core.methods;

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


import java.io.Serializable;

import org.jdom2.Document;
import org.mgnl.nicki.core.helper.XMLHelper;


/**
 * The Class StructuredData.
 */
@SuppressWarnings("serial")
public class StructuredData implements Serializable {
	
	/** The document. */
	private Document document;

	/**
	 * Instantiates a new structured data.
	 *
	 * @param xml the xml
	 */
	public StructuredData(String xml) {
		try {
			document = XMLHelper.documentFromString(xml);
		} catch (Exception e) {
			document = null;
		}
	}

	/**
	 * Gets the document.
	 *
	 * @return the document
	 */
	public Document getDocument() {
		return document;
	}	

}
