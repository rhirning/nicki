package org.mgnl.nicki.idm.novell.ws;

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

@SuppressWarnings("serial")
public class IdmMetricsWebServiceClient implements Serializable {
	/*
	 * nicki.idm.novell.ws.provisioning.wsdl = http://172.17.2.91:8180/IDMProv/metrics/service?wsdl
	 * nicki.idm.novell.ws.provisioning.user = cn=uaadmin,ou=sa,o=data
	 * nicki.idm.novell.ws.provisioning.password = netiq000
	 */

	private static String wsdl = Config.getString("nicki.idm.novell.ws.metrics.wsdl");
	private static String user = Config.getString("nicki.idm.novell.ws.metrics.user");
	private static String password = Config.getString("nicki.idm.novell.ws.metrics.password");

	private static IdmMetricsWebServiceClient instance;

	private MetricsService service;
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

	public static synchronized IdmMetricsWebServiceClient getInstance() {
		if (instance == null) {
			instance = new IdmMetricsWebServiceClient();
			instance.init();
		}
		return instance;
	}

	private IdmMetricsWebServiceClient() {
	}

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

	
	public double getActivityFlowTimeCalendarDays(String processId, String processVer, String activityId, Date startTime, Date completeTime,
			Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getActivityFlowTimeCalendarDays(processId, processVer, activityId, startTime, completeTime, filters);
	}

	
	public MetricsResultset getActivityFlowTimeWorkingDays(String processId, String processVer, String activityId, Date startTime, Date completeTime,
			Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getActivityFlowTimeWorkingDays(processId, processVer, activityId, startTime, completeTime, filters);
	}

	
	public double getActivityInventory(String processId, String processVer, String activityId, Date startTime, Date completeTime,
			Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getActivityInventory(processId, processVer, activityId, startTime, completeTime, filters);
	}

	
	public double getActivityThroughputCalendarDays(String processId, String processVer, String activityId, Date startTime, Date completeTime,
			Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getActivityThroughputCalendarDays(processId, processVer, activityId, startTime, completeTime, filters);
	}

	
	public MetricsResultset getActivityThroughputWorkingDays(String processId, String processVer, String activityId, Date startTime, Date completeTime,
			Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getActivityThroughputWorkingDays(processId, processVer, activityId, startTime, completeTime, filters);
	}

	
	public MetricsResultset getAllProvisioningFlows() throws MetricsServiceException, RemoteException {
		return metrics.getAllProvisioningFlows();
	}

	
	public double getClaimedFlowTimeCalendarDays(String processId, String processVersion, Date startCompletionTime, Date endCompletionTime, String teamDN,
			Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getClaimedFlowTimeCalendarDays(processId, processVersion, startCompletionTime, endCompletionTime, teamDN, filters);
	}

	
	public MetricsResultset getClaimedFlowTimeWorkingDays(String processId, String processVersion, Date startCompletionTime, Date endCompletionTime, String teamDN,
			Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getClaimedFlowTimeWorkingDays(processId, processVersion, startCompletionTime, endCompletionTime, teamDN, filters);
	}

	
	public double getClaimedInventory(String processId, String processVersion, Date startCompletionTime, Date endCompletionTime, String teamDN, Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getClaimedInventory(processId, processVersion, startCompletionTime, endCompletionTime, teamDN, filters);
	}

	
	public double getClaimedThroughputCalendarDays(String processId, String processVersion, Date startCompletionTime, Date endCompletionTime, String teamDN, Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getClaimedThroughputCalendarDays(processId, processVersion, startCompletionTime, endCompletionTime, teamDN, filters);
	}

	
	public MetricsResultset getClaimedThroughputWorkingDays(String processId, String processVersion, Date startCompletionTime, Date endCompletionTime, String teamDN, Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getClaimedThroughputWorkingDays(processId, processVersion, startCompletionTime, endCompletionTime, teamDN, filters);
	}

	
	public void getDummy(ProcessVO process, CommentVO comment, MetricsFilter filter)
			throws MetricsServiceException, RemoteException {
		metrics.getDummy(process, comment, filter);
	}

	
	public int getFlowCount(String processId, String processVersion, Map<MetricsFilter, Object> filters)
			throws MetricsServiceException, RemoteException {
		return metrics.getFlowCount(processId, processVersion, filters);
	}

	
	public MetricsResultset getFlowHistory(List requestIds) throws MetricsServiceException, RemoteException {
		return metrics.getFlowHistory(requestIds);
	}

	
	public MetricsResultset getFlowHistoryForInitiators(List initiators, Map<MetricsFilter, Object> filters)
			throws MetricsServiceException, RemoteException {
		return metrics.getFlowHistoryForInitiators(initiators, filters);
	}

	
	public MetricsResultset getFlowHistoryForRecipients(List recipients, Map<MetricsFilter, Object> filters)
			throws MetricsServiceException, RemoteException {
		return metrics.getFlowHistoryForRecipients(recipients, filters);
	}

	
	public double getFlowTimeCalendarDays(String processId, String processVer, Date startTime, Date completeTime,
			Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getFlowTimeCalendarDays(processId, processVer, startTime, completeTime, filters);
	}

	
	public MetricsResultset getFlowTimeWorkingDays(String processId, String processVer, Date startTime, Date completeTime,
			Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getFlowTimeWorkingDays(processId, processVer, startTime, completeTime, filters);
	}

	
	public double getInventory(String processId, String processVer, Date startTime, Date completeTime,
			Map<MetricsFilter, Object> filters)
			throws MetricsServiceException, RemoteException {
		return metrics.getInventory(processId, processVer, startTime, completeTime, filters);
	}

	
	public MetricsResultset getLongestClaimed(String processId, String processVer, Map<MetricsFilter, Object> filters)
			throws MetricsServiceException, RemoteException {
		return metrics.getLongestClaimed(processId, processVer, filters);
	}

	
	public MetricsResultset getLongestRunning(String processId, String processVer, Map<MetricsFilter, Object> filters)
			throws MetricsServiceException, RemoteException {
		return metrics.getLongestRunning(processId, processVer, filters);
	}

	
	public double getRunningTime(String processId, String processVersion, Map<MetricsFilter, Object> filters)
			throws MetricsServiceException, RemoteException {
		return metrics.getRunningTime(processId, processVersion, filters);
	}

	
	public int getTeamDecisionCount(String processId, String processVersion, String teamDN, Map<MetricsFilter, Object> filters)
			throws MetricsServiceException, RemoteException {
		return metrics.getTeamDecisionCount(processId, processVersion, teamDN, filters);
	}

	
	public MetricsResultset getTeamFlowHistory(List requestIds) throws MetricsServiceException, RemoteException {
		return metrics.getTeamFlowHistory(requestIds);
	}

	
	public MetricsResultset getTeamHistoryForInitiators(String teamDN, Map<MetricsFilter, Object> filters)
			throws MetricsServiceException, RemoteException {
		return metrics.getTeamHistoryForInitiators(teamDN, filters);
	}

	
	public MetricsResultset getTeamHistoryForRecipients(String teamDN, Map<MetricsFilter, Object> filters)
			throws MetricsServiceException, RemoteException {
		return metrics.getTeamHistoryForRecipients(teamDN, filters);
	}

	
	public int getTeamInitiatedCount(String processId, String processVersion, String teamDN, Map<MetricsFilter, Object> filters)
			throws MetricsServiceException, RemoteException {
		return metrics.getTeamInitiatedCount(processId, processVersion, teamDN, filters);
	}

	
	public MetricsResultset getTeamLongestClaimed(String processId, String processVersion, String teamDN, Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getTeamLongestClaimed(processId, processVersion, teamDN, filters);
	}

	
	public MetricsResultset getTeamLongestRunning(String processId, String processVersion, String teamDN, Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getTeamLongestRunning(processId, processVersion, teamDN, filters);
	}

	
	public MetricsResultset getTeamMembers(String teamDN) throws MetricsServiceException, RemoteException {
		return metrics.getTeamMembers(teamDN);
	}

	
	public int getTeamRecipientCount(String processId, String processVersion, String teamDN, Map<MetricsFilter, Object> filters)
			throws MetricsServiceException, RemoteException {
		return metrics.getTeamRecipientCount(processId, processVersion, teamDN, filters);
	}

	
	public double getTeamRunningTime(String processId, String processVersion, String teamDN, Map<MetricsFilter, Object> filters)
			throws MetricsServiceException, RemoteException {
		return metrics.getTeamRunningTime(processId, processVersion, teamDN, filters);
	}

	
	public MetricsResultset getTeams() throws MetricsServiceException, RemoteException {
		return metrics.getTeams();
	}

	
	public double getThroughputCalendarDays(String processId, String processVersion, Date startCompletionTime, Date endCompletionTime, String teamDN,
			Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getThroughputCalendarDays(processId, processVersion, startCompletionTime, endCompletionTime, filters);
	}

	
	public MetricsResultset getThroughputWorkingDays(String processId, String processVersion, Date startCompletionTime, Date endCompletionTime, String teamDN,
			Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getThroughputWorkingDays(processId, processVersion, startCompletionTime, endCompletionTime, filters);
	}

	
	public double getToClaimFlowTimeCalendarDays(String processId, String processVersion, Date startCompletionTime, Date endCompletionTime, String teamDN,
			Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getToClaimFlowTimeCalendarDays(processId, processVersion, startCompletionTime, endCompletionTime, teamDN, filters);
	}

	
	public MetricsResultset getToClaimFlowTimeWorkingDays(String processId, String processVersion, Date startCompletionTime, Date endCompletionTime, String teamDN,
			Map<MetricsFilter, Object> filters) throws MetricsServiceException, RemoteException {
		return metrics.getToClaimFlowTimeWorkingDays(processId, processVersion, startCompletionTime, endCompletionTime, teamDN, filters);
	}

	
	public BasicModelVO getUserActivityOnlyFlow(String processId, String processVer)
			throws MetricsServiceException, RemoteException {
		return metrics.getUserActivityOnlyFlow(processId, processVer);
	}

	
	public VersionVO getVersion() throws MetricsServiceException, RemoteException {
		return metrics.getVersion();
	}


}