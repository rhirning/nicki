package org.mgnl.nicki.dynamic.objects.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.dynamic.objects.reference.ReferenceDynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicReference;
import org.mgnl.nicki.ldap.objects.StructuredDynamicAttribute;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

@SuppressWarnings("serial")
public class Person extends DynamicTemplateObject {
	public void initDataModel() {
		addObjectClass("Person");
		addAdditionalObjectClass("nickiUserAux");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn",
				String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("surname", "sn", String.class);
		dynAttribute.setMandatory();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("givenname", "givenName",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("fullname", "fullName",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("lastWorkingDay",
				"nickiLastWorkingDay", String.class);
		addAttribute(dynAttribute);

		dynAttribute = new ReferenceDynamicAttribute(Person.class, "manager",
				"manager", String.class,
				Config.getProperty("nicki.users.basedn"));
		dynAttribute.setForeignKey(Person.class);
		addAttribute(dynAttribute);

		dynAttribute = new StructuredDynamicAttribute("entitlement",
				"DirXML-EntitlementRef", String.class);
		dynAttribute.setMultiple();
		dynAttribute.setForeignKey(Entitlement.class);
		addAttribute(dynAttribute);

		dynAttribute = new StructuredDynamicAttribute("role",
				"nrfAssignedRoles", String.class);
		dynAttribute.setMultiple();
		dynAttribute.setForeignKey(Role.class);
		addAttribute(dynAttribute);

		dynAttribute = new StructuredDynamicAttribute("resource",
				"nrfAssignedResources", String.class);
		dynAttribute.setMultiple();
		dynAttribute.setForeignKey(Resource.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("status", "nickiStatus",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("type", "employeeType",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("costCenter", "costCenter",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("gender", "nickiGender",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("language", "Language",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("birthDate", "nickiBirthDate",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("entryDate", "nickiEntryDate",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("owner", "nickiOwner", String.class);
		dynAttribute.setForeignKey(Person.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("company", "company", String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("occupation", "nickiOccupation",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("location", "nickiLocation",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("assignedRuleArticle", "nickiAssignedRuleArticle",
				String.class);
		dynAttribute.setMultiple();
		dynAttribute.setForeignKey("org.mgnl.nicki.shop.catalog.CatalogArticle");
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("attributeValue",
				"nickiCatalogAttribute", String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicReference(Group.class, "memberOf", Config.getProperty("nicki.data.basedn"), 
				"member", String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);
		


	}

	@Override
	public String getDisplayName() {
		StringBuffer sb = new StringBuffer();
		sb.append(StringUtils.trimToEmpty(getAttribute("givenname")));
		if (sb.length() > 0) {
			sb.append(" ");
		}
		sb.append(StringUtils.trimToEmpty(getAttribute("surname")));
		sb.append(" (");
		sb.append(getName());
		sb.append(")");
		return sb.toString();
	}

	public boolean hasRole(Role role2) {
		for (Iterator<Role> iterator = getRoles().iterator(); iterator
				.hasNext();) {
			Role role = (Role) iterator.next();
			if (StringUtils.equals(role.getPath(), role2.getPath())) {
				return true;
			}
		}
		return false;
	}

	public boolean hasResource(Resource resource2) {
		for (Iterator<Resource> iterator = getResources().iterator(); iterator
				.hasNext();) {
			Resource resource = iterator.next();
			if (StringUtils.equals(resource.getPath(), resource2.getPath())) {
				return true;
			}
		}
		return false;
	}

	public String getFullname() {
		return getAttribute("fullname");
	}

	// TODO - edit cna
	public static String getActiveFilter() {
		return "nickiStatus=" + STATUS.ACTIVE.getValue();
	}

	// TODO - edit cna
	public static String getInActiveFilter() {
		return "!(|(nickiStatus=" + STATUS.ACTIVE.getValue() + ")(nickiStatus="
				+ STATUS.REQUESTED.getValue() + "))";
	}

	public static String getAllFilter() {
		return "!(nickiStatus=" + STATUS.REQUESTED.getValue() + ")";
	}

	public void setExitDate(Date date) {
		if (date != null) {
			put("lastWorkingDay", DataHelper.formatDay.format(date));
		}
	}

	public void setStatus(STATUS status) {
		put("status", status.getValue());
	}

	public STATUS getStatus() {
		return STATUS.fromValue(getAttribute("status"));
	}

	public void setType(PERSONTYPE type) {
		put("type", type.getValue());
	}

	public PERSONTYPE getType() {
		return PERSONTYPE.fromValue(getAttribute("type"));
	}

	public void setCostCenter(String value) {
		put("costCenter", value);
	}

	public void setName(String value) {
		put("surname", value);
	}

	public void setGivenName(String value) {
		put("givenname", value);
	}

	public void setGender(GENDER value) {
		put("gender", value.toString());
	}

	public void setLanguage(String value) {
		put("language", value);
	}

	public void setBirthDate(Date value) {
		if (value != null) {
			put("birthDate", DataHelper.formatDay.format(value));
		}
	}

	public void setEntryDate(Date value) {
		if (value != null) {
			put("entryDate", DataHelper.formatDay.format(value));
		}
	}

	public void setOwner(Person value) {
		put("owner", value.getPath());
	}

	public void setCompany(String value) {
		put("company", value);
	}

	public void setOccupation(String value) {
		put("occupation", value);
	}

	@SuppressWarnings("unchecked")
	public List<Role> getRoles() {
		TemplateMethodModel method = (TemplateMethodModel) get("getRoles");
		if (method != null) {
			try {
				return (List<Role>) method.exec(null);
			} catch (TemplateModelException e) {
				e.printStackTrace();
			}
		}
		return new ArrayList<Role>();
	}

	@SuppressWarnings("unchecked")
	public List<Resource> getResources() {
		TemplateMethodModel method = (TemplateMethodModel) get("getResources");
		if (method != null) {
			try {
				return (List<Resource>) method.exec(null);
			} catch (TemplateModelException e) {
				e.printStackTrace();
			}
		}
		return new ArrayList<Resource>();
	}

	@SuppressWarnings("unchecked")
	public List<String> getCatalogAttributeValues() {
		if (get("attributeValue") != null) {
			return (List<String>) get("attributeValue");
		}
		return new ArrayList<String>();
	}

	public enum GENDER {
		MALE, FEMALE
	};

	public enum PERSONTYPE {
		INTERNAL_USER("INTERNAL"), EXTERNAL_USER("EXTERNAL"), TECHNICAL_USER(
				"TECHNICAL"), NOT_SET("");

		private final String type;

		private PERSONTYPE(String type) {
			this.type = type;
		}

		public static PERSONTYPE fromValue(String type) {
			if (INTERNAL_USER.getValue().equals(type)) {
				return INTERNAL_USER;
			} else if (EXTERNAL_USER.getValue().equals(type)) {
				return EXTERNAL_USER;
			} else if (TECHNICAL_USER.getValue().equals(type)) {
				return TECHNICAL_USER;
			}

			return NOT_SET;

		}

		public String getValue() {
			return type;
		}
	};

	public enum STATUS {

		REQUESTED("beantragt"), INACTIVE("inaktiv-vor-eintritt"), ACTIVE(
				"aktiv"), DEACTIVATED("deaktiviert"), RESIGNED("ausgetreten"), NOT_SET(
				"");

		private final String status;

		private STATUS(String status) {
			this.status = status;
		}

		public static STATUS fromValue(String status) {
			if (INACTIVE.getValue().equals(status)) {
				return INACTIVE;
			} else if (ACTIVE.getValue().equals(status)) {
				return ACTIVE;
			} else if (DEACTIVATED.getValue().equals(status)) {
				return DEACTIVATED;
			} else if (RESIGNED.getValue().equals(status)) {
				return RESIGNED;
			} else if (REQUESTED.getValue().equals(status)) {
				return REQUESTED;
			}

			return NOT_SET;

		}

		public String getValue() {
			return status;
		}
	}

}
