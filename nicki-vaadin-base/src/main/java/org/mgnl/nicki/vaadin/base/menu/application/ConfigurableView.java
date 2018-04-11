package org.mgnl.nicki.vaadin.base.menu.application;

import java.util.Map;

public interface ConfigurableView extends View {
	void setConfiguration(Map<String, String> configMap);

}
