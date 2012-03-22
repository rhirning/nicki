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
import org.mgnl.nicki.idm.novell.objects.Resource;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;

public class ResourceCatalogArticle extends CatalogArticle {

	private static final long serialVersionUID = 3397169876567940029L;

	public static final String ATTRIBUTE_RESOURCE = "resource";
	@Override
	public void initDataModel() {
		super.initDataModel();
		addObjectClass("nickiResourceArticle");
		
		DynamicAttribute dynAttribute = new ReferenceDynamicAttribute(Resource.class, ATTRIBUTE_RESOURCE, "nickiResourceRef", String.class,
				Config.getProperty("nicki.resources.basedn"));
		dynAttribute.setForeignKey(Resource.class);
		addAttribute(dynAttribute);

	}

	@Override
	public List<CatalogArticle> getArticles(Person person) {
		IDMExtension extension;
		try {
			extension = person.getContext().getObjectFactory().getDynamicObjectExtension(IDMExtension.class, person);
			@SuppressWarnings("unchecked")
			List<CatalogArticle> catalogArticles = (List<CatalogArticle>) extension.get("ResourceCatalogArticles");
			if (catalogArticles == null) {
				catalogArticles = new ArrayList<CatalogArticle>();
				List<Resource> resources = extension.getResources();
				List<CatalogArticle> articles = Catalog.getCatalog().getAllArticles();
				for (CatalogArticle catalogArticle : articles) {
					if (ResourceCatalogArticle.class.isAssignableFrom(catalogArticle.getClass())) {
						ResourceCatalogArticle resourceCatalogArticle = (ResourceCatalogArticle) catalogArticle;
						for (Resource resource : resources) {
							if (resourceCatalogArticle.contains(resource)) {
								catalogArticles.add(resourceCatalogArticle);
							}
						}
					}
				}
				extension.put("ResourceCatalogArticles", catalogArticles);
			}
			return catalogArticles;
		} catch (DynamicObjectException e) {
			e.printStackTrace();
		}
		return new ArrayList<CatalogArticle>();
	}

	public boolean contains(Resource resource) {
		if (StringUtils.equalsIgnoreCase(resource.getPath(), (String) get(ATTRIBUTE_RESOURCE))) {
			return true;
		}
		return false;
	}

	@Override
	public Date getStart(Person person) {
		Resource resource = getResource();
		if (resource != null) {
			return resource.getStartTime();
		}
		return null;
	}

	@Override
	public Date getEnd(Person person) {
		Resource resource = getResource();
		if (resource != null) {
			return resource.getEndTime();
		}
		return null;
	}

	@Override
	public String getSpecifier(Person person) {
		Resource resource = getResource();
		if (resource != null) {
			return resource.getParameter();
		}
		return null;
	}

	public Resource getResource() {
		return getForeignKeyObject(Resource.class, ATTRIBUTE_RESOURCE);
	}

}
