
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
@DynamicObject
@ObjectClass("group")
public class Group extends BaseDynamicObject {

	private static final long serialVersionUID = 9117516356852100038L;
	@DynamicAttribute(externalName="cn", naming=true)
	private String name;
	@DynamicAttribute(externalName="sAMAccountName")
	private String sAMAccountName;
	@DynamicAttribute(externalName="member",foreignKey=Person.class)
	private String[] member;
	@DynamicAttribute(externalName="objectGUID", readonly=true)
	private OctetString guid;
	@DynamicAttribute(externalName="groupType", mandatory=true)
	private String groupType;
	@DynamicAttribute(externalName="instanceType", readonly=true)
	private String instanceType;
	public enum TYPE {
		SECURITY_GLOBAL (-2147483646),
		SECURITY_LOCAL (-2147483644),
		BUILTIN (-2147483643),
		SECURITY_UNIVERSAL (-2147483640),
		DIST_GLOBAL (2),
		DIST_LOCAL (4),
		DIST_UNIVERSAL (8);

		private final int value;

		TYPE(int val) {
			this.value = val;
		}

		String getValue() {
			return Integer.toString(value);
		}
	};

	public void setGroupType(TYPE type) {
		put("groupType", type.getValue());
	}

	public void setInstanceType(int type) {
		put("instanceType", Integer.toString(type));
	}

}
