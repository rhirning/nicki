package org.mgnl.nicki.idm.novell.ws;

import java.io.Serializable;
import java.rmi.RemoteException;

import javax.naming.InitialContext;
import org.mgnl.nicki.core.config.Config;
import com.novell.soa.af.impl.soap.AdminException;
import com.novell.soa.af.impl.soap.CommentArray;
import com.novell.soa.af.impl.soap.Configuration;
import com.novell.soa.af.impl.soap.DataItemArray;
import com.novell.soa.af.impl.soap.Definition;
import com.novell.soa.af.impl.soap.EngineState;
import com.novell.soa.af.impl.soap.EngineStateArray;
import com.novell.soa.af.impl.soap.Process;
import com.novell.soa.af.impl.soap.ProcessArray;
import com.novell.soa.af.impl.soap.ProcessFlowXml;
import com.novell.soa.af.impl.soap.Provisioning;
import com.novell.soa.af.impl.soap.ProvisioningRequestArray;
import com.novell.soa.af.impl.soap.ProvisioningService;
import com.novell.soa.af.impl.soap.ProvisioningStatusArray;
import com.novell.soa.af.impl.soap.Quorum;
import com.novell.soa.af.impl.soap.SignaturePropertyArray;
import com.novell.soa.af.impl.soap.StringArray;
import com.novell.soa.af.impl.soap.T_Action;
import com.novell.soa.af.impl.soap.T_ApprovalStatus;
import com.novell.soa.af.impl.soap.T_CommentType;
import com.novell.soa.af.impl.soap.T_EntitlementState;
import com.novell.soa.af.impl.soap.T_EntitlementStatus;
import com.novell.soa.af.impl.soap.T_Operator;
import com.novell.soa.af.impl.soap.T_ProcessInfoQuery;
import com.novell.soa.af.impl.soap.T_ProcessStatus;
import com.novell.soa.af.impl.soap.T_ProvisioningStatusQuery;
import com.novell.soa.af.impl.soap.T_TerminationType;
import com.novell.soa.af.impl.soap.T_Version;
import com.novell.soa.af.impl.soap.T_WorkEntryQuery;
import com.novell.soa.af.impl.soap.TypedDataItemArray;
import com.novell.soa.af.impl.soap.WorkEntryArray;
import com.novell.soa.ws.portable.Stub;

@SuppressWarnings("serial")
public class IdmProvisioningWebServiceClient implements Serializable {
	/*
	 * pnw.idm.novell.ws.provisioning.wsdl = http://172.17.2.91:8180/IDMProv/provisioning/service?wsdl
	 * pnw.idm.novell.ws.provisioning.user = cn=uaadmin,ou=sa,o=data
	 * pnw.idm.novell.ws.provisioning.password = netiq000
	 */

	private static String wsdl = Config.getString("pnw.idm.novell.ws.provisioning.wsdl");
	private static String user = Config.getString("pnw.idm.novell.ws.provisioning..user");
	private static String password = Config.getString("pnw.idm.novell.ws.provisioning.password");

	private static IdmProvisioningWebServiceClient instance;

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

	public static synchronized IdmProvisioningWebServiceClient getInstance() {
		if (instance == null) {
			instance = new IdmProvisioningWebServiceClient();
			instance.init();
		}
		return instance;
	}

