package org.mgnl.nicki.shop.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.ldap.helper.LdapHelper;
import org.mgnl.nicki.ldap.helper.LdapHelper.LOGIC;
import org.mgnl.nicki.shop.catalog.CatalogArticle;
import org.mgnl.nicki.shop.catalog.Selector;

/* 
 * Attribute nickiDirectory: Pfad getrennt mit "/"
 */
public class RuleManager {
	
	public static List<CatalogArticle> getArticles(Person person) {
		String query = getArticleQuery(person);
		return person.getContext().loadObjects(CatalogArticle.class,
				Config.getProperty("nicki.catalogs.basedn"), query);
	}
	
	public static String getArticleQuery(Person person) {
		StringBuffer sb = new StringBuffer();
		LdapHelper.addQuery(sb, "nickiRule=*", LOGIC.AND);
		List<Selector> selectors = person.getContext().loadChildObjects(Selector.class, 
				Config.getProperty("nicki.selectors.basedn"), ""); 
		for (Selector selector : selectors) {
			Object value = person.get(selector.getName());
			if (selector.hasValueProvider()) {
				ValueProvider valueProvider = selector.getValueProvider();
				LdapHelper.addQuery(sb, valueProvider.getArticleQuery(person, value), LOGIC.AND);
			} else {
				StringBuffer sb2 = new StringBuffer();
				LdapHelper.addQuery(sb2, "nickiRule=" + selector.getName() + "=*", LOGIC.OR);
				LdapHelper.negateQuery(sb2);
				if (value == null) {
					// nothing to add
				} else if (value instanceof String) {
					String stringValue = (String) value;
					LdapHelper.addQuery(sb2, "nickiRule=" + selector.getName() + "=" + stringValue, LOGIC.OR);
				} else if (value instanceof List) {
					@SuppressWarnings("unchecked")
					List<String> values = (List<String>) value;
					for (String stringValue : values) {
						LdapHelper.addQuery(sb2, "nickiRule=" + selector.getName() + "=" + stringValue, LOGIC.OR);
					}
				}
				LdapHelper.addQuery(sb, sb2.toString(), LOGIC.AND);
			}
		}
		return sb.toString();
	}

	public static List<Person> getUsers(CatalogArticle article, String additionalQuery) {
		if (!article.hasRules()) {
			if (StringUtils.isEmpty(additionalQuery)) {
				return new ArrayList<Person>();
			} else {
				
			}
		}
		
		RuleQuery query = getRuleQuery(article);
		if (!query.isNeedQuery()) {
			return new ArrayList<Person>();
		}
		StringBuffer filter = new StringBuffer();
		if (StringUtils.isNotEmpty(query.getQuery())) {
			LdapHelper.addQuery(filter, query.getQuery(), LOGIC.AND);
		}
		if (StringUtils.isNotEmpty(additionalQuery)) {
			LdapHelper.addQuery(filter, additionalQuery, LOGIC.AND);
		}
		return article.getContext().loadObjects(Person.class, query.getBaseDn(), filter.toString());
	}

	public static String getAssignedRuleArticlesQuery(Person person) {
		StringBuffer sb = new StringBuffer();
		@SuppressWarnings("unchecked")
		List<String> articles = (List<String>) person.get("assignedRuleArticle");
		if (articles != null && articles.size() > 0) {
			for (String article : articles) {
				LdapHelper.addQuery(sb, "entryDn=" + article, LOGIC.OR);
			}
		}
		return sb.toString();
	}
	
	public static String getAssignedRulePersonsQuery(CatalogArticle article) {
		return "(nickiAssignedRuleArticle=" + article.getPath() + ")";
	}
	
