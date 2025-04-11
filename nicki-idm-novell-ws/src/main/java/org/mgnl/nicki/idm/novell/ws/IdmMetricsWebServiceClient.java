package org.mgnl.nicki.idm.novell.ws;

/*-
 * #%L
 * nicki-idm-novell-ws
 * %%
 * Copyright (C) 2017 - 2023 Ralf Hirning
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

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import org.mgnl.nicki.core.config.Config;

import com.novell.soa.af.metrics.MetricsFilter;
import com.novell.soa.af.metrics.impl.BasicModelVO;
import com.novell.soa.af.metrics.impl.CommentVO;
import com.novell.soa.af.metrics.impl.IRemoteMetrics;
import com.novell.soa.af.metrics.impl.MetricsResultset;
import com.novell.soa.af.metrics.impl.MetricsService;
import com.novell.soa.af.metrics.impl.ProcessVO;
import com.novell.soa.af.metrics.impl.VersionVO;
import com.novell.soa.af.metrics.soap.MetricsServiceException;
import com.novell.soa.ws.portable.Stub;

// TODO: Auto-generated Javadoc
/**
 * The Class IdmMetricsWebServiceClient.
 */
@SuppressWarnings("serial")
public class IdmMetricsWebServiceClient implements Serializable {
	/*
	 * nicki.idm.novell.ws.provisioning.wsdl = http://172.17.2.91:8180/IDMProv/metrics/service?wsdl
	 * nicki.idm.novell.ws.provisioning.user = cn=uaadmin,ou=sa,o=data
	 * nicki.idm.novell.ws.provisioning.password = netiq000
	 */

	/** The wsdl. */
	private static String wsdl = Config.getString("nicki.idm.novell.ws.metrics.wsdl");
	
	/** The user. */
	private static String user = Config.getString("nicki.idm.novell.ws.metrics.user");
	
	/** The password. */
	private static String password = Config.getString("nicki.idm.novell.ws.metrics.password");

	/** The instance. */
	private static IdmMetricsWebServiceClient instance;

	/** The service. */
	private MetricsService service;
	
	/** The metrics. */
	private IRemoteMetrics metrics;

	static {
		// for localhost testing only
		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier((hostname, sslSession) -> {
				if (hostname.equals("localhost")) {
					return true;
				}
				return true;
		});
	}

	/**
	 * Gets the single instance of IdmMetricsWebServiceClient.
	 *
	 * @return single instance of IdmMetricsWebServiceClient
	 */
	public static synchronized IdmMetricsWebServiceClient getInstance() {
		if (instance == null) {
			instance = new IdmMetricsWebServiceClient();
			instance.init();
		}
		return instance;
	}

	/**
	 * Instantiates a new idm metrics web service client.
	 */
	private IdmMetricsWebServiceClient() {
	}

