
package org.mgnl.nicki.portlets;

/*-
 * #%L
 * nicki-portlets
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


/**
 * The Class IframePortlet.
 */
public class IframePortlet extends GenericPortlet {

	/**
	 * Do view.
	 *
	 * @param request the request
	 * @param response the response
	 * @throws PortletException the portlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	protected void doView(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
		include(request, response, "/WEB-INF/jsp/nickiIframe.jsp");
	}


	/**
	 * Include.
	 *
	 * @param request the request
	 * @param response the response
	 * @param pageName the page name
	 * @throws PortletException the portlet exception
	 */
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
	
	/**
	 * Gets the app.
	 *
	 * @param request the request
	 * @return the app
	 */
	private String getApp(RenderRequest request) {
		  PortletPreferences pref = request.getPreferences();
		  String url = pref.getValue("url", "");
		  return url;
	}
	
	/**
	 * Gets the parameters.
	 *
	 * @param request the request
	 * @return the parameters
	 */
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
	
	/**
	 * Gets the parameters map.
	 *
	 * @param request the request
	 * @return the parameters map
	 */
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
	
	/**
	 * Gets the url.
	 *
	 * @param request the request
	 * @return the url
	 */
	private String getUrl(RenderRequest request) {
		  return getApp(request) + "?" + getParameters(request);
	}
}
