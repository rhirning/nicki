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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.annotation.AdditionalObjectClass;
import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.DynamicReferenceAttribute;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.annotation.RemoveDynamicAttribute;
import org.mgnl.nicki.core.annotation.StructuredDynamicAttribute;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.dynamic.objects.objects.Group;
import org.mgnl.nicki.dynamic.objects.objects.Org;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.dynamic.objects.shop.AssignedArticle;
import org.mgnl.nicki.idm.novell.catalog.ResourceCatalogArticle;
import org.mgnl.nicki.idm.novell.catalog.RoleCatalogArticle;
import org.mgnl.nicki.idm.novell.objects.Entitlement;
import org.mgnl.nicki.idm.novell.objects.Resource;
import org.mgnl.nicki.idm.novell.objects.Role;
import org.mgnl.nicki.shop.objects.Catalog;
import org.mgnl.nicki.shop.objects.CatalogArticle;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

@DynamicObject
@ObjectClass(value="inetOrgPerson", init=true)
@RemoveDynamicAttribute("memberOf")
@AdditionalObjectClass({"DirXML-EntitlementRecipient", "DirXML-EmployeeAux"})
public class IdmPerson extends Person implements Serializable {

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
	public static final String ATTRIBUTE_NEXTOU = "nextOu";
	public static final String ATTRIBUTE_NEXTCOSTCENTER = "nextCostCenter";
	public static final String ATTRIBUTE_OUTRANSFERDATE = "ouTransferDate";
	public static final String ATTRIBUTE_OWNER = "owner";
	public static final String ATTRIBUTE_COMPANY = "company";
	public static final String ATTRIBUTE_OU = "ou";
	public static final String ATTRIBUTE_OCCUPATION = "occupation";
	public static final String ATTRIBUTE_LOCATION = "location";
	public static final String ATTRIBUTE_WORKFORCEID = "workforceId";
	public static final String ATTRIBUTE_MAIL = "mail";
	public static final String ATTRIBUTE_PHONENUMBER = "phoneNumber";

	private List<Role> assignedRoles = null;
	private List<Resource> assignedResources = null;
	
	@DynamicAttribute(externalName="nickiLastWorkingDay")
	private String lastWorkingDay;
	@DynamicAttribute(externalName="nickiQuitDate")
	private String quitDate;
	@DynamicAttribute(externalName="nickiActivationDate")
	private String activationDate;
	@DynamicReferenceAttribute(externalName="manager", foreignKey=Person.class, reference=Person.class,
			baseProperty="nicki.users.basedn")
	private String manager;
	@DynamicAttribute(externalName="employeeType")
	private String type;
	@DynamicAttribute(externalName="costCenter")
	private String costCenter;
	@DynamicAttribute(externalName="nickiGender")
	private String gender;
	@StructuredDynamicAttribute(externalName="DirXML-EntitlementRef", foreignKey=Entitlement.class)
	private String[] entitlement;
	@StructuredDynamicAttribute(externalName="nrfAssignedRoles", foreignKey=Role.class)
	private String[] role;
	@StructuredDynamicAttribute(externalName="nrfAssignedResources", foreignKey=Resource.class)
	private String[] resource;
	@DynamicAttribute(externalName="nickiBirthDate")
	private String birthDate;
	@DynamicAttribute(externalName="nickiEntryDate")
	private String entryDate;
	@DynamicAttribute(externalName="nickiOuChangeDate")
	private String ouChangeDate;
	@DynamicAttribute(externalName="nickiNextOu")
	private String nextOu;
	@DynamicAttribute(externalName="nickiNextCostCenter")
	private String nextCostCenter;
	@DynamicAttribute(externalName="nickiOuTransferDate")
	private String ouTransferDate;
	@DynamicAttribute(externalName="nickiOwner", foreignKey=Person.class)
	private String owner;
	@DynamicAttribute(externalName="company")
	private String company;
	@DynamicAttribute(externalName="nickiOccupation")
	private String occupation;
	@DynamicAttribute(externalName="groupMembership", foreignKey=Group.class)
	private String[] memberOf;
	@DynamicAttribute(externalName="ou", foreignKey=Org.class)
	private String ou;
	@DynamicAttribute(externalName="workforceID")
	private String workforceId;
	@DynamicAttribute(externalName="mail")
	private String mail;
	@DynamicAttribute(externalName="telephoneNumber")
	private String phoneNumber;
	
