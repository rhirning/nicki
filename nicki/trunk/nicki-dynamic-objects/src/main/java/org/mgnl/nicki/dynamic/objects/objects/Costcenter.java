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
