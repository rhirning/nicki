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

	@Override
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

	@Override
	public String getPersonQuery(CatalogArticle article, String value) {
		return getLdapName(article, getSelector().getName()) + "=" + value;
	}

	@Override
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
