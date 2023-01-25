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

import javax.naming.InitialContext;

import org.mgnl.nicki.core.config.Config;

import com.novell.idm.nrf.soap.ws.resource.CategoryKey;
import com.novell.idm.nrf.soap.ws.resource.CodeMapRefreshStatus;
import com.novell.idm.nrf.soap.ws.resource.CodeMapValue;
import com.novell.idm.nrf.soap.ws.resource.CodeMapValueStatus;
import com.novell.idm.nrf.soap.ws.resource.CrudResourceRequestStatus;
import com.novell.idm.nrf.soap.ws.resource.DNString;
import com.novell.idm.nrf.soap.ws.resource.EntitlementRefreshInfo;
import com.novell.idm.nrf.soap.ws.resource.IRemoteResource;
import com.novell.idm.nrf.soap.ws.resource.LocalizedValue;
import com.novell.idm.nrf.soap.ws.resource.NrfServiceException;
import com.novell.idm.nrf.soap.ws.resource.ProvisioningCodeMap;
import com.novell.idm.nrf.soap.ws.resource.Resource;
import com.novell.idm.nrf.soap.ws.resource.ResourceAssignment;
import com.novell.idm.nrf.soap.ws.resource.ResourceAssignmentRequestStatus;
import com.novell.idm.nrf.soap.ws.resource.ResourceInfo;
import com.novell.idm.nrf.soap.ws.resource.ResourceRequestParam;
import com.novell.idm.nrf.soap.ws.resource.ResourceService;
import com.novell.soa.ws.portable.Stub;

@SuppressWarnings("serial")
public class IdmResourceWebServiceClient implements Serializable {
	/*
	 * nicki.idm.novell.ws.resource.wsdl = http://172.17.2.91:8180/IDMProv/resource/service?wsdl
	 * nicki.idm.novell.ws.resource.user = cn=uaadmin,ou=sa,o=data
	 * nicki.idm.novell.ws.resource.password = netiq000
	 */

	private static String wsdl = Config.getString("nicki.idm.novell.ws.resource.wsdl");
	private static String user = Config.getString("nicki.idm.novell.ws.resource.user");
	private static String password = Config.getString("nicki.idm.novell.ws.resource.password");

	private static IdmResourceWebServiceClient instance;

	private ResourceService service;
	private IRemoteResource res;

	static {
		// for localhost testing only
		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier((hostname, sslSession) -> {
				if (hostname.equals("localhost")) {
					return true;
				}
				return true;
		});
	}

	public static synchronized IdmResourceWebServiceClient getInstance() {
		if (instance == null) {
			instance = new IdmResourceWebServiceClient();
			instance.init();
		}
		return instance;
	}

	private IdmResourceWebServiceClient() {
	}