	/**
	 * Inits the.
	 */
	private void init() {

		try {
			if (service == null) {
				InitialContext ctx = new InitialContext();
				service = (MetricsService) ctx
						.lookup("xmlrpc:soap:com.novell.soa.af.impl.soap.MetricsService");
				metrics = service.getIRemoteMetricsPort();
				Stub stub = (Stub) metrics;
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
	 * Gets the activity flow time calendar days.
	 *
	 * @param processId the process id
	 * @param processVer the process ver
	 * @param activityId the activity id
	 * @param startTime the start time
	 * @param completeTime the complete time
	 * @param filters the filters
	 * @return the activity flow time calendar days
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public double getActivityFlowTimeCalendarDays(String processId, String processVer, String activityId, Date startTime, Date completeTime,
			Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getActivityFlowTimeCalendarDays(processId, processVer, activityId, startTime, completeTime, filters);
	}

	
	/**
	 * Gets the activity flow time working days.
	 *
	 * @param processId the process id
	 * @param processVer the process ver
	 * @param activityId the activity id
	 * @param startTime the start time
	 * @param completeTime the complete time
	 * @param filters the filters
	 * @return the activity flow time working days
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public MetricsResultset getActivityFlowTimeWorkingDays(String processId, String processVer, String activityId, Date startTime, Date completeTime,
			Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getActivityFlowTimeWorkingDays(processId, processVer, activityId, startTime, completeTime, filters);
	}

	
	/**
	 * Gets the activity inventory.
	 *
	 * @param processId the process id
	 * @param processVer the process ver
	 * @param activityId the activity id
	 * @param startTime the start time
	 * @param completeTime the complete time
	 * @param filters the filters
	 * @return the activity inventory
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public double getActivityInventory(String processId, String processVer, String activityId, Date startTime, Date completeTime,
			Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getActivityInventory(processId, processVer, activityId, startTime, completeTime, filters);
	}

	
	/**
	 * Gets the activity throughput calendar days.
	 *
	 * @param processId the process id
	 * @param processVer the process ver
	 * @param activityId the activity id
	 * @param startTime the start time
	 * @param completeTime the complete time
	 * @param filters the filters
	 * @return the activity throughput calendar days
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public double getActivityThroughputCalendarDays(String processId, String processVer, String activityId, Date startTime, Date completeTime,
			Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getActivityThroughputCalendarDays(processId, processVer, activityId, startTime, completeTime, filters);
	}

	
	/**
	 * Gets the activity throughput working days.
	 *
	 * @param processId the process id
	 * @param processVer the process ver
	 * @param activityId the activity id
	 * @param startTime the start time
	 * @param completeTime the complete time
	 * @param filters the filters
	 * @return the activity throughput working days
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public MetricsResultset getActivityThroughputWorkingDays(String processId, String processVer, String activityId, Date startTime, Date completeTime,
			Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getActivityThroughputWorkingDays(processId, processVer, activityId, startTime, completeTime, filters);
	}

	
	/**
	 * Gets the all provisioning flows.
	 *
	 * @return the all provisioning flows
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public MetricsResultset getAllProvisioningFlows() throws MetricsServiceException, RemoteException {
		return metrics.getAllProvisioningFlows();
	}

	
	/**
	 * Gets the claimed flow time calendar days.
	 *
	 * @param processId the process id
	 * @param processVersion the process version
	 * @param startCompletionTime the start completion time
	 * @param endCompletionTime the end completion time
	 * @param teamDN the team DN
	 * @param filters the filters
	 * @return the claimed flow time calendar days
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public double getClaimedFlowTimeCalendarDays(String processId, String processVersion, Date startCompletionTime, Date endCompletionTime, String teamDN,
			Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getClaimedFlowTimeCalendarDays(processId, processVersion, startCompletionTime, endCompletionTime, teamDN, filters);
	}

	
	/**
	 * Gets the claimed flow time working days.
	 *
	 * @param processId the process id
	 * @param processVersion the process version
	 * @param startCompletionTime the start completion time
	 * @param endCompletionTime the end completion time
	 * @param teamDN the team DN
	 * @param filters the filters
	 * @return the claimed flow time working days
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public MetricsResultset getClaimedFlowTimeWorkingDays(String processId, String processVersion, Date startCompletionTime, Date endCompletionTime, String teamDN,
			Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getClaimedFlowTimeWorkingDays(processId, processVersion, startCompletionTime, endCompletionTime, teamDN, filters);
	}

	
	/**
	 * Gets the claimed inventory.
	 *
	 * @param processId the process id
	 * @param processVersion the process version
	 * @param startCompletionTime the start completion time
	 * @param endCompletionTime the end completion time
	 * @param teamDN the team DN
	 * @param filters the filters
	 * @return the claimed inventory
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public double getClaimedInventory(String processId, String processVersion, Date startCompletionTime, Date endCompletionTime, String teamDN, Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getClaimedInventory(processId, processVersion, startCompletionTime, endCompletionTime, teamDN, filters);
	}

	
	/**
	 * Gets the claimed throughput calendar days.
	 *
	 * @param processId the process id
	 * @param processVersion the process version
	 * @param startCompletionTime the start completion time
	 * @param endCompletionTime the end completion time
	 * @param teamDN the team DN
	 * @param filters the filters
	 * @return the claimed throughput calendar days
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public double getClaimedThroughputCalendarDays(String processId, String processVersion, Date startCompletionTime, Date endCompletionTime, String teamDN, Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getClaimedThroughputCalendarDays(processId, processVersion, startCompletionTime, endCompletionTime, teamDN, filters);
	}

	
	/**
	 * Gets the claimed throughput working days.
	 *
	 * @param processId the process id
	 * @param processVersion the process version
	 * @param startCompletionTime the start completion time
	 * @param endCompletionTime the end completion time
	 * @param teamDN the team DN
	 * @param filters the filters
	 * @return the claimed throughput working days
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public MetricsResultset getClaimedThroughputWorkingDays(String processId, String processVersion, Date startCompletionTime, Date endCompletionTime, String teamDN, Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getClaimedThroughputWorkingDays(processId, processVersion, startCompletionTime, endCompletionTime, teamDN, filters);
	}

	
	/**
	 * Gets the dummy.
	 *
	 * @param process the process
	 * @param comment the comment
	 * @param filter the filter
	 * @return the dummy
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public void getDummy(ProcessVO process, CommentVO comment, MetricsFilter filter)
			throws MetricsServiceException, RemoteException {
		metrics.getDummy(process, comment, filter);
	}

	
	/**
	 * Gets the flow count.
	 *
	 * @param processId the process id
	 * @param processVersion the process version
	 * @param filters the filters
	 * @return the flow count
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public int getFlowCount(String processId, String processVersion, Map<MetricsFilter, Object> filters)
			throws MetricsServiceException, RemoteException {
		return metrics.getFlowCount(processId, processVersion, filters);
	}

	
	/**
	 * Gets the flow history.
	 *
	 * @param requestIds the request ids
	 * @return the flow history
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public MetricsResultset getFlowHistory(List requestIds) throws MetricsServiceException, RemoteException {
		return metrics.getFlowHistory(requestIds);
	}

	
	/**
	 * Gets the flow history for initiators.
	 *
	 * @param initiators the initiators
	 * @param filters the filters
	 * @return the flow history for initiators
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public MetricsResultset getFlowHistoryForInitiators(List initiators, Map<MetricsFilter, Object> filters)
			throws MetricsServiceException, RemoteException {
		return metrics.getFlowHistoryForInitiators(initiators, filters);
	}

	
	/**
	 * Gets the flow history for recipients.
	 *
	 * @param recipients the recipients
	 * @param filters the filters
	 * @return the flow history for recipients
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public MetricsResultset getFlowHistoryForRecipients(List recipients, Map<MetricsFilter, Object> filters)
			throws MetricsServiceException, RemoteException {
		return metrics.getFlowHistoryForRecipients(recipients, filters);
	}

	
	/**
	 * Gets the flow time calendar days.
	 *
	 * @param processId the process id
	 * @param processVer the process ver
	 * @param startTime the start time
	 * @param completeTime the complete time
	 * @param filters the filters
	 * @return the flow time calendar days
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public double getFlowTimeCalendarDays(String processId, String processVer, Date startTime, Date completeTime,
			Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getFlowTimeCalendarDays(processId, processVer, startTime, completeTime, filters);
	}

	
	/**
	 * Gets the flow time working days.
	 *
	 * @param processId the process id
	 * @param processVer the process ver
	 * @param startTime the start time
	 * @param completeTime the complete time
	 * @param filters the filters
	 * @return the flow time working days
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public MetricsResultset getFlowTimeWorkingDays(String processId, String processVer, Date startTime, Date completeTime,
			Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getFlowTimeWorkingDays(processId, processVer, startTime, completeTime, filters);
	}

	
	/**
	 * Gets the inventory.
	 *
	 * @param processId the process id
	 * @param processVer the process ver
	 * @param startTime the start time
	 * @param completeTime the complete time
	 * @param filters the filters
	 * @return the inventory
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public double getInventory(String processId, String processVer, Date startTime, Date completeTime,
			Map<MetricsFilter, Object> filters)
			throws MetricsServiceException, RemoteException {
		return metrics.getInventory(processId, processVer, startTime, completeTime, filters);
	}

	
	/**
	 * Gets the longest claimed.
	 *
	 * @param processId the process id
	 * @param processVer the process ver
	 * @param filters the filters
	 * @return the longest claimed
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public MetricsResultset getLongestClaimed(String processId, String processVer, Map<MetricsFilter, Object> filters)
			throws MetricsServiceException, RemoteException {
		return metrics.getLongestClaimed(processId, processVer, filters);
	}

	
	/**
	 * Gets the longest running.
	 *
	 * @param processId the process id
	 * @param processVer the process ver
	 * @param filters the filters
	 * @return the longest running
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public MetricsResultset getLongestRunning(String processId, String processVer, Map<MetricsFilter, Object> filters)
			throws MetricsServiceException, RemoteException {
		return metrics.getLongestRunning(processId, processVer, filters);
	}

	
	/**
	 * Gets the running time.
	 *
	 * @param processId the process id
	 * @param processVersion the process version
	 * @param filters the filters
	 * @return the running time
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public double getRunningTime(String processId, String processVersion, Map<MetricsFilter, Object> filters)
			throws MetricsServiceException, RemoteException {
		return metrics.getRunningTime(processId, processVersion, filters);
	}

	
	/**
	 * Gets the team decision count.
	 *
	 * @param processId the process id
	 * @param processVersion the process version
	 * @param teamDN the team DN
	 * @param filters the filters
	 * @return the team decision count
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public int getTeamDecisionCount(String processId, String processVersion, String teamDN, Map<MetricsFilter, Object> filters)
			throws MetricsServiceException, RemoteException {
		return metrics.getTeamDecisionCount(processId, processVersion, teamDN, filters);
	}

	
	/**
	 * Gets the team flow history.
	 *
	 * @param requestIds the request ids
	 * @return the team flow history
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public MetricsResultset getTeamFlowHistory(List requestIds) throws MetricsServiceException, RemoteException {
		return metrics.getTeamFlowHistory(requestIds);
	}

	
	/**
	 * Gets the team history for initiators.
	 *
	 * @param teamDN the team DN
	 * @param filters the filters
	 * @return the team history for initiators
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public MetricsResultset getTeamHistoryForInitiators(String teamDN, Map<MetricsFilter, Object> filters)
			throws MetricsServiceException, RemoteException {
		return metrics.getTeamHistoryForInitiators(teamDN, filters);
	}

	
	/**
	 * Gets the team history for recipients.
	 *
	 * @param teamDN the team DN
	 * @param filters the filters
	 * @return the team history for recipients
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public MetricsResultset getTeamHistoryForRecipients(String teamDN, Map<MetricsFilter, Object> filters)
			throws MetricsServiceException, RemoteException {
		return metrics.getTeamHistoryForRecipients(teamDN, filters);
	}

	
	/**
	 * Gets the team initiated count.
	 *
	 * @param processId the process id
	 * @param processVersion the process version
	 * @param teamDN the team DN
	 * @param filters the filters
	 * @return the team initiated count
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public int getTeamInitiatedCount(String processId, String processVersion, String teamDN, Map<MetricsFilter, Object> filters)
			throws MetricsServiceException, RemoteException {
		return metrics.getTeamInitiatedCount(processId, processVersion, teamDN, filters);
	}

	
	/**
	 * Gets the team longest claimed.
	 *
	 * @param processId the process id
	 * @param processVersion the process version
	 * @param teamDN the team DN
	 * @param filters the filters
	 * @return the team longest claimed
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public MetricsResultset getTeamLongestClaimed(String processId, String processVersion, String teamDN, Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getTeamLongestClaimed(processId, processVersion, teamDN, filters);
	}

	
	/**
	 * Gets the team longest running.
	 *
	 * @param processId the process id
	 * @param processVersion the process version
	 * @param teamDN the team DN
	 * @param filters the filters
	 * @return the team longest running
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public MetricsResultset getTeamLongestRunning(String processId, String processVersion, String teamDN, Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getTeamLongestRunning(processId, processVersion, teamDN, filters);
	}

	
	/**
	 * Gets the team members.
	 *
	 * @param teamDN the team DN
	 * @return the team members
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public MetricsResultset getTeamMembers(String teamDN) throws MetricsServiceException, RemoteException {
		return metrics.getTeamMembers(teamDN);
	}

	
	/**
	 * Gets the team recipient count.
	 *
	 * @param processId the process id
	 * @param processVersion the process version
	 * @param teamDN the team DN
	 * @param filters the filters
	 * @return the team recipient count
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public int getTeamRecipientCount(String processId, String processVersion, String teamDN, Map<MetricsFilter, Object> filters)
			throws MetricsServiceException, RemoteException {
		return metrics.getTeamRecipientCount(processId, processVersion, teamDN, filters);
	}

	
	/**
	 * Gets the team running time.
	 *
	 * @param processId the process id
	 * @param processVersion the process version
	 * @param teamDN the team DN
	 * @param filters the filters
	 * @return the team running time
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public double getTeamRunningTime(String processId, String processVersion, String teamDN, Map<MetricsFilter, Object> filters)
			throws MetricsServiceException, RemoteException {
		return metrics.getTeamRunningTime(processId, processVersion, teamDN, filters);
	}

	
	/**
	 * Gets the teams.
	 *
	 * @return the teams
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public MetricsResultset getTeams() throws MetricsServiceException, RemoteException {
		return metrics.getTeams();
	}

	
	/**
	 * Gets the throughput calendar days.
	 *
	 * @param processId the process id
	 * @param processVersion the process version
	 * @param startCompletionTime the start completion time
	 * @param endCompletionTime the end completion time
	 * @param teamDN the team DN
	 * @param filters the filters
	 * @return the throughput calendar days
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public double getThroughputCalendarDays(String processId, String processVersion, Date startCompletionTime, Date endCompletionTime, String teamDN,
			Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getThroughputCalendarDays(processId, processVersion, startCompletionTime, endCompletionTime, filters);
	}

	
	/**
	 * Gets the throughput working days.
	 *
	 * @param processId the process id
	 * @param processVersion the process version
	 * @param startCompletionTime the start completion time
	 * @param endCompletionTime the end completion time
	 * @param teamDN the team DN
	 * @param filters the filters
	 * @return the throughput working days
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public MetricsResultset getThroughputWorkingDays(String processId, String processVersion, Date startCompletionTime, Date endCompletionTime, String teamDN,
			Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getThroughputWorkingDays(processId, processVersion, startCompletionTime, endCompletionTime, filters);
	}

	
	/**
	 * Gets the to claim flow time calendar days.
	 *
	 * @param processId the process id
	 * @param processVersion the process version
	 * @param startCompletionTime the start completion time
	 * @param endCompletionTime the end completion time
	 * @param teamDN the team DN
	 * @param filters the filters
	 * @return the to claim flow time calendar days
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public double getToClaimFlowTimeCalendarDays(String processId, String processVersion, Date startCompletionTime, Date endCompletionTime, String teamDN,
			Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getToClaimFlowTimeCalendarDays(processId, processVersion, startCompletionTime, endCompletionTime, teamDN, filters);
	}

	
	/**
	 * Gets the to claim flow time working days.
	 *
	 * @param processId the process id
	 * @param processVersion the process version
	 * @param startCompletionTime the start completion time
	 * @param endCompletionTime the end completion time
	 * @param teamDN the team DN
	 * @param filters the filters
	 * @return the to claim flow time working days
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public MetricsResultset getToClaimFlowTimeWorkingDays(String processId, String processVersion, Date startCompletionTime, Date endCompletionTime, String teamDN,
			Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getToClaimFlowTimeWorkingDays(processId, processVersion, startCompletionTime, endCompletionTime, teamDN, filters);
	}

	
	/**
	 * Gets the user activity only flow.
	 *
	 * @param processId the process id
	 * @param processVer the process ver
	 * @return the user activity only flow
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public BasicModelVO getUserActivityOnlyFlow(String processId, String processVer)
			throws MetricsServiceException, RemoteException {
		return metrics.getUserActivityOnlyFlow(processId, processVer);
	}

	
	/**
	 * Gets the version.
	 *
	 * @return the version
	 * @throws MetricsServiceException the metrics service exception
	 * @throws RemoteException the remote exception
	 */
	public VersionVO getVersion() throws MetricsServiceException, RemoteException {
		return metrics.getVersion();
	}


}
