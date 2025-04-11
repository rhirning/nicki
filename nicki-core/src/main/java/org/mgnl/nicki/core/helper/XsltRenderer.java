
package org.mgnl.nicki.core.helper;

/*-
 * #%L
 * nicki-core
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

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


import lombok.extern.slf4j.Slf4j;

// TODO: Auto-generated Javadoc
/**
 * The Class XsltRenderer.
 */
@Slf4j
public class XsltRenderer extends Thread implements Runnable {
	
	/** The in. */
	InputStream in;
	
	/** The out. */
	OutputStream out;
	
	/** The xsl template. */
	InputStream xslTemplate;
	

	/**
	 * Instantiates a new xslt renderer.
	 *
	 * @param in the in
	 * @param out the out
	 * @param xslTemplate the xsl template
	 */
	public XsltRenderer(InputStream in, OutputStream out,
			InputStream xslTemplate) {
		super();
		this.in = in;
		this.out = out;
		this.xslTemplate = xslTemplate;
	}

	/**
	 * Run.
	 */
	public void run() {
		try {
            // Create transformer factory
            TransformerFactory factory = TransformerFactory.newInstance();

            // Use the factory to create a template containing the xsl file
            Templates template = factory.newTemplates(new StreamSource(xslTemplate));

            // Use the template to create a transformer
            Transformer xformer = template.newTransformer();

            // Prepare the input and output files
            Source source = new StreamSource(in);
            Result result = new StreamResult(out);

            // Apply the xsl file to the source file and write the result to the output file
            xformer.transform(source, result);
			out.flush();
			out.close();
		} catch (Exception e) {
			log.error("Error", e);
		}
	}
}
