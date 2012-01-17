/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
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
