package org.mgnl.nicki.vaadin.base.rights;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.rights.core.Right;
import org.mgnl.nicki.rights.core.RightAttribute;
import org.mgnl.nicki.rights.core.RightData;
import org.mgnl.nicki.rights.core.RightsGroup;
import org.mgnl.nicki.rights.core.RightsSet;
import org.mgnl.nicki.vaadin.base.command.SelectPersonCommand;
import org.mgnl.nicki.vaadin.base.editor.Icon;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;


@SuppressWarnings("serial")
public class RightsViewer extends CustomComponent {

	private AbsoluteLayout mainLayout;
	private Person user;
	private Person person;
	private RightsSet rightsSet;
	private Button saveButton;
	private DynamicObject parent = null;
	private PersonSelector personSelector;
	
	private static Map<String, AttributeComponent> attributeComponents = new HashMap<String, AttributeComponent>();
	static {
		attributeComponents.put("date", new DateComponent());
		attributeComponents.put("text", new TextComponent());
		attributeComponents.put("checkbox", new CheckboxComponent());
		attributeComponents.put("costCenter", new CostCenterComponent());
		attributeComponents.put("default", new LabelComponent());
	}


	public RightsViewer(Person user, RightsSet rightsSet, PersonSelector personSelector) {
		this.user = user;
		this.rightsSet = rightsSet;
		this.personSelector = personSelector;
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}


	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		Button selectPerson = new Button(I18n.getText("nicki.editor.generic.button.selectPerson"));
		selectPerson.addListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				personSelector.init(user.getContext(), new SelectPersonCommand() {
					
					@Override
					public void setSelectedPerson(Person selectedPerson) {
						person = selectedPerson;
						mainLayout.removeAllComponents();
						showTabs();
					}

				});
				Window window = new Window(I18n.getText("nicki.editor.bu.ouchange.window.person.title"), personSelector);
				window.setWidth(640, Sizeable.UNITS_PIXELS);
				window.setHeight(640, Sizeable.UNITS_PIXELS);
				window.setModal(true);

