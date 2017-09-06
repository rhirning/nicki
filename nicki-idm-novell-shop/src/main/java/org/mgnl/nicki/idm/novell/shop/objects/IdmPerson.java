/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mgnl.nicki.idm.novell.shop.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.annotation.AdditionalObjectClass;
import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.DynamicReferenceAttribute;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.annotation.RemoveDynamicAttribute;
import org.mgnl.nicki.core.annotation.StructuredDynamicAttribute;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.Group;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.dynamic.objects.shop.AssignedArticle;
import org.mgnl.nicki.idm.novell.catalog.ResourceCatalogArticle;
import org.mgnl.nicki.idm.novell.catalog.RoleCatalogArticle;
import org.mgnl.nicki.ldap.helper.LdapHelper;
import org.mgnl.nicki.shop.base.objects.Catalog;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

@DynamicObject
@ObjectClass(value="inetOrgPerson", init=true)
@RemoveDynamicAttribute("memberOf")
@AdditionalObjectClass({"DirXML-EntitlementRecipient", "DirXML-EmployeeAux"})
public class IdmPerson extends Person implements Serializable {
	private static final Logger LOG = LoggerFactory.getLogger(IdmPerson.class);

	private static final long serialVersionUID = -6791692458041112275L;
	public static final String ATTRIBUTE_LASTWORKINGDAY = "lastWorkingDay";
	public static final String ATTRIBUTE_QUITDATE = "quitDate";
	public static final String ATTRIBUTE_ACTIVATIONDATE = "activationDate";
	public static final String ATTRIBUTE_MANAGER = "manager";
	public static final String ATTRIBUTE_TYPE = "type";
	public static final String ATTRIBUTE_TYPE_AS_STRING = "typeAsString";
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
	public static final String ATTRIBUTE_USERPASSWORD = "userPassword";

	private Collection<Group> assignedGroups;
	private Collection<Role> assignedRoles;
	private Collection<Role> groupRoles;
	private Collection<Role> containerRoles;
	private Collection<Resource> assignedResources;

