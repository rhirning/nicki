package org.mgnl.nicki.vaadin.base.views;

import java.util.Map;

public interface InfoStore {

	void setData(String value) throws InfoStoreException;

	void setConfiguration(Map<String, String> configuration);

	void save() throws InfoStoreException;
	String getData() throws InfoStoreException;

}
