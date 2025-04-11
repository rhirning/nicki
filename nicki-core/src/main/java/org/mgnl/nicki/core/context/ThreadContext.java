
package org.mgnl.nicki.core.context;

/*-
 * #%L
 * nicki-core
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


import java.util.Locale;

// TODO: Auto-generated Javadoc
/**
 * The Class ThreadContext.
 */
public class ThreadContext {
    
    /** The thread local variable holding the current context. */
    private static AppThreadLocal localContext = new AppThreadLocal();

    /**
     * Get the current context of this thread.
     *
     * @return single instance of ThreadContext
     */
    public static Context getInstance() {
        return localContext.get();
    }
    
	/**
	 * Gets the request.
	 *
	 * @return the request
	 */
	public static Object getRequest() {
		return getInstance().getRequest();
	}

	/**
	 * Sets the request.
	 *
	 * @param request the new request
	 */
	public static void setRequest(Object request) {
		getInstance().setRequest(request);
	}

	/**
	 * Gets the locale.
	 *
	 * @return the locale
	 */
	public static Locale getLocale() {
		return getInstance().getLocale();
	}

	/**
	 * Sets the locale.
	 *
	 * @param locale the new locale
	 */
	public static void setLocale(Locale locale) {
		getInstance().setLocale(locale);
	}
}
