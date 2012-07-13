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
package org.mgnl.nicki.idm.novell.extensions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.dynamic.objects.extensions.BasicExtension;
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
import org.mgnl.nicki.ldap.objects.DynamicObjectExtension;
import org.mgnl.nicki.ldap.objects.StructuredDynamicAttribute;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

public class IDMExtension extends BasicExtension implements DynamicObjectExtension, Serializable {

	private static final long serialVersionUID = -6791692458041112275L;
	public static final String ATTRIBUTE_LASTWORKINGDAY = "lastWorkingDay";
	public static final String ATTRIBUTE_QUITDATE = "quitDate";
	public static final String ATTRIBUTE_ACTIVATIONDATE = "activationDate";
	public static final String ATTRIBUTE_MANAGER = "manager";
	public static final String ATTRIBUTE_TYPE = "type";
	public static final String ATTRIBUTE_COSTCENTER = "costCenter";
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

	@Override
	public void initExtensionModel() {
		super.initExtensionModel();
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

	public void setCostCenter(String value) {
		put(ATTRIBUTE_COSTCENTER, value);
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

	@SuppressWarnings("unchecked")
	public List<AssignedArticle> getAssignedArticles() {
		if (extensionGet(ATTRIBUTE_ASSIGNEDARTICLE) == null) {
			List<AssignedArticle> list = new ArrayList<AssignedArticle>();
			@SuppressWarnings("unchecked")
			List<String> articles = (List<String>) get(ATTRIBUTE_ASSIGNEDARTICLE);
			if (articles != null) {
				for (String text : articles) {
					list.add(new AssignedArticle(text));
				}
			}
			extensionPut(ATTRIBUTE_ASSIGNEDARTICLE, list);
		}
		return (List<AssignedArticle>) extensionGet(ATTRIBUTE_ASSIGNEDARTICLE);
	}

	@SuppressWarnings("unchecked")
	public List<Role> getRoles() {
		final String ROLES_ATTRIBUTE = "Roles";
		if (extensionGet(ROLES_ATTRIBUTE) == null) {
			TemplateMethodModel method = (TemplateMethodModel) get("getRoles");
			if (method != null) {
				try {
					extensionPut(ROLES_ATTRIBUTE, (List<Role>) method.exec(null));
				} catch (TemplateModelException e) {
					e.printStackTrace();
					extensionPut(ROLES_ATTRIBUTE, new ArrayList<Role>());
				}
			}
			for (AssignedArticle article : getAssignedArticles()) {
				if (article.getCatalogArticle().getClass().isAssignableFrom(RoleCatalogArticle.class)) {
					((List<Role>)extensionGet(ROLES_ATTRIBUTE)).add(((RoleCatalogArticle)article.getCatalogArticle()).getRole());
				}
			}
		}
		return (List<Role>)extensionGet(ROLES_ATTRIBUTE);
	}

	@SuppressWarnings("unchecked")
	public List<Resource> getResources() {
		final String RESOURCES_ATTRIBUTE = "RESOURCES";
		if (extensionGet(RESOURCES_ATTRIBUTE) == null) {
			TemplateMethodModel method = (TemplateMethodModel) get("getResources");
			if (method != null) {
				try {
					extensionPut(RESOURCES_ATTRIBUTE, (List<Role>) method.exec(null));
				} catch (TemplateModelException e) {
					e.printStackTrace();
					extensionPut(RESOURCES_ATTRIBUTE, new ArrayList<Role>());
				}
			}
			for (AssignedArticle article : getAssignedArticles()) {
				if (article.getCatalogArticle().getClass().isAssignableFrom(ResourceCatalogArticle.class)) {
					((List<Resource>)extensionGet(RESOURCES_ATTRIBUTE)).add(((ResourceCatalogArticle)article.getCatalogArticle()).getResource());
				}
			}
		}
		return (List<Resource>)extensionGet(RESOURCES_ATTRIBUTE);
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

}
