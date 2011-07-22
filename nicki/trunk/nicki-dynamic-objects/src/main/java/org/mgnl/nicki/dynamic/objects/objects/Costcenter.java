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
public class Costcenter extends DynamicObject{

    @Override
    public void initDataModel() {
        addObjectClass("nickiCostCenter");
        DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
        dynAttribute.setNaming();
        addAttribute(dynAttribute);
        
        dynAttribute = new DynamicAttribute("owner", "nickiOwner", String.class);
        dynAttribute.setForeignKey();
        addAttribute(dynAttribute);
        
        dynAttribute = new DynamicAttribute("value", "costCenter", String.class);
        dynAttribute.setForeignKey();
        addAttribute(dynAttribute);
    }
    
    public String getValue() {
        return (String) get("value");
    }
    
    public void setValue(String value) {
        put("value", value);
    }
    
}
