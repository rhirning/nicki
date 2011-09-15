package org.mgnl.nicki.shop.catalog;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.dynamic.objects.objects.DynamicTemplateObject;
import org.mgnl.nicki.dynamic.objects.types.TextArea;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.shop.rules.ValueProvider;

@SuppressWarnings("serial")
public class Selector extends DynamicTemplateObject {
	public void initDataModel() {
		addObjectClass("nickiSelector");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute("value", "nickiSelectorValue", String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute("valueProvider", "nickiSelectorValueProvider", TextArea.class);
		addAttribute(dynAttribute);
		
	}

	@SuppressWarnings("unchecked")
	public List<String> getValues() {
		List<String> values = (List<String>) get("value");
		if (values != null && values.size() > 0) {
			return values;
		} else {
			return new ArrayList<String>();
		}
	}

	public boolean hasValueProvider() {
		return StringUtils.isNotEmpty(getValueProviderClass());
	}

	public String getValueProviderClass() {
		return (String) get("valueProvider");
	}

	public ValueProvider getValueProvider() {
		try {
			ValueProvider provider = (ValueProvider)Class.forName(getValueProviderClass()).newInstance();
			return provider;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
