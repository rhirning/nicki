
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


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.mgnl.nicki.dynamic.objects.objects.EngineTemplate;
import org.mgnl.nicki.xls.engine.XlsEngine;
import org.mgnl.nicki.xls.template.XlsTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XlsTemplateRenderer extends Thread implements Runnable {
	InputStream in;
	OutputStream out;
	EngineTemplate template;
	byte[] master;

	public XlsTemplateRenderer(EngineTemplate template, InputStream in, OutputStream out) {
		super();
		this.template = template;
		this.in = in;
		this.out = out;
	}

	public XlsTemplateRenderer(byte[] master, InputStream in, OutputStream out) {
		super();
		this.master = master;
		this.in = in;
		this.out = out;
	}
	public void run() {
		try {
			XlsEngine engine = new XlsEngine();
			XlsTemplate template = new XlsTemplate(in);
			InputStream masterIn = null;
			if (master != null) {
				masterIn = new ByteArrayInputStream(master);
			} else if (this.template != null && this.template.getFile() != null) {
				masterIn = new ByteArrayInputStream(this.template.getFile());
			}
			engine.render(masterIn, template, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			log.error("Error", e);
		}
	}
}
