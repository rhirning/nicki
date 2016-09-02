package org.mgnl.nicki.xls.template;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.mgnl.nicki.xls.model.template.Document;

public class XlsTemplate {

	private Document document;

	public XlsTemplate(String templateContent) throws JAXBException {
		document = unmarshal(Document.class, templateContent);
	}

	public XlsTemplate(InputStream inputStream) throws JAXBException {
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
