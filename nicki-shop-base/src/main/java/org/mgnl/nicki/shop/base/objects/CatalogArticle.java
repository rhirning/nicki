
package org.mgnl.nicki.shop.base.objects;

/*-
 * #%L
 * nicki-shop-base
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


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.dynamic.objects.types.TextArea;
import org.mgnl.nicki.shop.base.inventory.InventoryArticle;

@DynamicObject
@ObjectClass("nickiCatalogArticle")
public class CatalogArticle extends CatalogObject {
	private static final long serialVersionUID = 2340086861870174607L;
	public static enum TYPE {RESOURCE, ROLE, ARTICLE};
	public static final String CAPTION_START = "nicki.rights.attribute.dateFrom.label";
	public static final String CAPTION_END = "nicki.rights.attribute.dateTo.label";

	@DynamicAttribute(externalName="cn", naming=true)
	private String name;
	@DynamicAttribute(externalName="displayName")
	private String displayName;
	@DynamicAttribute(externalName="nickiDescription")
	private TextArea description;
	@DynamicAttribute(externalName="nickiApproval")
	private String approval;
	@DynamicAttribute(externalName="nickiCategory")
	private String[] category;
	@DynamicAttribute(externalName="nickiRule", editorClass="org.mgnl.nicki.shop.rules.RuleAttributeField")
	private String[] rule;
	

	public boolean hasArticle(Person person, CatalogArticle article) {
		for (InventoryArticle inventoryArticle : getInventoryArticles(person)) {
			if (inventoryArticle.getArticle().getPath() == article.getPath()) {
				return true;
			}
		}
		return false;
	}

	public boolean hasArticle(Person person) {
		return false;
	}
	
	public TYPE getArticleType() {
		return TYPE.ARTICLE;
	}
	
	public String getArticlePath() {
		return this.getPath();
	}


	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[catalogArticle name='").append(getName());
		sb.append("' displayName='").append(getDisplayName());
		sb.append("']");
		return sb.toString();
	}

	public String getApprovalPath() {
		return getAttribute("approval");
	}
	
	public String getCatalogPath() {
		return getSlashPath(Catalog.getCatalog());
	}

	@SuppressWarnings("unchecked")
	public List<String> getCategories() {
		List<String> categories = new ArrayList<String>();
		if (null != get("category")) {
			categories.addAll((Collection<? extends String>) get("category"));
		}
		if (getParent(CatalogPage.class).hasCategories()) {
			categories.addAll(getParent(CatalogPage.class).getCategories());
		}
		return categories;
	}

	public boolean hasDescription() {
		return StringUtils.isNotEmpty(getDescription());
	}

	public String getDescription() {
		return getAttribute("description");
	}

	public boolean hasRules() {
		return getRules() != null && getRules().size() > 0;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getRules() {
		return (List<String>) get("rule");
	}
	
	@Override
	public String getDisplayName() {
		if (StringUtils.isNotEmpty((String) get("displayName"))) {
			return I18n.getText((String) get("displayName"));
		} else {
			return super.getDisplayName();
		}
	}

	public boolean isMultiple() {
		return false;
	}

	public List<InventoryArticle> getInventoryArticles(Person person) {
		return new ArrayList<InventoryArticle>();
	}

	public String getPermissionDn() {
		return null;
	}


	public void setDescription(String description) {

		put("description", description);
	}

	@Override
	public List<? extends CatalogObject> getChildList() {
		return new ArrayList<CatalogObject>();
	}
}
