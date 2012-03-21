package org.mgnl.nicki.idm.novell.catalog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.dynamic.objects.reference.ReferenceDynamicAttribute;
import org.mgnl.nicki.dynamic.objects.shop.Catalog;
import org.mgnl.nicki.dynamic.objects.shop.CatalogArticle;
import org.mgnl.nicki.idm.novell.extensions.IDMExtension;
import org.mgnl.nicki.idm.novell.objects.Role;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;

public class RoleCatalogArticle extends CatalogArticle {

	private static final long serialVersionUID = -5530389061310235734L;
	public static final String ATTRIBUTE_ROLE = "role";
	@Override
	public void initDataModel() {
		super.initDataModel();
		
		DynamicAttribute dynAttribute = new ReferenceDynamicAttribute(Role.class, ATTRIBUTE_ROLE, "nickiRoleRef", String.class,
				Config.getProperty("nicki.roles.basedn"));
		dynAttribute.setForeignKey(Role.class);
		addAttribute(dynAttribute);

	}


	@Override
	public List<CatalogArticle> getArticles(Person person) {
		IDMExtension extension;
		try {
			extension = person.getContext().getObjectFactory().getDynamicObjectExtension(IDMExtension.class, person);
			@SuppressWarnings("unchecked")
			List<CatalogArticle> catalogArticles = (List<CatalogArticle>) extension.get("RoleCatalogArticles");
			if (catalogArticles == null) {
				catalogArticles = new ArrayList<CatalogArticle>();
				List<Role> roles = extension.getRoles();
				List<CatalogArticle> articles = Catalog.getCatalog().getAllArticles();
				for (CatalogArticle catalogArticle : articles) {
					if (RoleCatalogArticle.class.isAssignableFrom(catalogArticle.getClass())) {
						RoleCatalogArticle roleCatalogArticle = (RoleCatalogArticle) catalogArticle;
						for (Role role : roles) {
							if (roleCatalogArticle.contains(role)) {
								catalogArticles.add(roleCatalogArticle);
							}
						}
					}
				}
				extension.put("RoleCatalogArticles", catalogArticles);
			}
			return catalogArticles;
		} catch (DynamicObjectException e) {
			e.printStackTrace();
		}
		return new ArrayList<CatalogArticle>();
	}

	public boolean contains(Role role) {
		if (StringUtils.equalsIgnoreCase(role.getPath(), (String) get(ATTRIBUTE_ROLE))) {
			return true;
		}
		return false;
	}


	@Override
	public Date getStart(Person person) {
		Role role = getRole();
		if (role != null) {
			return role.getStartTime();
		}
		return null;
	}

	@Override
	public Date getEnd(Person person) {
		Role role = getRole();
		if (role != null) {
			return role.getEndTime();
		}
		return null;
	}

	@Override
	public String getSpecifier(Person person) {
		return null;
	}

	public Role getRole() {
		return getForeignKeyObject(Role.class, ATTRIBUTE_ROLE);
	}

}
