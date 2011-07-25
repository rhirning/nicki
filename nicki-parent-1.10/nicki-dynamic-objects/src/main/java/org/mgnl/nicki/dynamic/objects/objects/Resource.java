package org.mgnl.nicki.dynamic.objects.objects;

import java.util.Date;

import org.mgnl.nicki.dynamic.objects.reference.ReferenceDynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;
import org.mgnl.nicki.ldap.objects.StructuredDynamicAttribute;



@SuppressWarnings("serial")
public class Resource extends DynamicStructObject {

	@Override
	public void initDataModel() {
		addObjectClass("nrfResource");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

		dynAttribute =  new ReferenceDynamicAttribute("resource", "nrfResource", String.class,
				"nicki.resources.basedn", "(objectClass=nrfResource)");
		dynAttribute.setForeignKey();
		addAttribute(dynAttribute);

		dynAttribute =  new StructuredDynamicAttribute("entitlement", "nrfEntitlementRef", String.class);
		dynAttribute.setForeignKey();
		addAttribute(dynAttribute);
	}
	
	public DynamicObject getEntitlement() throws DynamicObjectException {
		try {
			return (DynamicObject) execute("getEntitlement", null);
		} catch (Exception e) {
			return null;
		}
	}
	
	public Date getStartTime() {
		return getDateInfo("/assignment/start_tm");
	}

	public Date getRequestTime() {
		return getDateInfo("/assignment/req_tm");
	}

	public String getInstGUID() {
		return getInfo("/assignment/inst-guid");
	}

	public String getRequester() {
		return getInfo("/assignment/req");
	}
	
	public String getRequestDescription() {
		return getInfo("/assignment/req_desc");
	}

	public String getEntitlementRef() {
		return getInfo("/assignment/ent-ref");
	}

	public String getEntitlementDn() {
		return getInfo("/assignment/ent-dn");
	}

	public String getCauseType() {
		return getInfo("/assignment/cause/type");
	}



}
