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
