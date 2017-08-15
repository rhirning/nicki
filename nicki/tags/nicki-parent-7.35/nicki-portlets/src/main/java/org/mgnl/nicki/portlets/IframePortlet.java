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
package org.mgnl.nicki.portlets;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.codec.binary.Base64;
import org.mgnl.nicki.core.auth.SSOAdapter.TYPE;
import org.mgnl.nicki.idm.novell.jaas.UserAppAdapter;

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
		request.setAttribute("nickiApp", getApp(request));
		request.setAttribute("nickiParameters", getParameters(request));
		request.setAttribute("nickiParametersMap", getParametersMap(request));
		if (pageName == null || pageName.length() == 0) {
			throw new PortletException("null or empty page name");
		}
		try {
			PortletRequestDispatcher dispatcher = getPortletContext()
					.getRequestDispatcher(pageName);
			dispatcher.include(request, response);
		} catch (IOException ioe) {
			throw new PortletException(ioe);
		}
	}
	
	private String getApp(RenderRequest request) {
		  PortletPreferences pref = request.getPreferences();
		  String url = pref.getValue("url", "");
		  return url;
	}
	
	private String getParameters(RenderRequest request) {
		  UserAppAdapter uaa = new UserAppAdapter();
		  uaa.setRequest(request);
		  String user = uaa.getName();
		  char passwd[] = uaa.getPassword();
		  
		  String credentials = new String(passwd);
		  
		  String args;
		  
		  if (uaa.getType() == TYPE.SAML) {
			  args = "nickiToken=" + credentials;
		  } else {
			  args = "nickiName=" + new String(Base64.encodeBase64(user.getBytes())) + "&nickiPassword="
				  + new String(Base64.encodeBase64(credentials.getBytes()));
		  }
		  return args;
	}
	@SuppressWarnings("rawtypes")
	private Map getParametersMap(RenderRequest request) {
		Map<String, String> map = new HashMap<String, String>();
		  UserAppAdapter uaa = new UserAppAdapter();
		  uaa.setRequest(request);
		  String user = uaa.getName();
		  char passwd[] = uaa.getPassword();
		  
		  String credentials = new String(passwd);
		  		  
		  if (uaa.getType() == TYPE.SAML) {
			  map.put("nickiToken", credentials);
		  } else {
			  map.put("nickiName=", new String(Base64.encodeBase64(user.getBytes())));
			  map.put("nickiPassword", new String(Base64.encodeBase64(credentials.getBytes())));
		  }
		  return map;
	}
	
	private String getUrl(RenderRequest request) {
		  return getApp(request) + "?" + getParameters(request);
	}
}