/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
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
