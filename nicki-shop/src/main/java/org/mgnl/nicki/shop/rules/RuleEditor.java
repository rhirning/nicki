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
package org.mgnl.nicki.shop.rules;

import java.util.List;

import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.core.util.Classes;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.shop.base.objects.Selector;
import org.mgnl.nicki.vaadin.base.components.DialogBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.NativeSelect;

@SuppressWarnings("serial")
public class RuleEditor extends DialogBase {
	private static final Logger LOG = LoggerFactory.getLogger(RuleEditor.class);

	@AutoGenerated
	private AbsoluteLayout mainLayout;
	@AutoGenerated
	private Button addButton;
	@AutoGenerated
	private Component value;
	@AutoGenerated
	private NativeSelect selector;
	
	private ValueProviderComponent valueProvider;
	
	private RuleAttributeField.RuleHandler ruleHandler;
	
	private DynamicObject dynamicObject;
	private String i18nBase;

	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */
	public RuleEditor(DynamicObject dynamicObject, String i18nBase, String title) {
		super(title);
		this.dynamicObject = dynamicObject;
		this.i18nBase = i18nBase;
		buildMainLayout();
		initI18n();
		setCompositionRoot(mainLayout);
		initSelector();
		addButton.addClickListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				if (getValue() != null) {
					ruleHandler.setRule((Selector) selector.getValue(), getValue());
					close();
				}
			}
		});
	}


	private void initI18n() {
		selector.setCaption(I18n.getText(i18nBase + ".selector.title"));
		addButton.setCaption(I18n.getText(i18nBase + ".addbutton.title"));
	}


	protected String getValue() {
		return valueProvider.getValue();
	}


	private void initSelector() {
		selector.addValueChangeListener(new Property.ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				initValues((Selector)event.getProperty().getValue());
			}
		});
		selector.setNullSelectionAllowed(false);
		List<Selector> selectorList = 
			this.dynamicObject.getContext().loadChildObjects(Selector.class, Config.getProperty("nicki.selectors.basedn"),
					null);
		int count = 0;
		for (Selector s : selectorList) {
			count++;
			selector.addItem(s);
			selector.setItemCaption(s, s.getDisplayName());
			if (count == 1) {
				selector.select(s);
			}
		}
	}

	protected void initValues(Selector selectedSelector) {
		if (value != null) {
			mainLayout.removeComponent(value);
			value = null;
		}
		valueProvider = null;
		if (selectedSelector.hasValueProvider()) {
			try {
				valueProvider = getValueProvider(selectedSelector);
			} catch (Exception e) {
				valueProvider = null;
				LOG.error("Error", e);
			} 
		}
	
		if (this.valueProvider == null) {
			valueProvider = new ListValueProvider();
		}
		valueProvider.init(selectedSelector, this.i18nBase);
		value = valueProvider.getValueList();
		mainLayout.addComponent(value, "top:60.0px;left:20.0px;");
		
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
		
		// selector
		selector = new NativeSelect();
		selector.setCaption("Merkmal");
		selector.setImmediate(true);
		selector.setWidth("200px");
		selector.setHeight("-1px");
		mainLayout.addComponent(selector, "top:20.0px;left:20.0px;");
		
		// addButton
		addButton = new Button();
		addButton.setCaption("Add");
		addButton.setImmediate(false);
		addButton.setWidth("-1px");
		addButton.setHeight("-1px");
		mainLayout.addComponent(addButton, "top:20.0px;left:240.0px;");
		
		return mainLayout;
	}


	public void setHandler(RuleAttributeField.RuleHandler ruleHandler) {
		this.ruleHandler = ruleHandler;
	}
	
	public ValueProviderComponent getValueProvider(Selector selector) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		return (ValueProviderComponent) Classes.newInstance((String) selector.getValueProviderClass());
	};


}
