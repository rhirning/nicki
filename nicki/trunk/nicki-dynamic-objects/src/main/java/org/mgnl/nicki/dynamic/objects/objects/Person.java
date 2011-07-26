package org.mgnl.nicki.dynamic.objects.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.dynamic.objects.reference.ReferenceDynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.StructuredDynamicAttribute;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;


@SuppressWarnings("serial")
public class Person extends DynamicTemplateObject {
	public enum GENDER {MALE, FEMALE};

	public enum PERSONTYPE {
		INTERNAL_USER("INTERNAL"),
		EXTERNAL_USER("EXTERNAL"),
		TECHNICAL_USER("TECHNICAL"),
		NOT_SET("");

		private final String type;

		private PERSONTYPE(String type) {
			this.type = type;
		}

		public static PERSONTYPE fromValue(String type) {
			if (INTERNAL_USER.getValue().equals(type)) {
				return INTERNAL_USER;
			} else if (EXTERNAL_USER.getValue().equals(type))  {
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

		REQUESTED("beantragt"),
		INACTIVE("inaktiv-vor-eintritt"), 
		ACTIVE("aktiv"), 
		DEACTIVATED("deaktiviert"), 
		RESIGNED("ausgetreten"),
		NOT_SET("");
		
		private final String status;
		
		private STATUS(String status) {
			this.status = status;
		}
		
		public static STATUS fromValue(String status) {
			if (INACTIVE.getValue().equals(status)) {
				return INACTIVE;
			} else if (ACTIVE.getValue().equals(status))  {
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

	public void initDataModel()
	{
		addObjectClass("Person");
		addAdditionalObjectClass("nickiUserAux");
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
				"nicki.users.basedn", "objectClass=Person");
		dynAttribute.setForeignKey(Person.class);
		addAttribute(dynAttribute);
		
		dynAttribute =  new StructuredDynamicAttribute("entitlement", "DirXML-EntitlementRef", String.class);
		dynAttribute.setMultiple();
		dynAttribute.setForeignKey(Entitlement.class);
		addAttribute(dynAttribute);

		dynAttribute =  new StructuredDynamicAttribute("role", "nrfAssignedRoles", String.class);
		dynAttribute.setMultiple();
		dynAttribute.setForeignKey(Role.class);
		addAttribute(dynAttribute);

		dynAttribute =  new StructuredDynamicAttribute("resource", "nrfAssignedResources", String.class);
		dynAttribute.setMultiple();
		dynAttribute.setForeignKey(Resource.class);
		addAttribute(dynAttribute);

		dynAttribute =  new DynamicAttribute("status", "nickiStatus", String.class);
		addAttribute(dynAttribute);

		dynAttribute =  new DynamicAttribute("type", "employeeType", String.class);
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
	
	public List<CatalogArticle> getAllArticles(Catalog catalog) {
		List<CatalogArticle> articles = new ArrayList<CatalogArticle>();
		for (Iterator<CatalogArticle> iterator = catalog.getAllArticles().iterator(); iterator.hasNext();) {
			CatalogArticle catalogArticle = iterator.next();
			if (hasArticle(catalogArticle)) {
				articles.add(catalogArticle);
			}
		}
		return articles;
	}


	// TODO
	public boolean hasArticle(CatalogArticle article) {
		try {
			if (article.getArticleType() == CatalogArticle.TYPE.ARTICLE) {
				
			} else if (article.getArticleType() == CatalogArticle.TYPE.RESOURCE) {
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
			} else if (article.getArticleType() == CatalogArticle.TYPE.ROLE) {
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

	// TODO - edit cna
	public static String getActiveFilter() {
		return "nickiStatus=" + STATUS.ACTIVE.getValue();
	}

	// TODO - edit cna
	public static String getInActiveFilter() {
		return "!(|(nickiStatus=" + STATUS.ACTIVE.getValue() + ")(nickiStatus=" + STATUS.REQUESTED.getValue()+ "))";
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
		// TODO Auto-generated method stub
		
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

	public void setTask(String value) {
		put("task", value);		
	}


}
