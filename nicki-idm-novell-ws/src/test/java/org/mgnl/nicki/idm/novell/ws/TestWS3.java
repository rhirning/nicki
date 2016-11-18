package org.mgnl.nicki.idm.novell.ws;

import org.mgnl.nicki.idm.novell.ws.client.IdmWebServiceClient;

import com.novell.soa.af.impl.soap.AdminException;
import com.novell.soa.af.impl.soap.Process;
import com.novell.soa.af.impl.soap.ProcessArray;

public class TestWS3 {

	public static void main(String[] args) {
		IdmWebServiceClient client = IdmWebServiceClient.getInstance();
		
		try {
			listProcesses(client);
		} catch (AdminException ex) {
			System.out.println("command failed: " + ex.getReason());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void listProcesses(IdmWebServiceClient client) throws Exception {

		ProcessArray array = client.getAllProcesses();
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