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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.dynamic.objects.objects.Group;
import org.mgnl.nicki.dynamic.objects.objects.Org;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.dynamic.objects.reference.ReferenceDynamicAttribute;
import org.mgnl.nicki.dynamic.objects.shop.AssignedArticle;
import org.mgnl.nicki.idm.novell.catalog.ResourceCatalogArticle;
import org.mgnl.nicki.idm.novell.catalog.RoleCatalogArticle;
import org.mgnl.nicki.idm.novell.objects.Entitlement;
import org.mgnl.nicki.idm.novell.objects.Resource;
import org.mgnl.nicki.idm.novell.objects.Role;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.StructuredDynamicAttribute;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

public class IDMPerson extends Person implements Serializable {

	private static final long serialVersionUID = -6791692458041112275L;
	public static final String ATTRIBUTE_LASTWORKINGDAY = "lastWorkingDay";
	public static final String ATTRIBUTE_QUITDATE = "quitDate";
	public static final String ATTRIBUTE_ACTIVATIONDATE = "activationDate";
	public static final String ATTRIBUTE_MANAGER = "manager";
	public static final String ATTRIBUTE_TYPE = "type";
	public static final String ATTRIBUTE_COSTCENTER = "costCenter";
	public static final String ATTRIBUTE_GENDER = "gender";
	public static final String ATTRIBUTE_ENTITLEMENT = "entitlement";
	public static final String ATTRIBUTE_ROLE = "role";
	public static final String ATTRIBUTE_RESOURCE = "resource";
	public static final String ATTRIBUTE_BIRTHDATE = "birthDate";
	public static final String ATTRIBUTE_ENTRYDATE = "entryDate";
	public static final String ATTRIBUTE_OUCHANGEDATE = "ouChangeDate";
	public static final String ATTRIBUTE_NEXTOU = "nextOU";
	public static final String ATTRIBUTE_NEXTCOSTCENTER = "nextCostCenter";
	public static final String ATTRIBUTE_OUTRANSFERDATE = "ouTransferDate";
	public static final String ATTRIBUTE_OWNER = "owner";
	public static final String ATTRIBUTE_COMPANY = "company";
	public static final String ATTRIBUTE_OU = "ou";
	public static final String ATTRIBUTE_OCCUPATION = "occupation";
	public static final String ATTRIBUTE_LOCATION = "location";
	public static final String ATTRIBUTE_ASSIGNEDARTICLE = "assignedArticle";
	public static final String ATTRIBUTE_ATTRIBUTEVALUE = "attributeValue";
	public static final String ATTRIBUTE_WORKFORCEID = "workforceId";
	public static final String ATTRIBUTE_MAIL = "mail";
	public static final String ATTRIBUTE_PHONENUMBER = "phoneNumber";

	private List<AssignedArticle> assignedArticles = null;
	private List<Role> assignedRoles = null;
	private List<Resource> assignedResources = null;
	
