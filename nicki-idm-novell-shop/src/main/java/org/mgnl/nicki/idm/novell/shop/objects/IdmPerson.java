
package org.mgnl.nicki.idm.novell.shop.objects;

/*-
 * #%L
 * nicki-idm-novell-shop
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.annotation.AdditionalObjectClass;
import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.DynamicReferenceAttribute;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.annotation.RemoveDynamicAttribute;
import org.mgnl.nicki.core.annotation.StructuredDynamicAttribute;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.dynamic.objects.objects.Group;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.dynamic.objects.shop.AssignedArticle;
import org.mgnl.nicki.idm.novell.catalog.ResourceCatalogArticle;
import org.mgnl.nicki.idm.novell.catalog.RoleCatalogArticle;
import org.mgnl.nicki.ldap.helper.LdapHelper;
import org.mgnl.nicki.shop.base.objects.Catalog;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import lombok.extern.slf4j.Slf4j;


/**
 * The Class IdmPerson.
 */
@Slf4j
@DynamicObject
@ObjectClass(value="inetOrgPerson", init=true)
@RemoveDynamicAttribute("memberOf")
@AdditionalObjectClass({"DirXML-EntitlementRecipient", "DirXML-EmployeeAux"})
public class IdmPerson extends Person implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6791692458041112275L;
	
	/** The Constant ROLE_ATTRIBUTES. */
	public static final String[] ROLE_ATTRIBUTES = {"role", "groupRole", "containerRole", "inheritedRole"};
	
	/** The Constant ATTRIBUTE_LASTWORKINGDAY. */
	public static final String ATTRIBUTE_LASTWORKINGDAY = "lastWorkingDay";
	
	/** The Constant ATTRIBUTE_QUITDATE. */
	public static final String ATTRIBUTE_QUITDATE = "quitDate";
	
	/** The Constant ATTRIBUTE_ACTIVATIONDATE. */
	public static final String ATTRIBUTE_ACTIVATIONDATE = "activationDate";
	
	/** The Constant ATTRIBUTE_MANAGER. */
	public static final String ATTRIBUTE_MANAGER = "manager";
	
	/** The Constant ATTRIBUTE_COSTCENTER. */
	public static final String ATTRIBUTE_COSTCENTER = "costCenter";
	
	/** The Constant ATTRIBUTE_GENDER. */
	public static final String ATTRIBUTE_GENDER = "gender";
	
	/** The Constant ATTRIBUTE_ENTITLEMENT. */
	public static final String ATTRIBUTE_ENTITLEMENT = "entitlement";
	
	/** The Constant ATTRIBUTE_ROLE. */
	public static final String ATTRIBUTE_ROLE = "role";
	
	/** The Constant ATTRIBUTE_RESOURCE. */
	public static final String ATTRIBUTE_RESOURCE = "resource";
	
	/** The Constant ATTRIBUTE_BIRTHDATE. */
	public static final String ATTRIBUTE_BIRTHDATE = "birthDate";
	
	/** The Constant ATTRIBUTE_ENTRYDATE. */
	public static final String ATTRIBUTE_ENTRYDATE = "entryDate";
	
	/** The Constant ATTRIBUTE_OUCHANGEDATE. */
	public static final String ATTRIBUTE_OUCHANGEDATE = "ouChangeDate";
	
	/** The Constant ATTRIBUTE_NEXTOU. */
	public static final String ATTRIBUTE_NEXTOU = "nextOu";
	
	/** The Constant ATTRIBUTE_NEXTCOSTCENTER. */
	public static final String ATTRIBUTE_NEXTCOSTCENTER = "nextCostCenter";
	
	/** The Constant ATTRIBUTE_OUTRANSFERDATE. */
	public static final String ATTRIBUTE_OUTRANSFERDATE = "ouTransferDate";
	
	/** The Constant ATTRIBUTE_OWNER. */
	public static final String ATTRIBUTE_OWNER = "owner";
	
	/** The Constant ATTRIBUTE_COMPANY. */
	public static final String ATTRIBUTE_COMPANY = "company";
	
	/** The Constant ATTRIBUTE_OU. */
	public static final String ATTRIBUTE_OU = "ou";
	
	/** The Constant ATTRIBUTE_OCCUPATION. */
	public static final String ATTRIBUTE_OCCUPATION = "occupation";
	
	/** The Constant ATTRIBUTE_LOCATION. */
	public static final String ATTRIBUTE_LOCATION = "location";
	
	/** The Constant ATTRIBUTE_WORKFORCEID. */
	public static final String ATTRIBUTE_WORKFORCEID = "workforceId";
	
	/** The Constant ATTRIBUTE_MAIL. */
	public static final String ATTRIBUTE_MAIL = "mail";
	
	/** The Constant ATTRIBUTE_PHONENUMBER. */
	public static final String ATTRIBUTE_PHONENUMBER = "phoneNumber";
	
	/** The Constant ATTRIBUTE_USERPASSWORD. */
	public static final String ATTRIBUTE_USERPASSWORD = "userPassword";

	/** The assigned groups. */
	private Collection<Group> assignedGroups;
	
	/** The assigned roles. */
	private Collection<Role> assignedRoles;
	
	/** The group roles. */
	private Collection<Role> groupRoles;
	
	/** The container roles. */
	private Collection<Role> containerRoles;
	
	/** The inherited roles. */
	private Collection<Role> inheritedRoles;
	
	/** The assigned resources. */
	private Collection<Resource> assignedResources;

	/** The last working day. */
	@DynamicAttribute(externalName="nickiLastWorkingDay")
	private String lastWorkingDay;
	
	/** The quit date. */
	@DynamicAttribute(externalName="nickiQuitDate")
	private String quitDate;
	
	/** The activation date. */
	@DynamicAttribute(externalName="nickiActivationDate")
	private String activationDate;
	
	/** The manager. */
	@DynamicReferenceAttribute(externalName="manager", foreignKey=Person.class,
			reference=Person.class,
			baseProperty="nicki.users.basedn")
	private String manager;
	
	/** The type. */
	@DynamicAttribute(externalName="employeeType")
	private String type;
	
	/** The cost center. */
	@DynamicAttribute(externalName="costCenter")
	private String costCenter;
	
	/** The gender. */
	@DynamicAttribute(externalName="nickiGender")
	private String gender;
	
	/** The entitlement. */
	@StructuredDynamicAttribute(externalName="DirXML-EntitlementRef", foreignKey=Entitlement.class)
	private String[] entitlement;
	
	/** The role. */
	@StructuredDynamicAttribute(externalName="nrfAssignedRoles", foreignKey=Role.class)
	private String[] role;
	
	/** The group role. */
	@StructuredDynamicAttribute(externalName="nrfGroupRoles", foreignKey=Role.class)
	private String[] groupRole;
	
	/** The container role. */
	@StructuredDynamicAttribute(externalName="nrfContainerRoles", foreignKey=Role.class)
	private String[] containerRole;
	
	/** The inherited role. */
	@StructuredDynamicAttribute(externalName="nrfInheritedRoles", foreignKey=Role.class)
	private String[] inheritedRole;
	
	/** The resource. */
	@StructuredDynamicAttribute(externalName="nrfAssignedResources", foreignKey=Resource.class)
	private String[] resource;
	
	/** The birth date. */
	@DynamicAttribute(externalName="nickiBirthDate")
	private String birthDate;
	
	/** The entry date. */
	@DynamicAttribute(externalName="nickiEntryDate")
	private String entryDate;
	
	/** The ou change date. */
	@DynamicAttribute(externalName="nickiOuChangeDate")
	private String ouChangeDate;
	
	/** The next ou. */
	@DynamicAttribute(externalName="nickiNextOu")
	private String nextOu;
	
	/** The next cost center. */
	@DynamicAttribute(externalName="nickiNextCostCenter")
	private String nextCostCenter;
	
	/** The ou transfer date. */
	@DynamicAttribute(externalName="nickiOuTransferDate")
	private String ouTransferDate;
	
	/** The owner. */
	@DynamicAttribute(externalName="nickiOwner", foreignKey=Person.class)
	private String owner;
	
	/** The company. */
	@DynamicAttribute(externalName="company")
	private String company;
	
	/** The occupation. */
	@DynamicAttribute(externalName="nickiOccupation")
	private String occupation;
	
	/** The member of. */
	@DynamicAttribute(externalName="groupMembership", foreignKey=Group.class)
	private String[] memberOf;
	
	/** The ou. */
	@DynamicAttribute(externalName="ou")
	private String ou;
	
	/** The workforce id. */
	@DynamicAttribute(externalName="workforceID")
	private String workforceId;
	
	/** The mail. */
	@DynamicAttribute(externalName="mail")
	private String mail;
	
	/** The phone number. */
	@DynamicAttribute(externalName="telephoneNumber")
	private String phoneNumber;
	
	/** The user password. */
	@DynamicAttribute(externalName = "userPassword")
	private String userPassword;



	/**
	 * Gets the groups.
	 *
	 * @return the groups
	 */
	@Override
	public Collection<Group> getGroups() {
		if (assignedGroups == null) {
			assignedGroups = getForeignKeyObjects(Group.class, "memberOf");
		}
		return assignedGroups;
	}

	/**
	 * Checks for role.
	 *
	 * @param role2 the role 2
	 * @return true, if successful
	 */
	public boolean hasRole(Role role2) {
		for (Role role : getRoles()) {
			if (StringUtils.equals(role.getPath(), role2.getPath())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks for role.
	 *
	 * @param roleName the role name
	 * @return true, if successful
	 */
	@Override
	public boolean hasRole(String roleName) {
		for (String roleAttribute : ROLE_ATTRIBUTES) {
			if (get(roleAttribute) != null) {
				@SuppressWarnings("unchecked")
				Collection<String> foreignKeys = (Collection<String>) get(roleAttribute);
				if (LdapHelper.containsName(foreignKeys, roleName)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Checks for group.
	 *
	 * @param groupName the group name
	 * @return true, if successful
	 */
	@Override
	public boolean hasGroup(String groupName) {
		@SuppressWarnings("unchecked")
		Collection<String> foreignKeys = (Collection<String>) get("memberOf");
		return LdapHelper.containsName(foreignKeys, groupName);
	}


	/**
	 * Checks for resource.
	 *
	 * @param resource2 the resource 2
	 * @return true, if successful
	 */
	public boolean hasResource(Resource resource2) {
		for (Resource resource : getResources()) {
			if (StringUtils.equals(resource.getPath(), resource2.getPath())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Sets the cost center.
	 *
	 * @param value the new cost center
	 */
	public void setCostCenter(String value) {
		put(ATTRIBUTE_COSTCENTER, value);
	}

	/**
	 * Sets the gender.
	 *
	 * @param value the new gender
	 */
	public void setGender(GENDER value) {
		if(null != value) {
			put(ATTRIBUTE_GENDER, value.toString());
		} else {
			clear(ATTRIBUTE_GENDER);
		}
	}

	/**
	 * Sets the birth date.
	 *
	 * @param value the new birth date
	 */
	public void setBirthDate(Date value) {
		if (value != null) {
			put(ATTRIBUTE_BIRTHDATE, DataHelper.getDay(value));
		} else {
			clear(ATTRIBUTE_BIRTHDATE);
		}
	}

	/**
	 * Sets the quit date.
	 *
	 * @param value the new quit date
	 */
	public void setQuitDate(Date value) {
		if (value != null) {
			put(ATTRIBUTE_QUITDATE, DataHelper.getDay(value));
		} else {
			clear(ATTRIBUTE_QUITDATE);
		}
	}

	/**
	 * Sets the activation date.
	 *
	 * @param value the new activation date
	 */
	public void setActivationDate(Date value) {
		if (value != null) {
			put(ATTRIBUTE_ACTIVATIONDATE, DataHelper.getDay(value));
		} else {
			clear(ATTRIBUTE_ACTIVATIONDATE);
		}
	}

	/**
	 * Sets the entry date.
	 *
	 * @param value the new entry date
	 */
	public void setEntryDate(Date value) {
		if (value != null) {
			put(ATTRIBUTE_ENTRYDATE, DataHelper.getDay(value));
		} else {
			clear(ATTRIBUTE_ENTRYDATE);
		}
	}

	/**
	 * Sets the ou change date.
	 *
	 * @param value the new ou change date
	 */
	public void setOuChangeDate(Date value) {
		if (value != null) {
			put(ATTRIBUTE_OUCHANGEDATE, DataHelper.getDay(value));
		} else {
			clear(ATTRIBUTE_OUCHANGEDATE);
		}
	}

	/**
	 * Sets the ou transfer date.
	 *
	 * @param value the new ou transfer date
	 */
	public void setOuTransferDate(Date value) {
		if (value != null) {
			put(ATTRIBUTE_OUTRANSFERDATE, DataHelper.getDay(value));
		} else {
			clear(ATTRIBUTE_OUTRANSFERDATE);
		}
	}

	/**
	 * Sets the next ou.
	 *
	 * @param value the new next ou
	 */
	public void setNextOu(String value) {
		put(ATTRIBUTE_NEXTOU, value);
	}

	/**
	 * Sets the next cost center.
	 *
	 * @param value the new next cost center
	 */
	public void setNextCostCenter(String value) {
		put(ATTRIBUTE_NEXTCOSTCENTER, value);
	}

	/**
	 * Sets the last working day.
	 *
	 * @param date the new last working day
	 */
	public void setLastWorkingDay(Date date) {
		if (date != null) {
			put(ATTRIBUTE_LASTWORKINGDAY, DataHelper.getDay(date));
		} else {
			clear(ATTRIBUTE_LASTWORKINGDAY);
		}
	}

	/**
	 * Gets the quit date.
	 *
	 * @return the quit date
	 */
	public Date getQuitDate() {
		try {
			return DataHelper.dateFromString((String) get(ATTRIBUTE_QUITDATE));
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Gets the activation date.
	 *
	 * @return the activation date
	 */
	public Date getActivationDate() {
		try {
			return DataHelper.dateFromString((String) get(ATTRIBUTE_ACTIVATIONDATE));
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Gets the entry date.
	 *
	 * @return the entry date
	 */
	public Date getEntryDate() {
		try {
			return DataHelper.dateFromString((String) get(ATTRIBUTE_ENTRYDATE));
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Gets the birth date.
	 *
	 * @return the birth date
	 */
	public Date getBirthDate() {
		try {
			return DataHelper.dateFromString((String) get(ATTRIBUTE_BIRTHDATE));
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Gets the ou change date.
	 *
	 * @return the ou change date
	 */
	public Date getOuChangeDate() {
		try {
			return DataHelper.dateFromString((String) get(ATTRIBUTE_OUCHANGEDATE));
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Gets the ou transfer date.
	 *
	 * @return the ou transfer date
	 */
	public Date getOuTransferDate() {
		try {
			return DataHelper.dateFromString((String) get(ATTRIBUTE_OUTRANSFERDATE));
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Gets the next ou.
	 *
	 * @return the next ou
	 */
	public String getNextOu() {
		return getAttribute(ATTRIBUTE_NEXTOU);
	}


	/**
	 * Gets the next cost center.
	 *
	 * @return the next cost center
	 */
	public String getNextCostCenter() {
		return (String) get(ATTRIBUTE_NEXTCOSTCENTER);
	}

	/**
	 * Gets the last working day.
	 *
	 * @return the last working day
	 */
	public Date getLastWorkingDay() {
		try {
			return DataHelper.dateFromString((String) get(ATTRIBUTE_LASTWORKINGDAY));
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Sets the owner.
	 *
	 * @param value the new owner
	 */
	public void setOwner(Person value) {
		if (null != value) {
			put(ATTRIBUTE_OWNER, value.getPath());
		} else {
			clear(ATTRIBUTE_OWNER);
		}
	}


	/**
	 * Checks if is member of.
	 *
	 * @param groupName the group name
	 * @return true, if is member of
	 */
	@Override
	public boolean isMemberOf(String groupName) {
		for (Group group : getGroups()) {
			if (StringUtils.equalsIgnoreCase(group.getName(), groupName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Sets the company.
	 *
	 * @param value the new company
	 */
	public void setCompany(String value) {
		put(ATTRIBUTE_COMPANY, value);
	}

	/**
	 * Gets the company.
	 *
	 * @return the company
	 */
	public String getCompany() {
		return (String) get(ATTRIBUTE_COMPANY);
	}


	/**
	 * Sets the location.
	 *
	 * @param value the new location
	 */
	public void setLocation(String value) {
		put(ATTRIBUTE_LOCATION, value);
	}

	/**
	 * Gets the location.
	 *
	 * @return the location
	 */
	public String getLocation() {
		return (String) get(ATTRIBUTE_LOCATION);
	}

	/**
	 * Gets the mail.
	 *
	 * @return the mail
	 */
	public String getMail() {
		return (String) get(ATTRIBUTE_MAIL);
	}

	/**
	 * Sets the phone number.
	 *
	 * @param value the new phone number
	 */
	public void setPhoneNumber(String value) {
		put(ATTRIBUTE_PHONENUMBER, value);
	}

	/**
	 * Gets the phone number.
	 *
	 * @return the phone number
	 */
	public String getPhoneNumber() {
		return (String) get(ATTRIBUTE_PHONENUMBER);
	}

	/**
	 * Sets the ou.
	 *
	 * @param value the new ou
	 */
	public void setOu(String value) {
		put(ATTRIBUTE_OU, value);
	}

	/**
	 * Gets the ou.
	 *
	 * @return the ou
	 */
	public String getOu() {
		return (String) get(ATTRIBUTE_OU);
	}

	/**
	 * Sets the workforce id.
	 *
	 * @param value the new workforce id
	 */
	public void setWorkforceId(String value) {
		put(ATTRIBUTE_WORKFORCEID, value);
	}

	/**
	 * Gets the workforce id.
	 *
	 * @return the workforce id
	 */
	public String getWorkforceId() {
		return (String) get(ATTRIBUTE_WORKFORCEID);
	}

	/**
	 * Sets the occupation.
	 *
	 * @param value the new occupation
	 */
	public void setOccupation(String value) {
		put(ATTRIBUTE_OCCUPATION, value);
	}

	/**
	 * Gets the roles.
	 *
	 * @return the roles
	 */
	public Collection<Role> getRoles() {
		Set<Role> roles = new HashSet<Role>();
		roles.addAll(getAssignedRoles());
		roles.addAll(getGroupRoles());
		roles.addAll(getContainerRoles());
		roles.addAll(getInheritedRoles());
		return roles;
	}

	/**
	 * Gets the assigned roles.
	 *
	 * @return the assigned roles
	 */
	@SuppressWarnings("unchecked")
	public Collection<Role> getAssignedRoles() {
		if (assignedRoles == null) {
			TemplateMethodModelEx method = (TemplateMethodModelEx) get("getRoles");
			if (method != null) {
				try {
					assignedRoles = (Collection<Role>) method.exec(null);
				} catch (TemplateModelException e) {
					log.error("Error", e);
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

	/**
	 * Gets the group roles.
	 *
	 * @return the group roles
	 */
	@SuppressWarnings("unchecked")
	public Collection<Role> getGroupRoles() {
		if (groupRoles == null) {
			TemplateMethodModelEx method = (TemplateMethodModelEx) get("getGroupRoles");
			if (method != null) {
				try {
					groupRoles = (Collection<Role>) method.exec(null);
				} catch (TemplateModelException e) {
					log.error("Error", e);
					groupRoles =  new ArrayList<Role>();
				}
			}
		}
		return groupRoles;
	}

	/**
	 * Gets the container roles.
	 *
	 * @return the container roles
	 */
	@SuppressWarnings("unchecked")
	public Collection<Role> getContainerRoles() {
		if (containerRoles == null) {
			TemplateMethodModelEx method = (TemplateMethodModelEx) get("getContainerRoles");
			if (method != null) {
				try {
					containerRoles = (Collection<Role>) method.exec(null);
				} catch (TemplateModelException e) {
					log.error("Error", e);
					containerRoles =  new ArrayList<Role>();
				}
			}
		}
		return containerRoles;
	}
	
	/**
	 * Gets the inherited roles.
	 *
	 * @return the inherited roles
	 */
	@SuppressWarnings("unchecked")
	public Collection<Role> getInheritedRoles() {
		if (inheritedRoles == null) {
			TemplateMethodModelEx method = (TemplateMethodModelEx) get("getInheritedRoles");
			if (method != null) {
				try {
					inheritedRoles = (Collection<Role>) method.exec(null);
				} catch (TemplateModelException e) {
					log.error("Error", e);
					inheritedRoles =  new ArrayList<Role>();
				}
			}
		}
		return inheritedRoles;
	}

	/**
	 * Gets the resources.
	 *
	 * @return the resources
	 */
	@SuppressWarnings("unchecked")
	public Collection<Resource> getResources() {
		if (assignedResources == null) {
			TemplateMethodModelEx method = (TemplateMethodModelEx) get("getResources");
			if (method != null) {
				try {
					assignedResources = (Collection<Resource>) method.exec(null);
				} catch (TemplateModelException e) {
					log.error("Error", e);
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


	/**
	 * The Enum GENDER.
	 */
	public enum GENDER {

		/** The male. */
		MALE, /** The female. */
 FEMALE
	};


	/**
	 * Gets the manager.
	 *
	 * @return the manager
	 */
	public Person getManager() {
		return getForeignKeyObject(Person.class, ATTRIBUTE_MANAGER);
	}

	/**
	 * Sets the manager.
	 *
	 * @param manager the new manager
	 */
	public void setManager(Person manager) {
		if (manager != null) {
			put(ATTRIBUTE_MANAGER, manager.getId());
		} else {
			clear(ATTRIBUTE_MANAGER);
		}
	}

	/**
	 * Gets the owner.
	 *
	 * @return the owner
	 */
	public Person getOwner() {
		return getForeignKeyObject(Person.class, ATTRIBUTE_OWNER);
	}

	/**
	 * Sets the owner.
	 *
	 * @param owner the new owner
	 */
	public void setOwner(IdmPerson owner) {
		put(ATTRIBUTE_OWNER, owner.getId());
	}

	/**
	 * Sets the mail.
	 *
	 * @param mail the new mail
	 */
	public void setMail(String mail) {
		put(ATTRIBUTE_MAIL, mail);
	}
	
	/**
	 * Sets the user password.
	 *
	 * @param pwd the new user password
	 */
	public void setUserPassword(String pwd) {
		if (StringUtils.isNotBlank(pwd)) {
			this.put(ATTRIBUTE_USERPASSWORD, pwd);
		}
	}
}
