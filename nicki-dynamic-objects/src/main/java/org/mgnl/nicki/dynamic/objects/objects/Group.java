
package org.mgnl.nicki.dynamic.objects.objects;

/*-
 * #%L
 * nicki-dynamic-objects
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
import java.util.List;

import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.objects.BaseDynamicObject;


/**
 * The Class Group.
 */
@SuppressWarnings("serial")
@DynamicObject
@ObjectClass("groupOfNames")
public class Group extends BaseDynamicObject {
	
	/** The Constant ATTRIBUTE_DESCRIPTION. */
	public static final String ATTRIBUTE_DESCRIPTION = "description";
	
	/** The Constant ATTRIBUTE_MEMBER. */
	public static final String ATTRIBUTE_MEMBER = "member";
	
	/** The Constant ATTRIBUTE_OWNER. */
	public static final String ATTRIBUTE_OWNER = "owner";

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@DynamicAttribute(externalName="cn", naming=true)
	public String getName() {
		return super.getName();
	}
	
	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	@DynamicAttribute(externalName="description")
	public String getDescription() {
		return getAttribute(ATTRIBUTE_DESCRIPTION);
	}
	
	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		put(ATTRIBUTE_DESCRIPTION, description);
	}
	
	/**
	 * Gets the owner.
	 *
	 * @return the owner
	 */
	@DynamicAttribute(externalName="owner", foreignKey=Person.class)
	public String getOwner() {
		return getAttribute(ATTRIBUTE_OWNER);
	}
		
    /**
     * Gets the member.
     *
     * @return the member
     */
    @SuppressWarnings("unchecked")
	@DynamicAttribute(externalName = "member", foreignKey=Person.class)
    public List<String> getMember() {
    	return (List<String>) get(ATTRIBUTE_MEMBER);
    }
    

	/**
	 * Gets the members.
	 *
	 * @return the members
	 */
	public Collection<Person> getMembers() {
		return getForeignKeyObjects(Person.class, "member");
	}

	/**
	 * Adds the member.
	 *
	 * @param path the path
	 */
	public void addMember(String path) {
		List<String> list = getMember();
		if (list == null) {
			list = new ArrayList<>();
		}
		if (!list.contains(path)) {
			list.add(path);
		}
	}
	
	/**
	 * Removes the member.
	 *
	 * @param path the path
	 */
	public void removeMember(String path) {
		List<String> list = getMember();
		if (list != null && list.contains(path)) {
			list.remove(path);
		}
	}
}
