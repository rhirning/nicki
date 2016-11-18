package org.mgnl.nicki.idm.novell.ws;

import javax.naming.InitialContext;
import com.novell.soa.af.impl.soap.AdminException;
import com.novell.soa.af.impl.soap.Process;
import com.novell.soa.af.impl.soap.ProcessArray;
import com.novell.soa.af.impl.soap.Provisioning;
import com.novell.soa.af.impl.soap.ProvisioningService;
import com.novell.soa.ws.portable.Stub;

public class TestWS2 {
	private static final String USERNAME = "cn=padmin,ou=users,o=utopia";
	private static final String PASSWORD = "N0v3ll";

	public static void main(String[] args) {
		try {
			String url = args.length > 0 ? args[0] : "http://172.17.2.91:8080/IDM/provisioning/service";
			listProcesses(url);
		} catch (AdminException ex) {
			System.out.println("command failed: " + ex.getReason());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void listProcesses(String url) throws Exception {
		// get the stub
		InitialContext ctx = new InitialContext();
		ProvisioningService service = (ProvisioningService) ctx
				.lookup("xmlrpc:soap:com.novell.soa.af.impl.soap.ProvisioningService");
		Provisioning prov = service.getProvisioningPort();
		Stub stub = (Stub) prov;
		// set username and password
		stub._setProperty(Stub.USERNAME_PROPERTY, USERNAME);
		stub._setProperty(Stub.PASSWORD_PROPERTY, PASSWORD);
		// set the endpoint URL
		stub._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY, url);
		// invoke the getAllProcesses method
		ProcessArray array = prov.getAllProcesses();
		Process[] procs = array.getProcess();
		// print process array
		System.out.println("list of all processes:");
		if (procs != null) {
			for (int i = 0; i < procs.length; i++) {
				System.out.println(" process with request identifier " + procs[i].getRequestId());
				System.out.println(" initiator = " + procs[i].getInitiator());
				System.out.println(" recipient = " + procs[i].getRecipient());
				System.out.println(" processId = " + procs[i].getProcessId());
				System.out.println(" created = " + procs[i].getCreationTime().getTime());
				if (null != procs[i].getCompletionTime()) {
					System.out.println(" completed = " + procs[i].getCompletionTime().getTime());
				}
				System.out.println(" approval status = " + procs[i].getApprovalStatus());
				System.out.println(" process status = " + procs[i].getProcessStatus());
				if (i != procs.length - 1)
					System.out.println();
			}
		}
	}
}