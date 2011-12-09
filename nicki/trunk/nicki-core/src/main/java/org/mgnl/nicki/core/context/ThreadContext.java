/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.core.context;

import java.util.Locale;

public class ThreadContext {
    /**
     * The thread local variable holding the current context
     */
    private static AppThreadLocal localContext = new AppThreadLocal();

    /**
     * Get the current context of this thread.
     */
    public static Context getInstance() {
        return localContext.get();
    }
    
	public static Object getRequest() {
		return getInstance().getRequest();
	}

	public static void setRequest(Object request) {
		getInstance().setRequest(request);
	}

	public static Object getResponse() {
		return getInstance().getResponse();
	}

	public static void setResponse(Object response) {
		getInstance().setResponse(response);
	}

	public static Locale getLocale() {
		return getInstance().getLocale();
	}

	public static void setLocale(Locale locale) {
		getInstance().setLocale(locale);
	}
}
