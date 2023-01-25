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

import com.novell.idm.nrf.soap.ws.Category;
import com.novell.idm.nrf.soap.ws.CategoryKey;
import com.novell.idm.nrf.soap.ws.ConfigProperty;
import com.novell.idm.nrf.soap.ws.Configuration;
import com.novell.idm.nrf.soap.ws.Container;
import com.novell.idm.nrf.soap.ws.DNString;
import com.novell.idm.nrf.soap.ws.Group;
import com.novell.idm.nrf.soap.ws.IdentityType;
import com.novell.idm.nrf.soap.ws.IdentityTypeDnMap;
import com.novell.idm.nrf.soap.ws.LocalizedValue;
import com.novell.idm.nrf.soap.ws.NrfServiceException;
import com.novell.idm.nrf.soap.ws.ResourceAssociation;
import com.novell.idm.nrf.soap.ws.Role;
import com.novell.idm.nrf.soap.ws.RoleAssignment;
import com.novell.idm.nrf.soap.ws.RoleAssignmentRequest;
import com.novell.idm.nrf.soap.ws.RoleAssignmentRequestStatus;
import com.novell.idm.nrf.soap.ws.RoleAssignmentType;
import com.novell.idm.nrf.soap.ws.RoleAssignmentTypeInfo;
import com.novell.idm.nrf.soap.ws.RoleInfo;
import com.novell.idm.nrf.soap.ws.RoleLevel;
import com.novell.idm.nrf.soap.ws.RoleRequest;
import com.novell.idm.nrf.soap.ws.Sod;
import com.novell.idm.nrf.soap.ws.User;
import com.novell.idm.nrf.soap.ws.VersionVO;
import com.novell.idm.nrf.soap.ws.role.IRemoteRole;
import com.novell.idm.nrf.soap.ws.role.RoleService;
import com.novell.soa.ws.portable.Stub;

/**
 * see: https://www.netiq.com/documentation/idm45/agpro/data/bdux8cm.html
 * 
 * @author rhirning
 *
 */
@SuppressWarnings("serial")
public class IdmRoleWebServiceClient implements Serializable {
	/*
	 * nicki.idm.novell.ws.resource.wsdl =
	 * http://172.17.2.91:8180/IDMProv/role/service?wsdl
	 * nicki.idm.novell.ws.resource.user = cn=uaadmin,ou=sa,o=data
	 * nicki.idm.novell.ws.resource.password = netiq000
	 */

	private static String wsdl = Config.getString("nicki.idm.novell.ws.role.wsdl");
	private static String user = Config.getString("nicki.idm.novell.ws.role.user");
	private static String password = Config.getString("nicki.idm.novell.ws.role.password");

	private static IdmRoleWebServiceClient instance;

	private RoleService service;
	private IRemoteRole roleProxy;

	static {
		// for localhost testing only
		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier((hostname, sslSession) -> {
				if (hostname.equals("localhost")) {
					return true;
				}
				return true;
		});
	}

	public static synchronized IdmRoleWebServiceClient getInstance() {
		if (instance == null) {
			instance = new IdmRoleWebServiceClient();
			instance.init();
		}
		return instance;
	}

	private IdmRoleWebServiceClient() {
	}