	@DynamicAttribute(externalName="nickiLastWorkingDay")
	private String lastWorkingDay;
	@DynamicAttribute(externalName="nickiQuitDate")
	private String quitDate;
	@DynamicAttribute(externalName="nickiActivationDate")
	private String activationDate;
	@DynamicReferenceAttribute(externalName="manager", foreignKey=Person.class,
			reference=Person.class,
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
	@StructuredDynamicAttribute(externalName="nrfGroupRoles", foreignKey=Role.class)
	private String[] groupRole;
	@StructuredDynamicAttribute(externalName="nrfContainerRoles", foreignKey=Role.class)
	private String[] containerRole;
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
	@DynamicAttribute(externalName="ou")
	private String ou;
	@DynamicAttribute(externalName="workforceID")
	private String workforceId;
	@DynamicAttribute(externalName="mail")
	private String mail;
	@DynamicAttribute(externalName="telephoneNumber")
	private String phoneNumber;
	@DynamicAttribute(externalName = "userPassword")
	private String userPassword;



	@Override
	public Collection<Group> getGroups() {
		if (assignedGroups == null) {
			assignedGroups = getForeignKeyObjects(Group.class, "memberOf");
		}
		return assignedGroups;
	}

	public boolean hasRole(Role role2) {
		for (Role role : getRoles()) {
			if (StringUtils.equals(role.getPath(), role2.getPath())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasRole(String roleName) {
		for (Role role : getRoles()) {
			if (StringUtils.equalsIgnoreCase(role.getName(), roleName)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasGroup(String groupName) {
		@SuppressWarnings("unchecked")
		Collection<String> foreignKeys = (Collection<String>) get("memberOf");
		return LdapHelper.containsName(foreignKeys, groupName);
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

	public String getTypeAsString() {
		return getType().getDisplayName();
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
			put(ATTRIBUTE_BIRTHDATE, DataHelper.getDay(value));
		} else {
			clear(ATTRIBUTE_BIRTHDATE);
		}
	}

	public void setQuitDate(Date value) {
		if (value != null) {
			put(ATTRIBUTE_QUITDATE, DataHelper.getDay(value));
		} else {
			clear(ATTRIBUTE_QUITDATE);
		}
	}

	public void setActivationDate(Date value) {
		if (value != null) {
			put(ATTRIBUTE_ACTIVATIONDATE, DataHelper.getDay(value));
		} else {
			clear(ATTRIBUTE_ACTIVATIONDATE);
		}
	}

	public void setEntryDate(Date value) {
		if (value != null) {
			put(ATTRIBUTE_ENTRYDATE, DataHelper.getDay(value));
		} else {
			clear(ATTRIBUTE_ENTRYDATE);
		}
	}

	public void setOuChangeDate(Date value) {
		if (value != null) {
			put(ATTRIBUTE_OUCHANGEDATE, DataHelper.getDay(value));
		} else {
			clear(ATTRIBUTE_OUCHANGEDATE);
		}
	}

	public void setOuTransferDate(Date value) {
		if (value != null) {
			put(ATTRIBUTE_OUTRANSFERDATE, DataHelper.getDay(value));
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
			put(ATTRIBUTE_LASTWORKINGDAY, DataHelper.getDay(date));
		} else {
			clear(ATTRIBUTE_LASTWORKINGDAY);
		}
	}

	public Date getQuitDate() {
		try {
			return DataHelper.dateFromString((String) get(ATTRIBUTE_QUITDATE));
		} catch (Exception ex) {
			return null;
		}
	}

	public Date getActivationDate() {
		try {
			return DataHelper.dateFromString((String) get(ATTRIBUTE_ACTIVATIONDATE));
		} catch (Exception ex) {
			return null;
		}
	}

	public Date getEntryDate() {
		try {
			return DataHelper.dateFromString((String) get(ATTRIBUTE_ENTRYDATE));
		} catch (Exception ex) {
			return null;
		}
	}

	public Date getBirthDate() {
		try {
			return DataHelper.dateFromString((String) get(ATTRIBUTE_BIRTHDATE));
		} catch (Exception ex) {
			return null;
		}
	}

	public Date getOuChangeDate() {
		try {
			return DataHelper.dateFromString((String) get(ATTRIBUTE_OUCHANGEDATE));
		} catch (Exception ex) {
			return null;
		}
	}

	public Date getOuTransferDate() {
		try {
			return DataHelper.dateFromString((String) get(ATTRIBUTE_OUTRANSFERDATE));
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
			return DataHelper.dateFromString((String) get(ATTRIBUTE_LASTWORKINGDAY));
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


	@Override
	public boolean isMemberOf(String groupName) {
		for (Group group : getGroups()) {
			if (StringUtils.equalsIgnoreCase(group.getName(), groupName)) {
				return true;
			}
		}
		return false;
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

	public String getMail() {
		return (String) get(ATTRIBUTE_MAIL);
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

	public Collection<Role> getRoles() {
		Set<Role> roles = new HashSet<Role>();
		roles.addAll(getAssignedRoles());
		roles.addAll(getGroupRoles());
		roles.addAll(getContainerRoles());
		return roles;
	}

	@SuppressWarnings("unchecked")
	public Collection<Role> getAssignedRoles() {
		if (assignedRoles == null) {
			TemplateMethodModel method = (TemplateMethodModel) get("getRoles");
			if (method != null) {
				try {
					assignedRoles = (Collection<Role>) method.exec(null);
				} catch (TemplateModelException e) {
					LOG.error("Error", e);
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
	public Collection<Role> getGroupRoles() {
		if (groupRoles == null) {
			TemplateMethodModel method = (TemplateMethodModel) get("getGroupRoles");
			if (method != null) {
				try {
					groupRoles = (Collection<Role>) method.exec(null);
				} catch (TemplateModelException e) {
					LOG.error("Error", e);
					groupRoles =  new ArrayList<Role>();
				}
			}
		}
		return groupRoles;
	}

	@SuppressWarnings("unchecked")
	public Collection<Role> getContainerRoles() {
		if (containerRoles == null) {
			TemplateMethodModel method = (TemplateMethodModel) get("getContainerRoles");
			if (method != null) {
				try {
					containerRoles = (Collection<Role>) method.exec(null);
				} catch (TemplateModelException e) {
					LOG.error("Error", e);
					containerRoles =  new ArrayList<Role>();
				}
			}
		}
		return containerRoles;
	}

	@SuppressWarnings("unchecked")
	public Collection<Resource> getResources() {
		if (assignedResources == null) {
			TemplateMethodModel method = (TemplateMethodModel) get("getResources");
			if (method != null) {
				try {
					assignedResources = (Collection<Resource>) method.exec(null);
				} catch (TemplateModelException e) {
					LOG.error("Error", e);
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

		INTERNAL_USER("INTERNAL"),
		EXTERNAL_USER("EXTERNAL"),
		FUNCTIONAL_USER("FUNCTIONAL"),
		SECONDARY_USER("SECONDARY"),
		PROLIVE_USER("PROLIVE"),
		MAKLER_USER("MAKLER"),
		ADMIN_USER("ADMINACC"),
		WEST_USER("WEST"),
		NORTH_USER("NORTH"),
		NOT_SET("");
		private final String type;

		private PERSONTYPE(String type) {
			this.type = type;
		}

		public String getDisplayName() {
			return I18n.getText("nicki.dynamic.objects.persontype." + this);
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
			} else if (PROLIVE_USER.getValue().equals(type)) {
				return PROLIVE_USER;
			} else if (MAKLER_USER.getValue().equals(type)) {
				return MAKLER_USER;
			} else if (ADMIN_USER.getValue().equals(type)) {
				return ADMIN_USER;
			} else if (WEST_USER.getValue().equals(type)) {
				return WEST_USER;
			} else if (NORTH_USER.getValue().equals(type)) {
				return NORTH_USER;
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
		if (manager != null) {
			put(ATTRIBUTE_MANAGER, manager.getId());
		} else {
			clear(ATTRIBUTE_MANAGER);
		}
	}

	public Person getOwner() {
		return getForeignKeyObject(Person.class, ATTRIBUTE_OWNER);
	}

	public void setOwner(IdmPerson owner) {
		put(ATTRIBUTE_OWNER, owner.getId());
	}

	public void setMail(String mail) {
		put(ATTRIBUTE_MAIL, mail);
	}
	
	public void setUserPassword(String pwd) {
		if (StringUtils.isNotBlank(pwd)) {
			this.put(ATTRIBUTE_USERPASSWORD, pwd);
		}
	}
}
