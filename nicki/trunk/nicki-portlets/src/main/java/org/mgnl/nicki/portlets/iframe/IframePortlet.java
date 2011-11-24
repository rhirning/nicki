package org.mgnl.nicki.portlets.iframe;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

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
}