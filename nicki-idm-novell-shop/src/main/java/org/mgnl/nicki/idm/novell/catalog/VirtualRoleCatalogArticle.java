/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mgnl.nicki.idm.novell.catalog;

import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.idm.novell.shop.objects.Role;
import org.mgnl.nicki.shop.base.objects.Catalog;
import org.mgnl.nicki.shop.base.objects.CatalogPage;

@SuppressWarnings("serial")
@DynamicObject
public class VirtualRoleCatalogArticle extends RoleCatalogArticle {
	private Role role;
	private CatalogPage page;

	public VirtualRoleCatalogArticle(Role role, CatalogPage page) {
		this.role = role;
		this.page = page;
	}

	@Override
	public String getPath() {
		return role.getPath();
	}

	@Override
	public String getName() {
		return role.getPath();
	}

	@Override
	public String getDisplayName() {
		return role.getDisplayName();
	}
	
	@Override
	public String getCatalogPath() {
		return page.getCatalogPath() + Catalog.PATH_SEPARATOR + getName();
	}

	public Role getRole() {
		return role;
	}


}
