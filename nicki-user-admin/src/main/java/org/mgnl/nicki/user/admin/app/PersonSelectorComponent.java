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
package org.mgnl.nicki.user.admin.app;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.ldap.helper.LdapHelper;
import org.mgnl.nicki.ldap.helper.LdapHelper.LOGIC;
import org.mgnl.nicki.vaadin.base.command.SelectPersonCommand;
import org.mgnl.nicki.vaadin.base.components.DataView;
import org.mgnl.nicki.vaadin.base.helper.ContainerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;


/**
 * TODO: Use internalExternal in query
 * TODO: Use activeInactive in query
 * TODO: Use department in query
 * TODO: Filter: HA des Users
 * 
 * @author rhirning
 *
 */

@SuppressWarnings("serial")
public class PersonSelectorComponent extends CustomComponent {
	private static final Logger LOG = LoggerFactory.getLogger(PersonSelectorComponent.class);
	@AutoGenerated
	private AbsoluteLayout mainLayout;
	@AutoGenerated
	private Label errorLabel;
	@AutoGenerated
	private HorizontalLayout buttonLayout;
	@AutoGenerated
	private Button detailsButton;
	@AutoGenerated
	private Button selectButton;
	@AutoGenerated
	private Table searchResult;
	@AutoGenerated
	private Button searchButton;
	@AutoGenerated
	private OptionGroup activeInactive;
	@AutoGenerated
	private TextField department;
	@AutoGenerated
	private NativeSelect company;
	@AutoGenerated
	private TextField givenName;
	@AutoGenerated
	private TextField name;
	@AutoGenerated
	private TextField userId;

	public enum WILDCARD {YES, NO};


	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */




	public static final String USER_BASE = "nicki.users.basedn";
	private static Object[] defaultVisibleCols = {
		Person.ATTRIBUTE_DISPLAYNAME,
	};
	private static String[] detailCols = {
		Person.ATTRIBUTE_DISPLAYNAME,
		Person.ATTRIBUTE_LOCATION,
	};

	public enum ACTIVE_INACTIVE {
		ACTIVE, INACTIVE, NOT_SET, RESIGNED}

	private NickiContext context;
	private SelectPersonCommand command;
	private Person user;

	private boolean useActiveInactive;
	private boolean showSelect = true;
	private boolean showDetail = true;

	private String filter;
	private Object[] visibleCols;

	private Window detailsWindow;

	public PersonSelectorComponent() {
		buildMainLayout();
		searchResult.setSelectable(true);
		setCompositionRoot(mainLayout);
		initI18n();
	}

	public PersonSelectorComponent(Person user) {
		this.user = user;
		LOG.debug("User = " + this.user.getName());
		buildMainLayout();
		searchResult.setSelectable(true);
		setCompositionRoot(mainLayout);
		initI18n();
	}