				getWindow().addWindow(window);
			}
		});
		
		mainLayout.addComponent(selectPerson, "top:20.0px;left:20.0px;");
		return mainLayout;
		
	}
	
	private void showTabs() {
		AbsoluteLayout personLayout = new AbsoluteLayout();
		personLayout.setHeight("30px");
		personLayout.setWidth("100%");
		Label personLabel = new Label(person.getDisplayName());
		personLabel.setWidth("200px");
		personLayout.addComponent(personLabel, "top:0.0px;left:20.0px;");

		Button selectPerson = new Button();
		selectPerson.setWidth("-1px");
		selectPerson.setHeight("-1px");
		selectPerson.setCaption("Auswählen");
		selectPerson.setImmediate(false);
		personLayout.addComponent(selectPerson, "top:0.0px;left:240.0px;");

		mainLayout.addComponent(personLayout, "top:0.0px;left:0.0px;");
		
		VerticalLayout layout = new VerticalLayout();
		mainLayout.addComponent(layout, "top:30.0px;left:0.0px;");
		
		TabSheet tabSheet = new TabSheet();
		tabSheet.setHeight(480, UNITS_PIXELS);

		layout.addComponent(tabSheet);
		
		addTabs(tabSheet);

		saveButton = new Button(I18n.getText("nicki.editor.generic.button.save"));
		saveButton.addListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				// save();
			}
		});

		layout.addComponent(saveButton);
	}
	
	private void addTabs(TabSheet tabSheet) {
		if (rightsSet.hasRightDatas()) {
			for (Iterator<RightData> iterator = rightsSet.getRightsDataList().iterator(); iterator.hasNext();) {
				RightData right = iterator.next();
				addRightDataTab(tabSheet, right);
			}
		}
		if (rightsSet.hasRights()) {
			for (Iterator<Right> iterator = rightsSet.getRightsList().iterator(); iterator.hasNext();) {
				Right right = iterator.next();
				addRightTab(tabSheet, right);
			}
		}
		if (rightsSet.hasRightsGroups()) {
			for (Iterator<RightsGroup> iterator = rightsSet.getRightsGroupsList().iterator(); iterator.hasNext();) {
				RightsGroup rightsGroup = iterator.next();
				addRightsGroupTab(tabSheet, rightsGroup);
			}
		}
		tabSheet.addTab(getXMLComponent(), "XML", Icon.DOCUMENT.getResource());
	}


	private void addRightDataTab(TabSheet tabSheet, RightData rightData) {
		tabSheet.addTab(getRightDataComponent(rightData), rightData.getLabel(), Icon.SETTINGS.getResource());
	}


	private Component getRightDataComponent(RightData rightData) {
		AbsoluteLayout layout = new AbsoluteLayout();
		layout.setData(rightData);
		layout.setHeight("420px");
		showRightsAttributes(layout, "top:20.0px;left:20.0px;right:20.0px;");
		return layout;
	}


	private void addRightsGroupTab(TabSheet tabSheet, RightsGroup rightsGroup) {
		tabSheet.addTab(getRightsGroupComponent(rightsGroup), rightsGroup.getLabel(), Icon.SETTINGS.getResource());
	}


	private Component getRightsGroupComponent(RightsGroup rightsGroup) {
		if (rightsGroup.hasRights()) {
			TabSheet tabSheet = new TabSheet();
			tabSheet.setHeight(480, UNITS_PIXELS);
			for (Iterator<Right> iterator = rightsGroup.getRightsList().iterator(); iterator.hasNext();) {
				Right right = iterator.next();
				addRightTab(tabSheet, right);
			}

			return tabSheet;
		} else {
			// TODO Provider ...
			AbsoluteLayout layout = new AbsoluteLayout();
			layout.setHeight("420px");

			return layout;
		}
	}


	private void addRightTab(TabSheet tabSheet, Right right) {
		tabSheet.addTab(getRightComponent(right), right.getLabel(), Icon.SETTINGS.getResource());
	}


	private Component getRightComponent(Right right) {
		AbsoluteLayout layout = new AbsoluteLayout();
		layout.setData(right);
		layout.setHeight("420px");
		CheckBox checkBox = new CheckBox(I18n.getText("nicki.rights.checkbox.label"));
		checkBox.setImmediate(true);
		layout.addComponent(checkBox, "top:20.0px;left:20.0px;right:20.0px;");
		checkBox.addListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				  boolean enabled = event.getButton().booleanValue();
				  AbsoluteLayout parent = (AbsoluteLayout) event.getButton().getParent();
				  if (enabled) {
					  showRightsAttributes(parent, "top:60.0px;left:20.0px;right:20.0px;");
				  } else {
					  removeExcept(parent, event.getButton());
				  }
			}
		});

		return layout;
	}


	protected void removeExcept(AbsoluteLayout parent, Button button) {
		for (Iterator<Component> iterator = parent.getComponentIterator(); iterator.hasNext();) {
			Component component = iterator.next();
			if (component != button) {
				parent.removeComponent(component);
			}
		}
	}


	private void showRightsAttributes(AbsoluteLayout layout, String cssString) {
		Right right = (Right) layout.getData(); 
		VerticalLayout attrLayout = new VerticalLayout();
		
		if (right.hasAttributes()) {
			for (Iterator<RightAttribute> iterator = right.getAttributeList().iterator(); iterator.hasNext();) {
				RightAttribute rightAttribute = iterator.next();
				attrLayout.addComponent(getAttributeComponent(rightAttribute));
			}
			layout.addComponent(attrLayout, cssString);
		}
	}


	private Component getAttributeComponent(RightAttribute rightAttribute) {
		if (attributeComponents.containsKey(rightAttribute.getType())) {
			return attributeComponents.get(rightAttribute.getType()).getInstance(rightAttribute);
		} else {
			return attributeComponents.get("default").getInstance(rightAttribute);
		}
	}


	private Component getXMLComponent() {
		AbsoluteLayout layout = new AbsoluteLayout();
		layout.setHeight("420px");
		// textArea_1
		TextArea xml = new TextArea();
		xml.setWidth("100%");
		xml.setHeight("100%");
		xml.setImmediate(false);
		xml.setValue(rightsSet);
		layout.addComponent(xml, "top:20.0px;left:20.0px;right:20.0px;");

		return layout;
	}
}