	private IdmProvisioningWebServiceClient() {
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
	 * Used to get information about processes with a specified status (for example,
	 * running processes).
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
	 * Used to limit the number of processes returned. If the limit you specify is
	 * less than the system limit, the number you specify is returned. If you exceed
	 * the system limit, the Workflow Engine returns the system limit. If the limit
	 * you specify is less than or equal to 0, the Workflow Engine returns all
	 * processes.
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
	 * Used to get information about a specific process, specified by the Process
	 * Id.
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
	 * Used to get information about a running or completed provisioning request,
	 * specified by Request ID.
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
	 * Used to get information about processes created between the current time and
	 * the time at which the workflow process was created.
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

	/**
	 * Used to add a comment to a workflow activity.
	 */
	public void addComment(String workId, String comment) throws AdminException, RemoteException {
		prov.addComment(workId, comment);
	}

	/**
	 * Clear the Novell Integration Manager (previously named exteNd Composer)
	 * caches.
	 */
	public void clearNIMCaches() throws AdminException, RemoteException {
		prov.clearNIMCaches();
	}

	/**
	 * Used to forward a task to the next activity in the workflow with the
	 * appropriate action (approve, deny, refuse).
	 * 
	 * @param wid
	 *            The work Id.
	 * @param action
	 *            The action to take (approve, deny, refuse).
	 * @param items
	 *            The data items required by the workflow.
	 * @param comment
	 *            The comment.
	 */
	public void forward(String wid, T_Action action, DataItemArray items, String comment)
			throws AdminException, RemoteException {
		prov.forward(wid, action, items, comment);
	}

	/**
	 * Used to forward a provisioning request. For example, this can be used by an
	 * administrator to force a user-facing activity to be approved, denied or
	 * refused.
	 * 
	 * @param wid
	 *            The work Id.
	 * @param action
	 *            The action to take (approve, deny, refuse).
	 * @param items
	 *            The data items required by the workflow.
	 * @param comment
	 *            The comment.
	 * @param proxyUser
	 *            The DN of the proxy user.
	 */
	public void forwardAsProxy(String wid, T_Action action, DataItemArray items, String comment, String proxyUser)
			throws AdminException, RemoteException {
		prov.forwardAsProxy(wid, action, items, comment, proxyUser);
	}

	/**
	 * Used to forward a provisioning request with a digital signature and digital
	 * signature properties. For example, this can be used by an administrator to
	 * force a user-facing activity to be approved, denied or refused.
	 * 
	 * @param wid
	 *            The work Id.
	 * @param action
	 *            The action to take (approve, deny, refuse).
	 * @param items
	 *            The data items required by the workflow.
	 * @param comment
	 *            The comment.
	 * @param digitalSignature
	 *            The digital signature.
	 * @param digitalSignaturePropertyArray
	 *            The digital signature property map.
	 * @param proxyUser
	 *            The DN of the proxy user.
	 */

	public void forwardAsProxyWithDigitalSignature(String wid, T_Action action, DataItemArray items, String comment,
			String digitalSignature, SignaturePropertyArray digitalSignaturePropertyArray, String proxyUser)
			throws AdminException, RemoteException {
		prov.forwardAsProxyWithDigitalSignature(wid, action, items, comment, digitalSignature,
				digitalSignaturePropertyArray, proxyUser);

	}

	/**
	 * Used to forward a provisioning request with a digital signature and optional
	 * digital signature properties. For example, this can be used by an
	 * administrator to force a user-facing activity to be approved, denied or
	 * refused.
	 * 
	 * @param wid
	 *            The work Id.
	 * @param action
	 *            The action to take (approve, deny, refuse).
	 * @param items
	 *            The data items required by the workflow.
	 * @param comment
	 *            The comment.
	 * @param digitalSignature
	 *            The digital signature.
	 * @param digitalSignaturePropertyArray
	 *            The digital signature property map.
	 * @param proxyUser
	 *            The DN of the proxy user.
	 */

	public void forwardWithDigitalSignature(String wid, T_Action action, DataItemArray items, String comment,
			String digitalSignature, SignaturePropertyArray digitalSignaturePropertyArray)
			throws AdminException, RemoteException {
		prov.forwardWithDigitalSignature(wid, action, items, comment, digitalSignature, digitalSignaturePropertyArray);
	}

	/**
	 * Used to return an array of available provisioning requests.
	 */

	public ProvisioningRequestArray getAllProvisioningRequests(String recipient)
			throws AdminException, RemoteException {
		return prov.getAllProvisioningRequests(recipient);
	}

	public EngineStateArray getClusterState() throws AdminException, RemoteException {
		return prov.getClusterState();
	}

	/**
	 * Used to get comments from a workflow.
	 * 
	 * @param workId
	 *            The activity Id (UUID).
	 * @param maxRecords
	 *            An integer specifying the maximum number of records to retrieve.
	 */

	public CommentArray getComments(String workId, int maxRecords) throws AdminException, RemoteException {
		return prov.getComments(workId, maxRecords);
	}

	/**
	 * Used to get the comments for a specific activity.
	 * 
	 * @param requestId
	 *            The process identifier.
	 * @param aid
	 *            The activity identifier.
	 */

	public CommentArray getCommentsByActivity(String requestId, String aid) throws AdminException, RemoteException {
		return prov.getCommentsByActivity(requestId, aid);
	}

	/**
	 * Used to get comments made at a specific time.
	 * 
	 * @param requestId
	 *            The process identifier.
	 * @param time
	 *            The time stamp.
	 * @param op
	 *            The query operator to use. Possible values for operator are: EQ -
	 *            equals LT - less than LE - less than or equal to GT - greater than
	 *            GE - greater than or equal to
	 */

	public CommentArray getCommentsByCreationTime(String requestId, long time, T_Operator op)
			throws AdminException, RemoteException {
		return prov.getCommentsByCreationTime(requestId, time, op);
	}

	/**
	 * Used to get workflow comments that are of a specific type (for example, user,
	 * system).
	 * 
	 * @param requestId
	 *            The process identifier.
	 * @param type
	 *            The comment type (USER or SYSTEM)
	 */

	public CommentArray getCommentsByType(String requestId, T_CommentType type) throws AdminException, RemoteException {
		return prov.getCommentsByType(requestId, type);
	}

	/**
	 * Used to get the comments made by a specific user.
	 * 
	 * @param requestId
	 *            The process identifier.
	 * @param user
	 *            The the DN of the user (recipient) who created the comments
	 */

	public CommentArray getCommentsByUser(String requestId, String user) throws AdminException, RemoteException {
		return prov.getCommentsByUser(requestId, user);
	}

	/**
	 * Used to get the timeout for completed processes.
	 */

	public int getCompletedProcessTimeout() throws AdminException, RemoteException {
		return prov.getCompletedProcessTimeout();
	}

	public TypedDataItemArray getDataItems(String workId) throws AdminException, RemoteException {
		return prov.getDataItems(workId);
	}

	public Definition getDefinitionByID(String definitionID, String recipient) throws AdminException, RemoteException {
		return prov.getDefinitionByID(definitionID, recipient);
	}

	/**
	 * Used to determine if global e-mail notifications are enabled or disabled.
	 */

	public boolean getEmailNotifications() throws AdminException, RemoteException {
		return prov.getEmailNotifications();
	}

	/**
	 * Used to get the workflow engine configuration parameters.
	 */

	public Configuration getEngineConfiguration() throws AdminException, RemoteException {
		return prov.getEngineConfiguration();
	}

	/**
	 * Used to get the IEngineState for a workflow engine, specified by engine Id.
	 * 
	 * @param engineId
	 *            The Id of the workfow engine.
	 */

	public EngineState getEngineState(String engineId) throws AdminException, RemoteException {
		return prov.getEngineState(engineId);
	}

	/**
	 * Used to get the XML for a provisioning request.
	 * 
	 * @param processId
	 *            The request Id.
	 */

	public String getFlowDefinition(String processId) throws AdminException, RemoteException {
		return prov.getFlowDefinition(processId);
	}

	/**
	 * Used to get the XML for a form for a provisioning request.
	 * 
	 * @param processId
	 *            The request Id.
	 */

	public String getFormDefinition(String processId) throws AdminException, RemoteException {
		return prov.getFormDefinition(processId);
	}

	/**
	 * Used to get a JPG image of the workflow. The Graphviz program must be
	 * installed on the computer where the application server and the IDM User
	 * Application is running. For more information about Graphviz, see Graphviz.
	 * 
	 * @param processId
	 *            The request Id.
	 */

	public byte[] getGraph(String processId) throws AdminException, RemoteException {
		return prov.getGraph(processId);
	}

	public ProcessFlowXml getProcessFlowXml(String arg0) throws AdminException, RemoteException {
		return prov.getProcessFlowXml(arg0);
	}

	/**
	 * Used to get information about processes with a specified approval status
	 * (Approved, Denied, or Retracted).
	 */

	public ProcessArray getProcessesByApprovalStatus(T_ApprovalStatus status) throws AdminException, RemoteException {
		return prov.getProcessesByApprovalStatus(status);
	}

	/**
	 * Used to get information about processes started between two specified times.
	 * 
	 * @param startTime
	 *            The start time (YYYY/MM/DD).
	 * @param endTime
	 *            The end time (YYYY/MM/DD).
	 */

	public ProcessArray getProcessesByCreationInterval(long startTime, long endTime)
			throws AdminException, RemoteException {
		return prov.getProcessesByCreationInterval(startTime, endTime);
	}

	/**
	 * Used to get information about processes that have a specific initiator Id.
	 */

	public ProcessArray getProcessesByInitiator(String initiator) throws AdminException, RemoteException {
		return prov.getProcessesByInitiator(initiator);
	}

	/**
	 * Used to get information about processes that have a specific recipient Id.
	 */

	public ProcessArray getProcessesByRecipient(String recipient) throws AdminException, RemoteException {
		return prov.getProcessesByRecipient(recipient);
	}

	/**
	 * Used to get the list of available provisioning categories.
	 */

	public StringArray getProvisioningCategories() throws AdminException, RemoteException {
		return prov.getProvisioningCategories();
	}

	/**
	 * Used to return an array of provisioning requests for a specified category and
	 * operation.
	 * 
	 * @param recipient
	 *            The recipient of the provisioning request.
	 * @param category
	 *            The category of the provisioning request.
	 * @param operation
	 *            The provisioning request operation (IProvisioningRequest:
	 *            0=Grant,1=Revoke, 2=Both)
	 * 
	 *            ACHTUNG: nicht vollständige und somit falsch!!
	 */

	public ProvisioningRequestArray getProvisioningRequests(String recipient, String category, String operation,
			String type) throws AdminException, RemoteException {
		return prov.getProvisioningRequests(recipient, category, operation, type);
	}

	/**
	 * Used to get the status of provisioning requests.
	 * 
	 * @param query
	 *            Used to specify the query used to retrieve the list of activities.
	 *            The query has the following components:
	 * 
	 *            <ul>
	 *            <li>choice - the parameters used to filter the results. You can
	 *            specify multiple parameters. The possible parameters are:
	 *            <ul>
	 *            <li>Recipient - a DN
	 *            <li>RequestID
	 *            <li>ActivityID
	 *            <li>Status (an integer)
	 *            <li>State (an integer)
	 *            <li>ProvisioningTime (YYYY/MM/DD)
	 *            <li>ResultTime (YYYY/MM/DD)
	 *            </ul>
	 * 
	 *            <li>logic - AND or OR
	 * 
	 *            <li>order - the order in which to sort the results. Possible
	 *            values for order are:
	 *            <ul>
	 *            <li>ACTIVITY_ID
	 *            <li>RECIPIENT
	 *            <li>PROVISIONING_TIME
	 *            <li>RESULT_TIME
	 *            <li>STATE STATUS
	 *            <li>REQUEST_ID
	 *            <li>MESSAGE
	 *            </ul>
	 *            </ul>
	 * @param maxRecords
	 *            Used to specify maximum number of records to retrieve. A value of
	 *            -1 returns unlimited records.
	 * @return
	 * @throws AdminException
	 * @throws RemoteException
	 */
	public ProvisioningStatusArray getProvisioningStatuses(T_ProvisioningStatusQuery query, int maxRecords)
			throws AdminException, RemoteException {
		return prov.getProvisioningStatuses(query, maxRecords);
	}

	/**
	 * Used to get information about the quorum for a workflow activity. A quorum
	 * must have actually been specified for the workflow activity by the workflow
	 * designer for this method to work.
	 * 
	 * @param workId
	 *            The Id of the task.
	 * @return
	 * @throws AdminException
	 * @throws RemoteException
	 */
	public Quorum getQuorumForWorkTask(String workId) throws AdminException, RemoteException {
		return prov.getQuorumForWorkTask(workId);
	}

	/**
	 * Used to get the timeout for user-facing activities.
	 * 
	 * @return
	 * @throws AdminException
	 * @throws RemoteException
	 */
	public int getUserActivityTimeout() throws AdminException, RemoteException {
		return prov.getUserActivityTimeout();
	}

	/**
	 * Used to get the version of the workflow system.
	 * 
	 * @return
	 * @throws RemoteException
	 */
	public T_Version getVersion() throws RemoteException {
		return prov.getVersion();
	}

	/**
	 * Used to get the timeout for Web service activities.
	 * 
	 * @return
	 * @throws AdminException
	 * @throws RemoteException
	 */
	public int getWebServiceActivityTimeout() throws AdminException, RemoteException {
		return prov.getWebServiceActivityTimeout();
	}

	/**
	 * Used to retrieve data items for a work entry identified by the Id (UUID) of a
	 * task.
	 * 
	 * @param workId
	 *            The Id of the task.
	 * @return
	 * @throws AdminException
	 * @throws RemoteException
	 */
	public DataItemArray getWork(String workId) throws AdminException, RemoteException {
		return prov.getWork(workId);
	}

	/**
	 * Used to query the work entries (activities) and returns a list of WorkEntry
	 * objects that satisfy the query.
	 * 
	 * @param query
	 *            Used to specify the query used to retrieve the list of activities.
	 *            The query has the following components:
	 * 
	 *            <ul>
	 *            <li>choice - the parameters used to filter the results. You can
	 *            specify multiple parameters. The possible parameters are:
	 *            <ul>
	 *            <li>Addressee - Possible values for this parameter are a DN or
	 *            self. Use self if you want to retrieve work entries for the caller
	 *            of the query, as identified by the authentication header of the
	 *            SOAP header.
	 *            <li>ProcessId
	 *            <li>RequestId
	 *            <li>ActivityId
	 *            <li>Status (an integer)
	 *            <li>Owner
	 *            <li>Priority
	 *            <li>CreationTime (YYYY/MM/DD)
	 *            <li>ExpTime (YYYY/MM/DD)
	 *            <li>CompletionTime (YYYY/MM/DD)
	 *            <li>Recipient
	 *            <li>Initiator
	 *            <li>ProxyFor
	 *            </ul>
	 * 
	 *            <li>logic - AND or OR
	 * 
	 *            <li>order - the order in which to sort the results. Possible
	 *            values for order are:
	 *            <ul>
	 *            <li>ACTIVITY_ID
	 *            <li>RECIPIENT
	 *            <li>PROVISIONING_TIME
	 *            <li>RESULT_TIME
	 *            <li>STATE STATUS
	 *            <li>REQUEST_ID
	 *            <li>MESSAGE
	 *            </ul>
	 *            </ul>
	 * @param maxRecords
	 *            Used to specify maximum number of records to retrieve. A value of
	 *            -1 returns unlimited records.
	 * @return
	 * @throws AdminException
	 * @throws RemoteException
	 */
	public WorkEntryArray getWorkEntries(T_WorkEntryQuery query, int maxRecords)
			throws AdminException, RemoteException {
		return prov.getWorkEntries(query, maxRecords);
	}

	/**
	 * Used to start a workflow request for each specified recipient.
	 * 
	 * @param processId
	 *            The Id of the provisioning request to start.
	 * @param recipients
	 *            The DN of each recipient.
	 * @param items
	 *            The list of data items for the provisioning request.
	 * @return requestIdStringArray
	 * @throws AdminException
	 * @throws RemoteException
	 */
	public StringArray multiStart(String processId, StringArray recipients, DataItemArray items)
			throws AdminException, RemoteException {
		return prov.multiStart(processId, recipients, items);
	}

	/**
	 * Used to reassign a task from one user to another.
	 * 
	 * @param wid
	 *            The Id of the task.
	 * @param addressee
	 *            The addressee of the task.
	 * @param comment
	 *            A comment about the task.
	 * @throws AdminException
	 * @throws RemoteException
	 */
	public void reassign(String wid, String addressee, String comment) throws AdminException, RemoteException {
		prov.reassign(wid, addressee, comment);
	}

	/**
	 * Used to reassign all processes from the source engine to a list of target
	 * engines.
	 * 
	 * @param sourceEngineId
	 *            The Id of the source workflow engine.
	 * @param targetEngineIds
	 *            The Ids of the target workflow engines.
	 * @return
	 * @throws AdminException
	 * @throws RemoteException
	 */
	public int reassignAllProcesses(String sourceEngineId, StringArray targetEngineIds)
			throws AdminException, RemoteException {
		return prov.reassignAllProcesses(sourceEngineId, targetEngineIds);
	}

	/**
	 * Used to reassign a percentage of processes from the source engine to the
	 * target engine.
	 * 
	 * @param percent
	 *            An integer representing the percentage of processes to be
	 *            reassigned.
	 * @param sourceEngineId
	 *            The Id of the source workflow engine.
	 * @param targetEngineId
	 *            The Id of the target workflow engine.
	 * @return
	 * @throws AdminException
	 * @throws RemoteException
	 */
	public int reassignPercentageProcesses(int percent, String sourceEngineId, String targetEngineId)
			throws AdminException, RemoteException {
		return prov.reassignPercentageProcesses(percent, sourceEngineId, targetEngineId);
	}

	/**
	 * Used to reassign one or more processes from the source engine to the target
	 * engine.
	 * 
	 * @param requestIds
	 *            A list of requestIds of the processes to be reassigned.
	 * @param sourceEngineId
	 *            The Id of the source workflow engine.
	 * @param targetEngineId
	 *            The Id of the target workflow engine.
	 * @return
	 * @throws AdminException
	 * @throws RemoteException
	 */
	public int reassignProcesses(StringArray requestIds, String sourceEngineId, String targetEngineId)
			throws AdminException, RemoteException {
		return prov.reassignProcesses(requestIds, sourceEngineId, targetEngineId);
	}

	/**
	 * Used to reassign a task from one user to another.
	 * 
	 * @param wid
	 *            The Id of the task.
	 * @param addressee
	 *            The addressee of the task.
	 * @param comment
	 *            A comment about the task.
	 * @throws AdminException
	 * @throws RemoteException
	 */
	public void reassignWorkTask(String wid, String addressee, String comment) throws AdminException, RemoteException {
		prov.reassignWorkTask(wid, addressee, comment);
	}

	/**
	 * 
	 * @param requestId
	 * @param activityId
	 * @throws AdminException
	 * @throws RemoteException
	 */
	public void receive(String requestId, String activityId) throws AdminException, RemoteException {
		prov.receive(requestId, activityId);
	}

	/**
	 * Used to remove an engine from the cluster state table. The engine must be in
	 * the SHUTDOWN or TIMEDOUT state.
	 * 
	 * @param engineId
	 *            The Id of the workflow engine to be removed.
	 * @throws AdminException
	 * @throws RemoteException
	 */
	public void removeEngine(String engineId) throws AdminException, RemoteException {
		prov.removeEngine(engineId);
	}

	/**
	 * Used to reset the priority for a task. You should only use this method on
	 * provisioning requests that have a single approval branch.
	 * 
	 * @param workId
	 *            The Id of the activity.
	 * @param priority
	 *            The priority to set for the activity.
	 * @param comment
	 *            A comment about the action.
	 * @throws AdminException
	 * @throws RemoteException
	 */
	public void resetPriorityForWorkTask(String workId, int priority, String comment)
			throws AdminException, RemoteException {
		prov.resetPriorityForWorkTask(workId, priority, comment);
	}

	/**
	 * Used to set the timeout for completed processes. Processes that were
	 * completed more than timeout days ago are removed from the system. The default
	 * value is 120 days. The valid range is 0 days to 365 days.
	 * 
	 * @param time
	 * @throws AdminException
	 * @throws RemoteException
	 */
	public void setCompletedProcessTimeout(int time) throws AdminException, RemoteException {
		prov.setCompletedProcessTimeout(time);
	}

	/**
	 * Used to globally enable or disable email notifications.
	 * 
	 * @param enable
	 *            Email notifications are enabled if true; otherwise they are
	 *            disabled.
	 * @throws AdminException
	 * @throws RemoteException
	 */
	public void setEmailNotifications(boolean enable) throws AdminException, RemoteException {
		prov.setEmailNotifications(enable);
	}

	/**
	 * Used to set workflow engine configuration parameters.
	 * 
	 * minPoolSize: The minumum thread pool size.
	 * 
	 * maxnPoolSize: The maximum thread pool size.
	 * 
	 * initialPoolSize: The initial thread pool size.
	 * 
	 * keepAliveTime: Thread pool keep live time.
	 * 
	 * pendingInterval: The cluster synchronization time.
	 * 
	 * cleanupInterval: The interval between purging processes from databases.
	 * 
	 * retryQueueInterval: The interval between retrying failed processes.
	 * 
	 * maxShutdownTime: The maximum time to let threads complete work before engine
	 * shutdown.
	 * 
	 * userActivityTimeout: The default user activity timeout.
	 * 
	 * completedProcessTimeout: The default completed process timeout.
	 * 
	 * webServiceActivityTimeout: The default Web service activity timeout.
	 * 
	 * emailNotification: Turns email notification on or off.
	 * 
	 * processCacheInitialCapacity: The process cache initial capacity.
	 * 
	 * processCacheMaxCapacity: The process cache maximum capacity.
	 * 
	 * processCacheLoadFactor: The process cache load factor.
	 * 
	 * heartbeatInterval: The heartbeat interval.
	 * 
	 * heartbeatFactor: The heartbeat factor.
	 * 
	 * @param config
	 * @throws AdminException
	 * @throws RemoteException
	 */
	public void setEngineConfiguration(Configuration config) throws AdminException, RemoteException {
		prov.setEngineConfiguration(config);
	}

	/**
	 * Used to set the entitlement result (approval status) of a previously
	 * completed provisioning request.
	 * 
	 * @param requestId
	 *            The Id of the provisioning request.
	 * @param stateThe
	 *            state of the provisioning request. The possible values are:
	 * 
	 *            Unknown Granted Revoked
	 * @param status
	 *            The status of the provisioning request. The possible values are:
	 * 
	 *            Unknown Success Warning Error Fatal Submitted
	 * @param message
	 *            A message about the entitlement result.
	 * @throws AdminException
	 * @throws RemoteException
	 */
	public void setResult(String requestId, T_EntitlementState state, T_EntitlementStatus status, String message)
			throws AdminException, RemoteException {
		prov.setResult(requestId, state, status, message);
	}

	/**
	 * 
	 * @param nrfRequestId
	 * @param state
	 * @param originator
	 * @throws AdminException
	 * @throws RemoteException
	 */
	public void setRoleRequestStatus(String nrfRequestId, int state, String originator)
			throws AdminException, RemoteException {
		prov.setRoleRequestStatus(nrfRequestId, state, originator);
	}

	/**
	 * Used to set the timeout for user-facing activities. The default value is no
	 * timeout (a value of zero). The valid range is 1 hour to 365 days.
	 * 
	 * @param time
	 *            The timeout value in hours.
	 * @throws AdminException
	 * @throws RemoteException
	 */
	public void setUserActivityTimeout(int time) throws AdminException, RemoteException {
		prov.setUserActivityTimeout(time);
	}

	/**
	 * Used to set the timeout for Web service activities. The default value is 50
	 * minutes. The valid range is 1 minute to 7 days.
	 * 
	 * @param time
	 *            The timeout value in minutes.
	 * @throws AdminException
	 * @throws RemoteException
	 */
	public void setWebServiceActivityTimeout(int time) throws AdminException, RemoteException {
		prov.setWebServiceActivityTimeout(time);
	}

	/**
	 * Used to start a provisioning request.
	 * 
	 * @param processId
	 *            The request identifier.
	 * @param recipient
	 *            The request recipient.
	 * @param items
	 *            The data items for the provisioning request.
	 * @return
	 * @throws AdminException
	 * @throws RemoteException
	 */
	public String start(String processId, String recipient, DataItemArray items)
			throws AdminException, RemoteException {
		return prov.start(processId, recipient, items);
	}

	/**
	 * Used to start a workflow as a proxy.
	 * 
	 * @param processId
	 *            The request identifier.
	 * @param recipient
	 *            The request recipient.
	 * @param items
	 *            The data items for the provisioning request.
	 * @param proxyUser
	 *            The DN of the proxy user.
	 * @return
	 * @throws AdminException
	 * @throws RemoteException
	 */
	public String startAsProxy(String processId, String recipient, DataItemArray items, String proxyUser)
			throws AdminException, RemoteException {
		return prov.startAsProxy(processId, recipient, items, proxyUser);
	}

	/**
	 * Used to start a workflow using a proxy for the initiator, and specify that a
	 * digital signature is required.
	 * 
	 * @param processId
	 *            The request identifier.
	 * @param recipient
	 *            The request recipient.
	 * @param items
	 *            The data items for the provisioning request.
	 * @param digitalSignature
	 *            The digital signature.
	 * @param digitalSignaturePropertyArray
	 *            The digital signature property map.
	 * @param proxyUser
	 *            The DN of the proxy user.
	 * @return
	 * @throws AdminException
	 * @throws RemoteException
	 */
	public String startAsProxyWithDigitalSignature(String processId, String recipient, DataItemArray items,
			String digitalSignature, SignaturePropertyArray digitalSignaturePropertyArray, String proxyUser)
			throws AdminException, RemoteException {
		return prov.startAsProxyWithDigitalSignature(processId, recipient, items, digitalSignature,
				digitalSignaturePropertyArray, proxyUser);
	}

	/**
	 * Used to start a workflow with a correlation ID. The correlation ID provides a
	 * way to track a set of related workflow processes. When started with this
	 * method, workflow processes can be queried and sorted by correlation ID.
	 * 
	 * @param processId
	 *            The request identifier.
	 * @param recipient
	 *            The request recipient.
	 * @param items
	 *            The data items for the provisioning request.
	 * @param digitalSignature
	 *            The digital signature.
	 * @param digitalSignaturePropertyArray
	 *            The digital signature property map.
	 * @param proxyUser
	 *            The DN of the proxy user.
	 * @param correlationId
	 *            The string that identities the correlation ID. The correlation ID
	 *            cannot be longer than 32 characters.
	 * @return
	 * @throws AdminException
	 * @throws RemoteException
	 */
	public String startWithCorrelationId(String processId, String recipient, DataItemArray items,
			String digitalSignature, SignaturePropertyArray digitalSignaturePropertyArray, String proxyUser,
			String correlationId) throws AdminException, RemoteException {
		return prov.startWithCorrelationId(processId, recipient, items, digitalSignature, digitalSignaturePropertyArray,
				proxyUser, correlationId);
	}

	/**
	 * Used to start a workflow and specify that a digital signature is required.
	 * 
	 * @param processId
	 *            The request identifier.
	 * @param recipient
	 *            The request recipient.
	 * @param items
	 *            The data items for the provisioning request.
	 * @param digitalSignature
	 *            The digital signature.
	 * @param digitalSignaturePropertyArray
	 *            The digital signature property map.
	 * @return
	 * @throws AdminException
	 * @throws RemoteException
	 */
	public String startWithDigitalSignature(String processId, String recipient, DataItemArray items,
			String digitalSignature, SignaturePropertyArray digitalSignaturePropertyArray)
			throws AdminException, RemoteException {
		return prov.startWithDigitalSignature(processId, recipient, items, digitalSignature,
				digitalSignaturePropertyArray);
	}

	/**
	 * 
	 * @param wfXml
	 * @param wfName
	 * @param wfVersion
	 * @param recipient
	 * @param dataitems
	 * @param correlationId
	 * @return
	 * @throws AdminException
	 * @throws RemoteException
	 */
	public String startWorkFlow(String wfXml, String wfName, String wfVersion, String recipient,
			DataItemArray dataitems, String correlationId) throws AdminException, RemoteException {
		return prov.startWorkFlow(wfXml, wfName, wfVersion, recipient, dataitems, correlationId);
	}

	/**
	 * Used to unclaim a provisioning request. This method only works if the request
	 * was claimed in the User Application. You cannot unclaim a request once it has
	 * been forwarded using the SOAP interface, because the forward API method (see
	 * forward) claims and forwards in one operation.
	 * 
	 * @param wid
	 *            The Id of the activity to unclaim.
	 * @param comment
	 *            A comment about the action.
	 */
	public void unclaim(String wid, String comment) throws AdminException, RemoteException {
		prov.unclaim(wid, comment);
	}

}