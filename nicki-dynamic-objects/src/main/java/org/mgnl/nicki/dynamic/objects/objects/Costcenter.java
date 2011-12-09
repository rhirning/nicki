/**
 * This file Copyright (c) 2011 deron Consulting GmbH
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.dynamic.objects.objects;

import java.util.ArrayList;
import java.util.List;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicObject;

/**
 *
 * @author cna
 */
@SuppressWarnings("serial")
public class Costcenter extends DynamicObject {

    @Override
    public void initDataModel() {
        addObjectClass("nickiCostCenter");
        DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
        dynAttribute.setNaming();
        addAttribute(dynAttribute);

        dynAttribute = new DynamicAttribute("owner", "nickiOwner", String.class);
        dynAttribute.setForeignKey(Person.class);
        addAttribute(dynAttribute);

        dynAttribute = new DynamicAttribute("value", "costCenter", String.class);
        dynAttribute.setForeignKey(Costcenter.class);
        addAttribute(dynAttribute);
    }

    public String getValue() {
        return (String) get("value");
    }

    public void setValue(String value) {
        put("value", value);
    }

    public void setOwner(Person owner) {
        put("owner", owner.getPath());
    }

    public Person getOwner() {
        return getContext().loadObject(Person.class, (String) get("owner"));
    }

    public static List<Costcenter> getAllCostcenters(NickiContext ctx) {
        return ctx.loadChildObjects(Costcenter.class, Config.getProperty("nicki.costcenters.basedn"), null);
    }
    
	public static List<String> getAllCostcentersAsString(NickiContext ctx) {
        List<Costcenter> list = ctx.loadChildObjects(Costcenter.class, Config.getProperty("nicki.costcenters.basedn"), null);
		List<String> strings = new ArrayList<String>();
		for (Costcenter costcenter : list) {
			strings.add(costcenter.getValue());
		}
		
		return strings;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[COSTCENTER name=");
        sb.append(getName());
        sb.append(" value=");
        sb.append(getValue());
        sb.append(" owner=");
        sb.append(get("owner"));
        sb.append("]");

        return sb.toString();
    }
}
