package org.mgnl.nicki.core.auth;

import java.util.List;
import java.util.Map;

import javax.xml.ws.handler.MessageContext;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.auth.BasicAuthAdapter;
import org.mgnl.nicki.core.auth.SSOAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServiceSecurityAdapter extends BasicAuthAdapter implements SSOAdapter {

	private static Logger LOG = LoggerFactory.getLogger(WebServiceSecurityAdapter.class);
	
	@Override
	protected String getAuthPart(int num) {
		try {
			if (getRequest() instanceof MessageContext  ) {
				MessageContext   messageContext = (MessageContext ) getRequest();
				
				Map<?,?> http_headers = (Map<?,?>) messageContext.get(MessageContext.HTTP_REQUEST_HEADERS);
		        List<?> authList = (List<?>) http_headers.get("Authorization");
		        
				String header = authList.get(0).toString();
				String encodedCredentials = StringUtils.substringAfter(header, " ");
				return decode(encodedCredentials)[num];
			}
		} catch (Exception e) {
			LOG.error("Error reading Basic Authentication data", e);
		}
		return null;
	}
}
