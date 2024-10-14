
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


import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.MultiValuedMap;

public class CachedEntry implements Serializable {

	private static final long serialVersionUID = -6408276740586015355L;
	private final String contentType;
    private final String characterEncoding;
    private final int statusCode;
    private transient MultiValuedMap<String, Object> headers;
    private final byte[] plainContent;
	
	
	public CachedEntry(byte[] out, String contentType, String characterEncoding, int statusCode, MultiValuedMap<String, Object> headers) throws IOException {
		this.plainContent = out;
        this.contentType = contentType;
        this.characterEncoding = characterEncoding;
        this.statusCode = statusCode;
        this.headers = headers;
    }
	
    public void replay(HttpServletResponse response) throws IOException {
        response.setStatus(getStatusCode());

        addHeaders(response);

        response.setContentType(getContentType());
        response.setCharacterEncoding(getCharacterEncoding());

        writeContent(response);
    }
	
	@SuppressWarnings("rawtypes")
	protected void addHeaders(final HttpServletResponse response) {
        final MultiValuedMap<String, Object> headers = getHeaders();

        for (Object key : headers.keySet()) {
	        final String header = (String) key;

	        if ("Content-Encoding".equals(header) || "Vary".equals(header)) {
                continue;
            }
	        
            if (response.containsHeader(header)) {
                // do not duplicate headers.
                continue;
            }

            final Collection values = (Collection) headers.get(header);
            for(Object val :  values) {
                setHeader(response, header, val);
            }
        }
    }
	
	protected void writeContent(HttpServletResponse response) throws IOException  {
        final byte[] body = getPlainContent();

        response.setContentLength(body.length);
        response.getOutputStream().write(body);
    }

	public void setHeader(final HttpServletResponse response,
			final String name, final Object value) {
		if (value instanceof Long) {
			response.addDateHeader(name, ((Long) value).longValue());
		} else if (value instanceof Integer) {
			response.addIntHeader(name, ((Integer) value).intValue());
		} else if (value instanceof String) {
			response.addHeader(name, (String) value);
		} else {
			throw new IllegalStateException("Unrecognized type for header ["
					+ name + "], value is: " + value);
		}
	}

    public String getContentType() {
        return contentType;
    }

    public String getCharacterEncoding() {
        return characterEncoding;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public MultiValuedMap<String, Object> getHeaders() {
        return headers;
    }

    public byte[] getPlainContent() {
        return plainContent;
    }


}
