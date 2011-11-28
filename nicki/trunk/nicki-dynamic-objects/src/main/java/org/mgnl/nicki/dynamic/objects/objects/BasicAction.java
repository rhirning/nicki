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
package org.mgnl.nicki.dynamic.objects.objects;

import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicObject;

/**
 *
 * @author cna
 */
@SuppressWarnings("serial")
public class BasicAction extends DynamicObject {


	@Override
	public void initDataModel() {
		addObjectClass("nickiAction");

		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("parameter", "nickiParameter", String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("action", "nickiActionName", String.class);
		dynAttribute.setMandatory();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("noexec", "nickiNoExec", String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("target", "nickiTarget", String.class);
		dynAttribute.setForeignKey(DynamicObject.class);
		addAttribute(dynAttribute);
	}

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

	public void setNoExec(boolean noexec) {
		put("noexec", String.valueOf(noexec));
	}

	public boolean getNoExec() {
		return Boolean.valueOf((String) get("noexec"));
	}

	public void setActionName(String name) {
		put("action", name);
	}

	public String getActionName() {
		return (String) get("action");
	}

}
