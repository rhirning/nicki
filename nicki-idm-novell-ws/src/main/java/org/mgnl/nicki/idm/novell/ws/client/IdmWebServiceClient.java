package org.mgnl.nicki.idm.novell.ws.client;

import java.io.Serializable;
import java.rmi.RemoteException;

import javax.naming.InitialContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.mgnl.nicki.core.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.novell.soa.af.impl.soap.AdminException;
import com.novell.soa.af.impl.soap.ProcessArray;
import com.novell.soa.af.impl.soap.Provisioning;
import com.novell.soa.af.impl.soap.ProvisioningService;
import com.novell.soa.ws.portable.Stub;

@SuppressWarnings("serial")
public class IdmWebServiceClient implements Serializable {
	private static final Logger LOG = LoggerFactory.getLogger(IdmWebServiceClient.class);
	
	private static String wsdl		= Config.getProperty("nicki.idm.novell.ws.wsdl");
	private static String user		= Config.getProperty("nicki.idm.novell.ws.user");
	private static String password	= Config.getProperty("nicki.idm.novell.ws.password");

	private ProvisioningService service;
	private Provisioning prov;

	static {
	    //for localhost testing only
	    javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
	    new javax.net.ssl.HostnameVerifier(){

	        public boolean verify(String hostname,
	                javax.net.ssl.SSLSession sslSession) {
	            if (hostname.equals("localhost")) {
	                return true;
	            }
	            return true;
	        }
	    });
	}

	public static IdmWebServiceClient getInstance() {
		IdmWebServiceClient instance = new IdmWebServiceClient();
		instance.init();
		return instance;
	}

	
	private IdmWebServiceClient()  {
	}
	
	
	private void init() {
		
		try {
			if (service == null) {
				InitialContext ctx = new InitialContext();
				service = (ProvisioningService) ctx
						.lookup("xmlrpc:soap:com.novell.soa.af.impl.soap.ProvisioningService");
				prov = service.getProvisioningPort();
				Stub stub = (Stub) prov;
				// set username and password
				stub._setProperty(Stub.USERNAME_PROPERTY, user);
				stub._setProperty(Stub.PASSWORD_PROPERTY, password);
				// set the endpoint URL
				stub._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY, wsdl);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public ProcessArray getAllProcesses() throws RemoteException, AdminException {
		return prov.getAllProcesses();

	}

}