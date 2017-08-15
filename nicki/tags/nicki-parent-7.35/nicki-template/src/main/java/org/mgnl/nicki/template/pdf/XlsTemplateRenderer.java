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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.mgnl.nicki.dynamic.objects.objects.EngineTemplate;
import org.mgnl.nicki.xls.engine.XlsEngine;
import org.mgnl.nicki.xls.template.XlsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XlsTemplateRenderer extends Thread implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(XlsTemplateRenderer.class);
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
			LOG.error("Error", e);
		}
	}
}
