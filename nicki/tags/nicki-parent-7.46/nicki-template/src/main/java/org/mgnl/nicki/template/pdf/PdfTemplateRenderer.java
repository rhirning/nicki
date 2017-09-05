/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
