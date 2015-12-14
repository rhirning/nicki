/**
 * Copyright (c) 2003-2015 Dr. Ralf Hirning
 * All rights reserved.
 *  
 * This program is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 * 
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU Public License v3.0
 * which is available at
 * http://www.gnu.org/licenses/gpl.html
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 * 
 * Any modifications to this file must keep this entire header
 * intact.
*/
package org.mgnl.nicki.template.pdf;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;

public class PdfTemplateRenderer extends Thread implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(PdfTemplateRenderer.class);
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
			LOG.error("Error", e);
		}
	}
}