	private void init() {

		try {
			if (service == null) {
				InitialContext ctx = new InitialContext();
				service = (ResourceService) ctx.lookup("xmlrpc:soap:com.novell.soa.af.impl.soap.ResourceService");
				res = service.getIRemoteResourcePort();
				Stub stub = (Stub) res;
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
	 * Checks to see if a particular value exists in the code map table for a
	 * specified entitlement and logical system. The method returns the status for
	 * the code map value as a CodeMapValueStatus object.
	 * 
	 * This method is one of three SOAP endpoints to help you keep the code map
	 * tables for the Roles Based Provisioning Module synchronized with the code map
	 * tables for the Role Mapping Administrator. The user interface for the Role
	 * Mapping Administrator can trigger a code map refresh if a mismatch is
	 * discovered while a user is creating mappings. In addition, the Roles Based
	 * Provisioning Module allows you to use the three SOAP endpoints to refresh
	 * selected entitlements within its code map tables.
	 * 
	 * In addition to checkCodeMapValueStatus, the Roles Based Provisioning Module
	 * includes the following endpoints to help with code map synchronization:
	 * 
	 * getRefreshStatus
	 * 
	 * refreshCodeMap
	 * 
	 * The Entitlement Query Settings section of the Configure Roles and Resources
	 * Settings page in the User Application allows you to specify how often the
	 * Roles Based Provisioning Module code map tables are refreshed and also start
	 * a manual refresh. However, this page does not allow to refresh selected
	 * entitlements. To control which entitlements are refreshed, you need to use
	 * the SOAP endpoints.
	 * 
	 * For additional information on the getRefreshStatus endpoint, see
	 * getRefreshStatus. For additional information on the refreshCodeMap endpoint,
	 * see refreshCodeMap.
	 * 
	 * For code samples that use the new methods for code map synchronization, see
	 * Code Map Synchronization Code Samples.
	 * 
	 * @param entitilementDN
	 *            entitlement DN as a string.
	 * @param connectionName
	 *            connection (logical system) name. This is an optional parameter.
	 *            Only fanout drivers need to specify the connection name.
	 * @param codeMapValue
	 *            code map value to verify.
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public CodeMapValueStatus checkCodeMapValueStatus(String entitilementDN, String connectionName, String codeMapValue)
			throws NrfServiceException, RemoteException {
		return res.checkCodeMapValueStatus(entitilementDN, connectionName, codeMapValue);
	}

	/**
	 * Creates a new resource according to the specified parameters, and returns a
	 * DN of the created resource.
	 * 
	 * A correlation ID is generated automatically for this method that uses this
	 * format:
	 * 
	 * UserApp#RemoteResourceRequest#xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
	 * 
	 * The correlation ID is used for auditing.
	 * 
	 * @param resource
	 *            specifies the resource object to create.
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public String createResource(Resource resource) throws NrfServiceException, RemoteException {
		return res.createResource(resource);
	}

	/**
	 * Creates a new resource, with a correlation ID that you provide. The
	 * correlation ID is used for auditing to link a set of related resources. This
	 * method creates the resource according to the specified parameters, and
	 * returns a DN of the created resource.
	 * 
	 * @param resource
	 * @param correlationId
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public String createResourceAid(Resource resource, String correlationId)
			throws NrfServiceException, RemoteException {
		return res.createResourceAid(resource, correlationId);
	}

	/**
	 * Finds all Resource objects based on the search criteria specified in the
	 * given Resource object.
	 * 
	 * @param searchCriteria
	 *            specifies Query by Example (QBE) search criteria within a Resource
	 *            object.
	 * @param useAndForMultiValueSearch
	 *            determines whether AND or OR will be used for multi-value search
	 *            expressions. If you specify a value of true, AND will be used for
	 *            multi-value searches; if you specify a value of false, OR will be
	 *            used.
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public Resource[] findResourceByExampleWithOperator(Resource searchCriteria, boolean useAndForMultiValueSearch)
			throws NrfServiceException, RemoteException {
		return res.findResourceByExampleWithOperator(searchCriteria, useAndForMultiValueSearch);
	}

	/**
	 * 
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public String flushDriverCache() throws NrfServiceException, RemoteException {
		return res.flushDriverCache();
	}

	/**
	 * 
	 * @param resourceDn
	 *            DN of the target resource
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public ResourceAssignment[] getAssignmentsForResource(String resourceDn)
			throws NrfServiceException, RemoteException {
		return getAssignmentsForResource(resourceDn);
	}

	public CodeMapValue[] getCodeMapValues(String arg0, String arg1, String arg2, String arg3, String arg4)
			throws NrfServiceException, RemoteException {
		return res.getCodeMapValues(arg0, arg1, arg2, arg3, arg4);
	}

	/**
	 * Returns an array of ProvisioningCodeMap objects, which include code map
	 * information from the code map and code map label tables.
	 * 
	 * @param codeMapKey
	 *            specifies the code map key to retrieve values from. The codeMapKey
	 *            is a GUID that acts as a unique identifier for the code map. For
	 *            example:
	 * 
	 *            \2d\13\d1\a4\7b\99\d6\4c\03\9a\2d\13\d1\a4\7b\99
	 * @param type
	 *            specifies the code map type. A value of 0 filters the list to
	 *            include entitlement code maps only.
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public ProvisioningCodeMap[] getEntitlementCodeMap(String codeMapKey, int type)
			throws NrfServiceException, RemoteException {
		return res.getEntitlementCodeMap(codeMapKey, type);
	}

	/**
	 * Gets the refresh status of a code map based on a specified entitlement DN.
	 * This method returns the status as an array of CodeMapRefreshStatus objects.
	 * The structure returned contains the DN, GUID, connection name status, and
	 * last refresh time.
	 * 
	 * This method is one of three SOAP endpoints to help you keep the code map
	 * tables for the Roles Based Provisioning Module synchronized with the code map
	 * tables for the Role Mapping Administrator. The user interface for the Role
	 * Mapping Administrator can trigger a code map refresh if a mismatch is
	 * discovered while a user is creating mappings. In addition, the Roles Based
	 * Provisioning Module allows you to use the three SOAP endpoints to refresh
	 * selected entitlements within its code map tables.
	 * 
	 * In addition to getRefreshStatus, the Roles Based Provisioning Module includes
	 * the following endpoints to help with code map synchronization:
	 * 
	 * checkCodeMapValueStatus
	 * 
	 * refreshCodeMap
	 * 
	 * The Entitlement Query Settings section of the Configure Roles and Resources
	 * Settings page in the User Application allows you to specify how often the
	 * Roles Based Provisioning Module code map tables are refreshed and also start
	 * a manual refresh. However, this page does not allow to refresh selected
	 * entitlements. To control which entitlements are refreshed, you need to use
	 * the SOAP endpoints.
	 * 
	 * For additional information on the checkCodeMapValueStatus endpoint, see
	 * checkCodeMapValueStatus. For additional information on the refreshCodeMap
	 * endpoint, see refreshCodeMap.
	 * 
	 * For code samples that use the new methods for code map synchronization, see
	 * Code Map Synchronization Code Samples.
	 * 
	 * @param entitlementDN
	 *            entitlement DN as a string
	 * 
	 *            For example:
	 * 
	 *            cn=groups,cn=groupentitlementloopback,cn=driverset1,o=system
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public CodeMapRefreshStatus[] getRefreshStatus(String entitlementDN) throws NrfServiceException, RemoteException {
		return res.getRefreshStatus(entitlementDN);
	}

	/**
	 * Returns a resource object.
	 * 
	 * @param dn
	 *            specifies the DN of the resource you want to retrieve.
	 * @param locale
	 *            supplies an iso639 language code to format localized string
	 *            values; if the parameter is null, the language defaults to the
	 *            servlet request locale.
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public Resource getResource(String dn, String locale) throws NrfServiceException, RemoteException {
		return res.getResource(dn, locale);
	}

	/**
	 * Returns the resource assignments for the current user.
	 * 
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public ResourceAssignment[] getResourceAssignmentsForCurrentUser() throws NrfServiceException, RemoteException {
		return res.getResourceAssignmentsForCurrentUser();
	}

	/**
	 * 
	 * @param userDn
	 *            of the target user
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public ResourceAssignment[] getResourceAssignmentsForUser(String userDn)
			throws NrfServiceException, RemoteException {
		return res.getResourceAssignmentsForUser(userDn);
	}

	/**
	 * Gets the localized strings for a resource, such as the names and
	 * descriptions. The type parameter lets you specify whether the names or
	 * descriptions should be retrieved.
	 * 
	 * @param resourceDn
	 *            specifies the DN of the resource for which you want to get the
	 *            localized strings.
	 * @param type
	 *            specifies the type of localized strings you want to retrieve. A
	 *            type value of 1 retrieves a list of names for the resource,
	 *            whereas a type value of 2 retrieves a list of descriptions.
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public LocalizedValue[] getResourceLocalizedStrings(String resourceDn, int type)
			throws NrfServiceException, RemoteException {
		return res.getResourceLocalizedStrings(resourceDn, type);
	}

	/**
	 * 
	 * @param correlationId
	 *            specifies a resource assignment request correlation ID.
	 * @param locale
	 *            supplies an iso639 language code to format localized string
	 *            values; if the parameter is null, the language defaults to the
	 *            servlet request locale.
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public ResourceAssignmentRequestStatus[] getResourceRequestStatusByCorrelationId(String correlationId,
			String locale) throws NrfServiceException, RemoteException {
		return res.getResourceRequestStatusByCorrelationId(correlationId, locale);
	}

	/**
	 * Returns all resource assignment request status items for a particular user
	 * identity.
	 * 
	 * @param identity
	 *            specifies the DN for a user.
	 * @param locale
	 *            supplies an iso639 language code to format localized string
	 *            values; if the parameter is null, the language defaults to the
	 *            servlet request locale.
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public ResourceAssignmentRequestStatus[] getResourceRequestStatusByIdentity(String identity, String locale)
			throws NrfServiceException, RemoteException {
		return res.getResourceRequestStatusByIdentity(identity, locale);
	}

	/**
	 * Returns all resource request status items for the authenticated user.
	 * 
	 * @param locale
	 *            supplies an iso639 language code to format localized string
	 *            values; if the parameter is null, the language defaults to the
	 *            servlet request locale.
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public ResourceAssignmentRequestStatus[] getResourceRequestStatusForCurrentUser(String locale)
			throws NrfServiceException, RemoteException {
		return res.getResourceRequestStatusForCurrentUser(locale);
	}

	/**
	 * Returns a list of ResourceInfo instances given a list of resource DNs.
	 * 
	 * @param resDns
	 *            provides a list of resource DNs for which you want to retrieve
	 *            resource information objects.
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public ResourceInfo[] getResourcessInfo(DNString[] resDns) throws NrfServiceException, RemoteException {
		return res.getResourcessInfo(resDns);
	}

	/**
	 * Returns a list of ResourceInfo instances given a list of category keys.
	 * 
	 * @param resourceCategoryKeys
	 *            specifies the list of resource category keys to retrieve resource
	 *            information objects for.
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public ResourceInfo[] getResourcessInfoByCategory(CategoryKey[] resourceCategoryKeys)
			throws NrfServiceException, RemoteException {
		return res.getResourcessInfoByCategory(resourceCategoryKeys);
	}

	/**
	 * Modifies a resource definition. This method does not perform a localized
	 * string modification update. To update the localized names or descriptions for
	 * a resource, you need to use the setResourceLocalizedStrings method.
	 * 
	 * A correlation ID is generated automatically for this method that uses this
	 * format:
	 * 
	 * UserApp#RemoteResourceRequest#xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
	 * 
	 * @param resource
	 *            specifies the resource object to modify.
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public Resource modifyResource(Resource resource) throws NrfServiceException, RemoteException {
		return res.modifyResource(resource);
	}

	/**
	 * Modifies a resource definition, with a correlation ID that you provide. The
	 * correlation ID is used for auditing to link a set of related resources. This
	 * method does not perform a localized string modification update. To update the
	 * localized names or descriptions for a resource, you need to use the
	 * setResourceLocalizedStrings method.
	 * 
	 * @param resource
	 * @param correlationId
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public Resource modifyResourceAid(Resource resource, String correlationId)
			throws NrfServiceException, RemoteException {
		return res.modifyResourceAid(resource, correlationId);
	}

	/**
	 * Refreshes the code map based on a specified entitlement DN. The method
	 * returns the status of the refresh operation in the form of an
	 * EntitlementRefreshInfo object. This structure includes the detailed status as
	 * an array of CodeMapRefreshStatus objects.
	 * 
	 * This method is one of three SOAP endpoints to help you keep the code map
	 * tables for the Roles Based Provisioning Module synchronized with the code map
	 * tables for the Role Mapping Administrator. The user interface for the Role
	 * Mapping Administrator can trigger a code map refresh if a mismatch is
	 * discovered while a user is creating mappings. In addition, the Roles Based
	 * Provisioning Module allows you to use the three SOAP endpoints to refresh
	 * selected entitlements within its code map tables.
	 * 
	 * In addition to refreshCodeMap, the Roles Based Provisioning Module includes
	 * the following endpoints to help with code map synchronization:
	 * 
	 * checkCodeMapValueStatus
	 * 
	 * getRefreshStatus
	 * 
	 * The Entitlement Query Settings section of the Configuration > Roles and
	 * Resources page in the Identity Manager Dashboard allows you to specify how
	 * often the Roles Based Provisioning Module code map tables are refreshed and
	 * also start a manual refresh.
	 * 
	 * For additional information on the checkCodeMapValueStatus endpoint, see
	 * checkCodeMapValueStatus. For additional information on the getRefreshStatus
	 * endpoint, see getRefreshStatus.
	 * 
	 * For code samples that use the new methods for code map synchronization, see
	 * Code Map Synchronization Code Samples.
	 * 
	 * @param entitlementDN
	 *            entitlement DN to refresh the code map
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public EntitlementRefreshInfo refreshCodeMap(String entitlementDN) throws NrfServiceException, RemoteException {
		return res.refreshCodeMap(entitlementDN);
	}

	/**
	 * Deletes a specified resource from the Resource Catalog. Returns the DN for
	 * the deleted resource as a confirmation.
	 * 
	 * A correlation ID is generated automatically for this method that uses this
	 * format:
	 * 
	 * UserApp#RemoteResourceRequest#xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
	 * 
	 * The correlation ID is used for auditing.
	 * 
	 * @param resourceDn
	 *            specifies the DN of the resource to delete.
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public DNString removeResource(DNString resourceDn) throws NrfServiceException, RemoteException {
		return res.removeResource(resourceDn);
	}

	/**
	 * Deletes a specified resource from the Resource Catalog, with a correlation ID
	 * that you provide. The correlation ID is used for auditing to link a set of
	 * related resources. This method returns the DN for the deleted resource as a
	 * confirmation.
	 * 
	 * @param resourceDn
	 * @param correlationId
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public DNString removeResourceAid(DNString resourceDn, String correlationId)
			throws NrfServiceException, RemoteException {
		return res.removeResourceAid(resourceDn, correlationId);
	}

	/**
	 * Makes a grant resource request and returns a resource request correlation ID.
	 * 
	 * @param resourceTarget
	 *            specifies the target resource DN.
	 * @param requester
	 *            supplies an identifier for the remote client application making
	 *            the request to grant the resource.
	 * 
	 *            The requester parameter on this SOAP endpoint identifies the
	 *            originator of the request. This value is set in the resource
	 *            request object nrfOriginator attribute, following this convention:
	 * 
	 *            For a SOAP call: "REMOTE_CLIENT:<requester param value>"
	 * 
	 *            For a workflow action: "WF:<wf process id>"
	 * @param userTarget
	 *            specifies the DN for the being granted the resource.
	 * @param reasonForRequest
	 *            provides a reason for the request.
	 * @param requestParams
	 *            provides the parameter values for the request.
	 * @param correlationId
	 *            specifies a resource assignment request correlation ID; if the
	 *            parameter is null, a correlation ID is generated.
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public String requestResourceGrant(String resourceTarget, String requester, String userTarget,
			String reasonForRequest, ResourceRequestParam[] requestParams, String correlationId)
			throws NrfServiceException, RemoteException {
		return res.requestResourceGrant(resourceTarget, requester, userTarget, reasonForRequest, requestParams,
				correlationId);
	}

	/**
	 * Makes a revoke resource request and returns a resource request correlation
	 * ID.
	 * 
	 * The revoke invocation behavior mirrors the behavior for a grant opeation,
	 * except that a revoke request for the resource is posted on the server.
	 * 
	 * @param resourceTarget
	 *            specifies the target resource DN.
	 * @param requester
	 *            supplies an identifier for the remote client application making
	 *            the request to revoke the resource.
	 * 
	 *            The requester parameter on this SOAP endpoint identifies the
	 *            originator of the request. This value is set in the resource
	 *            request object nrfOriginator attribute, following this convention:
	 * 
	 *            For a SOAP call: "REMOTE_CLIENT:<requester param value>"
	 * 
	 *            For a workflow action: "WF:<wf process id>"
	 * 
	 *            For the user application user interface: "USER_APP"
	 * @param userTarget
	 *            specifies the DN for the user being granted the resource.
	 * @param reasonForRequest
	 *            provides a reason for the request.
	 * @param requestParams
	 *            provides the parameter values for the request.
	 * @param instanceGuid
	 *            provides a GUID identifier for the resource assignment instance.
	 *            The resource assignment instance GUID supports revoking a single
	 *            instance of a multi-value resource assignment, if not all
	 *            instances are to be revoked.
	 * 
	 *            IMPORTANT:If you do not specify the instanceGuid value, and the
	 *            user has more than one value of that resource assigned, all
	 *            instances of the resource assignment will be removed.
	 * 
	 *            When you create a new resource assignment request, the
	 *            instanceGuid is included just above the correlationid field:
	 * 
	 *            <ser:instanceGuid></ser:instanceGuid>
	 * 
	 *            You need to specify the instance of the resource you want to
	 *            revoke by supplying the value in the instanceGuid parameter.
	 * 
	 *            To find out which resources are assigned to a user, you need to
	 *            use the getResourceAssignmentsForUser method. This method returns
	 *            the following data structure, which also includes the
	 *            instanceGuid:
	 * 
	 *            <resourceassignment>
	 *            <instanceGuid>1b335aa9f4a14bd4a2a802eb4ba092da</instanceGuid>
	 *            <reason>3b-Test</reason>
	 *            <recipientDn>cn=ablake,ou=users,o=data</recipientDn>
	 *            <requestDate>2011-08-18T14:25:21</requestDate> <requestParams>
	 *            <resourcerequestparam> <name>param1</name> <value>3a3a</value>
	 *            </resourcerequestparam> </requestParams>
	 *            <requesterDn>cn=uaadmin,ou=sa,o=data</requesterDn>
	 * 
	 *            <resourceDn>cn=Vodacom,cn=ResourceDefs,cn=RoleConfig,cn=AppConfig,cn=User
	 *            Application Driver,cn=driverset1,o=system</resourceDn>
	 *            </resourceassignment>
	 * @param correlationId
	 *            specifies a resource assignment request correlation ID; if the
	 *            parameter is null, a correlation ID is generated.
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public String requestResourceRevoke(String resourceTarget, String requester, String userTarget,
			String reasonForRequest, ResourceRequestParam[] requestParams, String instanceGuid, String correlationId)
			throws NrfServiceException, RemoteException {
		return res.requestResourceRevoke(resourceTarget, requester, userTarget, reasonForRequest, requestParams,
				instanceGuid, correlationId);
	}

	public CrudResourceRequestStatus setAllResourceLocalizedStrings(String arg0, LocalizedValue[] arg1,
			LocalizedValue[] arg2) throws NrfServiceException, RemoteException {
		return res.setAllResourceLocalizedStrings(arg0, arg1, arg2);
	}

	public CrudResourceRequestStatus setAllResourceLocalizedStringsWithCorrelationId(String arg0, LocalizedValue[] arg1,
			LocalizedValue[] arg2, String arg3) throws NrfServiceException, RemoteException {
		return res.setAllResourceLocalizedStringsWithCorrelationId(arg0, arg1, arg2, arg3);
	}

	/**
	 * Sets the localized strings for a resource, such as the names and
	 * descriptions.
	 * 
	 * A correlation ID is generated automatically for this method that uses this
	 * format:
	 * 
	 * UserApp#RemoteResourceRequest#xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
	 * 
	 * @param resourceDn
	 *            specifies the DN of the resource for which you want to set the
	 *            localized strings.
	 * @param locStrings
	 *            provides an array of localized strings you want to define.
	 * @param type
	 *            specifies the type of localized strings you want to retrieve. A
	 *            type value of 1 retrieves a list of names for the resource,
	 *            whereas a type value of 2 retrieves a list of descriptions.
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public LocalizedValue[] setResourceLocalizedStrings(String resourceDn, LocalizedValue[] locStrings, int type)
			throws NrfServiceException, RemoteException {
		return res.setResourceLocalizedStrings(resourceDn, locStrings, type);
	}

	/**
	 * Sets the localized strings for a resource, such as the names and
	 * descriptions, with a correlation ID that you provide. The correlation ID is
	 * used for auditing to link a set of related resources.
	 * 
	 * @param resourceDn
	 * @param locStrings
	 * @param type
	 * @param correlationId
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public LocalizedValue[] setResourceLocalizedStringsAid(String resourceDn, LocalizedValue[] locStrings, int type,
			String correlationId) throws NrfServiceException, RemoteException {
		return res.setResourceLocalizedStringsAid(resourceDn, locStrings, type, correlationId);
	}

}
