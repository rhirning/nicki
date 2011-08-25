package org.mgnl.nicki.vaadin.base.rules;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.shop.catalog.Selector;
import org.mgnl.nicki.shop.rules.BaseDn;
import org.mgnl.nicki.shop.rules.BaseDn.TYPE;
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
public class OrgValueProvider implements ValueProviderComponent, Serializable {
	
	public static final String STOP = "|"; 
	public static final String NO_STOP = ""; 

	private Selector selector;
	private TreeSelector treeSelector;
	private TreeContainer treeContainer;
	private OptionGroup optionGroup;
	private HorizontalLayout layout;
	
	public OrgValueProvider() {
	}

	public void init (Selector selector) {
		this.selector = selector;
	}

	@Override
	public Component getValueList() {
		layout = new HorizontalLayout();
		layout.setSpacing(true);
		treeSelector = new TreeSelector();
		DataProvider treeDataProvider = new DynamicObjectRoot(Config.getProperty("nicki.users.basedn"), new OrgOnlyFilter());
		treeContainer = new TreeContainer(selector.getContext(), treeDataProvider, "Organsisation");
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
		optionGroup.setItemCaption(STOP, "nur diese");

		optionGroup.addItem(NO_STOP);
		optionGroup.setItemCaption(NO_STOP, "mit Kindern");

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
	public String getQuery(String value) {
		return null;
	}

	@Override
	public boolean isHierarchical() {
		return true;
	}

	@Override
	public BaseDn getBaseDn(String value) {
		StringBuffer base = new StringBuffer(Config.getProperty("nicki.users.basedn"));
		TYPE type = TYPE.ALL;
		if (StringUtils.equals(value, "/") || StringUtils.equals(value, "//")) {
			return new BaseDn(base.toString(), type);
		}
		if (StringUtils.endsWith(value, STOP)) {
			type = TYPE.SELF;
			value = StringUtils.substringBeforeLast(value, STOP);
		}
		String parts[] = StringUtils.split(value, "/");
		for (int i = 0; i < parts.length; i++) {
			base.insert(0, ",");
			base.insert(0, parts[i]);
			base.insert(0, "ou=");
		}
		return new BaseDn(base.toString(), type);
	}



}
