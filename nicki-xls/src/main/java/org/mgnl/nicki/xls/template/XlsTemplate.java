
package org.mgnl.nicki.xls.template;

/*-
 * #%L
 * nicki-xls
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.mgnl.nicki.xls.model.template.Document;

import lombok.extern.slf4j.Slf4j;

// TODO: Auto-generated Javadoc
/**
 * The Class XlsTemplate.
 */
@Slf4j
public class XlsTemplate {

	/** The document. */
	private Document document;

	/**
	 * Instantiates a new xls template.
	 *
	 * @param templateContent the template content
	 * @throws JAXBException the JAXB exception
	 */
	public XlsTemplate(String templateContent) throws JAXBException {
		document = unmarshal(Document.class, templateContent);
	}

	/**
	 * Instantiates a new xls template.
	 *
	 * @param inputStream the input stream
	 * @throws JAXBException the JAXB exception
	 */
	public XlsTemplate(InputStream inputStream) throws JAXBException {
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
	@SuppressWarnings("unchecked")
	public <T> T unmarshal(Class<T> docClass, InputStream inputStream)
			throws JAXBException {
		String packageName = docClass.getPackage().getName();
		JAXBContext jc = JAXBContext.newInstance(packageName);
		Unmarshaller u = jc.createUnmarshaller();
		if (log.isDebugEnabled()) {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			try {
				IOUtils.copy(inputStream, outputStream);
			} catch (IOException e) {
				log.debug("Error with InputStream", e);
			}
			ByteArrayInputStream in = new ByteArrayInputStream(outputStream.toByteArray());
			log.debug(outputStream.toString());
			return (T) u.unmarshal(in);
		} else {
			return (T) u.unmarshal(inputStream);
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

	/**
	 * Sets the document.
	 *
	 * @param document the new document
	 */
	public void setDocument(Document document) {
		this.document = document;
	}
}
