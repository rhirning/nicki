package org.mgnl.nicki.vaadin.base.components;

import org.mgnl.nicki.core.i18n.I18n;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Container;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class DataView extends CustomComponent {

	@AutoGenerated
	private AbsoluteLayout mainLayout;
	@AutoGenerated
	private Button closeButton;
	@AutoGenerated
	private Table userTable;
	@AutoGenerated
	private Label title;
	private String titleString;
	private Container container;
	private int length;
	private String i18nBase;
	
	private static final long serialVersionUID = -1786011895038782304L;
	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 * @param pnwPerson 
	 * @param actionsView 
	 */
	public DataView(Container container, String titleString, int length, String i18nBase) {
		this.titleString = titleString;
		this.container = container;
		this.length = length;
		this.i18nBase = i18nBase;

		buildMainLayout();
		setCompositionRoot(mainLayout);

		init();
	}
	
	private void init() {
		title.setValue(titleString);
		userTable.setContainerDataSource(this.container);
		userTable.setColumnWidth("name", 200);
		userTable.setColumnWidth("value", 600);
		userTable.setPageLength(this.length);
		userTable.setColumnHeader("name", I18n.getText(getI18nBase() + ".columnName"));
		userTable.setColumnHeader("value", I18n.getText(getI18nBase() + ".columnValue"));
		closeButton.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = -7852005230375984676L;

			@Override
			public void buttonClick(ClickEvent event) {
				((Window) getParent()).close();
			}
		});
		closeButton.setCaption(I18n.getText(getI18nBase() + ".closeButton"));
	}

	@AutoGenerated
	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// title
		title = new Label();
		title.setImmediate(false);
		title.setWidth("-1px");
		title.setHeight("-1px");
		title.setValue("Verantwortlichkeit aendern");
		mainLayout.addComponent(title, "top:20.0px;left:20.0px;");
		
		// userTable
		userTable = new Table();
		userTable.setImmediate(false);
		userTable.setWidth("-1px");
		userTable.setHeight("-1px");
		mainLayout.addComponent(userTable,
				"top:40.0px;right:20.0px;left:20.0px;");
		
		// closeButton
		closeButton = new Button();
		closeButton.setCaption("Close");
		closeButton.setImmediate(false);
		closeButton.setWidth("-1px");
		closeButton.setHeight("-1px");
		mainLayout.addComponent(closeButton, "bottom:20.0px;left:20.0px;");
		
		return mainLayout;
	}

	private String getI18nBase() {
		return i18nBase;
	}

	public void setI18nBase(String i18nBase) {
		this.i18nBase = i18nBase;
	}

}
