/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.portlets.iframe;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.codec.binary.Base64;
import org.mgnl.nicki.novell.userapp.jaas.UserAppAdapter;

public class IframePortlet extends GenericPortlet {

	@Override
	protected void doView(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
		include(request, response, "/WEB-INF/jsp/nickiIframe.jsp");
	}


	private void include(RenderRequest request, RenderResponse response,
			String pageName) throws PortletException {
		response.setContentType(request.getResponseContentType());
		request.setAttribute("javax.portlet.request", request);
		request.setAttribute("nickiUrl", getUrl(request));
		if (pageName == null || pageName.length() == 0) {
			throw new NullPointerException("null or empty page name");
		}
		try {
			PortletRequestDispatcher dispatcher = getPortletContext()
					.getRequestDispatcher(pageName);
			dispatcher.include(request, response);
		} catch (IOException ioe) {
			throw new PortletException(ioe);
		}
	}
	
	private String getUrl(RenderRequest request) {
		  PortletPreferences pref = request.getPreferences();
		  String url = pref.getValue("url", "");
		  UserAppAdapter uaa = new UserAppAdapter();
		  String user = uaa.getName(request);
		  char passwd[] = uaa.getPassword(request);
		  String args = "?nickiName=" + new String(Base64.encodeBase64(user.getBytes())) + "&nickiPassword="
				  + new String(Base64.encodeBase64(new String(passwd).getBytes()));
		  return url + args;
	}
}