
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

@DynamicObject
@ObjectClass("nrfResource")
public class Resource extends DynamicStructObject {
	private static final long serialVersionUID = 3550648745922369437L;
	
	@DynamicAttribute(externalName="cn", naming=true)
	private String name;
	@StructuredDynamicAttribute(externalName="nrfEntitlementRef", foreignKey=Entitlement.class)
	private String entitlement;
	@StructuredDynamicAttribute(externalName="nrfApprovers", foreignKey=Person.class)
	private String[] approver;
	@StructuredDynamicAttribute(externalName="nrfRevokeApprovers", foreignKey=Person.class)
	private String[] revokeApprover;
	@DynamicAttribute(externalName="nrfLocalizedNames")
	private String localizedName;
	@DynamicAttribute(externalName="nrfLocalizedDescrs")
	private String localizedDescription;
	
	public Entitlement getEntitlement() throws DynamicObjectException {
		try {
			return (Entitlement) execute("getEntitlement", null);
		} catch (Exception e) {
			return null;
		}
	}
	
	public Date getStartTime() {
		return getDateInfo("/assignment/start_tm");
	}
	
	public Date getEndTime() {
		return getDateInfo("/assignment/end_tm");
	}

	public Date getRequestTime() {
		return getDateInfo("/assignment/req_tm");
	}

	public String getInstGUID() {
		return getInfo("/assignment/inst-guid");
	}

	public String getRequester() {
		return getInfo("/assignment/req");
	}
	
	public String getRequestDescription() {
		return getInfo("/assignment/req_desc");
	}

	public String getEntitlementRef() {
		return getInfo("/assignment/ent-ref");
	}

	public String getEntitlementDn() {
		return getInfo("/assignment/ent-dn");
	}

	public String getCauseType() {
		return getInfo("/assignment/cause/type");
	}

	public String getParameter() {
		return getInfo(getInfo("/assignment/ent-ref"), "/ref/param");
	}


}