	public boolean hasRole(Role role2) {
		for (Role role : getRoles()) {
			if (StringUtils.equals(role.getPath(), role2.getPath())) {
				return true;
			}
		}
		return false;
	}

	public boolean hasResource(Resource resource2) {
		for (Resource resource : getResources()) {
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

	public String getNextOu() {
		return getAttribute(ATTRIBUTE_NEXTOU);
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

	public String getCompany() {
		return (String) get(ATTRIBUTE_COMPANY);
	}


	public void setLocation(String value) {
		put(ATTRIBUTE_LOCATION, value);
	}

	public String getLocation() {
		return (String) get(ATTRIBUTE_LOCATION);
	}
	
	public void setPhoneNumber(String value) {
		put(ATTRIBUTE_PHONENUMBER, value);
	}

	public String getPhoneNumber() {
		return (String) get(ATTRIBUTE_PHONENUMBER);
	}
	
	public void setOu(String value) {
		put(ATTRIBUTE_OU, value);
	}

	public String getOu() {
		return (String) get(ATTRIBUTE_OU);
	}

	public void setWorkforceId(String value) {
		put(ATTRIBUTE_WORKFORCEID, value);
	}

	public String getWorkforceId() {
		return (String) get(ATTRIBUTE_WORKFORCEID);
	}

	public void setOccupation(String value) {
		put(ATTRIBUTE_OCCUPATION, value);
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
				CatalogArticle catalogArticle = Catalog.getCatalog().getArticle(article.getArticleId());
				if (catalogArticle.getClass().isAssignableFrom(RoleCatalogArticle.class)) {
					assignedRoles.add(((RoleCatalogArticle)catalogArticle).getRole());
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
				CatalogArticle catalogArticle = Catalog.getCatalog().getArticle(article.getArticleId());
				if (catalogArticle.getClass().isAssignableFrom(ResourceCatalogArticle.class)) {
					assignedResources.add(((ResourceCatalogArticle)catalogArticle).getResource());
				}
			}
		}
		return assignedResources;
	}

	public enum GENDER {

		MALE, FEMALE
	};

	public enum PERSONTYPE {

		INTERNAL_USER("INTERNAL"), EXTERNAL_USER("EXTERNAL"), FUNCTIONAL_USER(
				"FUNCTIONAL"), SECONDARY_USER(
						"SECONDARY"), NOT_SET("");
		private final String type;

		private PERSONTYPE(String type) {
			this.type = type;
		}

		public static PERSONTYPE fromValue(String type) {
			if (INTERNAL_USER.getValue().equals(type)) {
				return INTERNAL_USER;
			} else if (EXTERNAL_USER.getValue().equals(type)) {
				return EXTERNAL_USER;
			} else if (FUNCTIONAL_USER.getValue().equals(type)) {
				return FUNCTIONAL_USER;
			} else if (SECONDARY_USER.getValue().equals(type)) {
				return SECONDARY_USER;
			}

			return NOT_SET;

		}

		public String getValue() {
			return type;
		}
	}

	public Person getManager() {
		return getForeignKeyObject(Person.class, ATTRIBUTE_MANAGER);
	}

	public void setManager(IdmPerson manager) {
		put(ATTRIBUTE_MANAGER, manager.getId());
	}

	public Person getOwner() {
		return getForeignKeyObject(Person.class, ATTRIBUTE_OWNER);
	}

	public void setOwner(IdmPerson owner) {
		put(ATTRIBUTE_OWNER, owner.getId());
	}
}
