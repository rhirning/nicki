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
