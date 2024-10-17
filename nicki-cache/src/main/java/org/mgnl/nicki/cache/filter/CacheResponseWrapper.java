
package org.mgnl.nicki.cache.filter;

/*-
 * #%L
 * nicki-cache
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
 * A response wrapper which records the status, headers and content.
 *
 * @version $Id$
 */
public class CacheResponseWrapper extends HttpServletResponseWrapper {

	public static final int DEFAULT_THRESHOLD = 500 * 1024;

	private final ServletOutputStream wrappedStream;
	private PrintWriter wrappedWriter;
	private final MultiValuedMap<String, Object> headers = new ArrayListValuedHashMap<>();
	private int status = SC_OK;
	private boolean isError;
	private String redirectionLocation;
	private ByteArrayOutputStream inMemoryBuffer;

	private int contentLength;

	public CacheResponseWrapper(final HttpServletResponse response) {
		super(response);
		inMemoryBuffer = new ByteArrayOutputStream();
		wrappedStream = new SimpleServletOutputStream(inMemoryBuffer);
	}

	public byte[] getBufferedContent() {
		return inMemoryBuffer.toByteArray();
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return wrappedStream;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (wrappedWriter == null) {
			String encoding = getCharacterEncoding();
			wrappedWriter = encoding != null ? new PrintWriter(new OutputStreamWriter(getOutputStream(), encoding))
					: new PrintWriter(new OutputStreamWriter(getOutputStream()));
		}

		return wrappedWriter;
	}

	@Override
	public void flushBuffer() throws IOException {
		flush();
	}

	public void flush() throws IOException {
		wrappedStream.flush();

		if (wrappedWriter != null) {
			wrappedWriter.flush();
		}
	}

	@Override
	public void reset() {
		super.reset();
		wrappedWriter = null;
		status = SC_OK;

		headers.clear();
	}

	@Override
	public void resetBuffer() {
		super.resetBuffer();
		wrappedWriter = null;
	}

	@Override
	public int getStatus() {
		return status;
	}

	public boolean isError() {
		return isError;
	}

	public MultiValuedMap<String, Object> getHeaders() {
		return headers;
	}

	public String getRedirectionLocation() {
		return redirectionLocation;
	}

	@Override
	public void setDateHeader(String name, long date) {
		replaceHeader(name, Long.valueOf(date));
	}

	@Override
	public void addDateHeader(String name, long date) {
		appendHeader(name, Long.valueOf(date));
	}

	@Override
	public void setHeader(String name, String value) {
		replaceHeader(name, value);
	}

	@Override
	public void addHeader(String name, String value) {
		appendHeader(name, value);
	}

	@Override
	public void setIntHeader(String name, int value) {
		replaceHeader(name, Integer.valueOf(value));
	}

	@Override
	public void addIntHeader(String name, int value) {
		appendHeader(name, Integer.valueOf(value));
	}

	@Override
	public boolean containsHeader(String name) {
		return headers.containsKey(name);
	}

	private void replaceHeader(String name, Object value) {
		headers.remove(name);
		headers.put(name, value);
	}

	private void appendHeader(String name, Object value) {
		headers.put(name, value);
	}

	@Override
	public void setStatus(int status) {
		this.status = status;
	}

	/*
	 * @Override public void setStatus(int status, String string) { this.status =
	 * status; }
	 */
	@Override
	public void sendRedirect(String location) throws IOException {
		status = SC_MOVED_TEMPORARILY;
		redirectionLocation = location;
	}

	@Override
	public void sendError(int status, String errorMsg) throws IOException {
		this.status = status;
		isError = true;
	}

	@Override
	public void sendError(int status) throws IOException {
		this.status = status;
		isError = true;
	}

	@Override
	public void setContentLength(int len) {
		contentLength = len;
	}

	public int getContentLength() {
		return contentLength >= 0 ? (int) contentLength : inMemoryBuffer.size();
	}

}
