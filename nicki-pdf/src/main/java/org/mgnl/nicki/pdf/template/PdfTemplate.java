
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


/**
 * The Class PdfTemplate.
 */
public class PdfTemplate {

	/** The document. */
	private Document document;

	/**
	 * Instantiates a new pdf template.
	 *
	 * @param templateContent the template content
	 * @throws JAXBException the JAXB exception
	 */
	public PdfTemplate(String templateContent) throws JAXBException {
		document = unmarshal(Document.class, templateContent);
	}

	/**
	 * Instantiates a new pdf template.
	 *
	 * @param inputStream the input stream
	 * @throws JAXBException the JAXB exception
	 */
	public PdfTemplate(InputStream inputStream) throws JAXBException {
		document = unmarshal(Document.class, inputStream);
	}

	/**
	 * Unmarshal.
	 *
	 * @param <T> the generic type
	 * @param docClass the doc class
	 * @param templateContent the template content
	 * @return the t
	 * @throws JAXBException the JAXB exception
	 */
	public <T> T unmarshal(Class<T> docClass, String templateContent) throws JAXBException {
		return unmarshal(docClass, new ByteArrayInputStream(templateContent.getBytes()));
	}

	/**
	 * Unmarshal.
	 *
	 * @param <T> the generic type
	 * @param docClass the doc class
	 * @param inputStream the input stream
	 * @return the t
	 * @throws JAXBException the JAXB exception
	 */
	public <T> T unmarshal(Class<T> docClass, InputStream inputStream)
			throws JAXBException {
		String packageName = docClass.getPackage().getName();
		JAXBContext jc = JAXBContext.newInstance(packageName);
		Unmarshaller u = jc.createUnmarshaller();
		@SuppressWarnings("unchecked")
		T doc = (T) u.unmarshal(inputStream);
		return doc;
	}

	/**
	 * Gets the document.
	 *
	 * @return the document
	 */
	public Document getDocument() {
		return document;
	}

	/**
	 * Sets the document.
	 *
	 * @param document the new document
	 */
	public void setDocument(Document document) {
		this.document = document;
	}
}
