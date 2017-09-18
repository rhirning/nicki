
package org.mgnl.nicki.spnego;

/*-
 * #%L
 * nicki-spnego
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

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Class adds capability to track/determine if the HTTP Status 
 * code has been set.
 * 
 * <p>
 * Also allows the ability to set the content length to zero 
 * and flush the buffer immediately after setting the HTTP 
 * Status code.
 * </p>
 * 
 * @author Darwin V. Felix
 * 
 */
public final class SpnegoHttpServletResponse extends HttpServletResponseWrapper {

    private transient boolean statusSet = false;

    /**
     * 
     * @param response
     */
    public SpnegoHttpServletResponse(final HttpServletResponse response) {
        super(response);
    }

    /**
     * Tells if setStatus has been called.
     * 
     * @return true if HTTP Status code has been set
     */
    public boolean isStatusSet() {
        return this.statusSet;
    }

    @Override
    public void setStatus(final int status) {
        super.setStatus(status);
        this.statusSet = true;
    }

    /**
     * Sets the HTTP Status Code and optionally set the the content 
     * length to zero and flush the buffer.
     * 
     * @param status http status code
     * @param immediate set to true to set content len to zero and flush
     * @throws IOException 
     * 
     * @see #setStatus(int)
     */
    public void setStatus(final int status, final boolean immediate) throws IOException {
        setStatus(status);
        if (immediate) {
            setContentLength(0);
            flushBuffer();
        }
    }
}
