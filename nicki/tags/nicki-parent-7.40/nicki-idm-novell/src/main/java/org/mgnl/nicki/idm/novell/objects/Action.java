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
package org.mgnl.nicki.idm.novell.objects;

import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.objects.BaseDynamicObject;
import org.mgnl.nicki.core.objects.DynamicObject;

@ObjectClass("nickiAction")
public class Action extends BaseDynamicObject {
	private static final long serialVersionUID = 650085730365865573L;

	@DynamicAttribute(externalName="cn", naming=true)
	private String name;

	@DynamicAttribute(externalName="nickiParameter")
	private String parameter;

	@DynamicAttribute(externalName="nickiActionName", mandatory=true)
	private String action;

	@DynamicAttribute(externalName="nickiTarget", foreignKey=DynamicObject.class)
	private String target;

	public void setParameter(String parameter) {
		put("parameter", parameter);
	}

	public String getParameter() {
		return (String) get("parameter");
	}

	public void setTarget(DynamicObject target) {
		setTarget(target.getPath());
	}

	public void setTarget(String target) {
		put("target", target);
	}

	public String getTarget() {
		return (String) get("target");
	}

	public void setActionName(String name) {
		put("action", name);
	}

	public String getActionName() {
		return (String) get("action");
	}

}
