/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.dynamic.objects.ad;

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
