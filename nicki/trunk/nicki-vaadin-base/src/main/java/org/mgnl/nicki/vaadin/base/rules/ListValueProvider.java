/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.rules;

import java.util.List;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.ldap.helper.LdapHelper;
import org.mgnl.nicki.ldap.helper.LdapHelper.LOGIC;
import org.mgnl.nicki.shop.catalog.CatalogArticle;
import org.mgnl.nicki.shop.rules.BasicValueProvider;

import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ListSelect;

public class ListValueProvider extends BasicValueProvider implements ValueProviderComponent {

	private AbstractSelect value;
	
	public ListValueProvider() {
	}

	public AbstractSelect getValueList() {
		
		value = new ListSelect();
		value.setCaption(I18n.getText(getI18nBase() + ".value.title"));
		value.setImmediate(false);
		value.setWidth("200px");
		value.setHeight("200px");

		value.setNullSelectionAllowed(false);

		for (String entry : getSelector().getValues()) {
			value.addItem(entry);
		}
		return value;
	}
	
	public String getValue() {
		return (String)value.getValue();
	}

	public String getPersonQuery(CatalogArticle article, String value) {
		return getLdapName(article, getSelector().getName()) + "=" + value;
	}

	public String getArticleQuery(Person person, Object value) {
		StringBuffer sb2 = new StringBuffer();
		LdapHelper.addQuery(sb2, "nickiRule=" + getSelector().getName() + "=*", LOGIC.OR);
		LdapHelper.negateQuery(sb2);
		if (value == null) {
			// nothing to add
		} else if (value instanceof String) {
			String stringValue = (String) value;
			LdapHelper.addQuery(sb2, "nickiRule=" + getSelector().getName() + "=" + stringValue, LOGIC.OR);
		} else if (value instanceof List) {
			@SuppressWarnings("unchecked")
			List<String> values = (List<String>) value;
			for (String stringValue : values) {
				LdapHelper.addQuery(sb2, "nickiRule=" + getSelector().getName() + "=" + stringValue, LOGIC.OR);
			}
		}
		return sb2.toString();
	}


}
