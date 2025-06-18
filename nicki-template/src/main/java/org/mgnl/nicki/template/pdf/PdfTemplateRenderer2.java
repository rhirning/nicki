
package org.mgnl.nicki.template.pdf;

/*-
 * #%L
 * nicki-template
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


import java.io.InputStream;
import java.io.OutputStream;

import org.mgnl.nicki.pdf.engine.PdfEngine;
import org.mgnl.nicki.pdf.template.PdfTemplate;

import lombok.extern.slf4j.Slf4j;


/**
 * The Class PdfTemplateRenderer2.
 */
@Slf4j
public class PdfTemplateRenderer2 extends Thread implements Runnable {
	
	/** The in. */
	InputStream in;
	
	/** The out. */
	OutputStream out;

	/**
	 * Instantiates a new pdf template renderer 2.
	 *
	 * @param in the in
	 * @param out the out
	 */
	public PdfTemplateRenderer2(InputStream in, OutputStream out) {
		super();
		this.in = in;
		this.out = out;
	}
	
	/**
	 * Run.
	 */
	public void run() {
		try {
			PdfEngine engine = PdfEngine.fromResource("pdf-configuration.xml", "nicki.pdf.contextBasePath");
			PdfTemplate template = new PdfTemplate(in);
			engine.render(template, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			log.error("Error", e);
		}
	}
}
