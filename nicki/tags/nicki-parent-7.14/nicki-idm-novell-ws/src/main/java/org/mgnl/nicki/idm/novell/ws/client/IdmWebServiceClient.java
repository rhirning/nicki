package org.mgnl.nicki.idm.novell.ws.client;

import java.io.Serializable;
import java.rmi.RemoteException;

import javax.naming.InitialContext;
import org.mgnl.nicki.core.config.Config;
import com.novell.soa.af.impl.soap.AdminException;
import com.novell.soa.af.impl.soap.Process;
import com.novell.soa.af.impl.soap.ProcessArray;
import com.novell.soa.af.impl.soap.Provisioning;
import com.novell.soa.af.impl.soap.ProvisioningService;
import com.novell.soa.af.impl.soap.T_Operator;
import com.novell.soa.af.impl.soap.T_ProcessInfoQuery;
import com.novell.soa.af.impl.soap.T_ProcessStatus;
import com.novell.soa.af.impl.soap.T_TerminationType;
import com.novell.soa.ws.portable.Stub;

@SuppressWarnings("serial")
public class IdmWebServiceClient implements Serializable {

	private static String wsdl = Config.getProperty("nicki.idm.novell.ws.wsdl");
	private static String user = Config.getProperty("nicki.idm.novell.ws.user");
	private static String password = Config.getProperty("nicki.idm.novell.ws.password");

	private ProvisioningService service;
	private Provisioning prov;

	static {
		// for localhost testing only
		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(new javax.net.ssl.HostnameVerifier() {

			public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
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

	private IdmWebServiceClient() {
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

	/**
	 * Used to get information about all running and completed provisioning
	 * requests.
	 * 
	 * @return
	 * @throws RemoteException
	 * @throws AdminException
	 */
	public ProcessArray getAllProcesses() throws RemoteException, AdminException {
		return prov.getAllProcesses();

	}

	/**
	 * Used to get information about processes.
	 * 
	 * @param query
	 * @param maxRecords
	 * @return
	 * @throws RemoteException
	 * @throws AdminException
	 */
	public ProcessArray getProcessesByQuery(T_ProcessInfoQuery query, int maxRecords)
			throws RemoteException, AdminException {
		return prov.getProcessesByQuery(query, maxRecords);

	}

	/**
	 * Used to get information about processes with a specified status (for
	 * example, running processes).
	 * 
	 * @param processStatus
	 * @return
	 * @throws RemoteException
	 * @throws AdminException
	 */
	public ProcessArray getProcessesByStatus(T_ProcessStatus processStatus) throws RemoteException, AdminException {
		return prov.getProcessesByStatus(processStatus);

	}

	/**
	 * Used to get information about processes, specified by processID.
	 * 
	 * @param id
	 * @param time
	 * @param operator
	 * @param initiator
	 * @param recipient
	 * @return
	 * @throws RemoteException
	 * @throws AdminException
	 */
	public ProcessArray getProcesses(String id, long time, T_Operator operator, String initiator, String recipient)
			throws RemoteException, AdminException {
		return prov.getProcesses(id, time, operator, initiator, recipient);

	}

	/**
	 * Used to limit the number of processes returned. If the limit you specify
	 * is less than the system limit, the number you specify is returned. If you
	 * exceed the system limit, the Workflow Engine returns the system limit. If
	 * the limit you specify is less than or equal to 0, the Workflow Engine
	 * returns all processes.
	 * 
	 * @param maxRecords
	 * @return
	 * @throws RemoteException
	 * @throws AdminException
	 */
	public ProcessArray getProcessesArray(int maxRecords) throws RemoteException, AdminException {
		return prov.getProcessesArray(maxRecords);

	}

	/**
	 * Used to get information about a specific process, specified by the
	 * Process Id.
	 * 
	 * @param id
	 * @return
	 * @throws RemoteException
	 * @throws AdminException
	 */
	public ProcessArray getProcessesById(String id) throws RemoteException, AdminException {
		return prov.getProcessesById(id);

	}

	/**
	 * Used to terminate a running provisioning request.
	 * 
	 * @param requestId
	 * @param state
	 * @param comment
	 * @throws RemoteException
	 * @throws AdminException
	 */
	public void terminate(String requestId, T_TerminationType state, String comment)
			throws RemoteException, AdminException {
		prov.terminate(requestId, state, comment);

	}

	/**
	 * Used to get information about a running or completed provisioning
	 * request, specified by Request ID.
	 * 
	 * @param requestId
	 * @return
	 * @throws RemoteException
	 * @throws AdminException
	 */
	public Process getProcess(String requestId) throws RemoteException, AdminException {
		return prov.getProcess(requestId);
	}

	/**
	 * Used to get information about processes created between the current time
	 * and the time at which the workflow process was created.
	 * 
	 * @param time
	 * @param op
	 * @return
	 * @throws RemoteException
	 * @throws AdminException
	 */
	public ProcessArray getProcessesByCreationTime(long time, T_Operator op) throws RemoteException, AdminException {
		return prov.getProcessesByCreationTime(time, op);

	}

}