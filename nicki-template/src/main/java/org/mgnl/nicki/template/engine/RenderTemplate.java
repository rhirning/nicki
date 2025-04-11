
package org.mgnl.nicki.template.engine;

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


import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Map;

import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;

// TODO: Auto-generated Javadoc
/**
 * The Class RenderTemplate.
 */
@Slf4j
public class RenderTemplate extends Thread implements Runnable {
	
	/** The Constant DEFAULT_CHARSET. */
	public final static String DEFAULT_CHARSET = "UTF-8";
	
	/** The template. */
	Template template;
	
	/** The data model. */
	Map<String, Object> dataModel;
	
	/** The out. */
	OutputStream out;
	
	/** The charset. */
	String charset;
	
	/**
	 * Instantiates a new render template.
	 *
	 * @param template the template
	 * @param dataModel the data model
	 * @param out the out
	 * @param charset the charset
	 */
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

	/**
	 * Run.
	 */
	public void run() {
		try {
			template.process(dataModel, new OutputStreamWriter(out, charset));
			out.flush();
			out.close();
		} catch (Exception e) {
			log.error("Error", e);
		}
	}

}
