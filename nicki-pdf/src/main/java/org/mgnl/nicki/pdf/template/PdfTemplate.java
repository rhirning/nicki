
package org.mgnl.nicki.pdf.template;

/*-
 * #%L
 * nicki-pdf
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


import java.io.ByteArrayInputStream;
import java.io.InputStream;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import org.mgnl.nicki.pdf.model.template.Document;

public class PdfTemplate {

	private Document document;

	public PdfTemplate(String templateContent) throws JAXBException {
		document = unmarshal(Document.class, templateContent);
	}

	public PdfTemplate(InputStream inputStream) throws JAXBException {
		document = unmarshal(Document.class, inputStream);
	}

	public <T> T unmarshal(Class<T> docClass, String templateContent) throws JAXBException {
		return unmarshal(docClass, new ByteArrayInputStream(templateContent.getBytes()));
	}

	public <T> T unmarshal(Class<T> docClass, InputStream inputStream)
			throws JAXBException {
		String packageName = docClass.getPackage().getName();
		JAXBContext jc = JAXBContext.newInstance(packageName);
		Unmarshaller u = jc.createUnmarshaller();
		@SuppressWarnings("unchecked")
		T doc = (T) u.unmarshal(inputStream);
		return doc;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}
}
