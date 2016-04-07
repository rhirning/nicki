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

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.ldap.helper.LdapHelper;
import org.mgnl.nicki.ldap.helper.LdapHelper.LOGIC;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;
import org.mgnl.nicki.vaadin.base.data.TreeContainer;
import org.mgnl.nicki.vaadin.base.editor.DataProvider;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectRoot;
import org.mgnl.nicki.vaadin.base.editor.TreeSelector;

import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ExpandEvent;

@SuppressWarnings("serial")
public class OrgValueProvider extends BasicValueProvider implements ValueProviderComponent, Serializable {
	
	public static final String STOP = "|"; 
	public static final String NO_STOP = ""; 

	private TreeSelector treeSelector;
	private TreeContainer treeContainer;
	private OptionGroup optionGroup;
	private HorizontalLayout layout;
	private String userBaseDn = Config.getProperty("nicki.users.basedn");
	private String userBasePath = LdapHelper.getSlashPath(null, userBaseDn);

	public OrgValueProvider() {
	}

	public Component getValueList() {
		layout = new HorizontalLayout();
		layout.setSpacing(true);
		treeSelector = new TreeSelector();
		DataProvider treeDataProvider = new DynamicObjectRoot(userBaseDn, new OrgOnlyFilter());
		treeContainer = new TreeContainer(getSelector().getContext(), treeDataProvider, I18n.getText(getI18nBase() + ".org.title"));
		treeSelector.setContainerDataSource(treeContainer.getTree());
		treeSelector.setItemCaptionPropertyId(TreeContainer.PROPERTY_NAME);
		treeSelector.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
		treeSelector.setItemIconPropertyId(TreeContainer.PROPERTY_ICON);

		treeSelector.addListener(new Tree.ExpandListener() {

			public void nodeExpand(ExpandEvent event) {
				DynamicObject object = (DynamicObject) event.getItemId();
				treeContainer.loadChildren(object);
			}
		});
		layout.addComponent(treeSelector.getComponent());
		
		optionGroup = new OptionGroup();
		optionGroup.addItem(STOP);
		optionGroup.setItemCaption(STOP, I18n.getText(getI18nBase() + ".self.title"));

		optionGroup.addItem(NO_STOP);
		optionGroup.setItemCaption(NO_STOP, I18n.getText(getI18nBase() + ".children.title"));

		optionGroup.setValue("|");
		optionGroup.setNullSelectionAllowed(false);
		layout.addComponent(optionGroup);
		return layout;
	}
	
	public String getValue() {
		
		if (treeSelector.getValue() != null) {
			return treeSelector.getValue().getSlashPath(treeContainer.getRoot()) + optionGroup.getValue();
		} else {
			return null;
		}
	}
	
	public String getPersonQuery(CatalogArticle article, String value) {
		value = StringUtils.stripEnd(value, "/");
		if (getType(value) == TYPE.ALL) {
			StringBuilder sb = new StringBuilder();
			LdapHelper.addQuery(sb, getSharpQuery(value), LOGIC.OR);
			LdapHelper.addQuery(sb, getChildrenQuery(value), LOGIC.OR);
			return sb.toString();
		} else {
			return getSharpQuery(value);
		}
	}
	
	private String getSharpQuery(String value) {
		return "nickiDirectory" + "=" + userBasePath + StringUtils.stripEnd(value, STOP);
	}

	private String getChildrenQuery(String value) {
		return "nickiDirectory" + "=" + userBasePath + value + "/*";
	}

	public TYPE getType(String value) {
		if (StringUtils.endsWith(value, STOP)) {
			return TYPE.SELF;
		} else {
			return TYPE.ALL;
		}
	}

	public String getArticleQuery(Person person, Object value) {
		StringBuilder sb = new StringBuilder();
		LdapHelper.addQuery(sb, "nickiRule=" + getSelector().getName() + "=*", LOGIC.OR);
		LdapHelper.negateQuery(sb);
		LdapHelper.addQuery(sb, "nickiRule=" + getSelector().getName() + "=/", LOGIC.OR);

		String path = person.getSlashPath(userBaseDn);
		// strip off username
		path = StringUtils.substringBeforeLast(path, "/");
		LdapHelper.addQuery(sb, "nickiRule=" + getSelector().getName() + "=" + path + "|", LOGIC.OR);

		while(StringUtils.isNotEmpty(path)) {
			LdapHelper.addQuery(sb, "nickiRule=" + getSelector().getName() + "=" + path, LOGIC.OR);
			path = StringUtils.substringBeforeLast(path, "/");
		}
		return sb.toString();
	}




}