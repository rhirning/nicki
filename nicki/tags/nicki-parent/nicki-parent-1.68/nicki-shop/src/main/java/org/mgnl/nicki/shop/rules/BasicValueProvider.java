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
package org.mgnl.nicki.shop.rules;

import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.dynamic.objects.shop.CatalogArticle;
import org.mgnl.nicki.dynamic.objects.shop.Selector;
import org.mgnl.nicki.ldap.data.InstantiateDynamicObjectException;

public class BasicValueProvider {
	private Selector selector;
	private String i18nBase;

	public void init(Selector selector, String i18nBase) {
		this.setSelector(selector);
		setI18nBase(i18nBase);
	}

	public void setSelector(Selector selector) {
		this.selector = selector;
	}

	public Selector getSelector() {
		return selector;
	}

	public static String getLdapName(CatalogArticle article, String selectorName) {
		try {
			Person person = article.getContext().getObjectFactory().getDynamicObject(Person.class);
			return person.getModel().getAttributes().get(selectorName).getLdapName();
		} catch (InstantiateDynamicObjectException e) {
			e.printStackTrace();
			return "INVALID";
		}
	}

	public void setI18nBase(String i18nBase) {
		this.i18nBase = i18nBase;
	}

	public String getI18nBase() {
		return i18nBase;
	}

}