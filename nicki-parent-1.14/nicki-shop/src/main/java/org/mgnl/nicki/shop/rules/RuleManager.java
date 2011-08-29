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

	public static Collection<Person> getUsers(CatalogArticle article) {
		HashMap<String, Person> persons = new HashMap<String, Person>();
		if (!article.hasRules()) {
			return persons.values();
		}
		
		RuleQuery query = getRuleQuery(article);
		if (!query.isNeedQuery()) {
			return persons.values();
		}
		for (BaseDn baseDn : query.getBaseDns()) {
			if (baseDn.getType() == BaseDn.TYPE.ALL) {
				addAll(persons, article.getContext().loadObjects(Person.class, baseDn.getPath(), query.getQuery()));
			} else {
				addAll(persons, article.getContext().loadChildObjects(Person.class, baseDn.getPath(), query.getQuery()));
			}
		}
		return persons.values();
	}

	private static void addAll(HashMap<String, Person> persons, List<Person> p2) {
		if (p2 != null && p2.size() > 0) {
			for (Person person : p2) {
				if (!persons.containsKey(person.getPath())) {
					persons.put(person.getPath(), person);
				}
			}
		}
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
					if (valueProvider.isHierarchical()) {
						ruleQuery.addBaseDn(valueProvider.getBaseDn(value));
					}
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
		if (ruleQuery.getBaseDns().isEmpty()) {
			ruleQuery.addBaseDn(new BaseDn(Config.getProperty("nicki.users.basedn"), BaseDn.TYPE.ALL));
		}
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
