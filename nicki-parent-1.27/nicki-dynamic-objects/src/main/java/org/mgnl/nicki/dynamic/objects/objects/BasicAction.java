/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
