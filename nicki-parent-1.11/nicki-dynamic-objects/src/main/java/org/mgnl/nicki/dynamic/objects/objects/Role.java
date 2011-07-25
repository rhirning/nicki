package org.mgnl.nicki.dynamic.objects.objects;

import java.util.Date;

import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicReference;


public class Role extends DynamicStructObject {
	
	private static final long serialVersionUID = 6170300879001415636L;

	public void initDataModel() {
		addObjectClass("nrfRole");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

                dynAttribute = new DynamicReference("resourceAssociations", "o=system", "nrfRole", String.class);
		addAttribute(dynAttribute);

                // TODO - o=utopia kann wohl nicht stimmen
		dynAttribute = new DynamicReference("member", "o=utopia", "nrfAssignedRoles", String.class);
		dynAttribute.setMultiple();
		dynAttribute.setForeignKey();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("approver", "nrfApprovers", String.class);
		//dynAttribute.setForeignKey();
		dynAttribute.setMultiple();
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


}
