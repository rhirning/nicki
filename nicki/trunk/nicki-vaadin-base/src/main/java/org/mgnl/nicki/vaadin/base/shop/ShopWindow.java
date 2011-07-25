package org.mgnl.nicki.vaadin.base.shop;


import java.util.Iterator;
import java.util.List;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.Catalog;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;
import org.mgnl.nicki.vaadin.base.editor.TreeAction;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.Action;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class ShopWindow extends CustomComponent {
	@AutoGenerated
	private AbsoluteLayout mainLayout;
	@AutoGenerated
	private VerticalLayout verticalLayout_1;
	@AutoGenerated
	private HorizontalLayout horizontalLayout_2;
	@AutoGenerated
	private Button closeButton;
	@AutoGenerated
	private Button saveButton;
	@AutoGenerated
	private HorizontalLayout horizontalLayout_3;
	@AutoGenerated
	private Table articles;
	@AutoGenerated
	private HorizontalLayout categoryContainer;
	@AutoGenerated
	private HorizontalLayout horizontalLayout_1;
	public Action ACTION_ORDER;
	public Action ACTION_CANCEL;
    public Action ACTIONS[];

	
	private DynamicObject person;
	private Catalog catalog;
	private ShopContainer shopContainer;
	private TreeAction treeAction;
	List<String> categories;
	IndexedContainer container;
	private String i18nBase;

	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 * @param i18nBase 
	 * @param treeAction 
	 */
	public ShopWindow(TreeAction _treeAction, DynamicObject dynamicObject, Catalog catalog, String i18nBase) {
		this.i18nBase = i18nBase;
		this.treeAction = _treeAction;
		this.person = dynamicObject;
		this.catalog = catalog;
		shopContainer = new RoleResourceShopContainer(this.person, i18nBase);
//		shopContainer = new ArticleShopContainer(this.shop, this.person, i18nBase);
		buildMainLayout();
		setCompositionRoot(mainLayout);
		
		loadCategories();
		initActions();
		
		try {
			loadArticles();
		} catch (DynamicObjectException e) {
			e.printStackTrace();
		}
		articles.addActionHandler(new Action.Handler() {
			
			public void handleAction(Action action, Object sender, Object target) {
				if (action == ACTION_ORDER) {
					shopContainer.orderItem(target);
					articles.requestRepaint();
				} else if (action == ACTION_CANCEL) {
						shopContainer.cancelItem(target);
						articles.requestRepaint();
					}
			}
			
			public Action[] getActions(Object target, Object sender) {
				return ACTIONS;
			}
		});
		
		closeButton.addListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				treeAction.close();
			}
		});
	}
	
	private void initActions() {
		ACTION_ORDER = new Action(I18n.getText(i18nBase + ".action.order"));
		ACTION_CANCEL = new Action(I18n.getText(i18nBase + ".action.cancel"));
		ACTIONS = new Action[]{ACTION_ORDER, ACTION_CANCEL};
	}

	private void loadArticles() throws DynamicObjectException {
		this.container = shopContainer.getArticles();
		articles.setContainerDataSource(container);
		articles.setVisibleColumns(shopContainer.getVisibleColumns());
		articles.setSelectable(true);
	}

	@SuppressWarnings("unchecked")
	private void loadCategories() {

		categories = (List<String>) catalog.get("category");
		if (categories != null && categories.size() > 0) {
			CategoryChangeListener listener = new CategoryChangeListener(this, categoryContainer); 
			for (Iterator<String> iterator = categories.iterator(); iterator.hasNext();) {
				String category = iterator.next();
				CheckBox checkBox = new CheckBox(category, false);
				checkBox.setImmediate(true);
				checkBox.addListener(listener);
				categoryContainer.addComponent(checkBox);
			}
		}
	}

	@AutoGenerated
	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// verticalLayout_1
		verticalLayout_1 = buildVerticalLayout_1();
		mainLayout.addComponent(verticalLayout_1, "top:20.0px;left:20.0px;");
		
		return mainLayout;
	}

	@AutoGenerated
	private VerticalLayout buildVerticalLayout_1() {
		// common part: create layout
		verticalLayout_1 = new VerticalLayout();
		verticalLayout_1.setWidth("100.0%");
		verticalLayout_1.setHeight("-1px");
		verticalLayout_1.setImmediate(false);
		verticalLayout_1.setMargin(false);
		
		// horizontalLayout_1
		horizontalLayout_1 = new HorizontalLayout();
		horizontalLayout_1.setWidth("-1px");
		horizontalLayout_1.setHeight("24px");
		horizontalLayout_1.setImmediate(false);
		horizontalLayout_1.setMargin(false);
		verticalLayout_1.addComponent(horizontalLayout_1);
		
		// categoryContainer
		categoryContainer = new HorizontalLayout();
		categoryContainer.setWidth("-1px");
		categoryContainer.setHeight("24px");
		categoryContainer.setImmediate(false);
		categoryContainer.setMargin(false);
		verticalLayout_1.addComponent(categoryContainer);
		
		// articles
		articles = new Table();
		articles.setWidth("100.0%");
		articles.setHeight("100.0%");
		articles.setImmediate(true);
		verticalLayout_1.addComponent(articles);
		
		// horizontalLayout_3
		horizontalLayout_3 = new HorizontalLayout();
		horizontalLayout_3.setWidth("-1px");
		horizontalLayout_3.setHeight("20px");
		horizontalLayout_3.setImmediate(false);
		horizontalLayout_3.setMargin(false);
		verticalLayout_1.addComponent(horizontalLayout_3);
		
		// horizontalLayout_2
		horizontalLayout_2 = buildHorizontalLayout_2();
		verticalLayout_1.addComponent(horizontalLayout_2);
		verticalLayout_1.setComponentAlignment(horizontalLayout_2,
				new Alignment(9));
		
		return verticalLayout_1;
	}

	@AutoGenerated
	private HorizontalLayout buildHorizontalLayout_2() {
		// common part: create layout
		horizontalLayout_2 = new HorizontalLayout();
		horizontalLayout_2.setWidth("-1px");
		horizontalLayout_2.setHeight("-1px");
		horizontalLayout_2.setImmediate(false);
		horizontalLayout_2.setMargin(false);
		
		// saveButton
		saveButton = new Button();
		saveButton.setWidth("-1px");
		saveButton.setHeight("-1px");
		saveButton.setCaption("Save");
		saveButton.setImmediate(true);
		horizontalLayout_2.addComponent(saveButton);
		
		// closeButton
		closeButton = new Button();
		closeButton.setWidth("-1px");
		closeButton.setHeight("-1px");
		closeButton.setCaption("Close");
		closeButton.setImmediate(true);
		horizontalLayout_2.addComponent(closeButton);
		
		return horizontalLayout_2;
	}

	public void setCategoryFilter(List<Object> values) {
		this.shopContainer.setCategoryFilter(values);
	}

}