	// missing = plan - assigned 	= (plannedArticles) && !(assignedArticles)
	// surplus = assigned - planned = (assignedArticles) && !(plannedArticles)
	public static ChangeSet getChangeSet(Person person) {
		ChangeSet changeSet = new ChangeSet();
		String planned = getArticleQuery(person);
		String assigned = getAssignedRuleArticlesQuery(person);
		
		// missing
		StringBuffer sb = new StringBuffer();
		LdapHelper.addQuery(sb, assigned, LOGIC.AND);
		LdapHelper.negateQuery(sb);
		LdapHelper.addQuery(sb, planned, LOGIC.AND);
		List<CatalogArticle> missingArticles = person.getContext().loadObjects(CatalogArticle.class,
				Config.getProperty("nicki.catalogs.basedn"), sb.toString());
		if (missingArticles != null && missingArticles.size() > 0) {
			for (CatalogArticle article : missingArticles) {
				changeSet.addToMissing(person, article);
			}
		}
		
		// surplus
		sb.setLength(0);
		LdapHelper.addQuery(sb, planned, LOGIC.AND);
		LdapHelper.negateQuery(sb);
		LdapHelper.addQuery(sb, assigned, LOGIC.AND);
		List<CatalogArticle> surplusArticles = person.getContext().loadObjects(CatalogArticle.class,
				Config.getProperty("nicki.catalogs.basedn"), sb.toString());
		if (surplusArticles != null && surplusArticles.size() > 0) {
			for (CatalogArticle article : surplusArticles) {
				changeSet.addToSurplus(person, article);
			}
		}
		return changeSet;
	}
	
	// missing = plan - assigned 	= (plannedArticles) && !(assignedArticles)
	// surplus = assigned - planned = (assignedArticles) && !(plannedArticles)
	public static ChangeSet getChangeSet(CatalogArticle article) {
		ChangeSet changeSet = new ChangeSet();
		StringBuffer sb = new StringBuffer();
		// missing
		if (article.hasRules()) {
			LdapHelper.addQuery(sb, getAssignedRulePersonsQuery(article), LOGIC.AND);
			LdapHelper.negateQuery(sb);
			Collection<Person> missing = getUsers(article, sb.toString());
			for (Person person : missing) {
				changeSet.addToMissing(person, article);
			}
		}
		
		return changeSet;
	}
	
	

	public static RuleQuery getRuleQuery(CatalogArticle article) {
		RuleQuery ruleQuery = new RuleQuery();
		if (!article.hasRules()) {
			return ruleQuery;
		}
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		for (String entry : article.getRules()) {
			add(map,entry);
		}
		StringBuffer sb = new StringBuffer();
		for (String selectorName : map.keySet()) {
			StringBuffer sb2 = new StringBuffer();
			for (String value : map.get(selectorName)) {
				Selector selector = getSelector(article, selectorName);
				if (selector.hasValueProvider()) {
					ValueProvider valueProvider = selector.getValueProvider();
					String query = valueProvider.getPersonQuery(article, value);
					LdapHelper.addQuery(sb2, query, LOGIC.OR);
				} else {
					String query = getPersonQuery(article, selectorName, value);
					LdapHelper.addQuery(sb2, query, LOGIC.OR);
				}
			}
			if (sb2.length() > 0) {
				LdapHelper.addQuery(sb, sb2.toString(), LOGIC.AND);
			}
		}
		LdapHelper.addQuery(sb, Person.getActiveFilter(), LOGIC.AND);

		ruleQuery.setBaseDn(Config.getProperty("nicki.users.basedn"));
		ruleQuery.setQuery(sb.toString());
		return ruleQuery;
	}

	private static Selector getSelector(CatalogArticle article, String selectorName) {
		return article.getContext().loadObject(Selector.class, "cn=" + selectorName + "," + Config.getProperty("nicki.selectors.basedn"));
	}

	public static String getPersonQuery(CatalogArticle article, String selectorName, String value) {
		Selector selector = getSelector(article, selectorName);
		if (selector.hasValueProvider()) {
			return selector.getValueProvider().getPersonQuery(article, value);
		} else {
			return BasicValueProvider.getLdapName(article, selectorName) + "=" + value;
		}
	}

	private static void add(Map<String, List<String>> map, String entry) {
		String parts[] = StringUtils.split(entry, "=");
		if (parts.length == 2) {
			List<String> list = map.get(parts[0]);
			if (list == null) {
				list = new ArrayList<String>();
				map.put(parts[0], list);
			}
			list.add(parts[1]);
		}
	}
	
	
}