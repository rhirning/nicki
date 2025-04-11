
package org.mgnl.nicki.idm.novell.shop.objects;

/*-
 * #%L
 * nicki-idm-novell-shop
 * %%
 * Copyright (C) 2017 Ralf Hirning
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


import java.util.Date;

import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.annotation.StructuredDynamicAttribute;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.dynamic.objects.objects.Person;

// TODO: Auto-generated Javadoc
/**
 * The Class Resource.
 */
@DynamicObject
@ObjectClass("nrfResource")
public class Resource extends DynamicStructObject {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3550648745922369437L;
	
	/** The name. */
	@DynamicAttribute(externalName="cn", naming=true)
	private String name;
	
	/** The entitlement. */
	@StructuredDynamicAttribute(externalName="nrfEntitlementRef", foreignKey=Entitlement.class)
	private String entitlement;
	
	/** The approver. */
	@StructuredDynamicAttribute(externalName="nrfApprovers", foreignKey=Person.class)
	private String[] approver;
	
	/** The revoke approver. */
	@StructuredDynamicAttribute(externalName="nrfRevokeApprovers", foreignKey=Person.class)
	private String[] revokeApprover;
	
	/** The localized name. */
	@DynamicAttribute(externalName="nrfLocalizedNames")
	private String localizedName;
	
	/** The localized description. */
	@DynamicAttribute(externalName="nrfLocalizedDescrs")
	private String localizedDescription;
	
	/**
	 * Gets the entitlement.
	 *
	 * @return the entitlement
	 * @throws DynamicObjectException the dynamic object exception
	 */
	public Entitlement getEntitlement() throws DynamicObjectException {
		try {
			return (Entitlement) execute("getEntitlement", null);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Gets the start time.
	 *
	 * @return the start time
	 */
	public Date getStartTime() {
		return getDateInfo("/assignment/start_tm");
	}
	
	/**
	 * Gets the end time.
	 *
	 * @return the end time
	 */
	public Date getEndTime() {
		return getDateInfo("/assignment/end_tm");
	}

	/**
	 * Gets the request time.
	 *
	 * @return the request time
	 */
	public Date getRequestTime() {
		return getDateInfo("/assignment/req_tm");
	}

	/**
	 * Gets the inst GUID.
	 *
	 * @return the inst GUID
	 */
	public String getInstGUID() {
		return getInfo("/assignment/inst-guid");
	}

	/**
	 * Gets the requester.
	 *
	 * @return the requester
	 */
	public String getRequester() {
		return getInfo("/assignment/req");
	}
	
	/**
	 * Gets the request description.
	 *
	 * @return the request description
	 */
	public String getRequestDescription() {
		return getInfo("/assignment/req_desc");
	}

	/**
	 * Gets the entitlement ref.
	 *
	 * @return the entitlement ref
	 */
	public String getEntitlementRef() {
		return getInfo("/assignment/ent-ref");
	}

	/**
	 * Gets the entitlement dn.
	 *
	 * @return the entitlement dn
	 */
	public String getEntitlementDn() {
		return getInfo("/assignment/ent-dn");
	}

	/**
	 * Gets the cause type.
	 *
	 * @return the cause type
	 */
	public String getCauseType() {
		return getInfo("/assignment/cause/type");
	}

	/**
	 * Gets the parameter.
	 *
	 * @return the parameter
	 */
	public String getParameter() {
		return getInfo(getInfo("/assignment/ent-ref"), "/ref/param");
	}


}
