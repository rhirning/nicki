
package org.mgnl.nicki.idm.novell.catalog;

/*-
 * #%L
 * nicki-idm-novell-shop
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.idm.novell.shop.objects.Role;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;
import org.mgnl.nicki.shop.base.objects.CatalogPage;
import org.mgnl.nicki.shop.base.objects.Provider;

@SuppressWarnings("serial")
public class RolesProvider implements Provider, Serializable {
	CatalogPage page = null;
	public List<CatalogArticle> getArticles(CatalogPage catalogPage) {
		List<CatalogArticle> articles = new ArrayList<CatalogArticle>();
		String filter = page.getAttribute("providerData");
		List<Role> roles = catalogPage.getContext().loadObjects(Role.class,
				Config.getProperty("nicki.roles.basedn"), filter);
		if (roles != null && roles.size() > 0) {
			for (Role role : roles) {
				articles.add(getCatalogArticle(role, catalogPage));
			}
		}
		return articles;
	}

	public CatalogArticle getArticle(String key) {
		Role role = this.page.getContext().loadObject(Role.class, key);
		if (role != null) {
			return  getCatalogArticle(role, this.page);
		}
		return null;
	}
	
	public CatalogArticle getCatalogArticle(Role role, CatalogPage catalogPage) {
		return new VirtualRoleCatalogArticle(role, catalogPage);
	}

	public void init(CatalogPage catalogPage) {
		this.page = catalogPage;
	}

	@Override
	public boolean isReadOnly() {
		return false;
	}

}
