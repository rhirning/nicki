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
package org.mgnl.nicki.idm.novell.catalog;

import java.util.ArrayList;
import java.util.List;

import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.idm.novell.shop.objects.Resource;
import org.mgnl.nicki.shop.base.objects.Catalog;
import org.mgnl.nicki.shop.base.objects.CatalogArticleAttribute;
import org.mgnl.nicki.shop.base.objects.CatalogPage;

@SuppressWarnings("serial")
@DynamicObject
public class VirtualResourceCatalogArticle extends ResourceCatalogArticle {
	private Resource resource;
	private CatalogPage page;

	public VirtualResourceCatalogArticle(Resource resource, CatalogPage page) {
		this.resource = resource;
		this.page = page;
	}

	public Resource getResource() {
		return resource;
	}

	@Override
	public List<CatalogArticleAttribute> getAttributes() {
		return new ArrayList<CatalogArticleAttribute>();
	}

	@Override
	public List<CatalogArticleAttribute> getInheritedAttributes() {
		return page.getAttributes();
	}

	@Override
	public List<CatalogArticleAttribute> getAllAttributes() {
		return getInheritedAttributes();
	}

	@Override
	public String getName() {
		return resource.getPath();
	}

	@Override
	public String getPath() {
		return resource.getPath();
	}

	@Override
	public String getDisplayName() {
		return resource.getDisplayName();
	}
	
	@Override
	public String getCatalogPath() {
		return page.getCatalogPath() + Catalog.PATH_SEPARATOR + getName();
	}


}