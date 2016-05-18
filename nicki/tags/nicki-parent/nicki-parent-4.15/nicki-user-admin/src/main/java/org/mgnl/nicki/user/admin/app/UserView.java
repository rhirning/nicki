package org.mgnl.nicki.user.admin.app;

import javax.json.JsonObject;

import org.mgnl.nicki.app.menu.application.View;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

public class UserView extends CustomComponent implements View, SearchDialog {

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	@AutoGenerated
	private VerticalLayout mainLayout;
	@AutoGenerated
	private HorizontalLayout resultLayout;
	@AutoGenerated
	private SearchResultComponent searchResultComponent;
	@AutoGenerated
	private HorizontalLayout searchLayout;
	@AutoGenerated
	private SearchComponent searchComponent;
	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */
	public UserView() {
		buildMainLayout();
		setCompositionRoot(mainLayout);
		searchComponent.init(this);

		// TODO add user code here
	}

	@AutoGenerated
	private VerticalLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// searchLayout
		searchLayout = buildSearchLayout();
		mainLayout.addComponent(searchLayout);
		
		// resultLayout
		resultLayout = buildResultLayout();
		mainLayout.addComponent(resultLayout);
		mainLayout.setExpandRatio(resultLayout, 1.0f);
		
		return mainLayout;
	}

	@AutoGenerated
	private HorizontalLayout buildSearchLayout() {
		// common part: create layout
		searchLayout = new HorizontalLayout();
		searchLayout.setImmediate(false);
		searchLayout.setWidth("100.0%");
		searchLayout.setHeight("-1px");
		searchLayout.setMargin(false);
		
		// searchComponent_1
		searchComponent = new SearchComponent();
		searchComponent.setImmediate(false);
		searchComponent.setWidth("100.0%");
		searchComponent.setHeight("-1px");
		searchLayout.addComponent(searchComponent);
		
		return searchLayout;
	}

	@AutoGenerated
	private HorizontalLayout buildResultLayout() {
		// common part: create layout
		resultLayout = new HorizontalLayout();
		resultLayout.setImmediate(false);
		resultLayout.setWidth("100.0%");
		resultLayout.setHeight("100.0%");
		resultLayout.setMargin(false);
		
		// searchResultComponent_1
		searchResultComponent = new SearchResultComponent();
		searchResultComponent.setImmediate(false);
		searchResultComponent.setWidth("100.0%");
		searchResultComponent.setHeight("100.0%");
		resultLayout.addComponent(searchResultComponent);
		
		return resultLayout;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isModified() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void search(JsonObject query) {
		// TODO Auto-generated method stub
		
	}

}