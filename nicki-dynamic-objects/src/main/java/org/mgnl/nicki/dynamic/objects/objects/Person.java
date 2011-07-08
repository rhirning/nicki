package org.mgnl.nicki.dynamic.objects.objects;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.dynamic.objects.objects.ShopArticle.TYPE;
import org.mgnl.nicki.dynamic.objects.reference.ReferenceDynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.StructuredDynamicAttribute;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;


@SuppressWarnings("serial")
public class Person extends DynamicTemplateObject {
	public final static String DATE_FORMAT = "yyyyMMdd";
	public static final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

	public void initDataModel()
	{
		addObjectClass("Person");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("surname", "sn", String.class);
		dynAttribute.setMandatory();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("givenname", "givenName", String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("fullname", "fullName", String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("lastWorkingDay", "nickiLastWorkingDay", String.class);
		addAttribute(dynAttribute);

		dynAttribute = new ReferenceDynamicAttribute("manager", "manager", String.class,
				"nicki.users.basedn", "(objectClass=Person)");
		dynAttribute.setForeignKey();
		addAttribute(dynAttribute);
		
		dynAttribute =  new StructuredDynamicAttribute("entitlement", "DirXML-EntitlementRef", String.class);
		dynAttribute.setMultiple();
		dynAttribute.setForeignKey();
		addAttribute(dynAttribute);

		dynAttribute =  new StructuredDynamicAttribute("role", "nrfAssignedRoles", String.class);
		dynAttribute.setMultiple();
		dynAttribute.setForeignKey();
		addAttribute(dynAttribute);

		dynAttribute =  new StructuredDynamicAttribute("resource", "nrfAssignedResources", String.class);
		dynAttribute.setMultiple();
		dynAttribute.setForeignKey();
		addAttribute(dynAttribute);

	}

	@Override
	public String getDisplayName() {
		return getAttribute("fullname") + " (" + getName() + ")";
	}


	// TODO
	public boolean hasArticle(ShopArticle article) {
		try {
			if (article.getArticleType() == TYPE.ARTICLE) {
				
			} else if (article.getArticleType() == TYPE.RESOURCE) {
				TemplateMethodModel method = (TemplateMethodModel) get("getResources");
				if (method != null) {
					@SuppressWarnings("unchecked")
					List<Object> resources = (List<Object>) method.exec(null);
					for (Iterator<Object> iterator = resources.iterator(); iterator
							.hasNext();) {
						Resource resource = (Resource) iterator.next();
						if (StringUtils.equals(resource.getPath(), article.getAttribute("resource"))) {
							return true;
						}
					}
				}
			} else if (article.getArticleType() == TYPE.ROLE) {
				TemplateMethodModel method = (TemplateMethodModel) get("getRoles");
				if (method != null) {
					@SuppressWarnings("unchecked")
					List<Object> roles = (List<Object>) method.exec(null);
					for (Iterator<Object> iterator = roles.iterator(); iterator
							.hasNext();) {
						Role role = (Role) iterator.next();
						if (StringUtils.equals(role.getPath(), article.getAttribute("role"))) {
							return true;
						}
					}
				}
			}
		} catch (TemplateModelException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean hasRole(Role role2) {
		try {
			TemplateMethodModel method = (TemplateMethodModel) get("getRoles");
			if (method != null) {
				@SuppressWarnings("unchecked")
				List<Object> roles = (List<Object>) method.exec(null);
				for (Iterator<Object> iterator = roles.iterator(); iterator
						.hasNext();) {
					Role role = (Role) iterator.next();
					if (StringUtils.equals(role.getPath(), role2.getPath())) {
						return true;
					}
				}
			}
		} catch (TemplateModelException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean hasResource(Resource resource2) {
		try {
			TemplateMethodModel method = (TemplateMethodModel) get("getResources");
			if (method != null) {
				@SuppressWarnings("unchecked")
				List<Object> resources = (List<Object>) method.exec(null);
				for (Iterator<Object> iterator = resources.iterator(); iterator
						.hasNext();) {
					Resource resource = (Resource) iterator.next();
					if (StringUtils.equals(resource.getPath(), resource2.getPath())) {
						return true;
					}
				}
			}
		} catch (TemplateModelException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String getFullname() {
		return getAttribute("fullname");
	}

	// TODO
	public static String getActiveFilter() {
		return "nickiInActive!=1";
	}

	// TODO
	public static String getInActiveFilter() {
		return "nickiInActive=1";
	}

	public void setExitDate(Date date) {
		put("lastWorkingDay", dateFormat.format(date));
	}
}