	public void init(NickiContext ctx, boolean useInternExtern, boolean useActiveInactive, SelectPersonCommand selectCommand) {
		this.context = ctx;
		this.useActiveInactive = useActiveInactive;
		this.command = selectCommand;

		userId.focus();
		initActiveInactive();

		searchButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (!isComplete()) {
					searchResult.setContainerDataSource(null);
					errorLabel.setValue(I18n.getText(getI18nBase() + ".error.searchstring"));
					return;
				} else {
					errorLabel.setValue("");
				}
				Container result = getBeanItems(searchAll());
				searchResult.setContainerDataSource(result);
				searchResult.setColumnHeader(Person.ATTRIBUTE_DISPLAYNAME, I18n.getText(getI18nBase() + ".property.displayName"));
				searchResult.setVisibleColumns(getVisibleCols());
				if (result != null && result.size() > 0) {
					selectFirstItem(searchResult);
				} else {
					Notification.show(I18n.getText(getI18nBase() + ".error.emptyresult"),
							Notification.Type.HUMANIZED_MESSAGE);
				}
			}
		});

		searchButton.setClickShortcut(KeyCode.ENTER);

		if (isShowSelect()) {
			selectButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if (getSelectedPerson() != null) {
						command.setSelectedPerson(getSelectedPerson());
					} else {
						Notification.show(I18n.getText(getI18nBase() + ".error.noperson"),
								Notification.Type.WARNING_MESSAGE);
					}
				}
			});
		} else {
			buttonLayout.removeComponent(selectButton);
		}
		if (isShowDetail()) {
			detailsButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if (getSelectedPerson() != null) {
						showDetails(getSelectedPerson());
					} else {
						Notification.show(I18n.getText(getI18nBase() + ".error.noperson"),
								Notification.Type.WARNING_MESSAGE);
					}
				}
			});
		} else {
			buttonLayout.removeComponent(detailsButton);
		}

	}


	protected void selectFirstItem(Table table) {
		table.select(table.getItemIds().iterator().next());
	}

	private void showDetails(Person person) {
		Container container = ContainerHelper.getDataContainer(person, detailCols, getI18nBase() + ".property");


		DataView dataView = new DataView(container, I18n.getText(getI18nBase() + ".details.title"), detailCols.length,
				getI18nBase() + ".property");

		if (detailsWindow != null) {
			detailsWindow.close();
		}
		detailsWindow = new Window(I18n.getText(getI18nBase()
				+ ".details.title"), dataView);
		detailsWindow.setWidth(900, Unit.PIXELS);
		detailsWindow.setHeight(640, Unit.PIXELS);
		detailsWindow.setModal(true);

		UI.getCurrent().addWindow(detailsWindow);
	}



	private void initActiveInactive() {
		if (useActiveInactive) {
			activeInactive.setVisible(true);
			activeInactive.addItem(ACTIVE_INACTIVE.ACTIVE);
			activeInactive.setItemCaption(ACTIVE_INACTIVE.ACTIVE, I18n.getText(getI18nBase() + ".status.active"));
			activeInactive.addItem(ACTIVE_INACTIVE.INACTIVE);
			activeInactive.setItemCaption(ACTIVE_INACTIVE.INACTIVE, I18n.getText(getI18nBase() + ".status.inactive"));
			activeInactive.addItem(ACTIVE_INACTIVE.NOT_SET);
			activeInactive.setItemCaption(ACTIVE_INACTIVE.NOT_SET, I18n.getText(getI18nBase() + ".status.notset"));
			activeInactive.addItem(ACTIVE_INACTIVE.RESIGNED);
			activeInactive.setItemCaption(ACTIVE_INACTIVE.RESIGNED, I18n.getText(getI18nBase() + ".status.resigned"));
			activeInactive.setValue(ACTIVE_INACTIVE.NOT_SET);
		} else {
			activeInactive.setVisible(false);
		}

	}

	protected boolean isComplete() {
		if (validateText(userId.getValue(), 3)) {
			return true;
		}
		if (validateText(name.getValue(), 3)) {
			return true;
		}
		if (validateText(givenName.getValue(), 3)) {
			return true;
		}
		if (validateText(department.getValue(), 2)) {
			return true;
		}
		return false;
	}


	protected Collection<Person> searchAll() {
		String baseDn = Config.getProperty("nicki.users.basedn");

		StringBuilder filter = new StringBuilder();
		addQuery(filter, "cn", userId.getValue(), LdapHelper.LOGIC.AND, WILDCARD.YES);
		addQuery(filter, "surname", name.getValue(), LdapHelper.LOGIC.AND, WILDCARD.YES);
		addQuery(filter, "givenname", givenName.getValue(), LdapHelper.LOGIC.AND, WILDCARD.YES);
		addQuery(filter, "company", (String) company.getValue(), LdapHelper.LOGIC.AND, WILDCARD.NO);
		addQuery(filter, "ou", department.getValue(), LdapHelper.LOGIC.AND, WILDCARD.YES);

		if (getFilter() != null) {
			LdapHelper.addQuery(filter, getFilter(), LOGIC.AND);
		}

		return this.context.loadObjects(Person.class, baseDn, filter.toString());
	}

	private void addQuery(StringBuilder filter, String attribute, String searchString,
			LOGIC andOR, WILDCARD wildcard) {
		if (StringUtils.isNotBlank(searchString)) {
			LdapHelper.addQuery(filter, attribute + "=" +
					(wildcard==WILDCARD.YES?searchString + "*":StringUtils.replace(searchString,"*","")),
					andOR);
		}
	}

	public Person getSelectedPerson() {
		return (Person) searchResult.getValue();
	}

	private Container getBeanItems(Collection<Person> results) {
		// Create a container for such beans
		BeanItemContainer<Person> persons =
				new BeanItemContainer<Person>(Person.class);
		if (results != null) {
			for (Person Person : results) {
				persons.addBean( Person);
			}
		}
		return persons;
	}

	private void initI18n() {
		userId.setCaption(I18n.getText(getI18nBase() + ".caption.userId"));
		name.setCaption(I18n.getText(getI18nBase() + ".caption.name"));
		givenName.setCaption(I18n.getText(getI18nBase() + ".caption.givenName"));
		company.setCaption(I18n.getText(getI18nBase() + ".caption.company"));
		department.setCaption(I18n.getText(getI18nBase() + ".caption.department"));
		searchButton.setCaption(I18n.getText(getI18nBase() + ".caption.search"));
		selectButton.setCaption(I18n.getText(getI18nBase() + ".caption.select"));
		detailsButton.setCaption(I18n.getText(getI18nBase() + ".caption.details"));
	}

	private String getI18nBase() {
		return "pnw.gui.personselector";
	}



	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public Object[] getVisibleCols() {
		if (visibleCols != null) {
			return visibleCols;
		} else {
			return defaultVisibleCols;
		}
	}

	public void setVisibleCols(Object[] visibleCols) {
		this.visibleCols = visibleCols;
	}

	public boolean isShowDetail() {
		return showDetail;
	}

	public void setShowDetail(boolean showDetail) {
		this.showDetail = showDetail;
	}

	public boolean isShowSelect() {
		return showSelect;
	}

	public void setShowSelect(boolean showSelect) {
		this.showSelect = showSelect;
	}

	public boolean validateText(String value, int minLength) {
		String newValue = StringUtils.remove(value, "*");
		if (StringUtils.isNotBlank(newValue) && newValue.length() >= minLength) {
			return true;
		}
		return false;
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

		// userId
		userId = new TextField();
		userId.setCaption("Benutzerkennung");
		userId.setImmediate(false);
		userId.setWidth("-1px");
		userId.setHeight("-1px");
		mainLayout.addComponent(userId, "top:20.0px;left:20.0px;");

		// name
		name = new TextField();
		name.setCaption("Nachname");
		name.setImmediate(false);
		name.setWidth("-1px");
		name.setHeight("-1px");
		mainLayout.addComponent(name, "top:60.0px;left:20.0px;");

		// givenName
		givenName = new TextField();
		givenName.setCaption("Vorname");
		givenName.setImmediate(false);
		givenName.setWidth("-1px");
		givenName.setHeight("-1px");
		mainLayout.addComponent(givenName, "top:100.0px;left:20.0px;");

		// company
		company = new NativeSelect();
		company.setCaption("Firma");
		company.setImmediate(false);
		company.setWidth("-1px");
		company.setHeight("-1px");
		mainLayout.addComponent(company, "top:140.0px;left:20.0px;");

		// department
		department = new TextField();
		department.setCaption("Abteilung");
		department.setImmediate(false);
		department.setWidth("-1px");
		department.setHeight("-1px");
		mainLayout.addComponent(department, "top:180.0px;left:20.0px;");

		// activeInactive
		activeInactive = new OptionGroup();
		activeInactive.setImmediate(false);
		activeInactive.setWidth("-1px");
		activeInactive.setHeight("-1px");
		mainLayout.addComponent(activeInactive, "top:20.0px;left:320.0px;");

		// searchButton
		searchButton = new Button();
		searchButton.setCaption("Suche");
		searchButton.setImmediate(true);
		searchButton.setWidth("-1px");
		searchButton.setHeight("-1px");
		mainLayout.addComponent(searchButton, "top:180.0px;left:180.0px;");

		// searchResult
		searchResult = new Table();
		searchResult.setImmediate(false);
		searchResult.setWidth("100.0%");
		searchResult.setHeight("320px");
		mainLayout.addComponent(searchResult,
				"top:220.0px;right:20.0px;left:20.0px;");

		// buttonLayout
		buttonLayout = buildButtonLayout();
		mainLayout.addComponent(buttonLayout, "top:550.0px;left:20.0px;");

		// errorLabel
		errorLabel = new Label();
		errorLabel.setStyleName("red");
		errorLabel.setImmediate(false);
		errorLabel.setWidth("100.0%");
		errorLabel.setHeight("180px");
		mainLayout.addComponent(errorLabel,
				"top:20.0px;right:20.0px;left:490.0px;");

		return mainLayout;
	}

	@AutoGenerated
	private HorizontalLayout buildButtonLayout() {
		// common part: create layout
		buttonLayout = new HorizontalLayout();
		buttonLayout.setImmediate(false);
		buttonLayout.setWidth("-1px");
		buttonLayout.setHeight("-1px");
		buttonLayout.setMargin(false);

		// selectButton
		selectButton = new Button();
		selectButton.setCaption("Ausw�hlen");
		selectButton.setImmediate(true);
		selectButton.setWidth("-1px");
		selectButton.setHeight("-1px");
		buttonLayout.addComponent(selectButton);

		// detailsButton
		detailsButton = new Button();
		detailsButton.setCaption("Details");
		detailsButton.setImmediate(true);
		detailsButton.setWidth("-1px");
		detailsButton.setHeight("-1px");
		buttonLayout.addComponent(detailsButton);

		return buttonLayout;
	}

}
