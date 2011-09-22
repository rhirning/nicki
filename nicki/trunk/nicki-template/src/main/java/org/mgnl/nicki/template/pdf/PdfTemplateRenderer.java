package org.mgnl.nicki.template.pdf;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;

public class PdfTemplateRenderer extends Thread implements Runnable {
	InputStream in;
	OutputStream out;

	public PdfTemplateRenderer(InputStream in, OutputStream out) {
		super();
		this.in = in;
		this.out = out;
	}
	public void run() {
		try {
		    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		    Document doc = builder.parse(in);

		    ITextRenderer renderer = new ITextRenderer();
		    renderer.setDocument(doc, null);

		    renderer.layout();
		    renderer.createPDF(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
