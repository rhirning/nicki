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
package org.mgnl.nicki.core.helper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamConverter extends Thread implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(StreamConverter.class);
	InputStream in;
	OutputStream out;
	Charset charsetIn;
	Charset charsetOut;

	public StreamConverter(InputStream in, OutputStream out,
			Charset charsetIn, Charset charsetOut) {
		super();
		this.in = in;
		this.out = out;
		this.charsetIn = charsetIn;
		this.charsetOut = charsetOut;
	}

	public void run() {
		try {
			byte[] byteArray = IOUtils.toByteArray(in);
			String string = new String(byteArray, charsetIn);
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(string.getBytes(charsetOut));
			IOUtils.copy(byteArrayInputStream, out);
			out.flush();
			out.close();
		} catch (IOException e) {
			LOG.error("Error", e);
		}
	}
}
