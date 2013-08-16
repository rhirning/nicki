/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.template.engine;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Map;

import freemarker.template.Template;

public class RenderTemplate extends Thread implements Runnable {
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
			e.printStackTrace();
		}
	}

}
