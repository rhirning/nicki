package org.mgnl.nicki.vaadin.base.application;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.context.AppContext;

import com.vaadin.server.VaadinServlet;

public class NickiServlet extends VaadinServlet {
	private static final long serialVersionUID = 6114917183362066306L;
	private static final String[] PARAMETERS = {
		"nickiToken", "nickiName", "nickiPassword"
	};

	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		Map<String, String> map = new HashMap<String, String>();
		for (String param : PARAMETERS) {
			if (StringUtils.isNotBlank(request.getParameter(param))) {
				map.put(param, request.getParameter(param));
			}
		}

		AppContext.setRequestParameters(map);

		for (String paramName : map.keySet()) {

			System.out.println(paramName + "=" + map.get(paramName));
		}
		// TODO Auto-generated method stub
		super.service(request, response);
	}


}
