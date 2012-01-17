/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
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
 */
package org.mgnl.nicki.template.engine;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import freemarker.template.Template;

public class RenderTemplate extends Thread implements Runnable {
	Template template;
	Map<String, Object> dataModel;
	OutputStream out;
	
	public RenderTemplate(Template template, Map<String, Object> dataModel,
			OutputStream out) {
		super();
		this.template = template;
		this.dataModel = dataModel;
		this.out = out;
	}

	public void run() {
		try {
			template.process(dataModel, new OutputStreamWriter(out, "UTF-8"));
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
