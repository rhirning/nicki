
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


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.DynamicReferenceAttribute;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.annotation.StructuredDynamicAttribute;
import org.mgnl.nicki.dynamic.objects.objects.Person;



/**
 * The Class Role.
 */
@DynamicObject
@ObjectClass("nrfRole")
public class Role extends DynamicStructObject {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6170300879001415636L;
	
	/** The name. */
	@DynamicAttribute(externalName="cn", naming=true)
	private String name;
	
	/** The resource association. */
	@DynamicReferenceAttribute(externalName="nrfRole", reference=ResourceAssociation.class, baseProperty="nicki.system.basedn")
	private String[] resourceAssociation;
	
	/** The child role. */
	@StructuredDynamicAttribute(externalName="nrfChildRoles", foreignKey=Role.class)
	private String[] childRole;
	
	/** The member. */
	@DynamicReferenceAttribute(externalName="nrfAssignedRoles", reference=Person.class, 
			foreignKey=Person.class, baseProperty="nicki.data.basedn")
	private String[] member;
	
	/** The approver. */
	@StructuredDynamicAttribute(externalName="nrfApprovers", foreignKey=Person.class)
	private String[] approver;
	
	/** The localized name. */
	@DynamicAttribute(externalName="nrfLocalizedNames")
	private String localizedName;
	
	/** The localized description. */
	@DynamicAttribute(externalName="nrfLocalizedDescrs")
	private String localizedDescription;
	
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
		try {
			return getDateInfo("/assignment/end_tm");
		} catch (Exception e) {
			return null;
		}
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
	 * Gets the update time.
	 *
	 * @return the update time
	 */
	public Date getUpdateTime() {
		return getDateInfo("/assignment/upd_tm");
	}
	
	/**
	 * Gets the members.
	 *
	 * @return the members
	 */
	public Collection<Person> getMembers() {
		if (get("member") != null) {
			return getForeignKeyObjects(Person.class, "member");
		} else {
			return new ArrayList<Person>();
		}
	}
	
	/**
	 * Gets the resource associations.
	 *
	 * @return the resource associations
	 */
	@SuppressWarnings("unchecked")
	public Collection<ResourceAssociation> getResourceAssociations() {
		try {
			return (Collection<ResourceAssociation>) execute("getResourceAssociations", null);
		} catch (Exception e) {
			return null;
		}
	}

}