	@Override
	public void initDataModel() {
		super.initDataModel();

		getModel().getObjectClasses().clear();
		getModel().getAttributes().remove(ATTRIBUTE_MEMBEROF);
		addObjectClass("inetOrgPerson");
		addAdditionalObjectClass("DirXML-EntitlementRecipient");
		addAdditionalObjectClass("DirXML-EmployeeAux");

		DynamicAttribute dynAttribute = new DynamicAttribute(ATTRIBUTE_LASTWORKINGDAY,
				"nickiLastWorkingDay", String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_QUITDATE,
				"nickiQuitDate", String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_ACTIVATIONDATE,
				"nickiActivationDate", String.class);
		addAttribute(dynAttribute);

		dynAttribute = new ReferenceDynamicAttribute(Person.class, "ATTRIBUTE_MANAGER",
				"manager", String.class,
				Config.getProperty("nicki.users.basedn"));
		dynAttribute.setForeignKey(Person.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_TYPE, "employeeType",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_COSTCENTER, "costCenter",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_GENDER, "nickiGender",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new StructuredDynamicAttribute(ATTRIBUTE_ENTITLEMENT,
				"DirXML-EntitlementRef", String.class);
		dynAttribute.setMultiple();
		dynAttribute.setForeignKey(Entitlement.class);
		addAttribute(dynAttribute);

		dynAttribute = new StructuredDynamicAttribute(ATTRIBUTE_ROLE,
				"nrfAssignedRoles", String.class);
		dynAttribute.setMultiple();
		dynAttribute.setForeignKey(Role.class);
		addAttribute(dynAttribute);

		dynAttribute = new StructuredDynamicAttribute(ATTRIBUTE_RESOURCE,
				"nrfAssignedResources", String.class);
		dynAttribute.setMultiple();
		dynAttribute.setForeignKey(Resource.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_BIRTHDATE, "nickiBirthDate",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_ENTRYDATE, "nickiEntryDate",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_OUCHANGEDATE, "nickiOuChangeDate",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_NEXTOU, "nickiNextOu",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_NEXTCOSTCENTER, "nickiNextCostCenter",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_OUTRANSFERDATE, "nickiOuTransferDate",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_OWNER, "nickiOwner", String.class);
		dynAttribute.setForeignKey(Person.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_COMPANY, "company", String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_OCCUPATION, "nickiOccupation",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_LOCATION, "nickiLocation",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_ASSIGNEDARTICLE, "nickiCatalogArticle",
				String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_ATTRIBUTEVALUE,
				"nickiCatalogAttribute", String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_MEMBEROF, "groupMembership", String.class);
		dynAttribute.setForeignKey(Group.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute(ATTRIBUTE_OU, "ou", String.class);
		dynAttribute.setForeignKey(Org.class);
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute(ATTRIBUTE_WORKFORCEID, "workforceID", String.class);
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute(ATTRIBUTE_MAIL, "mail", String.class);
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute(ATTRIBUTE_PHONENUMBER, "telephoneNumber", String.class);
		addAttribute(dynAttribute);
	}


	public boolean hasRole(Role role2) {
		for (Iterator<Role> iterator = getRoles().iterator(); iterator.hasNext();) {
			Role role = (Role) iterator.next();
			if (StringUtils.equals(role.getPath(), role2.getPath())) {
				return true;
			}
		}
		return false;
	}

	public boolean hasResource(Resource resource2) {
		for (Iterator<Resource> iterator = getResources().iterator(); iterator.hasNext();) {
			Resource resource = iterator.next();
			if (StringUtils.equals(resource.getPath(), resource2.getPath())) {
				return true;
			}
		}
		return false;
	}

	public void setType(PERSONTYPE type) {
		if(null != type) {
			put(ATTRIBUTE_TYPE, type.getValue());
		} else {
			clear(ATTRIBUTE_TYPE);
		}
	}

	public PERSONTYPE getType() {
		return PERSONTYPE.fromValue(getAttribute(ATTRIBUTE_TYPE));
	}

	public void setCostCenter(String value) {
		put(ATTRIBUTE_COSTCENTER, value);
	}

	public void setGender(GENDER value) {
		if(null != value) {
			put(ATTRIBUTE_GENDER, value.toString());
		} else {
			clear(ATTRIBUTE_GENDER);
		}
	}

	public void setBirthDate(Date value) {
		if (value != null) {
			put(ATTRIBUTE_BIRTHDATE, DataHelper.formatDay.format(value));
		} else {
			clear(ATTRIBUTE_BIRTHDATE);
		}
	}

	public void setQuitDate(Date value) {
		if (value != null) {
			put(ATTRIBUTE_QUITDATE, DataHelper.formatDay.format(value));
		} else {
			clear(ATTRIBUTE_QUITDATE);
		}
	}

	public void setActivationDate(Date value) {
		if (value != null) {
			put(ATTRIBUTE_ACTIVATIONDATE, DataHelper.formatDay.format(value));
		} else {
			clear(ATTRIBUTE_ACTIVATIONDATE);
		}
	}

	public void setEntryDate(Date value) {
		if (value != null) {
			put(ATTRIBUTE_ENTRYDATE, DataHelper.formatDay.format(value));
		} else {
			clear(ATTRIBUTE_ENTRYDATE);
		}
	}

	public void setOuChangeDate(Date value) {
		if (value != null) {
			put(ATTRIBUTE_OUCHANGEDATE, DataHelper.formatDay.format(value));
		} else {
			clear(ATTRIBUTE_OUCHANGEDATE);
		}
	}

	public void setOuTransferDate(Date value) {
		if (value != null) {
			put(ATTRIBUTE_OUTRANSFERDATE, DataHelper.formatDay.format(value));
		} else {
			clear(ATTRIBUTE_OUTRANSFERDATE);
		}
	}

	public void setNextOu(String value) {
		put(ATTRIBUTE_NEXTOU, value);
	}

	public void setNextCostCenter(String value) {
		put(ATTRIBUTE_NEXTCOSTCENTER, value);
	}

	public void setLastWorkingDay(Date date) {
		if (date != null) {
			put(ATTRIBUTE_LASTWORKINGDAY, DataHelper.formatDay.format(date));
		} else {
			clear(ATTRIBUTE_LASTWORKINGDAY);
		}
	}

	public Date getQuitDate() {
		try {
			return DataHelper.formatDay.parse((String) get(ATTRIBUTE_QUITDATE));
		} catch (Exception ex) {
			return null;
		}
	}

	public Date getActivationDate() {
		try {
			return DataHelper.formatDay.parse((String) get(ATTRIBUTE_ACTIVATIONDATE));
		} catch (Exception ex) {
			return null;
		}
	}

	public Date getEntryDate() {
		try {
			return DataHelper.formatDay.parse((String) get(ATTRIBUTE_ENTRYDATE));
		} catch (Exception ex) {
			return null;
		}
	}
	
	public Date getBirthDate() {
		try {
			return DataHelper.formatDay.parse((String) get(ATTRIBUTE_BIRTHDATE));
		} catch (Exception ex) {
			return null;
		}
	}
	
	public Date getOuChangeDate() {
		try {
			return DataHelper.formatDay.parse((String) get(ATTRIBUTE_OUCHANGEDATE));
		} catch (Exception ex) {
			return null;
		}
	}

	public Date getOuTransferDate() {
		try {
			return DataHelper.formatDay.parse((String) get(ATTRIBUTE_OUTRANSFERDATE));
		} catch (Exception ex) {
			return null;
		}
	}

	public Org getNextOu() {
		return getForeignKeyObject(Org.class, ATTRIBUTE_NEXTOU);
	}


	public String getNextCostCenter() {
		return (String) get(ATTRIBUTE_NEXTCOSTCENTER);
	}

	public Date getLastWorkingDay() {
		try {
			return DataHelper.formatDay.parse((String) get(ATTRIBUTE_LASTWORKINGDAY));
		} catch (Exception ex) {
			return null;
		}
	}

	public void setOwner(Person value) {
		if (null != value) {
			put(ATTRIBUTE_OWNER, value.getPath());
		} else {
			clear(ATTRIBUTE_OWNER);
		}
	}


	public void setCompany(String value) {
		put(ATTRIBUTE_COMPANY, value);
	}

	public void setOccupation(String value) {
		put(ATTRIBUTE_OCCUPATION, value);
	}

	public List<AssignedArticle> getAssignedArticles() {
		if (this.assignedArticles == null) {
			this.assignedArticles = new ArrayList<AssignedArticle>();
			@SuppressWarnings("unchecked")
			List<String> articles = (List<String>) get(ATTRIBUTE_ASSIGNEDARTICLE);
			if (articles != null) {
				for (String text : articles) {
					this.assignedArticles.add(new AssignedArticle(text));
				}
			}
		}
		return this.assignedArticles;
	}

	@SuppressWarnings("unchecked")
	public List<Role> getRoles() {
		if (assignedRoles == null) {
			TemplateMethodModel method = (TemplateMethodModel) get("getRoles");
			if (method != null) {
				try {
					assignedRoles = (List<Role>) method.exec(null);
				} catch (TemplateModelException e) {
					e.printStackTrace();
					assignedRoles =  new ArrayList<Role>();
				}
			}
			for (AssignedArticle article : getAssignedArticles()) {
				if (article.getCatalogArticle().getClass().isAssignableFrom(RoleCatalogArticle.class)) {
					assignedRoles.add(((RoleCatalogArticle)article.getCatalogArticle()).getRole());
				}
			}
		}
		return assignedRoles;
	}

	@SuppressWarnings("unchecked")
	public List<Resource> getResources() {
		if (assignedResources == null) {
			TemplateMethodModel method = (TemplateMethodModel) get("getResources");
			if (method != null) {
				try {
					assignedResources = (List<Resource>) method.exec(null);
				} catch (TemplateModelException e) {
					e.printStackTrace();
					assignedResources = new ArrayList<Resource>();
				}
			}
			for (AssignedArticle article : getAssignedArticles()) {
				if (article.getCatalogArticle().getClass().isAssignableFrom(ResourceCatalogArticle.class)) {
					assignedResources.add(((ResourceCatalogArticle)article.getCatalogArticle()).getResource());
				}
			}
		}
		return assignedResources;
	}

	@SuppressWarnings("unchecked")
	public List<String> getCatalogAttributeValues() {
		if (get("attributeValue") != null) {
			return (List<String>) get(ATTRIBUTE_ATTRIBUTEVALUE);
		}
		return new ArrayList<String>();
	}
	
	public String getCatalogAttribute(String key) {
		String retVal = "";
		String value;
		
		for(Iterator<String> i = getCatalogAttributeValues().iterator(); i.hasNext();) {
			value = i.next();
			if(value.startsWith(key)) {
				retVal = StringUtils.substringAfter(value, "=");
				break;
			}
		}
		
		return retVal;
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

}
