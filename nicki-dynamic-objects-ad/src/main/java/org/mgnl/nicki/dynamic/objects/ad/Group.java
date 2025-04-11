
package org.mgnl.nicki.dynamic.objects.ad;

/*-
 * #%L
 * nicki-dynamic-objects-ad
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


import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.data.OctetString;
import org.mgnl.nicki.core.objects.BaseDynamicObject;
// TODO: Auto-generated Javadoc

/**
 * The Class Group.
 */
@DynamicObject
@ObjectClass("group")
public class Group extends BaseDynamicObject {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 9117516356852100038L;
	
	/** The name. */
	@DynamicAttribute(externalName="cn", naming=true)
	private String name;
	
	/** The s AM account name. */
	@DynamicAttribute(externalName="sAMAccountName")
	private String sAMAccountName;
	
	/** The member. */
	@DynamicAttribute(externalName="member",foreignKey=Person.class)
	private String[] member;
	
	/** The guid. */
	@DynamicAttribute(externalName="objectGUID", readonly=true)
	private OctetString guid;
	
	/** The group type. */
	@DynamicAttribute(externalName="groupType", mandatory=true)
	private String groupType;
	
	/** The instance type. */
	@DynamicAttribute(externalName="instanceType", readonly=true)
	private String instanceType;
	
	/**
	 * The Enum TYPE.
	 */
	public enum TYPE {
		
		/** The security global. */
		SECURITY_GLOBAL (-2147483646),
		
		/** The security local. */
		SECURITY_LOCAL (-2147483644),
		
		/** The builtin. */
		BUILTIN (-2147483643),
		
		/** The security universal. */
		SECURITY_UNIVERSAL (-2147483640),
		
		/** The dist global. */
		DIST_GLOBAL (2),
		
		/** The dist local. */
		DIST_LOCAL (4),
		
		/** The dist universal. */
		DIST_UNIVERSAL (8);

		/** The value. */
		private final int value;

		/**
		 * Instantiates a new type.
		 *
		 * @param val the val
		 */
		TYPE(int val) {
			this.value = val;
		}

		/**
		 * Gets the value.
		 *
		 * @return the value
		 */
		String getValue() {
			return Integer.toString(value);
		}
	};

	/**
	 * Sets the group type.
	 *
	 * @param type the new group type
	 */
	public void setGroupType(TYPE type) {
		put("groupType", type.getValue());
	}

	/**
	 * Sets the instance type.
	 *
	 * @param type the new instance type
	 */
	public void setInstanceType(int type) {
		put("instanceType", Integer.toString(type));
	}

}
