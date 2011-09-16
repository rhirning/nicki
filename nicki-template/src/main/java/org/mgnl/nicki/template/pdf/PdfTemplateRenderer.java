package org.mgnl.nicki.template.pdf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.SAXException;

import com.lowagie.text.DocumentException;

public class PdfTemplateRenderer {
	private static PdfTemplateRenderer instance = new PdfTemplateRenderer();

	public void render(InputStream inputStream, OutputStream outputStream)
		throws ParserConfigurationException, SAXException, IOException, DocumentException {
	    
	    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    Document doc = builder.parse(inputStream);

	    ITextRenderer renderer = new ITextRenderer();
	    renderer.setDocument(doc, null);

	    renderer.layout();
	    renderer.createPDF(outputStream);
	    outputStream.close();
	}

	public static PdfTemplateRenderer getInstance() {
		return instance;
	}

}
