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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;

public class ChangeSet {
	private List<ArticleChange> changes = new ArrayList<ArticleChange>();

	public void addToMissing(Person person, CatalogArticle article) {
		changes.add(new ArticleChange(ArticleChange.TYPE.ADD, person, article));
	}
	public void addToSurplus(Person person, CatalogArticle article) {
		changes.add(new ArticleChange(ArticleChange.TYPE.REMOVE, person, article));
	}
	
	public boolean isEmpty() {
		return changes.isEmpty();
	}
	
	public boolean isNotEmpty() {
		return !isEmpty();
	}
	public List<ArticleChange> getChanges() {
		return Collections.unmodifiableList(changes);
	}
}
