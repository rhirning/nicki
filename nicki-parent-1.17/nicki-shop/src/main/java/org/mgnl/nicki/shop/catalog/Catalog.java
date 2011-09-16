package org.mgnl.nicki.shop.catalog;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.dynamic.objects.objects.DynamicTemplateObject;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.dynamic.objects.objects.Resource;
import org.mgnl.nicki.dynamic.objects.objects.Role;
import org.mgnl.nicki.ldap.auth.InvalidPrincipalException;
import org.mgnl.nicki.ldap.context.AppContext;
import org.mgnl.nicki.ldap.methods.LoadObjectsMethod;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;

@SuppressWarnings("serial")
public class Catalog extends DynamicTemplateObject {
	public static final String PATH_SEPARATOR = "/";
	private static final int renew = 1; // minutes
	private static Catalog instance;
	private static long nextInit = 0;
	
	private List<CatalogPage> pages = null;
	private List<CatalogArticle> articles = null;

	public void initDataModel() {
		// objectClass
		addObjectClass("nickiCatalog");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute("category", "nickiCategory", String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);
		
		addMethod("allArticles", new LoadObjectsMethod(CatalogArticle.class, this, "objectClass=nickiCatalogArticle"));

		addChild("page", "objectClass=nickiCatalogPage");
	}

	/*
	 * CatalogID: /separatedPathToArticle
	 */
	public CatalogArticle getArticle(String catalogArticleId) {
		String key = catalogArticleId;
		if (StringUtils.startsWith(key, PATH_SEPARATOR)) {
			key = StringUtils.substring(catalogArticleId, 1);
		}
		String pageKey = StringUtils.substringBefore(key, PATH_SEPARATOR);
		String articleKey = StringUtils.substringAfter(key, PATH_SEPARATOR);
		CatalogPage page = getPage(pageKey);
		if (page != null) {
			return page.getArticle(articleKey);
		} else {
			return null;
		}
	}
	
	public CatalogPage getPage(String key) {
		return getContext().loadChildObject(CatalogPage.class, this, key);
	}

	public List<CatalogArticle> getArticles() {
		if (articles == null) {
			articles = new ArrayList<CatalogArticle>();
			for (Iterator<CatalogPage> iterator = getPages().iterator(); iterator
					.hasNext();) {
				CatalogPage page= iterator.next();
				articles.addAll(page.getAllArticles()); 
			}
		}
		return articles;
	}
	
	public static synchronized Catalog getCatalog() {
		if (nextInit == 0 || nextInit < new Date().getTime()) {
			load();
			nextInit = new Date().getTime() + renew * 60 * 1000;
		}
		return instance;
	}

	private static void load() {
		try {
			instance = AppContext.getSystemContext().loadObject(Catalog.class, Config.getProperty("nicki.catalog"));
		} catch (InvalidPrincipalException e) {
			e.printStackTrace();
			instance = null;
		}
	}

	public CatalogPage getReferencedPage(
			String referencedPage) {
		String key = referencedPage;
		if (StringUtils.startsWith(key, PATH_SEPARATOR)) {
			key = StringUtils.substring(referencedPage, 1);
		}

		if (StringUtils.contains(key, PATH_SEPARATOR)) {
			String pageKey = StringUtils.substringBefore(key, PATH_SEPARATOR);
			String subPageKey = StringUtils.substringAfter(key, PATH_SEPARATOR);
			return getPage(pageKey).getPage(subPageKey);
		} else {
			return getPage(key);
		}
	}

	public CatalogArticle getArticle(Role role) {
		for (Iterator<CatalogArticle> iterator = getArticles().iterator(); iterator.hasNext();) {
			CatalogArticle article = iterator.next();
			if (article.getArticleType() == CatalogArticle.TYPE.ROLE) {
				if (StringUtils.equalsIgnoreCase(role.getPath(), article.getRolePath())) {
					return article;
				}
			}
		}
		return null;
	}

	public CatalogArticle getArticle(Resource resource) {
		for (Iterator<CatalogArticle> iterator = getArticles().iterator(); iterator.hasNext();) {
			CatalogArticle article = iterator.next();
			if (article.getArticleType() == CatalogArticle.TYPE.RESOURCE) {
				if (StringUtils.equalsIgnoreCase(resource.getPath(), article.getName())) {
					return article;
				}
			}
		}
		return null;
	}

	public List<CatalogPage> getPages() {
		if (pages == null) {
			pages = getContext().loadChildObjects(CatalogPage.class, this.getPath(), null);
		}
		return pages;
	}
	
	public List<CatalogArticle> getAllArticles(Person person) {
		List<CatalogArticle> articles = new ArrayList<CatalogArticle>();
		for (Iterator<CatalogArticle> iterator = getArticles().iterator(); iterator.hasNext();) {
			CatalogArticle catalogArticle = iterator.next();
			if (hasArticle(person, catalogArticle)) {
				articles.add(catalogArticle);
			}
		}
		return articles;
	}


	// TODO
	public boolean hasArticle(Person person, CatalogArticle article) {
		if (article.getArticleType() == CatalogArticle.TYPE.ARTICLE) {
			
		} else if (article.getArticleType() == CatalogArticle.TYPE.RESOURCE) {
			List<Resource> resources = person.getResources();
			for (Iterator<Resource> iterator = resources.iterator(); iterator
					.hasNext();) {
				Resource resource = iterator.next();
				if (StringUtils.equals(resource.getPath(), article.getAttribute("resource"))) {
					return true;
				}
			}
		} else if (article.getArticleType() == CatalogArticle.TYPE.ROLE) {
			List<Role> roles = person.getRoles();
			for (Iterator<Role> iterator = roles.iterator(); iterator
					.hasNext();) {
				Role role = iterator.next();
				if (StringUtils.equals(role.getPath(), article.getAttribute("role"))) {
					return true;
				}
			}
		}
		return false;
	}

	public List<CatalogArticle> getArticles(String path) {
		String key = path;
		if (StringUtils.startsWith(key, PATH_SEPARATOR)) {
			key = StringUtils.substring(path, 1);
		}
		String pageKey = StringUtils.substringBefore(key, PATH_SEPARATOR);
		String articleKey = StringUtils.substringAfter(key, PATH_SEPARATOR);
		CatalogPage page = getPage(pageKey);
		if (page != null) {
			return page.getArticles(articleKey);
		} else {
			return null;
		}
	}

}
