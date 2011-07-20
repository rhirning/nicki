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
import org.mgnl.nicki.vaadin.base.editor.Icon;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class RightsViewer extends CustomComponent {

	private AbsoluteLayout mainLayout;
	private Person person;
	private RightsSet rightsSet;
	private Button saveButton;
	private DynamicObject parent = null;
	
	private static Map<String, AttributeComponent> attributeComponents = new HashMap<String, AttributeComponent>();
	static {
		attributeComponents.put("date", new DateComponent());
		attributeComponents.put("text", new TextComponent());
		attributeComponents.put("checkbox", new CheckboxComponent());
		attributeComponents.put("costCenter", new CostCenterComponent());
		attributeComponents.put("default", new LabelComponent());
	}


	public RightsViewer(Person person, RightsSet rightsSet) {
		this.person = person;
		this.rightsSet = rightsSet;
		buildMainLayout();
		setCompositionRoot(mainLayout);
		
		

	}


	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		VerticalLayout layout = new VerticalLayout();
		mainLayout.addComponent(layout, "top:0.0px;left:0.0px;");

		TabSheet tabSheet = new TabSheet();
		tabSheet.setHeight(480, UNITS_PIXELS);

		layout.addComponent(tabSheet);
		
		addTabs(tabSheet);

//		layout.addComponent(getLayout());
		
		saveButton = new Button(I18n.getText("nicki.editor.generic.button.save"));
		saveButton.addListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				// save();
			}
		});

		layout.addComponent(saveButton);
		return mainLayout;
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


	protected Layout getLayout() {
		VerticalLayout layout = new VerticalLayout();
		/*
		DynamicObjectFieldFactory factory = new DynamicObjectFieldFactory(listener);
		factory.addFields(layout, dynamicObject, create);
		*/
		return layout;
	}

}
