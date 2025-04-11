
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
import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.dynamic.objects.types.TextArea;
import org.mgnl.nicki.shop.base.inventory.InventoryArticle;

// TODO: Auto-generated Javadoc
/**
 * The Class CatalogArticle.
 */
@DynamicObject
@ObjectClass("nickiCatalogArticle")
public class CatalogArticle extends CatalogObject {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2340086861870174607L;
	
	/**
	 * The Enum TYPE.
	 */
	public static enum TYPE {
/** The resource. */
RESOURCE, 
 /** The role. */
 ROLE, 
 /** The article. */
 ARTICLE};
	
	/** The Constant CAPTION_START. */
	public static final String CAPTION_START = "nicki.rights.attribute.dateFrom.label";
	
	/** The Constant CAPTION_END. */
	public static final String CAPTION_END = "nicki.rights.attribute.dateTo.label";

	/** The name. */
	@DynamicAttribute(externalName="cn", naming=true)
	private String name;
	
	/** The display name. */
	@DynamicAttribute(externalName="displayName")
	private String displayName;
	
	/** The description. */
	@DynamicAttribute(externalName="nickiDescription")
	private TextArea description;
	
	/** The approval. */
	@DynamicAttribute(externalName="nickiApproval")
	private String approval;
	
	/** The category. */
	@DynamicAttribute(externalName="nickiCategory")
	private String[] category;
	
	/** The rule. */
	@DynamicAttribute(externalName="nickiRule", editorClass="org.mgnl.nicki.shop.rules.RuleAttributeField")
	private String[] rule;
	

	/**
	 * Checks for article.
	 *
	 * @param person the person
	 * @param article the article
	 * @return true, if successful
	 */
	public boolean hasArticle(Person person, CatalogArticle article) {
		for (InventoryArticle inventoryArticle : getInventoryArticles(person)) {
			if (inventoryArticle.getArticle().getPath() == article.getPath()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks for article.
	 *
	 * @param person the person
	 * @return true, if successful
	 */
	public boolean hasArticle(Person person) {
		return false;
	}
	
	/**
	 * Gets the article type.
	 *
	 * @return the article type
	 */
	public TYPE getArticleType() {
		return TYPE.ARTICLE;
	}
	
	/**
	 * Gets the article path.
	 *
	 * @return the article path
	 */
	public String getArticlePath() {
		return this.getPath();
	}


	/**
	 * To string.
	 *
	 * @return the string
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[catalogArticle name='").append(getName());
		sb.append("' displayName='").append(getDisplayName());
		sb.append("']");
		return sb.toString();
	}

	/**
	 * Gets the approval path.
	 *
	 * @return the approval path
	 */
	public String getApprovalPath() {
		return getAttribute("approval");
	}
	
	/**
	 * Gets the catalog path.
	 *
	 * @return the catalog path
	 */
	public String getCatalogPath() {
		return getSlashPath(Catalog.getCatalog());
	}

	/**
	 * Gets the categories.
	 *
	 * @return the categories
	 */
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

	/**
	 * Checks for description.
	 *
	 * @return true, if successful
	 */
	public boolean hasDescription() {
		return StringUtils.isNotEmpty(getDescription());
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return getAttribute("description");
	}

	/**
	 * Checks for rules.
	 *
	 * @return true, if successful
	 */
	public boolean hasRules() {
		return getRules() != null && getRules().size() > 0;
	}
	
	/**
	 * Gets the rules.
	 *
	 * @return the rules
	 */
	@SuppressWarnings("unchecked")
	public List<String> getRules() {
		return (List<String>) get("rule");
	}
	
	/**
	 * Gets the display name.
	 *
	 * @return the display name
	 */
	@Override
	public String getDisplayName() {
		if (StringUtils.isNotEmpty((String) get("displayName"))) {
			return I18n.getText((String) get("displayName"));
		} else {
			return super.getDisplayName();
		}
	}

	/**
	 * Checks if is multiple.
	 *
	 * @return true, if is multiple
	 */
	public boolean isMultiple() {
		return false;
	}

	/**
	 * Gets the inventory articles.
	 *
	 * @param person the person
	 * @return the inventory articles
	 */
	public List<InventoryArticle> getInventoryArticles(Person person) {
		return new ArrayList<InventoryArticle>();
	}

	/**
	 * Gets the permission dn.
	 *
	 * @return the permission dn
	 */
	public String getPermissionDn() {
		return null;
	}


	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {

		put("description", description);
	}

	/**
	 * Gets the child list.
	 *
	 * @return the child list
	 */
	@Override
	public List<? extends CatalogObject> getChildList() {
		return new ArrayList<CatalogObject>();
	}
}
