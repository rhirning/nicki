package org.mgnl.nicki.vaadin.base.rules;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.ldap.helper.LdapHelper;
import org.mgnl.nicki.ldap.helper.LdapHelper.LOGIC;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.shop.catalog.CatalogArticle;
import org.mgnl.nicki.shop.rules.BasicValueProvider;
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

	@Override
	public Component getValueList() {
		layout = new HorizontalLayout();
		layout.setSpacing(true);
		treeSelector = new TreeSelector();
		DataProvider treeDataProvider = new DynamicObjectRoot(userBaseDn, new OrgOnlyFilter());
		treeContainer = new TreeContainer(getSelector().getContext(), treeDataProvider, I18n.getText(getI18nBase() + ".org.title"));
		treeSelector.setContainerDataSource(treeContainer.getTree());
		treeSelector.setItemCaptionPropertyId(TreeContainer.PROPERTY_NAME);
		treeSelector.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
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
	
	@Override
	public String getPersonQuery(CatalogArticle article, String value) {
		value = StringUtils.stripEnd(value, "/");
		if (getType(value) == TYPE.ALL) {
			StringBuffer sb = new StringBuffer();
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

	@Override
	public String getArticleQuery(Person person, Object value) {
		StringBuffer sb = new StringBuffer();
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
