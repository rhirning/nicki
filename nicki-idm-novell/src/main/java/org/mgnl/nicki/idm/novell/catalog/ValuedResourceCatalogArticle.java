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

import org.mgnl.nicki.core.util.Classes;
import org.mgnl.nicki.dynamic.objects.types.TextArea;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.shop.objects.CatalogValueProvider;
import org.mgnl.nicki.shop.objects.MultipleInstancesCatalogArticle;
import org.mgnl.nicki.shop.objects.XmlValueProvider;

public class ValuedResourceCatalogArticle extends ResourceCatalogArticle implements MultipleInstancesCatalogArticle {

	private static final long serialVersionUID = -7208705030668378943L;
	private CatalogValueProvider provider = null;

	@Override
	public void initDataModel() {
		super.initDataModel();
		addObjectClass("nickiValuedResourceArticle");
		
		DynamicAttribute dynAttribute = new DynamicAttribute("provider", "nickiProvider", String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("providerData", "nickiProviderData", TextArea.class);
		addAttribute(dynAttribute);
	}

	public boolean isMultiple() {
		return true;
	}

	public String getValueProviderClass() {
		return getAttribute("provider");
	}

	public CatalogValueProvider getValueProvider() {
		if (provider == null) {
			try {
				provider = (CatalogValueProvider)Classes.newInstance(getValueProviderClass());
				provider.init(this);
				return provider;
			} catch (Exception e) {
				provider = new XmlValueProvider();
				provider.init(this);				
			}
		}
		return provider;
	}

}
