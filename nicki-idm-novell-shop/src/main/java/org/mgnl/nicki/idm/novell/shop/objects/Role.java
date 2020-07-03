
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


@DynamicObject
@ObjectClass("nrfRole")
public class Role extends DynamicStructObject {
	
	private static final long serialVersionUID = 6170300879001415636L;
	@DynamicAttribute(externalName="cn", naming=true)
	private String name;
	@DynamicReferenceAttribute(externalName="nrfRole", reference=ResourceAssociation.class, baseProperty="nicki.system.basedn")
	private String[] resourceAssociation;
	@StructuredDynamicAttribute(externalName="nrfChildRoles", foreignKey=Role.class)
	private String[] childRole;
	@DynamicReferenceAttribute(externalName="nrfAssignedRoles", reference=Person.class, 
			foreignKey=Person.class, baseProperty="nicki.data.basedn")
	private String[] member;
	@StructuredDynamicAttribute(externalName="nrfApprovers", foreignKey=Person.class)
	private String[] approver;
	@DynamicAttribute(externalName="nrfLocalizedNames")
	private String localizedName;
	@DynamicAttribute(externalName="nrfLocalizedDescrs")
	private String localizedDescription;
	
	public Date getStartTime() {
		return getDateInfo("/assignment/start_tm");
	}
	
	public Date getEndTime() {
		try {
			return getDateInfo("/assignment/end_tm");
		} catch (Exception e) {
			return null;
		}
	}
	
	public String getRequester() {
		return getInfo("/assignment/req");
	}
	
	public String getRequestDescription() {
		return getInfo("/assignment/req_desc");
	}

	public Date getUpdateTime() {
		return getDateInfo("/assignment/upd_tm");
	}
	
	public Collection<Person> getMembers() {
		if (get("member") != null) {
			return getForeignKeyObjects(Person.class, "member");
		} else {
			return new ArrayList<Person>();
		}
	}
	
	public Collection<ResourceAssociation> getResourceAssociations() {
		if (get("resourceAssociation") != null) {
			return getForeignKeyObjects(ResourceAssociation.class, "resourceAssociation");
		} else {
			return new ArrayList<ResourceAssociation>();
		}
	}

}
