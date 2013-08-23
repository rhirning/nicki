package org.mgnl.nicki.vaadin.base.helper;

import org.apache.commons.beanutils.BeanUtils;
import org.mgnl.nicki.core.i18n.I18n;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;

public class ContainerHelper {
	private static final Logger LOG = LoggerFactory.getLogger(ContainerHelper.class);


	
	public static <T extends Object> Container getDataContainer(T data, String[] properties, String i18nBase) {
		Container container = new BeanItemContainer<ValuePair>(ValuePair.class);
		for (String property : properties) {
			addItem(container, data, property, i18nBase);
		}
		return container;
	}
	
	private static <T extends Object> void addItem(Container container, T data, String name, String i18nBase) {
		String translatedName = name;
		if (i18nBase != null) {
			translatedName = I18n.getText(i18nBase + "." + name);
		}
		try {
			
			if (data != null) {
				container.addItem(new ValuePair(translatedName, BeanUtils.getProperty(data, name)));
				
			} else {
				container.addItem(new ValuePair(translatedName, ""));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.error("Error", e);
		}
	}
}
