package org.mgnl.nicki.vaadin.base.application;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.VaadinServlet;

public class NickiServlet extends VaadinServlet {
	private static final Logger LOG = LoggerFactory.getLogger(NickiServlet.class);
	private static final long serialVersionUID = 6114917183362066306L;
	public static final String NICKI_PARAMETERS = "NICKI_PARAMETERS";
	private static final String[] PARAMETERS = {
		"nickiToken", "nickiName", "nickiPassword"
	};

	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (request.getSession().getAttribute(NICKI_PARAMETERS) == null) {
			Map<String, String> map = new HashMap<String, String>();
			for (String param : PARAMETERS) {
				if (StringUtils.isNotBlank(request.getParameter(param))) {
					map.put(param, request.getParameter(param));
				}
			}
			request.getSession().setAttribute(NICKI_PARAMETERS, map);
			LOG.debug("ParameterMap: " + map);
		}
		super.service(request, response);
	}


}
