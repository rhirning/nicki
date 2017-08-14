/**
 * Copyright (c) 2003-2015 Dr. Ralf Hirning
 * All rights reserved.
 *  
 * This program is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 * 
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU Public License v3.0
 * which is available at
 * http://www.gnu.org/licenses/gpl.html
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 * 
 * Any modifications to this file must keep this entire header
 * intact.
*/
package org.mgnl.nicki.shop.rules;

import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;
import org.mgnl.nicki.shop.base.objects.Selector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicValueProvider {
	private static final Logger LOG = LoggerFactory.getLogger(BasicValueProvider.class);
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
			return person.getModel().getAttributes().get(selectorName).getExternalName();
		} catch (InstantiateDynamicObjectException e) {
			LOG.error("Error", e);
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