	private void init() {

		try {
			if (service == null) {
				InitialContext ctx = new InitialContext();
				service = (RoleService) ctx.lookup("xmlrpc:soap:com.novell.soa.af.impl.soap.ResourceService");
				roleProxy = service.getIRemoteRolePort();
				Stub stub = (Stub) roleProxy;
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
	 * Create a resource association and return the resource association object with
	 * the newly created resource association DN.
	 * 
	 * @param resourceAssociation
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public ResourceAssociation createResourceAssociation(ResourceAssociation resourceAssociation)
			throws NrfServiceException, RemoteException {
		return roleProxy.createResourceAssociation(resourceAssociation);
	}

	/**
	 * Creates a new role according to the specified parameters and returns the DN
	 * of the created role.
	 * <br/>
	 * A correlation ID is generated automatically for this method that uses this
	 * format:
	 * <br/><br/>
	 * UserApp#RemoteRoleRequest#xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
	 * <br/><br/>
	 * The correlation ID is used for auditing.
	 * 
	 * @param role
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public DNString createRole(RoleRequest role) throws NrfServiceException, RemoteException {
		return roleProxy.createRole(role);
	}

	/**
	 * Creates a new role with a correlation ID that you provide. The correlation ID
	 * is used for auditing to link a set of related roles. This method returns the
	 * DN of the created role.
	 * 
	 * @param role
	 * @param correlationId
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public DNString createRoleAid(RoleRequest role, String correlationId) throws NrfServiceException, RemoteException {
		return roleProxy.createRoleAid(role, correlationId);
	}

	/**
	 * Deletes a resource association object.
	 * 
	 * @param resourceAssociationDn
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public void deleteResourceAssociation(DNString resourceAssociationDn) throws NrfServiceException, RemoteException {
		roleProxy.deleteResourceAssociation(resourceAssociationDn);
	}

	/**
	 * Finds an array of Role objects based on the search criteria specified in the
	 * given Role object. This method also lets you specify whether to use AND as
	 * the operator for multi-value searches. This method follows a query by example
	 * approach. It allows you to populate a Role object to specify the desired
	 * search criteria. An AND operation is always used across multiple attributes
	 * within the Role search object. For example, you might provide a value for the
	 * name and description attributes, which indicates that the criteria for both
	 * attributes must be satisfied for a successful search.
	 * <br/><br/>
	 * The second parameter (useAndForMultiValueSearch) allows you to specify which
	 * operator should be used for multi-valued attributes (such as when multiple
	 * child roles are provided). A value of true indicates that AND should be used
	 * for these operations, whereas a value of false indicates that OR should be
	 * used.
	 * <br/><br/>
	 * Not all attributes in the Role object can be used for the search expression.
	 * Values found in the non-supported search attributes are ignored.
	 * <br/><br/>
	 * 
	 * Guidelines for Defining Search Criteria in the Role Object
	 * <br/><br/>
	 * <table>
	 * <tr>
	 * <th>
	 * Attribute
	 * </th>
	 * 
	 * <th>
	 * Supported?
	 * </th>
	 * <th>
	 * Description
	 * </th>
	 * </tr>
	 * <tr>
	 * <td>
	 * approvers
	 * </td>
	 * <td>
	 * Yes
	 * </td>
	 * <td>
	 * Uses a standard LDAP equal operator for the search. You can enter multiple
	 * approvers and use the operator parameter to determine whether an AND or an OR
	 * is used for the multi-valued search. You need to provide valid Dns for the
	 * approvers. Note that an approver is made up of multiple parts. It is of type
	 * TypedNameSyntax. You need to specify the sequence number of the approver to
	 * execute a successful search. This is a limitation in LDAP.
	 * <br>
	 * Sample SOAP Request:
	 * <pre>{@code
	 * <ser:findRoleByExampleWithOperatorRequest>
	 *   <ser:role>
	 *     <ser:approvers>
	 *       <!--Zero or more repetitions:-->
	 *       <ser:approver>
	 *         <ser:approverDN>cn=ablake,ou=users,ou=medical-idmsample,o=netiq</ser:approverDN>
	 *         <ser:sequence>1</ser:sequence>
	 *       </ser:approver>
	 *     </ser:approvers>
	 *   </ser:role>
	 *   <ser:operator>false</ser:operator>
	 * </ser:findRoleByExampleWithOperatorRequest>
	 * }</pre>
	 * 
	 * The example above shows how to find roles that have the specified approver
	 * associated with them. An OR search is used since the operator parameter is
	 * set to false.
	 * </td>
	 * </tr>
	 * <tr>
	 * <td>
	 * childRoles
	 * </td>
	 * <td> 
	 * Yes
	 * </td>
	 * <td>
	 * Uses a standard LDAP equal operator for the search. You can enter multiple
	 * child roles and use the operator parameter to determine whether an AND or an
	 * OR is used for the multi-valued search. You need to provide valid Dns for the
	 * child roles.
	 * 
	 * Sample SOAP Request:
	 * 
	 * <pre>{@code
	 * <ser:findRoleByExampleWithOperatorRequest>
	 *   <ser:role>
	 *     <ser:childRoles>
	 *     <!--Zero or more repetitions:-->
	 *       <ser:dnstring>
	 *         <ser:dn>cn=Doctor,cn=Level20,cn=RoleDefs,cn=RoleConfig,cn=AppConfig,cn=PicassoDriver,cn=TestDrivers,o=netiq</ser:dn>
	 *       </ser:dnstring>
	 *       <ser:dnstring>
	 *         <ser:dn>cn=Nurse,cn=Level20,cn=RoleDefs,cn=RoleConfig,cn=AppConfig,cn=PicassoDriver,cn=TestDrivers,o=netiq</ser:dn>
	 *       </ser:dnstring>
	 *     </ser:childRoles>
	 *   </ser:role>
	 *   <ser:operator>false</ser:operator>
	 * </ser:findRoleByExampleWithOperatorRequest>
	 * }</pre>
	 * 
	 * The example above shows how to find roles with a child role of “Doctor” or
	 * “Nurse. An OR search is used since the operator parameter is set to false.
	 * </td></tr>
	 * <tr><td>
	 * description
	 * </td><td>
	 * Yes
	 * </td><td>
	 * 
	 * Uses an LDAP contains search. All entries are prefixed and suffixed with the
	 * * (wild card character). Therefore, a search for “Doctor” translates to
	 * “*Doctor*”. This is to accommodate searches across any localized language.
	 * 
	 * Sample SOAP Request:
	 * 
	 * <pre>{@code
	 * <ser:findRoleByExampleWithOperatorRequest>
	 *   <ser:role>
	 *     <ser:description>Doctor</ser:description>
	 *   </ser:role>
	 *   <ser:operator>false</ser:operator>
	 * </ser:findRoleByExampleWithOperatorRequest>
	 * 
	 * }</pre>
	 * The example above shows how to find roles with a description of “Doctor”.
	 * This description string results in a search string of “*Doctor*”.
	 * </td></tr>
	 * <tr><td>
	 * entityKey
	 * 
	 * </td><td>
	 * 
	 * Yes
	 * 
	 * </td><td>
	 * 
	 * If entered, this attribute causes a getRole operation to be performed. All
	 * other search criteria are ignored in this case.
	 * 
	 * Sample SOAP Request:
	 * 
	 * <pre>{@code
	 * <ser:findRoleByExampleWithOperatorRequest>
	 *   <ser:role>
	 *     <ser:entityKey>cn=Doctor,cn=Level20,cn=RoleDefs,cn=RoleConfig,cn=AppConfig,cn=PicassoDriver,cn=TestDrivers,o=netiq</ser:entityKey>
	 *   </ser:role>
	 *   <ser:operator>false</ser:operator>
	 * </ser:findRoleByExampleWithOperatorRequest>
	 * }</pre>
	 * 
	 * The example above shows how to retrieve a role with a specific entity key.
	 * 
	 * </td></tr>
	 * <tr><td>
	 * implicitContainers
	 * 
	 * </td><td>
	 * 
	 * Yes
	 * 
	 * </td><td>
	 * 
	 * Uses a standard LDAP equal operator for the search. You can enter multiple
	 * implicit containers and use the operator parameter to determine whether an
	 * AND or an OR will be used for the multi-valued search. You need to provide
	 * valid Dns for the implicit containers.
	 * 
	 * Sample SOAP Request:
	 * 
	 * <pre>{@code
	 * <ser:findRoleByExampleWithOperatorRequest>
	 *   <ser:role>
	 *     <ser:implicitContainers>
	 *       <!--Zero or more repetitions:-->
	 *       <ser:dnstring>
	 *         <ser:dn>ou=medical-idmsample,o=netiq</ser:dn> </ser:dnstring>
	 *     </ser:implicitContainers>
	 *   </ser:role>
	 *   <ser:operator>false</ser:operator>
	 * </ser:findRoleByExampleWithOperatorRequest>
	 * }</pre>
	 * 
	 * The example above shows how to find roles that have the specified implicit
	 * container associated with them. An OR search is used since the operator
	 * parameter is set to false.
	 * 
	 * </td></tr>
	 * <tr><td>
	 * implicitGroups
	 * 
	 * </td><td>
	 * 
	 * Yes
	 * 
	 * </td><td>
	 * 
	 * Uses a standard LDAP equal operator for the search. You can enter multiple
	 * implicit groups and use the operator parameter to determine whether an AND or
	 * an OR will be used for the multi-valued search. You need to provide valid Dns
	 * for the implicit groups.
	 * 
	 * Sample SOAP Request:
	 * 
	 * <pre>{@code
	 * <ser:findRoleByExampleWithOperatorRequest>
	 *   <ser:role>
	 *     <ser:implicitGroups>
	 *       <!--Zero or more repetitions:-->
	 *       <ser:dnstring>
	 *         <ser:dn>cn=HR,ou=groups,ou=medical-idmsample,o=netiq</ser:dn>
	 *       </ser:dnstring>
	 *     </ser:implicitGroups>
	 *   </ser:role>
	 *   <ser:operator>false</ser:operator>
	 * </ser:findRoleByExampleWithOperatorRequest>
	 * }</pre>
	 * 
	 * The example above shows how to find roles that have the specified implicit
	 * group associated with them. An OR search is used since the operator parameter
	 * is set to false.
	 * 
	 * </td></tr>
	 * <tr><td>
	 * name
	 * 
	 * 
	 * </td><td>
	 * Yes
	 * 
	 * </td><td>
	 * 
	 * Uses an LDAP contains search. All entries will be prefixed and suffixed with
	 * the * (wild card character). Therefore, a search for “Doctor” translates to
	 * “*Doctor*”. This is to accommodate searches across any localized language.
	 * 
	 * Sample SOAP Request:
	 * <pre>{@code
	 * <ser:findRoleByExampleWithOperatorRequest>
	 *   <ser:role>
	 *     <ser:name>Doctor</ser:name>
	 *   </ser:role>
	 *   <ser:operator>false</ser:operator>
	 * </ser:findRoleByExampleWithOperatorRequest>
	 * }</pre>
	 * 
	 * The above example shows how to find roles with a name of “Doctor”. The name
	 * string results in a search string of “*Doctor*”.
	 * 
	 * </td></tr>
	 * <tr><td>
	 * owners
	 * 
	 * </td><td>
	 * 
	 * Yes
	 * 
	 * </td><td>
	 * 
	 * Uses a standard LDAP equal operator for the search. You can enter multiple
	 * owners and use the operator parameter to determine whether an AND or an OR is
	 * used for the multi-valued search. You must provide valid Dns for the owners.
	 * 
	 * SoapUI Example Request:
	 * <pre>{@code
	 * <ser:findRoleByExampleWithOperatorRequest>
	 *   <ser:role>
	 *     <ser:owners>
	 *       <!--Zero or more repetitions:-->
	 *       <ser:dnstring>
	 *         <ser:dn>cn=ablake,ou=users,ou=medical-idmsample,o=netiq</ser:dn>
	 *       </ser:dnstring>
	 *       <ser:dnstring>
	 *         <ser:dn>cn=mmackenzie,ou=users,ou=medical-idmsample,o=netiq</ser:dn>
	 *       </ser:dnstring>
	 *     </ser:owners>
	 *   </ser:role>
	 *   <ser:operator>true</ser:operator>
	 * </ser:findRoleByExampleWithOperatorRequest>
	 * }</pre>
	 * 
	 * The example above shows how to find roles that have the specified owners. An
	 * AND search is used since the operator parameter is set to true.
	 * 
	 * </td></tr>
	 * <tr><td>
	 * parentRoles
	 * 
	 * </td><td>
	 * 
	 * Yes
	 * 
	 * </td><td>
	 * 
	 * Uses a standard LDAP equal operator for the search. You can enter multiple
	 * parent roles and use the operator parameter to determine whether an AND or an
	 * OR is used for the multi-valued search. You must provide valid Dns for the
	 * parent roles.
	 * 
	 * Sample SOAP Request:
	 * <pre>{@code
	 * <ser:findRoleByExampleWithOperatorRequest>
	 *   <ser:role>
	 *     <ser:parentRoles>
	 *       <!--Zero or more repetitions:-->
	 *       <ser:dnstring>
	 *         <ser:dn>cn=Doctor-East,cn=Level30,cn=RoleDefs,cn=RoleConfig,cn=AppConfig,cn=PicassoDriver,cn=TestDrivers,o=netiq</ser:dn>
	 *       </ser:dnstring>
	 *       <ser:dnstring>
	 *         <ser:dn>cn=Doctor-West,cn=Level30,cn=RoleDefs,cn=RoleConfig,cn=AppConfig,cn=PicassoDriver,cn=TestDrivers,o=netiq</ser:dn>
	 *       </ser:dnstring>
	 *     </ser:parentRoles>
	 *   </ser:role>
	 *   <ser:operator>true</ser:operator>
	 * </ser:findRoleByExampleWithOperatorRequest>
	 * }</pre>
	 * 
	 * The example above shows how to find roles that have the specified parent
	 * roles. An AND search is used since the operator parameter is set to true.
	 * 
	 * </td></tr>
	 * <tr><td>
	 * quorum
	 * 
	 * 
	 * </td><td>
	 * Yes
	 * 
	 * </td><td>
	 * 
	 * Uses a standard LDAP equal operator for the search.
	 * 
	 * Sample SOAP Request:
	 * <pre>{@code
	 * <ser:findRoleByExampleWithOperatorRequest>
	 *   <ser:role>
	 *     <ser:quorum>50%</ser:quorum>
	 *   </ser:role>
	 *   <ser:operator>false</ser:operator>
	 * </ser:findRoleByExampleWithOperatorRequest>
	 * }</pre>
	 * 
	 * The example above shows how to find roles with the specified quorum search
	 * string. The search string can include the wild card character (“*”).
	 * 
	 * </td></tr>
	 * <tr><td>
	 * requestDef
	 * 
	 * </td><td>
	 * 
	 * Yes
	 * 
	 * </td><td>
	 * 
	 * Uses a standard LDAP equal operator for the search. You must provide a valid
	 * DN for the request definition.
	 * 
	 * Sample SOAP Request:
	 * <pre>{@code
	 * <ser:findRoleByExampleWithOperatorRequest>
	 *   <ser:role>
	 *     <ser:requestDef>cn=Role Approval,cn=RequestDefs,cn=AppConfig,cn=PicassoDriver,cn=TestDrivers,o=netiq</ser:requestDef>
	 *   </ser:role>
	 *   <ser:operator>false</ser:operator>
	 * </ser:findRoleByExampleWithOperatorRequest>
	 * }</pre>
	 * 
	 * The example above shows how to find roles with the specified request
	 * definition DN.
	 * 
	 * </td></tr>
	 * <tr><td>
	 * roleCategoryKeys
	 * 
	 * </td><td>
	 * 
	 * Yes
	 * 
	 * </td><td>
	 * 
	 * Uses a standard LDAP equal operator for the search. You can enter multiple
	 * category keys and use the operator parameter to determine whether an AND or
	 * an OR is used for the multi-valued search.
	 * 
	 * Sample SOAP Request:
	 * <pre>{@code
	 * <ser:findRoleByExampleWithOperatorRequest>
	 *   <ser:role>
	 *     <ser:roleCategoryKeys>
	 *       <!--Zero or more repetitions:-->
	 *       <ser:categorykey>
	 *         <ser:categoryKey>doctor</ser:categoryKey>
	 *       </ser:categorykey>
	 *       <ser:categorykey>
	 *         <ser:categoryKey>nurse</ser:categoryKey>
	 *       </ser:categorykey>
	 *     </ser:roleCategoryKeys>
	 *   </ser:role>
	 *   <ser:operator>false</ser:operator>
	 * </ser:findRoleByExampleWithOperatorRequest>
	 * }</pre>
	 * 
	 * The example above shows how to find roles with a category of “doctor” or
	 * “nurse. An OR search is used since the operator parameter is set to false.
	 * 
	 * </td></tr>
	 * <tr><td>
	 * roleLevel
	 * 
	 * </td><td>
	 * 
	 * Yes
	 * 
	 * </td><td>
	 * 
	 * Uses a standard LDAP equal operator for the search. You can only enter one
	 * level at a time.
	 * 
	 * Sample SOAP Request:
	 * <pre>{@code
	 * <ser:findRoleByExampleWithOperatorRequest>
	 *   <ser:role>
	 *     <ser:roleLevel>
	 *       <ser:level>10</ser:level>
	 *     </ser:roleLevel>
	 *   </ser:role>
	 *   <ser:operator>false</ser:operator>
	 * </ser:findRoleByExampleWithOperatorRequest>
	 * }</pre>
	 * 
	 * The example above shows how to find all level 10 roles.
	 * 
	 * </td></tr>
	 * <tr><td>
	 * associatedRoles
	 * 
	 * 
	 * </td><td>
	 * No
	 * 
	 * </td><td>
	 * 
	 * Not supported.
	 * 
	 * </td></tr>
	 * <tr><td>
	 * entitlementRef
	 * 
	 * </td><td>
	 * 
	 * No
	 * 
	 * </td><td>
	 * 
	 * Not supported.
	 * </td></tr>
	 * <tr><td>
	 * 
	 * roleAssignments
	 * 
	 * 
	 * </td><td>
	 * No
	 * 
	 * </td><td>
	 * Not supported.
	 * 
	 * </td></tr>
	 * <tr><td>
	 * systemRole
	 * 
	 * 
	 * </td><td>
	 * No
	 * 
	 * </td><td>
	 * 
	 * Not supported.
	 * </td>
	 * </tr>
	 * </table>
	 * @param searchCriteria
	 * @param useAndForMultiValueSearch
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public Role[] findRoleByExampleWithOperator(Role searchCriteria, boolean useAndForMultiValueSearch)
			throws NrfServiceException, RemoteException {
		return roleProxy.findRoleByExampleWithOperator(searchCriteria, useAndForMultiValueSearch);
	}

	/**
	 * Finds all SoD objects based on the search criteria in the given SOD object.
	 * 
	 * @param sod
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public Sod[] findSodByExample(Sod sod) throws NrfServiceException, RemoteException {
		return roleProxy.findSodByExample(sod);
	}

	/**
	 * Finds all SoD objects based on the search criteria found in the given SOD
	 * object. This method also lets you specify whether to use And as the operator
	 * for multi-value searches.
	 * 
	 * @param sod
	 * @param useAndForMultiValueSearch
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public Sod[] findSodByExampleWithOperator(Sod sod, boolean useAndForMultiValueSearch)
			throws NrfServiceException, RemoteException {
		return roleProxy.findSodByExampleWithOperator(sod, useAndForMultiValueSearch);
	}

	/**
	 * Find by key.
	 * 
	 * @param entityKey
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public Sod findSodById(String entityKey) throws NrfServiceException, RemoteException {
		return roleProxy.findSodById(entityKey);
	}

	/**
	 * Returns returns the list of identities having a particular role DN.
	 * 
	 * @param roleDN
	 * @param identityType
	 * @param directAssignOnly
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public RoleAssignment[] getAssignedIdentities(String roleDN, IdentityType identityType, boolean directAssignOnly)
			throws NrfServiceException, RemoteException {
		return roleProxy.getAssignedIdentities(roleDN, identityType, directAssignOnly);
	}

	/**
	 * Retrieves configuration properties stored in the User Application
	 * configuration XML files by passing in a configuration property key or macro
	 * name.
	 * <br><br>
	 * The configPropertyKey parameter can accept a fully qualified configuration
	 * key name from any of the configuration XML files, such as the following:
	 * <br><br>
	 * <code>DirectoryService/realms/jndi/params/USER_ROOT_CONTAINER</code
	 * <br><br><br>
	 * Alternativelly, the configPropertyKey parameter can accept a macro name that
	 * references a fully qualified configuration key name. The following macro
	 * names are allowed:
	 * <br><br>
	 * Macro Names Allowed
	 * <table>
	 * <tr><th> 
	 * Configuration Macro Name
	 * </th>th>
	 * 
	 * Configuration Key Value
	 * </th></tr>
	 * <tr><td>
	 * USER_CONTAINER
	 * </td><td>
	 * 
	 * DirectoryService/realms/jndi/params/USER_ROOT_CONTAINER
	 * </td></tr>
	 * <tr><td>
	 * 
	 * GROUP_CONTAINER
	 * </td><td>
	 * 
	 * 
	 * DirectoryService/realms/jndi/params/GROUP_ROOT_CONTAINER
	 * </td></tr>
	 * <tr><td>
	 * 
	 * ROOT_CONTAINER
	 * 
	 * </td><td>
	 * 
	 * DirectoryService/realms/jndi/params/ROOT_NAME
	 * </td></tr>
	 * <tr><td>
	 * 
	 * PROVISIONING_DRIVER
	 * 
	 * </td><td>
	 * 
	 * DirectoryService/realms/jndi/params/PROVISIONING_ROOT
	 * </td></tr></table>
	 * @param configPropertyKey
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public ConfigProperty getConfigProperty(String configPropertyKey) throws NrfServiceException, RemoteException {
		return roleProxy.getConfigProperty(configPropertyKey);
	}

	/**
	 * Returns the role system configuration defined in the Role Catalog root
	 * (nrfConfiguration).
	 * 
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public Configuration getConfiguration() throws NrfServiceException, RemoteException {
		return roleProxy.getConfiguration();
	}

	/**
	 * Gets container and role information for a given container DN.
	 * 
	 * @param containerDn
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public Container getContainer(String containerDn) throws NrfServiceException, RemoteException {
		return roleProxy.getContainer(containerDn);
	}

	/**
	 * Returns a list of Sod instances for all SOD violations found for a specific
	 * identity and type.
	 * 
	 * @param identity
	 * @param identityType
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public Sod[] getExceptionsList(String identity, IdentityType identityType)
			throws NrfServiceException, RemoteException {
		return roleProxy.getExceptionsList(identity, identityType);
	}

	/**
	 * Gets group and role information for a given group DN.
	 * 
	 * @param groupDn
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public Group getGroup(String groupDn) throws NrfServiceException, RemoteException {
		return roleProxy.getGroup(groupDn);
	}

	/**
	 * Returns a map of identities which are in violation of a given SoD.
	 * 
	 * @param sodDn
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public IdentityTypeDnMap[] getIdentitiesInViolation(String sodDn) throws NrfServiceException, RemoteException {
		return roleProxy.getIdentitiesInViolation(sodDn);
	}

	/**
	 * Returns a list of Sod instances for all SOD conflicts found for a given list
	 * of roles for a given identity.
	 * 
	 * @param identity
	 * @param identityType
	 * @param requestedRoles
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public Sod[] getIdentityRoleConflicts(String identity, IdentityType identityType, DNString[] requestedRoles)
			throws NrfServiceException, RemoteException {
		return roleProxy.getIdentityRoleConflicts(identity, identityType, requestedRoles);
	}

	/**
	 * Retrieves resource association objects for a given role DN or resource DN. If
	 * the roleDn and resourceDn parameters are null, the entire list is returned.
	 * 
	 * @param resourceAssociationDn
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public ResourceAssociation getResourceAssociation(DNString resourceAssociationDn)
			throws NrfServiceException, RemoteException {
		return roleProxy.getResourceAssociation(resourceAssociationDn);
	}

	/**
	 * Retrieves resource association objects for a given role DN or resource DN. If
	 * the roleDn and resourceDn parameters are null, the entire list is returned.
	 * 
	 * @param roleDn
	 * @param resourceDn
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public ResourceAssociation[] getResourceAssociations(DNString roleDn, DNString resourceDn)
			throws NrfServiceException, RemoteException {
		return roleProxy.getResourceAssociations(roleDn, resourceDn);
	}

	/**
	 * Retrieves a role object defined by a role DN. Returns several role
	 * attributes, such as name, dn, description, role level. Returns child roles,
	 * assigned containers, and assigned groups. However, this API does not return
	 * assigned users. If you want assigned users, use the getAssignedIdentities API
	 * with USER for identityType and true for directAssignOnly.
	 * 
	 * @param roleDn
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public Role getRole(String roleDn) throws NrfServiceException, RemoteException {
		return roleProxy.getRole(roleDn);
	}

	/**
	 * Returns a list of role assignment request status instances given a
	 * correlation ID.
	 * 
	 * @param correlationId
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public RoleAssignmentRequestStatus[] getRoleAssignmentRequestStatus(String correlationId)
			throws NrfServiceException, RemoteException {
		return roleProxy.getRoleAssignmentRequestStatus(correlationId);
	}

	/**
	 * 
	 * @param requestDns
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public RoleAssignmentRequestStatus[] getRoleAssignmentRequestStatusByDN(DNString[] requestDns)
			throws NrfServiceException, RemoteException {
		return roleProxy.getRoleAssignmentRequestStatusByDN(requestDns);
	}

	/**
	 * Returns a list of role assignment request status instances given an identity
	 * and an identity type.
	 * 
	 * @param identityDn
	 * @param identityType
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public RoleAssignmentRequestStatus[] getRoleAssignmentRequestStatusByIdentityType(String identityDn,
			IdentityType identityType) throws NrfServiceException, RemoteException {
		return roleProxy.getRoleAssignmentRequestStatusByIdentityType(identityDn, identityType);
	}

	/**
	 * Retrieves details about a RoleAssignmentType.
	 * 
	 * @param type
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public RoleAssignmentTypeInfo getRoleAssignmentTypeInfo(RoleAssignmentType type)
			throws NrfServiceException, RemoteException {
		return roleProxy.getRoleAssignmentTypeInfo(type);
	}

	/**
	 * Gets role categories.
	 * 
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public Category[] getRoleCategories() throws NrfServiceException, RemoteException {
		return roleProxy.getRoleCategories();
	}

	/**
	 * Returns a list of Sod instances found for all given roles. This method always
	 * returns a list.
	 * 
	 * @param roles
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public Sod[] getRoleConflicts(DNString[] roles) throws NrfServiceException, RemoteException {
		return roleProxy.getRoleConflicts(roles);
	}

	/**
	 * Gets the role levels.
	 * 
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public RoleLevel[] getRoleLevels() throws NrfServiceException, RemoteException {
		return roleProxy.getRoleLevels();
	}

	/**
	 * Gets role localized strings, such as names and descriptions. The method takes
	 * an integer parameter that allows you to specify the type of the string. The
	 * number 1 indicates names; the number 2 indicates descriptions.
	 * 
	 * @param roleDn
	 * @param type
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public LocalizedValue[] getRoleLocalizedStrings(DNString roleDn, int type)
			throws NrfServiceException, RemoteException {
		return roleProxy.getRoleLocalizedStrings(roleDn, type);
	}

	/**
	 * Returns a list of RoleInfo instances given a list of role DNs.
	 * 
	 * @param roleDns
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public RoleInfo[] getRolesInfo(DNString[] roleDns) throws NrfServiceException, RemoteException {
		return roleProxy.getRolesInfo(roleDns);
	}

	/**
	 * Returns a list of RoleInfo instances given a list of role category keys.
	 * 
	 * @param roleCategoryKeys
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public RoleInfo[] getRolesInfoByCategory(CategoryKey[] roleCategoryKeys)
			throws NrfServiceException, RemoteException {
		return roleProxy.getRolesInfoByCategory(roleCategoryKeys);
	}

	/**
	 * Returns a list of RoleInfo instances given a list of role levels.
	 * 
	 * @param roleLevels
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public RoleInfo[] getRolesInfoByLevel(long[] roleLevels) throws NrfServiceException, RemoteException {
		return roleProxy.getRolesInfoByLevel(roleLevels);
	}

	/**
	 * Returns a list of Sod instances for all SOD conflicts defined between the
	 * target role DN and the source role DN.
	 * 
	 * @param targetName
	 * @param sourceName
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public Sod[] getTargetSourceConflicts(String targetName, String sourceName)
			throws NrfServiceException, RemoteException {
		return roleProxy.getTargetSourceConflicts(targetName, sourceName);
	}

	/**
	 * Gets user info including all role assignments for a given user DN stored in a
	 * UserIdentity object.
	 * 
	 * @param userDn
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public User getUser(String userDn) throws NrfServiceException, RemoteException {
		return roleProxy.getUser(userDn);
	}

	/**
	 * Returns the version of this Web Service.
	 * 
	 * @return
	 * @throws RemoteException
	 */
	public VersionVO getVersion() throws RemoteException {
		return roleProxy.getVersion();
	}

	/**
	 * Returns boolean flag; true if role has been assigned to a User identity.
	 * 
	 * @param userDn
	 * @param roleDn
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public boolean isUserInRole(String userDn, String roleDn) throws NrfServiceException, RemoteException {
		return roleProxy.isUserInRole(userDn, roleDn);
	}

	/**
	 * Modifies a role definition. This method does not update localized strings.
	 * Use the getRoleLocalizedStrings(DNString roleDn, LocalizedString[]
	 * locStrings, int strType) method to update localized names or descriptions for
	 * a role.
	 * 
	 * A correlation ID is generated automatically for this method that uses this
	 * format:
	 * <br>
	 * <code>
	 * UserApp#RemoteRoleRequest#xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
	 * </code><br>
	 * The correlation ID is used for auditing.
	 * 
	 * @param role
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public Role modifyRole(Role role) throws NrfServiceException, RemoteException {
		return roleProxy.modifyRole(role);
	}

	/**
	 * Modifies a role definition with a correlation ID that you provide. The
	 * correlation ID is used for auditing to link a set of related roles. This
	 * method does not update localized strings. Use the
	 * <code>
	 * getRoleLocalizedStrings(DNString roleDn, LocalizedString[] locStrings, int  strType)
	 * </code> method to update localized names or descriptions for a role.
	 * 
	 * @param role
	 * @param correlationId
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public Role modifyRoleAid(Role role, String correlationId) throws NrfServiceException, RemoteException {
		return roleProxy.modifyRoleAid(role, correlationId);
	}

	/**
	 * Deletes specified roles from the Role Catalog and returns an array of DNs for
	 * the deleted roles as a confirmation.
	 * 
	 * A correlation ID is generated automatically for this method that uses this
	 * format:
	 * 
	 * <br>
	 * <code>
	 * UserApp#RemoteRoleRequest#xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
	 * </code><br>
	 * 
	 * The correlation ID is used for auditing.
	 * 
	 * @param roleDns
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public DNString[] removeRoles(DNString[] roleDns) throws NrfServiceException, RemoteException {
		return roleProxy.removeRoles(roleDns);
	}

	/**
	 * Deletes specified roles from the Role Catalog with a correlation ID that you
	 * provide. The correlation ID is used for auditing to link a set of related
	 * roles. This method returns an array of DNs for the deleted roles as a
	 * confirmation.
	 * 
	 * @param roleDns
	 * @param correlationId
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public DNString[] removeRolesAid(DNString[] roleDns, String correlationId)
			throws NrfServiceException, RemoteException {
		return roleProxy.removeRolesAid(roleDns, correlationId);
	}

	/**
	 * Returns a list of request DNs created by the role assignment.
	 * 
	 * If you do not want to supply date (effective or expiration) for role
	 * assignments with the requestRolesAssignment endpoint, then you must remove
	 * these two elements from the SOAP call. They must not be included with empty
	 * tags:
	 * 
	 * <ser:effectiveDate/> <ser:expirationDate/>
	 * 
	 * If you want to omit the effective date or the expiration date, a request
	 * similar to the following will work:
	 * <pre>{@code
	 * <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://www.netiq.com/role/service">
	 *   <soapenv:Header/>
	 *   <soapenv:Body>
	 *     <ser:requestRolesAssignmentRequest>
	 *       <!--Optional:-->
	 *       <ser:assignRequest>
	 *         <ser:actionType>grant</ser:actionType>
	 *         <ser:assignmentType>USER_TO_ROLE</ser:assignmentType>
	 *         <ser:correlationID>testpolina</ser:correlationID>
	 *         <ser:identity>cn=uaadmin,ou=sa,o=data</ser:identity>
	 *         <ser:originator/>
	 *         <ser:reason>test without expiration date</ser:reason>
	 *         <ser:roles>
	 *           <!--Zero or more repetitions:-->
	 *           <ser:dnstring>
	 *             <ser:dn>cn=test2 id,cn=Level10,cn=RoleDefs,cn=RoleConfig,cn=AppConfig,cn=User Application Driver,cn=driverset1,o=system</ser:dn>
	 *           </ser:dnstring>
	 *         </ser:roles>
	 *         <ser:sodOveridesRequested/>
	 *       </ser:assignRequest>
	 *     </ser:requestRolesAssignmentRequest>
	 *   </soapenv:Body>
	 * </soapenv:Envelope>
	 * }</pre>
	 * With that said, without the these two elements in the soap request, the
	 * request will not validate. It will work, but will not validate.
	 * 
	 * @param roleAssignmentRequest
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public DNString[] requestRolesAssignment(RoleAssignmentRequest roleAssignmentRequest)
			throws NrfServiceException, RemoteException {
		return roleProxy.requestRolesAssignment(roleAssignmentRequest);
	}

	/**
	 * ets role localized strings, such as names and descriptions.
	 * 
	 * A correlation ID is generated automatically for this method that uses this
	 * format:
	 * 
	 * UserApp#RemoteRoleRequest#xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
	 * 
	 * The correlation ID is used for auditing.
	 * 
	 * @param roleDn
	 * @param locStrings
	 * @param type
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public LocalizedValue[] setRoleLocalizedStrings(DNString roleDn, LocalizedValue[] locStrings, int type)
			throws NrfServiceException, RemoteException {
		return roleProxy.setRoleLocalizedStrings(roleDn, locStrings, type);
	}

	/**
	 * Sets role localized strings, such as name and description, with a correlation
	 * ID that you provide. The correlation ID is used for auditing to link a set of
	 * related roles.
	 * 
	 * @param roleDn
	 * @param correlationId
	 * @param locStrings
	 * @param type
	 * @return
	 * @throws NrfServiceException
	 * @throws RemoteException
	 */
	public LocalizedValue[] setRoleLocalizedStringsAid(DNString roleDn, String correlationId,
			LocalizedValue[] locStrings, int type) throws NrfServiceException, RemoteException {
		return roleProxy.setRoleLocalizedStringsAid(roleDn, correlationId, locStrings, type);
	}

}
