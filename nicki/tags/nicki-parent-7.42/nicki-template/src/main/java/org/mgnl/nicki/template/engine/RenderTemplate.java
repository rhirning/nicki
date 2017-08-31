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
package org.mgnl.nicki.template.engine;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Template;

public class RenderTemplate extends Thread implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(RenderTemplate.class);
	public final static String DEFAULT_CHARSET = "UTF-8";
	Template template;
	Map<String, Object> dataModel;
	OutputStream out;
	String charset;
	
	public RenderTemplate(Template template, Map<String, Object> dataModel,
			OutputStream out, String charset) {
		super();
		this.template = template;
		this.dataModel = dataModel;
		this.out = out;
		if (Charset.isSupported(charset)) {
			this.charset = charset; 
		} else {
			this.charset = DEFAULT_CHARSET;
		}
	}

	public void run() {
		try {
			template.process(dataModel, new OutputStreamWriter(out, charset));
			out.flush();
			out.close();
		} catch (Exception e) {
			LOG.error("Error", e);
		}
	}

}
