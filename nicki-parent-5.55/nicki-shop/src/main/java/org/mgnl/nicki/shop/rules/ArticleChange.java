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

import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;
import org.mgnl.nicki.shop.base.objects.CartEntry.ACTION;

public class ArticleChange {
	public enum TYPE {ADD, REMOVE};
	
	private TYPE type;
	private Person person;
	private CatalogArticle article;
	public ArticleChange(TYPE type, Person person, CatalogArticle article) {
		super();
		this.type = type;
		this.person = person;
		this.article = article;
	}
	public TYPE getType() {
		return type;
	}
	public Person getPerson() {
		return person;
	}
	public CatalogArticle getArticle() {
		return article;
	}
	
	public ACTION getCartEntryType() {
		if (type == TYPE.REMOVE) {
			return ACTION.DELETE;
		} else {
			return ACTION.ADD;
		}
		
	}

}
