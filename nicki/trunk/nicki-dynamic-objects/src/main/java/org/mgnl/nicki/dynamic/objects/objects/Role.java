package org.mgnl.nicki.dynamic.objects.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicReference;
import org.mgnl.nicki.ldap.objects.StructuredDynamicAttribute;
import org.mgnl.nicki.ldap.objects.StructuredDynamicReference;


public class Role extends DynamicStructObject {
	
	private static final long serialVersionUID = 6170300879001415636L;

	public void initDataModel() {
		addObjectClass("nrfRole");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicReference(ResourceAssociation.class, "resourceAssociation", Config.getProperty("nicki.system.basedn"), "nrfRole", String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);
		
		dynAttribute =  new StructuredDynamicAttribute("childRole", "nrfChildRoles", String.class);
		dynAttribute.setMultiple();
		dynAttribute.setForeignKey(Role.class);
		addAttribute(dynAttribute);

		dynAttribute = new StructuredDynamicReference(Person.class, "member", Config.getProperty("nicki.data.basedn"), "nrfAssignedRoles", String.class);
		dynAttribute.setMultiple();
		dynAttribute.setForeignKey(Person.class);
		addAttribute(dynAttribute);

		dynAttribute = new StructuredDynamicAttribute("approver", "nrfApprovers", String.class);
		dynAttribute.setForeignKey(Person.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("localizedName", "nrfLocalizedNames", String.class);
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute("localizedDescription", "nrfLocalizedDescrs", String.class);
		addAttribute(dynAttribute);
	};
	
	public Date getStartTime() {
		return getDateInfo("/assignment/start_tm");
	}
	
	public String getRequester() {
		return getInfo("/assignment/req");
	}
	
	public String getRequestDescription() {
		return getInfo("/assignment/req_desc");
	}

	public Date getUpdateTime() {
		return getDateInfo("/assignment/upd_tm");
	}
	
	public List<Person> getMembers() {
		if (get("member") != null) {
			return getForeignKeyObjects(Person.class, "member");
		} else {
			return new ArrayList<Person>();
		}
	}

}
